package com.palmwin.gifview;

import android.graphics.Bitmap;

public class GifFrame {
	
	public Bitmap image;
	public int[] colors;
	public int delay;
	public GifFrame nextFrame = null;

	public GifFrame(Bitmap im, int del) {
		this.image = im;
		this.delay = del;
	}
	public GifFrame(int[] colors, int del) {
		this.colors = colors;
		this.delay = del;
	}
}