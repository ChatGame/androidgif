package com.palmwin.gifview;

import android.graphics.Bitmap;

public class GifFrame {
	
	public Bitmap image;
	public int delay;
	public GifFrame nextFrame = null;

	public GifFrame(Bitmap im, int del) {
		this.image = im;
		this.delay = del;
	}
}