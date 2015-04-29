package com.palmwin.gifview.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.TextView;

import com.palmwin.gif.R;
import com.palmwin.gifview.AbstractGifView;
import com.palmwin.gifview.GifCircleView;
import com.palmwin.gifview.GifDrawable;
import com.palmwin.gifview.GifSpan;

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
			AbstractGifView circleView = (AbstractGifView) this
					.findViewById(id);
			circleView.setDefaultBitmap(R.drawable.th);
			try {
				circleView.setBorderColor(Color.GREEN);
				circleView.setBorderSize(6);
				circleView.setGif(this.getAssets().open("a"+index+".gif"), "test"+index);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			index++;
		}
		TextView tv=(TextView)this.findViewById(R.id.txtFace);
		try {
		        String content="测试动画表情[smile]hahah";
            GifSpan span=new GifSpan(this, new GifDrawable(this.getAssets().open("a1.gif"), "smile",60), tv);
            Pattern pattern = Pattern.compile("\\[[a-zA-Z_0-9_.-]+\\]");
            Matcher m=pattern.matcher(content);
            SpannableString spannableString = new SpannableString(content);
            while(m.find()){
                spannableString.setSpan(span, m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            tv.setText(spannableString);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}

}
