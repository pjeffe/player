package com.mixzing.android;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mixzing.log.Logger;

public class MediaStoreUtil {
	private static final Logger log = Logger.getRootLogger();

	public static Cursor query(Context context, Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cur = null;
		try {
			cur = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
		}
		catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug("MediaStoreUtil.query:", e);
		}
		return cur;
	}
}
