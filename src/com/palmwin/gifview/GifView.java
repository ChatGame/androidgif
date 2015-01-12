package com.palmwin.gifview;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GifView extends View {

	private static final String TAG="GIF";
	private Bitmap bitmap;
	private int showWidth = -1;
	private int showHeight = -1;
	private Rect rect = null;
	private GifItem gifItem = null;
	private Handler handler = new Handler();
	public GifView(Context context) {
		super(context);
	}

	public void setGif(String imgPath, String gifName, int width, int height) {
		if (gifItem != null) {
			gifItem.removeView(this);
		}
		this.showHeight = height;
		this.showWidth = width;
		gifItem = GifItem.getGifItem(gifName, imgPath);
		gifItem.addView(this);
		rect = new Rect(0, 0, width, height);
	}
	public void setGif(InputStream inputStream, String gifName, int width, int height) {
		if (gifItem != null) {
			gifItem.removeView(this);
		}
		this.showHeight = height;
		this.showWidth = width;
		gifItem = GifItem.getGifItem(gifName, inputStream);
		gifItem.addView(this);
		rect = new Rect(0, 0, width, height);
	}
	public GifView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int saveCount = canvas.getSaveCount();
		canvas.save();
		canvas.translate(getPaddingLeft(), getPaddingTop());
		if (this.bitmap != null) {
			canvas.drawBitmap(this.bitmap, null, this.rect, null);
		}
		canvas.restoreToCount(saveCount);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int pleft = getPaddingLeft();
		int pright = getPaddingRight();
		int ptop = getPaddingTop();
		int pbottom = getPaddingBottom();

		int w = this.showWidth;
		int h = this.showHeight;

		w += pleft + pright;
		h += ptop + pbottom;

		w = Math.max(w, getSuggestedMinimumWidth());
		h = Math.max(h, getSuggestedMinimumHeight());

		int widthSize = resolveSize(w, widthMeasureSpec);
		int heightSize = resolveSize(h, heightMeasureSpec);
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		gifItem.addView(this);

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		gifItem.removeView(this);
	}

	// 设置绘制的图
	public void render(Bitmap image) {
		this.bitmap = image;
		handler.post(new Runnable() {
			@Override
			public void run() {
				GifView.this.invalidate();
			}
		});

	}

}