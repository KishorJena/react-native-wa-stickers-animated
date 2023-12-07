package com.n4no.webpencoder.webp.webpData;

public class FrameData {

    // ANMF
    public int height;
    public int width;
    public int x;
    public int y;
    public int duration;
    public boolean useAlphaBlending;
    public boolean disposeToBackgroundColor;

    // VP8(L)
    public byte[] bitStream;

    // ALPH
    public byte[] alphaData;

    // flags
    public boolean isLoseLess;
    public boolean hasAlpha;
    public boolean hasALPHchunk;
    public boolean hasVP8chunk;
    public boolean hasVP8Lchunk;

}
