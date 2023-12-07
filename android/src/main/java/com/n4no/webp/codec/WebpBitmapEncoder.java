package com.n4no.webpencoder.webp.codec;

import static android.graphics.Bitmap.CompressFormat.WEBP;
import static android.graphics.Bitmap.CompressFormat.WEBP_LOSSLESS;
import static android.graphics.Bitmap.CompressFormat.WEBP_LOSSY;

import android.graphics.Bitmap;
import android.os.Build;

import com.n4no.webpencoder.webp.utils.Logs;
import com.n4no.webpencoder.webp.stream.FileSeekableOutputStream;
import com.n4no.webpencoder.webp.io.WebpContainerWriter;
import com.n4no.webpencoder.webp.webpProcess.WebpMuxer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class WebpBitmapEncoder {
    private final FileSeekableOutputStream _outputStream;
    private final WebpContainerWriter _writer;
    private final WebpMuxer _muxer;
    private boolean _isFirstFrame = true;
    public WebpBitmapEncoder(File file) throws FileNotFoundException {
        if (file == null) throw new NullPointerException("file");

        _outputStream = new FileSeekableOutputStream(file);
        _writer = new WebpContainerWriter(_outputStream);
        _muxer = new WebpMuxer(_writer);
    }

    public void setLoops(int loops) {
        _muxer.setLoops(loops);
    }

    public void setDuration(int duration) {
        _muxer.setDuration(duration);
    }
    public void setBg(int bg) {
        _muxer.setBg(bg);
    }

    public void writeFrame(Bitmap frame, int compress) throws IOException {
        if (frame == null) throw new NullPointerException("null frame @ WebpBitmapEncoder.writeFrame");

        if (_isFirstFrame) {
            _isFirstFrame = false;
            _muxer.setWidth(frame.getWidth());
            _muxer.setHeight(frame.getHeight());
            Logs.i(this,"W ->"+frame.getWidth()+", H->"+frame.getHeight());
        }

        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        Bitmap.CompressFormat format;

        if(android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            if(compress==100){
                format = WEBP_LOSSLESS;
            }else{
                format = WEBP_LOSSY;
            }
        }else{
            format = WEBP;
        }


        frame.compress(format, compress, outBuffer);
        ByteArrayInputStream inBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
        _muxer.writeFirstFrameFromWebm(inBuffer);
        outBuffer.close();
        inBuffer.close();
    }

    public void close() throws IOException {
        _muxer.close();
//        _writer.close();
        _outputStream.close();
    }


}
