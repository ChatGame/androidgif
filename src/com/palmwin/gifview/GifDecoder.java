package com.palmwin.gifview;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import android.graphics.Bitmap;
import android.util.Log;

public class GifDecoder extends Thread {

	public static final int STATUS_PARSING = 0;
	public static final int STATUS_FORMAT_ERROR = 1;
	public static final int STATUS_OPEN_ERROR = 2;
	public static final int STATUS_FINISH = -1;

	private InputStream in;
	private int status;
	public int width;
	public int height;
	private boolean gctFlag;
	private int gctSize;
	private int loopCount = 1;
	private int[] gct;
	private int[] lct;
	private int[] act;
	private int bgIndex;
	private int bgColor;
	private int lastBgColor;
	private int pixelAspect;
	private boolean lctFlag;
	private boolean interlace;
	private int lctSize;
	private int ix;
	private int iy;
	private int iw;
	private int ih;
	private int lrx;
	private int lry;
	private int lrw;
	private int lrh;
	private Bitmap image;
	private int[] colors;
	private int[] lastColors;
	private GifFrame currentFrame = null;

	private boolean isShow = false;

	private byte[] block = new byte[256];
	private int blockSize = 0;

	private int dispose = 0;

	private int lastDispose = 0;
	private boolean transparency = false;
	private int delay = 0;

	public Hashtable<Integer, Integer> delaysHashtable = new Hashtable();
	private int transIndex;
	private static final int MaxStackSize = 4096;
	private short[] prefix;
	private byte[] suffix;
	private byte[] pixelStack;
	private byte[] pixels;
	private GifFrame gifFrame;
	private int frameCount;

	private String gifName = null;

	private byte[] gifData = null;

	public boolean decodingFlag = false;
	private static final String TAG = "GIF";
	
	public GifDecoder(String gifName) {
		this.gifName = gifName;
	}

	public GifDecoder(InputStream is, String gifName) {
		this.gifName = gifName;
		this.in = is;
		this.decodingFlag = true;
		new Thread(this).start();
	}

	public void run() {
		Log.d(TAG, "Gif decode start");
		if (this.in != null)
			readStream();
		else if (this.gifData != null) {
			readByte();
		}
		this.decodingFlag = false;
		buffer=null;
		lastColors=null;
		colors=null;
		act=null;
		gct=null;
		line=null;
		Log.d(TAG, "Gif decode2 over " + gifName + "," + this.frameCount
				+ " frames");
	}

	public void free() {
		GifFrame fg = this.gifFrame;
		while (fg != null) {
			fg.image = null;
			fg = null;
			this.gifFrame = this.gifFrame.nextFrame;
			fg = this.gifFrame;
		}
		if (this.in != null) {
			try {
				this.in.close();
			} catch (Exception localException) {
			}
			this.in = null;
		}
		this.gifData = null;
		currentFrame=null;
	}

	public int getStatus() {
		return this.status;
	}

	public boolean parseOk() {
		return this.status == -1;
	}

	public int getDelay(int n) {
		this.delay = -1;
		if ((n >= 0) && (n < this.frameCount)) {
			GifFrame f = getFrame(n);
			if (f != null)
				this.delay = f.delay;
		}
		return this.delay;
	}

	public int[] getDelays() {
		GifFrame f = this.gifFrame;
		int[] d = new int[this.frameCount];
		int i = 0;
		while ((f != null) && (i < this.frameCount)) {
			d[i] = f.delay;
			f = f.nextFrame;
			i++;
		}
		return d;
	}

	public int getFrameCount() {
		return this.frameCount;
	}

	public void setFrameCount(int frameCount) {
		this.frameCount = frameCount;
	}

	public int[] getImage() {
		return getFrameImage(0);
	}

	public int getLoopCount() {
		return this.loopCount;
	}

