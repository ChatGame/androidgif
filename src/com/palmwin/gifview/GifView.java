package com.palmwin.gifview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

public class GifView extends AbstractGifView {
	Matrix matrix=new Matrix();
	Paint paint=new Paint();
	public GifView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GifView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	protected  void onDraw(Canvas canvas) {
		if(bitmap==null){
			return;
		}
		float scale=Math.max(this.getWidth()*1.0f/bitmap.getWidth(), this.getHeight()*1.0f/bitmap.getHeight()); 
		matrix.setScale(scale, scale);
		int saveCount = canvas.getSaveCount();
		canvas.save();
		canvas.translate(getPaddingLeft(), getPaddingTop());
		if (this.bitmap != null) {
			canvas.drawBitmap(bitmap, matrix, paint);
		}
		canvas.restoreToCount(saveCount);
	}


}