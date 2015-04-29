package com.palmwin.gifview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.style.DynamicDrawableSpan;
import android.util.Log;
import android.view.View;

@SuppressLint("NewApi")
public class GifSpan extends DynamicDrawableSpan {

    private GifDrawable drawable;
    private int currentFrame;
    Context context;

    public GifSpan(final Context context, final GifDrawable drawable,
            final View view) {
        this.drawable = drawable;
        this.context = context;
        currentFrame = 0;
        final Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (view!=null && view.hasWindowFocus()
                        && view.getVisibility() == View.VISIBLE
                        && view.isShown()) {
                    int delay = 100;
                    GifFrame frame = drawable.getFrame(currentFrame);
                    GifFrame nextFrame = drawable.getFrame(currentFrame + 1);
                    if (frame != null) {
                        currentFrame++;
                        delay = nextFrame.delay;
                        if (view != null) {
                            view.invalidate();
                        }
                    }
                    handler.postDelayed(this, delay);
                }
            }
        });

    }

    @Override
    public Drawable getDrawable() {
        GifFrame frame = drawable.getFrame(currentFrame);
        if (frame != null) {
            BitmapDrawable rlt = new BitmapDrawable(context.getResources(),
                    drawable.getFrame(currentFrame).image);
            rlt.setBounds(new Rect(0, 0, drawable.getWidth(), drawable
                    .getWidth()));
            return rlt;
        } else {
            return null;
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
            FontMetricsInt fm) {
        // TODO Auto-generated method stub
        return drawable.getWidth();
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
            float x, int top, int y, int bottom, Paint paint) {
        Drawable d = this.getDrawable();
        d.setBounds((int) x, top, (int) x + drawable.getWidth(),
                top + drawable.getWidth());
        d.draw(canvas);
    }

}
