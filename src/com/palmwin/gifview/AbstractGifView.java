package com.palmwin.gifview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public abstract class AbstractGifView extends View {

    private static final String TAG = "GIF";
    Bitmap bitmap;
    private GifFile gifFile;
    private int currentFrame = 0;
    final Paint mBorderPaint = new Paint();
    boolean playing=false;
    int mBorderColor = Color.BLACK;
    int mBorderWidth = 0;

    public AbstractGifView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    private void setup() {
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    public void setBorderColor(int color) {
        this.mBorderColor = color;
        setup();
        invalidate();
    }

    public void setBorderSize(int size) {
        this.mBorderWidth = size;
        invalidate();
    }

    public void setGif(String imgPath, String gifName) {
        Log.d(TAG, "set gif "+gifName);
        try {
            setGif(new FileInputStream(new File(imgPath,gifName+".gif")), gifName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setGif(InputStream inputStream, String gifName) {
        gifFile = GifFile.getGifFile(gifName, inputStream, false);
        startPlay();
    }

    private void startPlay() {
        if(playing){
            return;
        }
        playing=true;
        final Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (AbstractGifView.this.hasWindowFocus()
                        && AbstractGifView.this.getVisibility() == View.VISIBLE
                        && AbstractGifView.this.isShown()) {
                    int delay = 100;
                    GifFrame frame = gifFile.getFrame(currentFrame);
                    GifFrame nextFrame = gifFile.getFrame(currentFrame + 1);
                    if (frame != null) {
                        drawFrame(frame);
                        currentFrame++;
                        delay = nextFrame.delay;
                    }
                    handler.postDelayed(this, delay);
                }else{
                    Log.d(TAG, "gif stop "+gifFile.getFileName());
                    playing=false;
                }
            }
        });
    }

    private void drawFrame(GifFrame frame) {
        this.bitmap = frame.image;
        this.invalidate();
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
        startPlay();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setDefaultBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.invalidate();
    }

    public void setDefaultBitmap(int res) {
        setDefaultBitmap(BitmapFactory.decodeResource(this.getResources(), res));
    }

}