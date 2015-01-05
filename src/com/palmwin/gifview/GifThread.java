package com.palmwin.gifview;

import java.util.ArrayList;
import java.util.List;


public class GifThread implements Runnable {

	private static GifThread instance = null;
	private boolean running = true;
	private static Boolean newInstance=false;
	private List<GifItem> items = new ArrayList<GifItem>();
	public static GifThread getGifThread() {
		if (!newInstance) {
			synchronized (newInstance) {
				if (!newInstance) {
					instance = new GifThread();
				}
			}
		}
		return instance;
	}

	private GifThread() {
		newInstance=true;
		new Thread(this).start();
	}
	public void addGifItem(GifItem item) {
		synchronized (items) {
			items.add(item);
		}
		// 新元素进来，通知线程启动
		if (items.size() == 1) {
			synchronized (this) {
				this.notify();
			}
		}
	}

	public void removeGifItem(GifItem item) {
		synchronized (items) {
			items.remove(item);
		}
	}

	@Override
	public void run() {
		GifItem[] tempItems = null;
		while (running) {
			// 没有GIF在跑，暂停
			if (items.size() == 0) {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			synchronized (items) {
				tempItems = items.toArray(new GifItem[0]);
			}
			// 尝试下一帧
			for (GifItem item : tempItems) {
				item.next();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
