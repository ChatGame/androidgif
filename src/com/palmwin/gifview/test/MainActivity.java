package com.palmwin.gifview.test;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;

import com.palmwin.gif.R;
import com.palmwin.gifview.GifCircleView;
import com.palmwin.gifview.GifView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	int[] ids = new int[] { R.id.gifCircleView1, R.id.gifCircleView2,
			R.id.gifCircleView3, R.id.gifCircleView4, R.id.gifCircleView5,
			R.id.gifCircleView6 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		int index=1;
		for (int id : ids) {
			GifCircleView circleView = (GifCircleView) this
					.findViewById(id);
			circleView.setDefaultBitmap(R.drawable.th);
			try {
				circleView.setGif(this.getAssets().open("a"+index+".gif"), "test"+index);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			index++;
		}
	}

}
