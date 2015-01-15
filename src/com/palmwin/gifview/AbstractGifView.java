package com.palmwin.gifview;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public abstract class AbstractGifView extends View {

	private static final String TAG = "GIF";
	Bitmap bitmap;
	private GifItem gifItem = null;
	private Handler handler = new Handler();
	private String imgPath;
	private String gifName;
	private boolean running = true;

	public AbstractGifView(Context context) {
		super(context);
	}

	public void setGif(String imgPath, String gifName) {
		if (gifItem != null) {
			gifItem.removeView(this);
		}
		gifItem = GifItem.getGifItem(gifName, imgPath);
		this.resume();
		this.gifName = gifName;
		this.imgPath = imgPath;
	}

	public void setGif(InputStream inputStream, String gifName) {
		if (gifItem != null) {
			gifItem.removeView(this);
		}
		gifItem = GifItem.getGifItem(gifName, inputStream);
		this.resume();
	}

	public AbstractGifView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AbstractGifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (running) {
			if (imgPath != null) {
				this.setGif(imgPath, gifName);
			}
		}

	}

	public void pause() {
		running = false;
		gifItem.removeView(this);
	}

	public void resume() {
		running = true;
		gifItem.addView(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (running) {
			gifItem.removeView(this);
			gifItem = null;
		}
	}
	public void setDefaultBitmap(Bitmap bitmap){
		this.bitmap=bitmap;
		this.invalidate();
	}
	public void setDefaultBitmap(int res){
		setDefaultBitmap(BitmapFactory.decodeResource(this.getResources(), res));
	}
	// 设置绘制的图
	public void render(Bitmap image) {
		this.bitmap = image;
		handler.post(new Runnable() {
			@Override
			public void run() {
				AbstractGifView.this.invalidate();
			}
		});

	}

}