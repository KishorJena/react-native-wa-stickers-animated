package com.n4no.webpencoder.webp.webpProcess;


import android.util.Log;

import com.n4no.webpencoder.webp.utils.Logs;
import com.n4no.webpencoder.webp.io.WebpContainerReader;
import com.n4no.webpencoder.webp.webpData.FrameData;
import com.n4no.webpencoder.webp.utils.WebpChunk;
import com.n4no.webpencoder.webp.webpData.AnimationData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Demuxer {
    private final String TAG = "Demuxer";
    private final WebpContainerReader _containerReader;

    public Demuxer(InputStream inputStream) throws FileNotFoundException {
        _containerReader = new WebpContainerReader(inputStream, false);
        Logs.disable(this);
    }

    public AnimationData getFrames() throws IOException {
        Log.d(TAG, "getFrames...");
        _containerReader.readHeader();

        WebpChunk chunk;
        AnimationData animationData = new AnimationData();


        while ((chunk = _containerReader.read()) != null) {
//            Logs.v(this,i+" types "+chunk.type);
            switch (chunk.type) {
                case VP8X:
                    Log.d(TAG, "case VP8X: "+chunk.type);
                    // hasAlpha, canvas
                    animationData.hasAlpha = chunk.hasAlpha;
                    animationData.canvasWidth = chunk.canvasWidth;
                    animationData.canvasHeight =  chunk.canvasHeight;
                    animationData.flags = chunk.flags;
                    break;
                case ANIM:
                    Log.d(TAG, "case ANIM: "+chunk.type);
                    // bg, loop
                    animationData.background = chunk.background;
                    animationData.loops = chunk.loops;
                    break;
                case ANMF:
                    Log.d(TAG, "case ANMF: "+chunk.type);
                    // x,y,h,w, delay, alphaData, bitStream, isLossy
                    FrameData frameData = new FrameData();

                    //
                    frameData.x = chunk.x;
                    frameData.y = chunk.y;
                    frameData.height = chunk.height;
                    frameData.width = chunk.width;
                    frameData.duration = chunk.duration;
//                    Logs.i(this, "chunk.duration "+chunk.duration);
                    //
                    frameData.useAlphaBlending = chunk.useAlphaBlending;
                    frameData.disposeToBackgroundColor = chunk.disposeToBackgroundColor;

                    // frame data
                    frameData.hasVP8Lchunk = chunk.hasVP8Lchunk;
                    frameData.hasALPHchunk = chunk.hasALPHchunk;
                    frameData.hasVP8chunk = chunk.hasVP8chunk;

                    if(frameData.hasALPHchunk) {
//                        Logs.i(this,"hasALPH ");
                        frameData.alphaData = chunk.alphaData;
                    }

                    if(frameData.hasVP8Lchunk || frameData.hasVP8chunk ) {
                        frameData.bitStream = chunk.bitStream;
                    }else{
                        Log.e(TAG,"Unknown - Demuxer | While  |ANMF ...");
                    }
                    animationData.add(frameData);
                    break;
                case VP8:
                case VP8L:
                case ALPH:
                case XMP:
                case ICCP:
                case EXIF:
                    Log.e(TAG, "Ignored Chunk "+chunk.type);
                    break;
                default:
                    Logs.i(this," default -> chunk "+chunk.type);

            }

        }
//        _containerReader.close();
        return animationData;
    }
    public void close() throws IOException{
        _containerReader.close();
    }


}
