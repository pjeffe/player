package com.mixzing.util;

import android.graphics.Bitmap;

import com.mixzing.util.MemoryCacheObject;

public class CachedBitmap implements MemoryCacheObject {
	private Bitmap bmp;
	private long time;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public CachedBitmap(Bitmap bmp) {
		this.bmp = bmp;
	}
	
	public Bitmap getBitmap () {
		return bmp;
	}
}

