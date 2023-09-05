package com.mixzing.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.mixzing.log.Logger;

import android.graphics.Bitmap;

public class FileCacheWorker {
	
	public static class QEntry {
		private QEntry(String storageDir, String key, Bitmap bitmap, long lifetime) {
			this.storageDir = storageDir;
			this.key = key;
			this.bitmap = bitmap;
			this.lifetime = lifetime;
		}
		private String storageDir;
		private String  key;
		private Bitmap bitmap;
		private long lifetime;
	}
	
	private static final Logger log = Logger.getRootLogger();
	private static LinkedBlockingQueue<QEntry> queue = new LinkedBlockingQueue<QEntry>();
	private static HashMap<String, Long> storageSizeMap = new HashMap<String, Long>();
	private static final long MAX_CACHE_SIZE = 1024 * 1024 * 5; // 5MB
	
	public static void enqueue (String storageDir, String key, Bitmap bitmap, long lifetime) {
		queue.offer(new QEntry(storageDir, key, bitmap, lifetime));
	}
	
	public static boolean checkCacheSize (String storageDir, long lifetime) {
		long totalSize = 0;
		try {
			Long cacheSize = storageSizeMap.get(storageDir);
			File[] files = null;
			final File dir = new File(storageDir);
			if (cacheSize == null) {
				if (dir.exists()) {
					files = dir.listFiles();
					for (File f : files) {
						totalSize += f.length();
					}
				}
			}
			else {
				totalSize = cacheSize.longValue();
			}
			// enforce a max cache size 
			if (totalSize >= MAX_CACHE_SIZE) {
				if (files == null) {
					files = dir.listFiles();
				}
				if (files != null) {
					// sort oldest (earliest modified time) to most recent
					Arrays.sort(files, new Comparator<File>() {
						public int compare(File file1, File file2) {
							return Long.valueOf(file1.lastModified()).compareTo(Long.valueOf(file2.lastModified()));
						}
					}); 
					// delete oldest files until we have room 
					final int len = files.length;
					for (int i = 0; i < len && totalSize > MAX_CACHE_SIZE; i++) {
						final File f = files[i];
						final long size = f.length();
						if (f.delete()) {
							totalSize -= size;
						}
					}
				}
			}
			// cache size to avoid costly rescans of file system
			storageSizeMap.put(storageDir, Long.valueOf(totalSize));
		}
		catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("FileCacheWorker.trimCache exception: ", e);
			}
		}
		return totalSize < MAX_CACHE_SIZE;
	}
	
	// This method was written, but it is not currently used. The idea is that expired images remain in the cache until
	// they are replaced. That way if we have a stale image but no access to the image source we can still present an image.
	/*
	private static long deleteOldFiles(final File dir, final long lifetime) throws SecurityException{
		long deletedSize = 0;
		final long now = System.currentTimeMillis();
		final File[] oldFiles = dir.listFiles(new FileFilter(){
			public boolean accept(File f) {
				return f.lastModified() + lifetime < now;
			}
		});
		for (File f: oldFiles) {
			deletedSize += f.length();
			f.delete();
		}
		return deletedSize;
	}
	*/
	
	
	private static Thread fileCacheInsertThread = new LowPriThread() {
		public void run() {
			while (true) {
				QEntry entry;
				try {
					entry = queue.take();
				}
				catch (InterruptedException e1) {
					return;
				}
				final String key = entry.key;
				final String storageDir = entry.storageDir;
				final String fileName = storageDir + URLEncoder.encode(key.toString());
				final long lifetime = entry.lifetime;
				File f = new File(fileName);
				OutputStream outstream = null;
				boolean success = false;
				try {
					// if the file is being overwritten don't check the cache otherwise
					// try to trim the cache if it is too large
					if (f.exists() || checkCacheSize(storageDir, lifetime)) {
						// compress it to the output file
						final Bitmap bmp = entry.bitmap;
						outstream = new FileOutputStream(f);
						success = bmp.compress(Bitmap.CompressFormat.JPEG, 90, outstream);
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("FileCacheWorker: " + (success ? "saved" : "failed to save") +
									" cached thumb for " + fileName);
						}
					}
				}
				catch (FileNotFoundException e) {
					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("FileCacheWorker: error opening output file: " + fileName);
					}
				}
				finally {
					try {
						if (outstream != null) {
							outstream.close();
						}
						if (!success) {
							f.delete();
						}
						else {
							Long cacheSize = storageSizeMap.get(storageDir);
							cacheSize = Long.valueOf(f.length() + cacheSize);
							storageSizeMap.put(storageDir, cacheSize);
						}
					}
					catch (Exception e) {
					}
				}
			}
		}
	};
	
	static {
		fileCacheInsertThread.setName("File Cache Insert Worker");
		fileCacheInsertThread.start();
	}

}
