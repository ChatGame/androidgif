package com.palmwin.gifview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;

public class GifCircleView extends AbstractGifView {
	private BitmapShader mBitmapShader;
	private final Paint mBitmapPaint = new Paint();
	Matrix matrix=new Matrix();
	public GifCircleView(Context context) {
		super(context);
	}

	public GifCircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GifCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setDefaultBitmap(Bitmap bitmap) {
		this.bitmap=bitmap;
		resetPaint();
		this.invalidate();
	}

	private void resetPaint(){
		if(bitmap==null){
			return;
		}
		mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP);
		float scale=Math.max(this.getWidth()*1.0f/bitmap.getWidth(), this.getHeight()*1.0f/bitmap.getHeight());
		matrix.setScale(scale, scale);
		mBitmapShader.setLocalMatrix(matrix);
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);

	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		resetPaint();
	}

	protected void onDraw(Canvas canvas) {
		if(bitmap==null){
			return;
		}
		resetPaint();
		int r=Math.min(canvas.getWidth()/2, canvas.getHeight()/2);
		canvas.drawCircle(r, r, r, mBitmapPaint);
	}

}