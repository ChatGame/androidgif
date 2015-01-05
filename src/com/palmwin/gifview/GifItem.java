package com.palmwin.gifview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;

public class GifItem {
	private static Hashtable<String, GifItem> gifItemHashtable = new Hashtable<String, GifItem>();
	public String gifName;
	public GifDecoder gifDecoder;
	private Hashtable<Integer, GifView> listViews = new Hashtable<Integer, GifView>();
	private GifView[] listViewsBuffer = null;

	public static GifItem getGifItem(String gifName, String imgPath) {
		//TODO 同步陷阱
		GifItem item = gifItemHashtable.get(gifName);;
		if (item != null) {
			return item;
		} else {
			synchronized (gifItemHashtable) {
				item=gifItemHashtable.get(gifName);;
				if(item==null){
					item = new GifItem(gifName, imgPath);
					gifItemHashtable.put(gifName, item);
					return item;
				}else
				{
					return item;
				}
			}
			
		}

	}

	public GifItem(String gifName, String imgPath) {
		this.gifName = gifName;
		try {
			gifDecoder = new GifDecoder(new FileInputStream(new File(imgPath,
					gifName + ".gif")), gifName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private long lastFramePlay = 0;

	public void next() {
		if (gifDecoder.getCurrentFrame() == null) {
			return;
		}
		if(this.listViews.size()==0){
			return;
		}
		boolean changed = false;
		synchronized (listViews) {
			listViewsBuffer = listViews.values().toArray(new GifView[0]);
		}
		if (lastFramePlay == 0) {
			changed=true;
			lastFramePlay = System.currentTimeMillis();
		} else {
			if (System.currentTimeMillis() - lastFramePlay > gifDecoder
					.getCurrentFrame().delay) {
				gifDecoder.next();
				lastFramePlay = System.currentTimeMillis();
				changed = true;
			}

		}
		if (changed) {
			for (GifView view : listViewsBuffer) {
				view.render(gifDecoder.getCurrentFrame().image);
			}
		}

	}

	public void addView(GifView view) {
		listViews.put(view.hashCode(), view);
		//有View了，加入Thread开始跑
		if(listViews.size()==1){
			GifThread.getGifThread().addGifItem(this);
		}
	}

	public void removeView(GifView view) {
		listViews.remove(view.hashCode());
		//没有View了，移除
		if(listViews.size()==0){
			GifThread.getGifThread().removeGifItem(this);
		}
	}
}