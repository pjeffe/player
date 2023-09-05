package com.mixzing.util;

import android.graphics.Bitmap;

public interface BitmapCache {
	public void put(String key, Bitmap bitmap);
	public Bitmap get (String key);
	public Bitmap getFromMemoryCache (String key);
	public Bitmap getFromPersistentCache (String key, boolean ignoreExpiration);
}
