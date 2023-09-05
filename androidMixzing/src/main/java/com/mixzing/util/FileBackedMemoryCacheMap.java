package com.mixzing.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mixzing.android.AndroidUtil;
import com.mixzing.log.Logger;

/**
 * This class provides both a memory cache and a file based cache. The file based cache can only be used
 * from outside the UI Thread and protects itself against infractions of this rule. The file memory cache delegates to the 
 * MemoryCacheMap class. The file based cache also supports optional expiration of entries in the cache. This means we can use
 * this to cache bitmaps that are not likely to change very often at their source. If they do change they will update at the end of
 * their expiration period, so that the image does not become stuck on a stale value. Currently there is no support for the user to
 * refresh a stale image or flush the cache. That can be added if it becomes necessary. Expired images remain in the cache until replaced.
 * That way if the bitmap source is unavailable we can still display a bitmap even if it's possibly stale. Writes to the file system
 * and handling of the cache to enforce maximum content size are handled by the FileCacheWorker class, requests to write are simply posted
 * to a concurrent queue.
 * @author guy
 *
 */

public class FileBackedMemoryCacheMap implements BitmapCache {
	
	private static final long serialVersionUID = 657800118986676875L;
	private final MemoryCacheMap<String, CachedBitmap> memoryCache;
	private String storageDir;
	private final Thread uiThread;
	private long lifetime;
	
	private static final Logger log = Logger.getRootLogger();
	public static final String HTTP_PREFIX = "http://";
	private static final int PREFIX_LENGTH = HTTP_PREFIX.length();
	

	public FileBackedMemoryCacheMap(int memoryCacheSize, final String storageDir, final long lifetime) {
		if (memoryCacheSize >= 0) {
			this.memoryCache = new MemoryCacheMap<String, CachedBitmap>(memoryCacheSize);
		}
		else {
			this.memoryCache = null;
		}
		this.lifetime = lifetime;
		uiThread = AndroidUtil.getAppContext().getMainLooper().getThread();
		final File extCacheDir = AndroidUtil.getAppContext().getExternalCacheDir();
		if (extCacheDir != null && extCacheDir.exists()) {
			final File fullStorageDir = new File(extCacheDir.getAbsolutePath() + "/" + storageDir);
			if(!fullStorageDir.exists()) {
				if (fullStorageDir.mkdir()) {
					this.storageDir = fullStorageDir.getAbsolutePath() + "/";
				}	
			}
			else {
				this.storageDir = fullStorageDir.getAbsolutePath() + "/";
			}
		}
	}

	public Bitmap get(String key) {
		synchronized(this) {
			Bitmap bmp = getFromMemoryCache(key);
			if (bmp == null) {
				bmp = getFromPersistentCache(key, false);
			}
			return bmp;
		}
	}

	public Bitmap getFromMemoryCache(String key) {
		synchronized(this) {
			if (memoryCache != null) {
				final CachedBitmap cbmp =  memoryCache.get(normalizeKey(key));
				if (cbmp != null) {
					return cbmp.getBitmap();
				}
			}
			return null;
		}
	}

	public String normalizeKey(String key) {
		if (key != null && key.length() > 0 && key.startsWith(HTTP_PREFIX)) {
			key = key.substring(PREFIX_LENGTH);
		}
		return key;
	}

	/**
	 * Note this has the special behavior of only fetching from the file cache if
	 * the current thread is not the UI Thread, i.e. when fetching thumbs on a background thread.
	 */
	public Bitmap getFromPersistentCache(String key, boolean ignoreExpiration) {
		synchronized(this) {
			Bitmap bmp = null;
			key = normalizeKey(key);
			if (storageDir != null && key != null) {
				final Thread currentThread = Thread.currentThread();
				// only go to file backup if not on the UI Thread
				if (currentThread.equals(uiThread)) {
					return null;
				}
				final String fileName = URLEncoder.encode(key.toString());
				final File f = new File (storageDir + fileName);
				if (f.exists()) {
					if (!ignoreExpiration && lifetime > 0) {
						final long ftime = f.lastModified() + lifetime;
						if (ftime < System.currentTimeMillis()) {
							if (Logger.IS_DEBUG_ENABLED) {
								log.debug("FileBackedMemoryCacheMap: file expired for key = " + key);
							}
							// note we don't delete it, it will get overwritten if it is re-fetched and inserted
							return null;
						}
					}
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(f);
						bmp = BitmapFactory.decodeStream(fis);
						if (Logger.IS_DEBUG_ENABLED) {
							if (bmp == null) {
								log.debug("FileBackedMemoryCacheMap: failed to decoede bitmap for key = " + key);
							}
						}
						if (bmp != null && memoryCache != null) {
							memoryCache.put(key.toString(), new CachedBitmap(bmp));
						}
					}
					catch (IOException e) {
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug(e);
						}
						f.delete();
					}
					catch (Throwable t) {
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug(t);
						}
					}
					finally {
						try {
							if (fis != null) {
								fis.close();
							}
							if (bmp == null) {
								f.delete();
							}
						}
						catch (Exception e) {
						}
					}
				}
			}

			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("FileBackedMemoryCacheMap.getFromPersistentCache(" + key + "): cache " + (bmp == null ? "miss" : "hit"));
			}

			return bmp;
		}
	}
	

	/**
	 * This just queues the file cache entry to the background worker thread. 
	 * It adds to the memory cache immediately which means future fetches in the short term will most likely
	 * get a memory cache hit unless the cache size is too small.
	 */
	public void put(String key, Bitmap bitmap) {
		synchronized(this) {
			if (key == null || bitmap == null) {
				return;
			}
			key = normalizeKey(key);
			if (memoryCache == null || !memoryCache.containsKey(key)) {
				if (memoryCache != null) {
					memoryCache.put(key, new CachedBitmap(bitmap));
				}
				FileCacheWorker.enqueue(storageDir, key, bitmap, lifetime);
			}
		}
	}

	// current set of caches used by GenericDataCursor based classes.
	private static final int MEMORY_CACHE_SIZE = 200;
	private static final int ONE_WEEK = 3600 * 24 * 7 * 1000;
	public static FileBackedMemoryCacheMap videoCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "ArtistThumbs", ONE_WEEK);
	public static FileBackedMemoryCacheMap artistCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "ArtistThumbs", ONE_WEEK);
	public static FileBackedMemoryCacheMap trackCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "TrackThumbs", ONE_WEEK);
	public static FileBackedMemoryCacheMap userCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "UserThumbs", ONE_WEEK);
	public static FileBackedMemoryCacheMap eventCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "EventThumbs", ONE_WEEK);
	public static FileBackedMemoryCacheMap largeUserCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "UserImages", ONE_WEEK);
	public static FileBackedMemoryCacheMap placesCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE * 2, "PlaceImages", ONE_WEEK * 4);
	public static FileBackedMemoryCacheMap contestCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "ContestImages", ONE_WEEK);
	public static FileBackedMemoryCacheMap contestTrackCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "ContestTrackImages", ONE_WEEK);
	public static FileBackedMemoryCacheMap stationCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "StationImages", ONE_WEEK);
	public static FileBackedMemoryCacheMap radioStationCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "RadioStationImages", ONE_WEEK);
	public static FileBackedMemoryCacheMap radioArtistCache = new FileBackedMemoryCacheMap(MEMORY_CACHE_SIZE, "RadioArtistImages", ONE_WEEK);
}
