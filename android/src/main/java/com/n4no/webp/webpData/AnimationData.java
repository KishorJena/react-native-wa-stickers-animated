package com.n4no.webpencoder.webp.webpData;

import java.util.ArrayList;
import java.util.List;

public class AnimationData {
    public boolean hasAlpha;
    public int canvasHeight;
    public int canvasWidth;
    public int background;
    public int loops;
    public List<FrameData> framesData;
    public byte[] flags;

    // constructor
    public AnimationData() {
        framesData = new ArrayList<>();
    }

    public void add(FrameData frameData) {
        this.framesData.add(frameData);
    }
}