	int[] line = null;
	int lastColor = 0;
	int[][] buffer=null;
	int curPos=0;
	private void setPixels() {
		if(buffer==null || curPos==buffer.length-1){
			buffer=new int[10][this.width * this.height];
			curPos=0;
		}
		int[] dest = buffer[curPos];
		curPos++;
		if (this.lastDispose > 0) {
			if (this.lastDispose == 3) {
				int n = this.frameCount - 2;
				if (n > 0)
					this.lastColors = getFrameImage(n - 1);
				else {
					this.lastColors = null;
				}
			}
			if (this.lastColors != null) {
				System.arraycopy(lastColors, 0, dest, 0, lastColors.length);
				if (this.lastDispose == 2) {
					int c = 0;
					if (!this.transparency) {
						c = this.lastBgColor;
					}
					if (line == null) {
						line = new int[width];
						lastColor = 0;
					}
					if (c != lastColor) {
						for (int i = 0; i < width; i++) {
							line[i] = c;
						}
						lastColor = c;
					}
					for (int i = 0; i < this.lrh; i++) {
						int n1 = (this.lry + i) * this.width + this.lrx;
						int n2 = n1 + this.lrw;
						System.arraycopy(line, 0, dest, n1, n2 - n1);
					}
				}
				
			}

		}

		int pass = 1;
		int inc = 8;
		int iline = 0;
		for (int i = 0; i < this.ih; i++) {
			int line = i;
			if (this.interlace) {
				if (iline >= this.ih) {
					pass++;
					switch (pass) {
					case 2:
						iline = 4;
						break;
					case 3:
						iline = 2;
						inc = 4;
						break;
					case 4:
						iline = 1;
						inc = 2;
					}
				}
				line = iline;
				iline += inc;
			}
			line += this.iy;
			if (line < this.height) {
				int k = line * this.width;
				int dx = k + this.ix;
				int dlim = dx + this.iw;
				if (k + this.width < dlim) {
					dlim = k + this.width;
				}
				int sx = i * this.iw;
				while (dx < dlim) {
					int index = this.pixels[(sx++)] & 0xFF;
					int c = this.act[index];
					if (c != 0) {
						dest[dx] = c;
					}
					dx++;
				}
			}

		}
		this.colors = dest;
	}

	public int[] getFrameImage(int n) {
		GifFrame frame = getFrame(n);
		if (frame == null) {
			return null;
		}
		return frame.colors;
	}

	public GifFrame getCurrentFrame() {
		GifFrame frame=this.currentFrame;
		if (frame == null)
			return null;
		if (image == null) {
			image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		}
		image.setPixels(frame.colors, 0, width, 0, 0, width, height);
		
		frame.image = image;

		return frame;
	}

	public GifFrame getFrame(int n) {
		GifFrame frame = this.gifFrame;
		int i = 0;
		while (frame != null) {
			if (i == n) {
				return frame;
			}
			frame = frame.nextFrame;

			i++;
		}
		return null;
	}

	public void reset() {
		this.currentFrame = this.gifFrame;
	}

	public GifFrame next() {
		if (!this.isShow) {
			this.isShow = true;
			return this.gifFrame;
		}
		if (this.currentFrame == null) {
			return null;
		}
		if (this.status == 0) {
			if (this.currentFrame.nextFrame != null)
				this.currentFrame = this.currentFrame.nextFrame;
		} else {
			this.currentFrame = this.currentFrame.nextFrame;
			if (this.currentFrame == null) {
				this.currentFrame = this.gifFrame;
			}
		}
		return this.currentFrame;
	}

	private int readByte() {
		this.in = new ByteArrayInputStream(this.gifData);
		this.gifData = null;
		return readStream();
	}

