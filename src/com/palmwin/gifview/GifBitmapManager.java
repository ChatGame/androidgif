package com.palmwin.gifview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class GifBitmapManager {

	private String filePath = null;
	private static GifBitmapManager instance;
	private HashMap<String, SoftReference<Bitmap>> imageCache = null;

	private static byte[] a = new byte[0];

	private GifBitmapManager() {
		this.imageCache = new HashMap();
	}

	public static GifBitmapManager getInstance() {
		if (instance == null) {
			instance = new GifBitmapManager();
		}
		return instance;
	}

	public void initFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Bitmap getBitmap(String key, float w, float h)
			throws FileNotFoundException {
		Bitmap bitmap = null;
		if ((key == null) || ("".equals(key))) {
			return bitmap;
		}
		synchronized (a) {
			if (this.imageCache.containsKey(key)) {
				bitmap = (Bitmap) ((SoftReference) this.imageCache.get(key))
						.get();
			}
		}
		if (bitmap == null) {
			bitmap = getLocalBitmap(key);
			if ((w > 0.0F) && (h > 0.0F)) {
				bitmap = matrixBitmap(bitmap, w, h, 0);
			}
		}
		synchronized (a) {
			this.imageCache.put(key, new SoftReference(bitmap));
		}

		return bitmap;
	}

	public void addBitmap(String key, Bitmap bitmap, int w, int h) {
		synchronized (a) {
			if (this.imageCache.containsKey(key)) {
				return;
			}
			this.imageCache.put(key, new SoftReference(bitmap));
		}
		saveBitmap(bitmap, key);
	}

	private Bitmap getLocalBitmap(String fileName) throws FileNotFoundException {
		String filePath = this.filePath + fileName;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inPurgeable = true;
		opts.inInputShareable = true;

		FileInputStream inStream = null;
		inStream = new FileInputStream(new File(filePath));

		return BitmapFactory.decodeStream(inStream, null, opts);
	}

	public void saveBitmap(Bitmap bitmap, String fileName) {
		String filePath = this.filePath + fileName;
		File file = new File(filePath);
		if (file.exists()) {
			return;
		}
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			randomAccessFile.seek(randomAccessFile.length());
			randomAccessFile.write(getBitmapBytes(bitmap, 50,
					Bitmap.CompressFormat.PNG));
		} catch (IOException e) {
			e.printStackTrace();
			try {
				randomAccessFile.close();
				randomAccessFile = null;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				randomAccessFile.close();
				randomAccessFile = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public byte[] getBitmapBytes(Bitmap bitmap, int quality,
			Bitmap.CompressFormat format) {
		ByteArrayOutputStream baops;
		try {
			baops = new ByteArrayOutputStream();

			bitmap.compress(format, quality, baops);
		} finally {
			bitmap = null;
		}

		return baops.toByteArray();
	}

	public boolean isGifBitmapExist(String fileName) {
		String filePath = this.filePath + fileName;
		return new File(filePath).exists();
	}

	public Bitmap matrixBitmap(Bitmap bitmap, float toW, float toH,
			int scaleType) {
		
		if (null == bitmap) {
			return null;
		}
		
		int bitmapW = bitmap.getWidth();
		int bitmapH = bitmap.getHeight();

		if ((toW == bitmapW) && (toH == bitmapH)) {
			return bitmap;
		}

		Matrix matrix = new Matrix();

		float scaleW = toW / bitmapW;
		float scaleH = toH / bitmapH;
		if (scaleType == 0)
			matrix.postScale(scaleW, scaleH);
		else if (scaleType == 1)
			matrix.postScale(scaleW, scaleH);
		else {
			matrix.postScale(scaleW, scaleH);
		}

		Bitmap returenBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapW,
				bitmapH, matrix, true);
		matrix = null;

		return returenBitmap;
	}

	public int getImageCount(String fileName) {
		File file = new File(this.filePath);
		File[] files = file.listFiles();
		if (files == null) {
			return 0;
		}
		int count = 0;
		for (int i = files.length - 1; i >= 0; i--) {
			if ((files[i].getName().startsWith(fileName))
					&& (!files[i].getName().endsWith(".gif"))) {
				count++;
			}
		}

		return count;
	}

	public void delFrameBitmaps(String fileName) {
		File file = new File(this.filePath);
		File[] files = file.listFiles();

		File tempFile = null;
		for (int i = files.length - 1; i >= 0; i--)
			if ((files[i].getName().startsWith(fileName))
					&& (!files[i].getName().endsWith(".gif"))) {
				tempFile = new File(this.filePath + fileName + i);
				if (tempFile.exists())
					tempFile.delete();
			}
	}
}