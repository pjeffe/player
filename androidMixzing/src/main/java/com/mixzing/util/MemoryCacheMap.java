package com.mixzing.util;

import java.util.HashMap;

import com.mixzing.log.Logger;


// MRU memory cache

public class MemoryCacheMap<K, V extends MemoryCacheObject> extends HashMap<K, V> {
	private static final long serialVersionUID = 1998467748231462601L;
	private static final Logger log = Logger.getRootLogger();
	private int cacheSize;  // max entries in cache
	private K oldestKey;
	private String tag = "";

	public MemoryCacheMap(int cacheSize) {
		super(cacheSize);
		if (cacheSize == 0) {
			throw new IllegalArgumentException("Invalid cacheSize");
		}
		this.cacheSize = cacheSize;
	}

	@Override
	public V put(K key, V value) {
		if (Logger.IS_DEBUG_ENABLED) {
			if (tag == "") {
				tag = value.getClass().getName();
			}
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("MemoryCacheMap<" + tag + ">.put(" + key + ", " + value + ")");
			}
		}
		synchronized (this) {
			value.setTime(System.currentTimeMillis());
			if (oldestKey == null) {
				oldestKey = key;
			}
			else {
				trim();
			}
			return super.put(key, value);
		}
	}

	@Override
	public V get(Object key) {
		synchronized (this) {
			V value = super.get(key);
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("MemoryCacheMap<" + tag + ">.get(" + key + "): cache " + (value == null ? "miss" : "hit"));
			}
			if (value != null) {
				value.setTime(System.currentTimeMillis());
			}
			return value;
		}
	}

	@Override
	public void clear() {
		synchronized (this) {
			super.clear();
		}
	}

	@Override
	public V remove(Object key) {
		synchronized (this) {
			return super.remove(key);
		}
	}

	// remove oldest member if necessary
	private void trim() {
		synchronized (this) {
			if (size() >= cacheSize && oldestKey != null) {
				// remove oldest
				MemoryCacheObject o = super.remove(oldestKey);
				if (Logger.IS_DEBUG_ENABLED) {
					log.debug("MemoryCacheMap<" + tag + ">.trim: removed " + o);
				}

				// set new oldest
				K oldestKey = null;
				long oldestTime = Long.MAX_VALUE;
				for (final K key : keySet()) {
					o = super.get(key);
					long time = o.getTime();
					if (time < oldestTime) {
						oldestTime = time;
						oldestKey = key;
					}
				}
				this.oldestKey = oldestKey;
			}
		}
	}
}
