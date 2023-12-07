package com.n4no.webpencoder.webp.io;

import android.graphics.Color;
import android.util.Log;

import com.n4no.webpencoder.webp.utils.Logs;
import com.n4no.webpencoder.webp.utils.WebpChunk;
import com.n4no.webpencoder.webp.utils.WebpChunkType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public class WebpContainerReader {

	private final InputStream _inputStream;
	private final boolean _debug;
	private int _fileSize;
	private int _offset;
	private String TAG = "WebpContainerReader";
	public WebpContainerReader(InputStream inputStream, boolean debug) {
		_inputStream = inputStream;
		_debug = debug;
		Logs.enable(this);
	}

	public void close() throws IOException {
//		_inputStream.close();
	}

	public void readHeader() throws IOException {
		byte[] fcc = new byte[4];
		read(fcc, 4);
		if (!isFourCc(fcc, 'R', 'I', 'F', 'F'))
			throw new IOException("Expected RIFF file.");

		_fileSize = readUInt32() + 8 - 1;

//		Logs.i(this,"_fileSize:"+_fileSize);

		read(fcc, 4);
		if (!isFourCc(fcc, 'W', 'E', 'B', 'P'))
			throw new IOException("Expected Webp file.");
	}

	public WebpChunk read() throws IOException {
		byte[] fcc = new byte[4];

		if (read(fcc, 4) > 0) {
			Log.d(TAG,"read() 4C - "+new String(fcc));
			if (isFourCc(fcc, 'V', 'P', '8', 'X')) {
				return readVp8x();
			}
			if (isFourCc(fcc, 'A', 'N', 'I', 'M')) {
				return readAnim();
			}

			if (isFourCc(fcc, 'A', 'N', 'M', 'F')) {
				return readAnmf();
			}

			if (isFourCc(fcc, 'V', 'P', '8', ' ')) {
				return readVp8();
			}

			if (isFourCc(fcc, 'V', 'P', '8', 'L')) {
				return readVp8l();
			}
			//
			if (isFourCc(fcc, 'I', 'C', 'C', 'P'))
				return readIccp();
			if (isFourCc(fcc, 'A', 'L', 'P', 'H'))
				return readAlph();
			if (isFourCc(fcc, 'X', 'M', 'P', ' '))
				return readXmp();

			if (isFourCc(fcc, 'E', 'X', 'I', 'F'))
				return readExif();

			try{
				readUnknown(fcc);
//				Logs.w(this,"readUnknown() - "+new String(fcc));
			}catch (Exception e){
				throw new IOException(String.format("Not supported FourCC: %c.%c.%c.%c.",
						fcc[0], fcc[1], fcc[2], fcc[3]));
			}

		}

		if (_fileSize != _offset)
			throw new IOException(String.format("Header has wrong file size: %d, expected: %d", 
					_fileSize, _offset));
		return null;
	}


	private WebpChunk readUnknown(byte[] fcc) throws IOException {
		int chunkSize = readUInt32();
		byte[] payload = readPayload(chunkSize);

		if (payload.length < 0) {
			throw new IOException("Invalid chunk size");
		}

		return new WebpChunk(WebpChunkType.UNKNOWN);
	}

	private WebpChunk readVp8x() throws IOException {
		int chunkSize = readUInt32();
		if (chunkSize != 10)
			throw new IOException("Expected 10 bytes for VP8X.");

		WebpChunk chunk = new WebpChunk(WebpChunkType.VP8X);

		byte[] flags = new byte[4];
		read(flags, 4);
		BitSet bs = BitSet.valueOf(flags);

//		bs.get(0); 					 // R reserved
		chunk.hasAnim	= bs.get(1); // A Animation
		chunk.hasXmp	= bs.get(2); // X XMP
		chunk.hasExif	= bs.get(3); // E Exif
		chunk.hasAlpha	= bs.get(4); // L Alpha
		chunk.hasIccp	= bs.get(5); // I ICCP

		Logs.i(this,"vp8x-bs: " + bs);

		chunk.canvasWidth = readUInt24();
		chunk.canvasHeight = readUInt24();
		chunk.flags = flags;

//		Logs.enable(this);
		Logs.i(this,"canvasWidth " + chunk.canvasWidth + " chunk.canvasHeight " + chunk.canvasHeight );

		debug(String.format("VP8X: size = %dx%d", chunk.width, chunk.height));
		return chunk;
	}

	private WebpChunk readAnim() throws IOException {
//		Logs.e(this," ANIM- ");
		int chunkSize = readUInt32();
		if (chunkSize != 6)
			throw new IOException("Expected 6 bytes for ANIM.");

		WebpChunk chunk = new WebpChunk(WebpChunkType.ANIM);
		chunk.background = readUInt32();
		chunk.loops = readUInt16();

		Logs.i(this,"anim-bg: " + chunk.background+", color.trans->" +Color.TRANSPARENT);
		debug(String.format("ANIM: loops = %d", chunk.loops));
		return chunk;
	}

	private WebpChunk readAnmf() throws IOException {
		int chunkSize = readUInt32();
//		Logs.v(this,"chunkSize "+(chunkSize-16));
		WebpChunk chunk = new WebpChunk(WebpChunkType.ANMF);

		// 15 bytes
		chunk.x = readUInt24();
		chunk.y = readUInt24();
		chunk.width = readUInt24();
		chunk.height = readUInt24();
		int duration = readUInt24();
//		Logs.enable(this);
//		Logs.i(this, "duration: " + duration);
//		Logs.i(this, "width: " + chunk.width + ", height " + chunk.height);
//		Logs.i(this, "x: " + chunk.x + ", y " + chunk.y);
		chunk.duration = duration;

		// +1 = 16
		byte[] flags = new byte[1];
		read(flags, 1);
		BitSet bs = BitSet.valueOf(flags);
		chunk.useAlphaBlending = bs.get(1);
		chunk.disposeToBackgroundColor = bs.get(0);

		// log bs with each index from 0 to end

//		Logs.i(this,"ANMF:: - blend, dispose "+bs);

		// +4 = 20
		byte[] cch = new byte[4];
		read(cch, 4);

		byte[] bitStream = null;
		byte[] alphaData = null;
//		Logs.enable(this);
//		Logs.d(this,"ANMF payload size "+chunkSize);
		if (isFourCc(cch, 'A', 'L', 'P', 'H')) {
			chunk.isLossless = false;
			chunk.hasALPHchunk = true;
			int AlphaSize = readUInt32();


			if((AlphaSize&1)==1){
				AlphaSize += 1;
				Logs.w(this,"ANMF/ALPH payload size :" +(AlphaSize%4)+" | "+AlphaSize);
			}

			chunk.alphaData = readPayload(AlphaSize);

			byte[] cc = new byte[4];
			read(cc, 4);

			if(isFourCc(cc,'V','P','8',' ')){
				chunk.hasVP8chunk = true;
				int vp8Size = readUInt32();
				if((vp8Size&1)==1){
					vp8Size += 1;
					Log.e(TAG,"ANMF/ALPH/VP8 payload size "+vp8Size);
				}
				chunk.bitStream = readPayload(vp8Size);
			}
		} else if (isFourCc(cch, 'V', 'P', '8', ' ')) {
			chunk.isLossless = false;
			chunk.hasVP8chunk = true;
			int vp8Size = readUInt32();
			if((vp8Size&1)==1){
				vp8Size += 1;
				Log.e(TAG,"ANMF/VP8 payload size "+vp8Size);
			}
			chunk.bitStream  = readPayload(vp8Size);
		} else if (isFourCc(cch, 'V', 'P', '8', 'L')) {
			chunk.isLossless = true;
			chunk.hasVP8Lchunk = true;
			int vp8lSize = readUInt32();
			if((vp8lSize&1)==1){
				vp8lSize += 1;
				Log.v(TAG,"ANMF/VP8L | payload size "+vp8lSize);
			}

			chunk.bitStream  = readPayload(vp8lSize);
			bitStream = chunk.bitStream;
//			int align = padding(vp8lSize);
//			Logs.i(this,"padding "+align);

//			if((vp8lSize&1)==1){
//				readPayload(1);
//			}

		}else{
			throw new IOException("Not supported ANMF payload.");
		}


		return chunk;
	}


	byte[] concatenateByteArrays(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}


	private WebpChunk readVp8() throws IOException {
		int chunkSize = readUInt32();

		WebpChunk chunk = new WebpChunk(WebpChunkType.VP8);
		chunk.isLossless = false;
		chunk.payload = readPayload(chunkSize);

		debug(String.format("VP8: bytes = %d", chunkSize));
		return chunk;
	}

	private WebpChunk readVp8l() throws IOException {
		int chunkSize = readUInt32();

		WebpChunk chunk = new WebpChunk(WebpChunkType.VP8L);
		chunk.isLossless = true;
//		chunkSize is not telling the correct size of payload to read.
//		chunk.payload = readPayload(chunkSize);
		chunk.payload = readAllBytes();
		debug(String.format("VP8L: bytes = %d", chunkSize));
		return chunk;
	}



	private WebpChunk readAlph() throws IOException {
		int chunkSize = readUInt32(); // 4
		WebpChunk chunk = new WebpChunk(WebpChunkType.ALPH);
		Logs.i(this,"chunkSize of alph "+chunkSize);

		if((chunkSize&1) == 1 )
			chunkSize += 1 ;
		byte[] payload = readPayload(chunkSize);
		chunk.alphaData = payload;
		chunk.hasAlpha = true;


		return chunk;
	}

	private WebpChunk readIccp() throws IOException {
		int chunkSize = readUInt32();
		WebpChunk chunk = new WebpChunk(WebpChunkType.ICCP);

		readPayload(chunkSize);
		// no need to store the payload to this chunk as Animated Webp does not require ICCP

		return chunk;
	}

	private WebpChunk readExif() throws IOException {
		int chunkSize = readUInt32();
		WebpChunk chunk = new WebpChunk(WebpChunkType.EXIF);
		byte[] payload = readPayload(chunkSize);
		chunk.payload = payload;
		return chunk;
	}

	private WebpChunk readXmp() throws IOException {
		int chunkSize = readUInt32();
		WebpChunk chunk = new WebpChunk(WebpChunkType.XMP);
		byte[] payload = readPayload(chunkSize);
		chunk.payload = payload;
		return chunk;
	}

	//
	private byte[] readPayload(int bytes) throws IOException {
//		Logs.i(this,"readPayload() "+bytes);
		byte[] payload = new byte[bytes];
		if (read(payload, bytes) != bytes)
			throw new IOException("Can not read all bytes.");
		return payload;
	}

	private final int read(byte[] buffer, int bytes) throws IOException {
		int count = _inputStream.read(buffer, 0, bytes);
		_offset += count;
		return count;
	}

	private final int readUint(int bytes) throws IOException {
		byte[] b = new byte[] { 0, 0, 0, 0 };
		read(b, bytes);
		return ByteBuffer.wrap(b, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	private final int readUInt32() throws IOException {
		return readUint(4);
	}

	private final int readUInt24() throws IOException {
		return readUint(3);
	}

	private final int readUInt16() throws IOException {
		return readUint(2);
	}

	private boolean isFourCc(byte[] h, char a, char b, char c, char d) {
		return h[0] == a && h[1] == b && h[2] == c && h[3] == d;
	}

	private byte[] readAllBytes() throws IOException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024];
			int numRead;
			while ((numRead = _inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, numRead);
				_offset += numRead;
			}
			return outputStream.toByteArray();
		}
	}
	private void debug(String message) {
		if (_debug)
			System.out.println(message);
	}
}
