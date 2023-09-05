package com.mixzing.source.impl.android;


import android.content.ContentResolver;
import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;

import com.mixzing.android.AndroidUtil;
import com.mixzing.android.SdCardHandler;
import com.mixzing.log.Logger;
import com.mixzing.source.impl.android.StoreUtilImpl.MyEmptyCursor;

public class StoreUtilImpl implements StoreUtils {


	private ContentResolver resolver;
	private Context context;
	private static final Logger log = Logger.getRootLogger();
	private long initUptime;
	protected static final int SCANNING_RETRY = 5 * 1000;
	protected boolean isShuttingDown = false;
	protected Object sleepLock = new Object();
	
	public StoreUtilImpl(Context con) {
		this.context = con;
		resolver = con.getContentResolver();
		this.initUptime = SystemClock.uptimeMillis();
	}

	
	public void shutdown() {
		isShuttingDown = true;
		synchronized (sleepLock) {
			sleepLock.notifyAll();
		}
	}
	
	protected void waitForMediaToBeAvailable() {
		while (!AndroidUtil.isMediaReady()) {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug("StoreUtilImpl.waitMediaScanning: media scanner scanning, sleeping");			
			synchronized (sleepLock) {
				if(isShuttingDown) {
					return;
				}
				try {
					sleepLock.wait(SCANNING_RETRY);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mixzing.source.impl.android.StoreUtilsI#query(android.content.Context, android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	public Cursor query(Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {

		Cursor cur = null;
		int retries = 0;
		long entryTime = SystemClock.uptimeMillis();
		
		do {
			if(isShuttingDown) {
				return new MyEmptyCursor(true);
			}
			
			waitForMediaToBeAvailable();

			if(isShuttingDown) {
				return new MyEmptyCursor(true);
			}
			
			// Media is mounted and not scanning
			cur = null;
			try {
				cur = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
				if(cur != null) {
					break;
				} else if(SdCardHandler.isMounted()) {
					if(Logger.IS_TRACE_ENABLED) {
						log.trace("StoreUtilImpl.query: media mounted but still got a null cursor");
					}
				}
			} catch (Exception e) {
				if (Logger.shouldSelectivelyLog()) {
					log.error("StoreUtilImpl.query: media scanner scanning, sleeping again.");
				}
			}
			if(cur == null) {
				synchronized (sleepLock) {
					try {
						sleepLock.wait(SCANNING_RETRY);
					} catch (InterruptedException e) {
					}	
				}
			} else {
				
			}
				
			// Log an error to server every 5 minutes
			if(++retries > 60) {
				log.error("StoreUtilImpl.query: unable to get cursor: isMediaScanning = " +
					AndroidUtil.isMediaScanning(context) + ", is_media_mounted = " +
					SdCardHandler.isMounted() + " uri = " + uri);
				retries = 0;
			}
			
			
			// if scanning started in between for some reason lets restart before returing back
		} while(!AndroidUtil.isMediaReady() || cur == null);
		return cur;
	}
	
	protected class MyEmptyCursor extends AbstractCursor {

		private boolean shutDown = false;
		
		public MyEmptyCursor() {
			this(false);
		}

		public MyEmptyCursor(boolean isShut) {
			super();
			shutDown = isShut;
		}

		public boolean isShuttingDown() {
			return shutDown;
		}
		
		@Override
		public String[] getColumnNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double getDouble(int column) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getFloat(int column) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getInt(int column) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getLong(int column) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public short getShort(int column) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getString(int column) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isNull(int column) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}

