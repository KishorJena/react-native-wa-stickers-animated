package com.n4no.webpencoder.webp.webpProcess;

import android.graphics.Color;
import android.util.Log;

import com.n4no.webpencoder.webp.utils.Logs;
import com.n4no.webpencoder.webp.io.WebpContainerReader;
import com.n4no.webpencoder.webp.io.WebpContainerWriter;
import com.n4no.webpencoder.webp.utils.WebpChunk;
import com.n4no.webpencoder.webp.utils.WebpChunkType;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Bartlomiej Tadych, b4rtaz
 */
public class WebpMuxer {
    private final String TAG = "WebpMuxer";
    private WebpContainerWriter _writer;
    private boolean _isFirstFrame = true;
    private int _loops = -1;
    private int _duration = -1;
    private int _width;
    private int _height;

    private int _cWidth;
    private int _cHeight;
    private byte[] _alphaData;
    private boolean _hasAlpha;
    private boolean _blending = true;
    private boolean _dispose = true;
    private int _bg = Color.TRANSPARENT;
    private boolean _isAnim;
    public void setWidth(int width) {
        _width = width;
    }
    public void setHeight(int height) {
        _height = height;
    }
    public void setLoops(int loops) {
        _loops = loops;
    }
    public void setDuration(int duration) {
        _duration = duration;
    }
    public void setBg(int bg) {
        _bg = bg;
    }

    public WebpMuxer(WebpContainerWriter writer) {
        _writer = writer;
//        Logs.disable(this);
        Logs.enable(this);
    }
    public void writeFirstFrameFromWebm(InputStream inputStream) throws IOException {

        WebpContainerReader reader = new WebpContainerReader(inputStream, false);
        reader.readHeader();
        WebpChunk chunk = readFirstChunkWithPayload(reader);
        reader.close();
        writeFrame(chunk, chunk.payload, chunk.isLossless);
    }

    public void writeFrame(WebpChunk chunk, byte[] payload, boolean isLossless) throws IOException {
        if (_isFirstFrame) {
            _isFirstFrame = false;
            writeHeader(chunk);
            Logs.d(this,"is First Frame");
        }

//        Log.i(TAG,"check if has anim?");
        if (hasAnim()) {
//            Log.i(TAG, "hasANIM then writeANMF");
            writeAnmf(chunk, payload, isLossless);
        } else {
//            Log.w(TAG, "!hasANIM then writeVP8");
            writeVp8(payload, isLossless);
        }
    }

    public void close() throws IOException {
        _writer.close();
    }

    //

    private boolean hasAnim() {
//        Logs.i(this, "_loops "+_loops+", _duration "+_duration+" _width "+_width+" _height ");
        return _loops >= 0 && _duration > 0;
    }

    private WebpChunk readFirstChunkWithPayload(WebpContainerReader reader) throws IOException {
        WebpChunk chunk;
        while ((chunk = reader.read()) != null) {

            if (chunk.type == WebpChunkType.ALPH)
                _alphaData = chunk.alphaData;

            if (chunk.type == WebpChunkType.VP8X){
                _hasAlpha = chunk.hasAlpha;
                _cWidth   = chunk.canvasWidth;
                _cHeight  = chunk.canvasHeight;
            }

            if (chunk.type == WebpChunkType.ANIM){
                Log.i(TAG, "hasANIM ");
            }

            if (chunk.type == WebpChunkType.ANMF) {
                _blending = chunk.useAlphaBlending;
                _dispose = chunk.disposeToBackgroundColor;
            }

            if (chunk.payload != null)
                return chunk;
        }
        throw new IOException("Can not find chunk with payload.");
    }

    private void writeHeader(WebpChunk chunk) throws IOException {
        _writer.writeHeader();

        WebpChunk vp8x = new WebpChunk(WebpChunkType.VP8X);
        vp8x.hasAnim = true; // TODO: make it dyanamic
        vp8x.hasAlpha = false; // TODO: make it dyanamic
        vp8x.hasXmp = false;
        vp8x.hasExif = false;
        vp8x.hasIccp = false;
        vp8x.width = _width-1;
        vp8x.height = _height-1;
        _writer.write(vp8x);

        if (vp8x.hasAnim) {
            Logs.i(this, "this.hasAnim then writeANIM "+chunk.type);
            WebpChunk anim = new WebpChunk(WebpChunkType.ANIM);
            anim.background = _bg; // TODO: make it dyanamic
            anim.loops = _loops;
            _writer.write(anim);
        }
    }

    private void writeAnmf(WebpChunk chunk, byte[] payload, boolean isLossless) throws IOException {
        Logs.i(this, "writeAnmf "+chunk.type);
        WebpChunk anmf = new WebpChunk(WebpChunkType.ANMF);
        anmf.x = 0; // it was 0
        anmf.y = 0; // it was 0
        anmf.width = _width-1;
        anmf.height = _height-1;
        anmf.duration = _duration;

        anmf.isLossless = isLossless;
        anmf.payload = payload;

//        Logs.i(this,"ANMF🚧 _blending "+String.valueOf(_blending)+", _dispose "+String.valueOf(_dispose));
        anmf.useAlphaBlending = _blending;
        anmf.disposeToBackgroundColor = _dispose;

        anmf.alphaData = _alphaData;
//        Logs.i(this,"_alphaData "+_alphaData.length);
//        Logs.i(this,"_blending "+_blending);
//        Logs.i(this,"_dispose "+_dispose);
        _writer.write(anmf);
    }

    private void writeVp8(byte[] payload, boolean isLossless) throws IOException {
        WebpChunk vp8 = new WebpChunk(isLossless
                ? WebpChunkType.VP8L
                : WebpChunkType.VP8);
        vp8.isLossless = isLossless;
        vp8.payload = payload;

        _writer.write(vp8);
    }
}
