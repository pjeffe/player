package com.mixzing.source.impl.android;

import android.database.Cursor;
import android.net.Uri;

public interface StoreUtils {

	public Cursor query(Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder);
	
	public void shutdown();

}