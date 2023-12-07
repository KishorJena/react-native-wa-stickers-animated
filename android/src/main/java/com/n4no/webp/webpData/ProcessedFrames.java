package com.n4no.webpencoder.webp.webpData;

import android.graphics.Bitmap;

public class ProcessedFrames {
    public Bitmap bitmap;
    public int delay;
    public boolean dispose; // Not required
    public boolean blending; // Not required
    public int bg; // Not required
    public int x; // Not required
    public int y; // Not required
    // as bitmaps are created in such a way that it does ont
    // need to deal with dispose and blend;
    // bitmaps are combined togather to to show image according to dispose and blend
    // also bitmaps are already aligned according to x y offset and canvas scale.
}