	private int readStream() {
		init();
		if (this.in != null) {
			readHeader();
			if (!err()) {
				readContents();
				if (this.frameCount < 0) {
					this.status = 1;
				} else {
					this.status = -1;
				}
			}
			try {
				if (this.in == null) {
					return this.status;
				}
				this.in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.status = 2;
		}
		return this.status;
	}

	private void decodeImageData() {
		int NullCode = -1;
		int npix = this.iw * this.ih;

		if ((this.pixels == null) || (this.pixels.length < npix)) {
			this.pixels = new byte[npix];
		}
		if (this.prefix == null) {
			this.prefix = new short[4096];
		}
		if (this.suffix == null) {
			this.suffix = new byte[4096];
		}
		if (this.pixelStack == null) {
			this.pixelStack = new byte[4097];
		}

		int data_size = read();
		int clear = 1 << data_size;
		int end_of_information = clear + 1;
		int available = clear + 2;
		int old_code = NullCode;
		int code_size = data_size + 1;
		int code_mask = (1 << code_size) - 1;
		for (int code = 0; code < clear; code++) {
			this.prefix[code] = 0;
			this.suffix[code] = ((byte) code);
		}
		int bi;
		int pi;
		int top;
		int first;
		int count;
		int bits;
		int code;
		int datum = bits = count = first = top = pi = bi = 0;
		for (int i = 0; i < npix;)
			if (top == 0) {
				if (bits < code_size) {
					if (count == 0) {
						count = readBlock();
						if (count <= 0) {
							break;
						}
						bi = 0;
					}
					datum += ((this.block[bi] & 0xFF) << bits);
					bits += 8;
					bi++;
					count--;
				} else {
					code = datum & code_mask;
					datum >>= code_size;
					bits -= code_size;

					if ((code > available) || (code == end_of_information)) {
						break;
					}
					if (code == clear) {
						code_size = data_size + 1;
						code_mask = (1 << code_size) - 1;
						available = clear + 2;
						old_code = NullCode;
					} else if (old_code == NullCode) {
						this.pixelStack[(top++)] = this.suffix[code];
						old_code = code;
						first = code;
					} else {
						int in_code = code;
						if (code == available) {
							this.pixelStack[(top++)] = ((byte) first);
							code = old_code;
						}
						while (code > clear) {
							this.pixelStack[(top++)] = this.suffix[code];
							code = this.prefix[code];
						}
						first = this.suffix[code] & 0xFF;

						if (available >= 4096) {
							break;
						}
						this.pixelStack[(top++)] = ((byte) first);
						this.prefix[available] = ((short) old_code);
						this.suffix[available] = ((byte) first);
						available++;
						if (((available & code_mask) == 0)
								&& (available < 4096)) {
							code_size++;
							code_mask += available;
						}
						old_code = in_code;
					}
				}
			} else {
				top--;
				this.pixels[(pi++)] = this.pixelStack[top];
				i++;
			}
		for (int i = pi; i < npix; i++)
			this.pixels[i] = 0;
	}

	private boolean err() {
		return this.status != 0;
	}

	private void init() {
		this.status = 0;
		this.frameCount = 0;
		this.gifFrame = null;
		this.gct = null;
		this.lct = null;
	}

	private int read() {
		int curByte = 0;
		try {
			curByte = this.in.read();
		} catch (Exception e) {
			this.status = 1;
		}
		return curByte;
	}

	private int readBlock() {
		this.blockSize = read();
		int n = 0;
		if (this.blockSize > 0) {
			try {
				int count = 0;
				while (n < this.blockSize) {
					count = this.in.read(this.block, n, this.blockSize - n);
					if (count == -1) {
						break;
					}
					n += count;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (n < this.blockSize) {
				this.status = 1;
			}
		}
		return n;
	}

	private int[] readColorTable(int ncolors) {
		int nbytes = 3 * ncolors;
		int[] tab = null;
		byte[] c = new byte[nbytes];
		int n = 0;
		try {
			n = this.in.read(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (n < nbytes) {
			this.status = 1;
		} else {
			tab = new int[256];
			int i = 0;
			int j = 0;
			while (i < ncolors) {
				int r = c[(j++)] & 0xFF;
				int g = c[(j++)] & 0xFF;
				int b = c[(j++)] & 0xFF;
				tab[(i++)] = (0xFF000000 | r << 16 | g << 8 | b);
			}
		}
		return tab;
	}

	private void readContents() {
		boolean done = false;
		while ((!done) && (!err())) {
			int code = read();
			switch (code) {
			case 44:
				readImage();
				break;
			case 33:
				code = read();
				switch (code) {
				case 249:
					readGraphicControlExt();
					break;
				case 255:
					readBlock();
					String app = "";
					for (int i = 0; i < 11; i++) {
						app = app + (char) this.block[i];
					}
					if (app.equals("NETSCAPE2.0"))
						readNetscapeExt();
					else {
						skip();
					}
					break;
				default:
					skip();
				}
				break;
			case 59:
				done = true;
				break;
			case 0:
				break;
			default:
				this.status = 1;
			}
		}
	}

	private void readGraphicControlExt() {
		read();
		int packed = read();
		this.dispose = ((packed & 0x1C) >> 2);
		if (this.dispose == 0) {
			this.dispose = 1;
		}
		this.transparency = ((packed & 0x1) != 0);
		this.delay = (readShort() * 10);

		this.delaysHashtable.put(Integer.valueOf(this.frameCount),
				Integer.valueOf(this.delay));
		this.transIndex = read();
		read();
	}

	private void readHeader() {
		String id = "";
		for (int i = 0; i < 6; i++) {
			id = id + (char) read();
		}
		if (!id.startsWith("GIF")) {
			this.status = 1;
			return;
		}
		readLSD();
		if ((this.gctFlag) && (!err())) {
			this.gct = readColorTable(this.gctSize);
			this.bgColor = this.gct[this.bgIndex];
		}
	}

	private void readImage() {
		this.ix = readShort();
		this.iy = readShort();
		this.iw = readShort();
		this.ih = readShort();
		int packed = read();
		this.lctFlag = ((packed & 0x80) != 0);
		this.interlace = ((packed & 0x40) != 0);

		this.lctSize = (2 << (packed & 0x7));
		if (this.lctFlag) {
			this.lct = readColorTable(this.lctSize);
			this.act = this.lct;
		} else {
			this.act = this.gct;
			if (this.bgIndex == this.transIndex) {
				this.bgColor = 0;
			}
		}
		int save = 0;
		if (this.transparency) {
			save = this.act[this.transIndex];
			this.act[this.transIndex] = 0;
		}
		if (this.act == null) {
			this.status = 1;
		}
		if (err()) {
			return;
		}
		decodeImageData();
		skip();
		if (err()) {
			return;
		}
		this.frameCount += 1;
		setPixels();
		if (this.gifFrame == null) {
			this.gifFrame = new GifFrame(this.colors, this.delay);
			this.currentFrame = this.gifFrame;
		} else {
			GifFrame f = this.gifFrame;
			while (f.nextFrame != null) {
				f = f.nextFrame;
			}
			f.nextFrame = new GifFrame(this.colors, this.delay);
		}

		if (this.transparency) {
			this.act[this.transIndex] = save;
		}
		resetFrame();
	}

	private void readLSD() {
		this.width = readShort();
		this.height = readShort();

		int packed = read();
		this.gctFlag = ((packed & 0x80) != 0);

		this.gctSize = (2 << (packed & 0x7));
		this.bgIndex = read();
		this.pixelAspect = read();
	}

	private void readNetscapeExt() {
		do {
			readBlock();
			if (this.block[0] == 1) {
				int b1 = this.block[1] & 0xFF;
				int b2 = this.block[2] & 0xFF;
				this.loopCount = (b2 << 8 | b1);
			}
		} while ((this.blockSize > 0) && (!err()));
	}

	private int readShort() {
		return read() | read() << 8;
	}

	private void resetFrame() {
		this.lastDispose = this.dispose;
		this.lrx = this.ix;
		this.lry = this.iy;
		this.lrw = this.iw;
		this.lrh = this.ih;
		this.lastColors = this.colors;
		this.lastBgColor = this.bgColor;
		this.dispose = 0;
		this.transparency = false;
		this.delay = 0;
		this.lct = null;
	}

	private void skip() {
		do
			readBlock();
		while ((this.blockSize > 0) && (!err()));
	}
}