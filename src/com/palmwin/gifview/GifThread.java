package com.palmwin.gifview;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class GifThread implements Runnable {

	private static GifThread instance = null;
	private boolean running = true;
	private Hashtable<String, GifItem> items = new Hashtable<String, GifItem>();

	public static GifThread getGifThread() {
		if (instance == null) {
			synchronized (GifThread.class) {
				if (instance == null) {
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

	List<GifItem> tempItems = new ArrayList<GifItem>();

	@Override
	public void run() {
		while (running) {
			// 没有GIF在跑，暂停
			if (items.size() == 0) {
				sleep(100);
			}
			tempItems.addAll(items.values());
			// 尝试下一帧
			long start = System.currentTimeMillis();
			for (GifItem item : tempItems) {
				item.next();
			}
			long sleep = 20 - System.currentTimeMillis() - start;
			if (sleep < 0) {
				sleep = 10;
			}
			sleep(sleep);
			tempItems.clear();
		}
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
