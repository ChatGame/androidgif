package com.palmwin.gifview;

import java.util.Hashtable;

import android.util.Log;


public class GifThread implements Runnable {

	private static GifThread instance = null;
	private boolean running = true;
	private Hashtable<String,GifItem> items = new Hashtable<String,GifItem>();
	public static GifThread getGifThread() {
		if (instance==null) {
			synchronized (GifThread.class) {
				if (instance==null) {
					instance = new GifThread();
				}
			}
		}
		return instance;
	}

	private GifThread() {
		new Thread(this).start();
	}
	public void addGifItem(GifItem item) {
		synchronized (items) {
			items.put(item.gifName, item);
		}
	}

	public void removeGifItem(GifItem item) {
		synchronized (items) {
			items.remove(item.gifName);
		}
	}

	@Override
	public void run() {
		GifItem[] tempItems = null;
		while (running) {
			tempItems = null;
			// 没有GIF在跑，暂停
			if (items.size() == 0) {
				sleep(100);
			}
			synchronized (items) {
				tempItems = items.values().toArray(new GifItem[0]);
			}
			// 尝试下一帧
			for (GifItem item : tempItems) {
				item.next();
			}
			sleep(10);
		}
	}
	private void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
