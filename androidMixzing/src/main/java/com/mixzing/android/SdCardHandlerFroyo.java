package com.mixzing.android;

import java.io.File;

import android.content.Context;

public class SdCardHandlerFroyo {

	public static File getExternalCacheDir(Context context) {
		return context.getExternalCacheDir();
	}
}
