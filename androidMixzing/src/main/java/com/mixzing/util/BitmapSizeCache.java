package com.mixzing.util;

import android.graphics.Bitmap;

import com.mixzing.util.BitmapSizeCache.BitmapItem;


public class BitmapSizeCache extends SizeCache<String, BitmapItem> {
	private static final int ITEM_SIZE = 100; // order-of-magnitude estimate of overhead for item objects

	public static class BitmapItem implements MemoryItem {
		final Bitmap bitmap;

		private BitmapItem(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		@Override
		public int getMemorySize() {
			return bitmap == null ? 0 : bitmap.getRowBytes() * bitmap.getHeight();
		}

		@Override
		public String toString() {
			return super.toString() + ": bitmap = " + bitmap;
		}
	}

	public BitmapSizeCache(String tag, long maxCacheSize, int initialCapacity) {
		super(tag, initialCapacity, maxCacheSize, null, 0, ITEM_SIZE);
	}

	public void putBitmap(String key, Bitmap bitmap) {
		super.put(key, new BitmapItem(bitmap));
	}

	public Bitmap getBitmap(String key) {
		final BitmapItem item = super.get(key);
		return item == null ? null : item.bitmap;
	}
}
