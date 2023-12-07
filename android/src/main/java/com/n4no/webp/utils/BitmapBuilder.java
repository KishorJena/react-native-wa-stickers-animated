package com.n4no.webpencoder.webp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.n4no.webpencoder.webp.webpData.AnimationData;
import com.n4no.webpencoder.webp.webpData.FrameData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class BitmapBuilder {
    public Bitmap getBitmap(FrameData frameData, AnimationData animationData) throws IOException {
        byte[] bytes = encodeToStillWebP(frameData, animationData);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    public byte[] encodeToStillWebP(FrameData frameData, AnimationData animationData) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            baos.write(new byte[]{'R', 'I', 'F', 'F'});
            baos.write(intToByteArray(getFileSize(frameData))); // File Size
            baos.write(new byte[]{'W', 'E', 'B', 'P'}); // 4
            baos.write(new byte[]{'V', 'P', '8', 'X'}); // 4
            baos.write(intToByteArray(10)); // 4

            BitSet bs = new BitSet(32);
            bs.set(1, false); // A hasAnim
            bs.set(2, false); // X hasXmp
            bs.set(3, false); // E hasExif
            bs.set(4, animationData.hasAlpha);  // L hasAlpha
            bs.set(5, false); // I hasIccp

            baos.write(bitSetToBytes(bs,4)); // 4
            baos.write(intTo3ByteArray(frameData.width)); // 3
            baos.write(intTo3ByteArray(frameData.height)); // 3

            // alpha, bitStream
            if(frameData.hasVP8chunk && !frameData.hasVP8Lchunk) {
                if(frameData.hasALPHchunk) {
//                Logs.e(this,"writeByte[] hasALPHchunk "+frame.alphaData.length);
                    int chunkSize = frameData.alphaData.length;
                    baos.write(new byte[]{'A', 'L', 'P', 'H'}); // 4
                    baos.write(intToByteArray(chunkSize)); // 4
                    baos.write(frameData.alphaData);
                }
                baos.write(new byte[]{'V', 'P', '8', ' '}); // 4
//            Logs.e(this,"writeByte[] hasVP8chunk "+frame.bitStream.length);
                int chunkSize = frameData.bitStream.length;
                baos.write(intToByteArray(chunkSize)); // 4
                baos.write(frameData.bitStream);
                if(((chunkSize&1)==1)) {
                    baos.write(0);
                    Logs.i(this," padded VP8 ");
                }
            }else{
//            Logs.i(this,"write byte[] -> VP8L "+frame.bitStream.length+" | baso - "+baos.size());
                baos.write(new byte[]{'V', 'P', '8', 'L'}); // 4
                int chunkSize = frameData.bitStream.length;
                baos.write(intToByteArray(chunkSize)); // 4
                baos.write(frameData.bitStream);

                if(((chunkSize&1)==1)) {
                    baos.write(0);
                    Logs.i(this," vp8L padded "+baos.size());
                }

            }

            return baos.toByteArray();
        }



    }
    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) value,
                (byte) (value >>> 8),
                (byte) (value >>> 16),
                (byte) (value >>> 24)
        };
    }
    private int getFileSize(FrameData frameData) {
        int webp = 4;
        int vp8x = 10+8; // fourc(4) + chunkSize(4) + parameters(10)
        int alph = frameData.alphaData==null?0:8+ frameData.alphaData.length;
        int vp8 = frameData.bitStream.length+8;
        return 26 + alph + vp8;
    }

    private byte[] bitSetToBytes(BitSet bs, int bytes) {
        byte[] b = new byte[bytes];
        byte[] a = bs.toByteArray();
        for (int i = 0; i < a.length; i++)
            b[i] = a[i];
        return b;
    }

    public static byte[] intTo3ByteArray(int value) {
        return new byte[] {
                (byte)value,
                (byte)(value >>> 8),
                (byte)(value >>> 16)
        };
    }
}
