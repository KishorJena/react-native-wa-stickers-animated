package com.n4no.webpencoder.webp.codec;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.n4no.webpencoder.webp.utils.BitmapBuilder;
import com.n4no.webpencoder.webp.webpData.AnimationData;
import com.n4no.webpencoder.webp.webpData.FrameData;
import com.n4no.webpencoder.webp.webpData.ProcessedFrames;
import com.n4no.webpencoder.webp.webpProcess.Demuxer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class WebpAnimatedDecoder {
    // bytearray outputstream for temporarly creating webp static image
    public final String TAG = "WebpAnimatedDecoder";
    private FileInputStream _inputStream ;
    private final Demuxer _demux;
    private static final PorterDuffXfermode MODE_SRC_OVER = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
    private static final PorterDuffXfermode MODE_SRC = new PorterDuffXfermode(PorterDuff.Mode.SRC);

    private AnimationData _animationData;
    public WebpAnimatedDecoder(File file) throws FileNotFoundException {
        if (file == null) throw new NullPointerException("file");
        _inputStream = new FileInputStream(file);
        _demux = new Demuxer(_inputStream);
    }

    public AnimationData getAnimationData(){
        return getAnimationData();
    }
    private void setAnimationData(AnimationData animationData) {
        _animationData = animationData;
    }

    public List<ProcessedFrames> decode() throws IOException {
        BitmapBuilder bitmapBuilder = new BitmapBuilder();
        AnimationData frames = _demux.getFrames();
        close(); // FIXME

        List<ProcessedFrames> resizedFrameList = new ArrayList<>();
        Bitmap canvasBitmap = Bitmap.createBitmap(frames.canvasWidth, frames.canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);

        for (int i = 0; i < frames.framesData.size(); i++) {
            FrameData frameData = frames.framesData.get(i);
            Bitmap currentBitmap = bitmapBuilder.getBitmap(frameData, frames);
            boolean disposePrevious = i > 0 && frames.framesData.get(i - 1).disposeToBackgroundColor;
            boolean useAlphaBlending = !frames.framesData.get(i).useAlphaBlending;

            // Disposal
            if (disposePrevious) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }

            // Alpha blend
            Paint blendPaint = new Paint();
            blendPaint.setAntiAlias(true);
            blendPaint.setStyle(Paint.Style.FILL);
            if (useAlphaBlending) {
                blendPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            } else {
                blendPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            }

            // Draw current frame

            canvas.drawBitmap(currentBitmap, frameData.x * 2, frameData.y * 2, blendPaint);

            ProcessedFrames resizedFrame = new ProcessedFrames();
            resizedFrame.bitmap = Bitmap.createBitmap(canvasBitmap);
            resizedFrame.delay = frameData.duration;
            resizedFrameList.add(resizedFrame);
            currentBitmap.recycle();
        }

        setAnimationData(frames);
        return resizedFrameList;
//        BitmapBuilder bitmapBuilder = new BitmapBuilder();
//
//        AnimationData frames = _demux.getFrames();
//
//        close(); // FiXME
//
//        List<ProcessedFrames> resizedFrameList = new ArrayList<>();
//
////        int maxWidth = FindMax.getWidth(frames.framesData);
////        int maxHeight = FindMax.getHeight(frames.framesData);
//
//        Bitmap previousBitmap = null;
//
//
//        for (int i = 0; i < frames.framesData.size(); i++) {
//
//            FrameData frameData = frames.framesData.get(i);
//
//            Bitmap currentBitmap = bitmapBuilder.getBitmap(frameData, frames);
//
//            boolean disposePrevious = i > 0 && frames.framesData.get(i - 1).disposeToBackgroundColor;
//            boolean useAlphaBlending = !frames.framesData.get(i).useAlphaBlending;
//
////            Bitmap canvasBitmap = Bitmap.createBitmap(currentBitmap.getWidth(), currentBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//            Bitmap canvasBitmap = Bitmap.createBitmap(frames.canvasWidth, frames.canvasHeight, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(canvasBitmap);
//
//            // disposal
//            if (disposePrevious) {
////                Log.d(TAG,"animationData.background "+frames.background);
//                drawDisposedToBackgroundColor(canvas, Color.TRANSPARENT, canvasBitmap.getWidth(), canvasBitmap.getHeight());
//            } else if (previousBitmap != null) {
//                drawPreviousBitmap(canvas, previousBitmap);
//            }
//
//            // alpha blend
//            Paint blendPaint = new Paint();
//            blendPaint.setAntiAlias(true);
////            blendPaint.setAlpha(60);
//            if(useAlphaBlending){
//                blendPaint.setXfermode(MODE_SRC_OVER);
//            }else{
//                blendPaint.setXfermode(MODE_SRC);
//            }
//
//            // draw current frame
//            canvas.drawBitmap(currentBitmap, frameData.x*2, frameData.y*2, blendPaint);
//
//            ProcessedFrames resizedFrame = new ProcessedFrames();
//            resizedFrame.bitmap = canvasBitmap;
//            resizedFrame.delay = frameData.duration;
////            Log.d(TAG, "frameData.duration "+frameData.duration);
//            resizedFrameList.add(resizedFrame);
//
//            // store the current frame for next iteration
//            previousBitmap = canvasBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        }
//
//        setAnimationData(frames);
//        return resizedFrameList;
    }

    private void drawDisposedToBackgroundColor(Canvas canvas, int backgroundColor, int width, int height) {
        Paint bgPaint = new Paint();
        bgPaint.setColor(backgroundColor);
        bgPaint.setStyle(Paint.Style.FILL);
//        bgPaint.setAntiAlias(true);
        canvas.drawRect(0, 0, width, height, bgPaint);
    }
    private void drawPreviousBitmap(Canvas canvas, Bitmap previousBitmap) {
        Paint disposePaint = new Paint();
//        disposePaint.setAntiAlias(true);
        canvas.drawBitmap(previousBitmap, 0, 0, disposePaint);
    }




    public void close() throws IOException {
        try {
            _demux.close();
        } finally {
            _inputStream.close();
        }
    }

}
