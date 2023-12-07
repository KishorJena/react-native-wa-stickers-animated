package com.n4no.webpencoder.webp.utils;

/**
 * @author Bartlomiej Tadych, b4rtaz
 */
public class WebpChunk {

	public final WebpChunkType type;

	public int x;
	public int y;
	public int width;
	public int height;
	public int loops;
	public int duration;
	public int background;

	public int canvasWidth;
	public int canvasHeight;

	public byte[] payload;
	public byte[] alphaData;
	public byte[] bitStream;
	public boolean isLossless;
	// chunks existance
	public boolean hasALPHchunk;
	public boolean hasVP8chunk;
	public boolean hasVP8Lchunk;

	public boolean hasAnim;
	public boolean hasXmp;
	public boolean hasExif;
	public boolean hasAlpha;
	public boolean hasIccp;

	public boolean useAlphaBlending;
	public boolean disposeToBackgroundColor;
	public byte[] flags;


	public WebpChunk(WebpChunkType t) {
		type = t;
	}
}
