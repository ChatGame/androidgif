package com.palmwin.gifview;

import java.io.InputStream;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

@SuppressLint("NewApi")
public class GifDrawable extends Drawable{
    Matrix matrix = new Matrix();
    Bitmap bitmap;
    Paint paint = new Paint();
    private GifFile gifFile = null;
    private int width;
    public GifDrawable(InputStream inputStream, String gifName,int width) {
        gifFile=new GifFile(gifName,inputStream,true);
        this.width=width;
    }
    @Override
    public void draw(Canvas canvas) {
        
        if (bitmap == null) {
            return;
        }
        float scale = Math.max(canvas.getWidth() * 1.0f / bitmap.getWidth(),
                canvas.getHeight() * 1.0f / bitmap.getHeight());
        
        matrix.setScale(scale, scale);
        int saveCount = canvas.getSaveCount();
        canvas.save();
        if (this.bitmap != null) {
            canvas.drawBitmap(bitmap, new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()), this.getBounds(),paint);
        }
        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

   
    public GifFrame getNextFrame(int frame){
        frame++;
        return getFrame(frame);
    }
    public GifFrame getFrame(int frame){
        return gifFile.getFrame(frame); 
    }
    public int getFrameCount(){
        return gifFile.getFrameCount();
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
}
