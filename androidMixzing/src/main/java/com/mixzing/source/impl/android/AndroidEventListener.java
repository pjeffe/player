package com.mixzing.source.impl.android;


import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;

import com.mixzing.MixzingConstants;
import com.mixzing.android.SdCardHandler;
import com.mixzing.log.Logger;
import com.mixzing.source.android.AndroidSourceManager;


public class AndroidEventListener {
	private static final Logger log = Logger.getRootLogger();
	private SourcePlaylistManagerImpl playlistManager;
	private SourceTrackManagerImpl trackManager;
	private Context context;
	private AndroidSourceManager srcMgr;
	protected Runnable playlistRun, trackRun;

	private static final long NINE_SECONDS = 9000;
	private static final long TEN_SECONDS = 10000;
	
	private static final long EMPTY_CHECK_DELAY = MixzingConstants.FIVE_MINUTE;	
	private static final long TRACK_RELOAD_DELAY = NINE_SECONDS;
	private static final long PLAYLIST_RELOAD_DELAY = TEN_SECONDS;
	private static final long NON_INIT_DELAY = MixzingConstants.FIVE_MINUTE/5;
		
	protected long lastTrackChangeTime = 0;
	protected long lastPlaylistChangeTime = 0;

	protected Handler trackHandler, playlistHandler;
	protected boolean isRegistered = false;
	
	protected PlaylistContentObserver playlistObserver;
	protected TrackContentObserver    trackObserver;
	protected boolean isShuttingDown = false;
	
	public AndroidEventListener(AndroidSourceManager srcMgr, Context con) {
		context = con;
		this.srcMgr = srcMgr;
		this.playlistRun = new PlaylistChangeHandler();
		this.trackRun = new TrackChangeHandler();
		registerContentObservers();
	}

	public void addManagers(SourcePlaylistManagerImpl pm, SourceTrackManagerImpl tm) {
		playlistManager = pm;
		trackManager = tm;
	}
	
	protected synchronized void registerContentObservers() {
		registerTrackChangeObserver();
		registerPlaylistChangeObserver();
		isRegistered = true;
	}

	private void unRegisterContentObservers() {
		// register to be notified when playlists change
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver != null) {
				playlistObserver.shutdown();
				trackObserver.shutdown();
				resolver.unregisterContentObserver(playlistObserver);
				resolver.unregisterContentObserver(trackObserver);
			}
		}
		catch (UnsupportedOperationException e) {
			log.error("StorePlaylistManagerImpl.register:", e);
		}
	}
	
	public synchronized void shutdown() {
		isShuttingDown = true;
		if(isRegistered) {
			unRegisterContentObservers();
		}
		isRegistered = false;
	}
	
	private void registerTrackChangeObserver() {
		// register to be notified when tracks change
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver != null) {
				Uri uri = MediaStore.Audio.Media.getContentUri(SdCardHandler.getVolume());
				trackObserver = new TrackContentObserver(trackHandler = new Handler());
				resolver.registerContentObserver(uri, true, trackObserver);
			}
		}
		catch (UnsupportedOperationException e) {
			log.error("StoreTrackManagerImpl.register:", e);
		}
	}

	private class TrackContentObserver extends ContentObserver {

		protected Handler handle;
		public TrackContentObserver(Handler handler) {
			super(handler);
			this.handle = handler;
		}
		@Override
		public synchronized void onChange(boolean selfChange) {
			if(Logger.IS_DEBUG_ENABLED)
				log.debug("TrackContentObserver.onChange: selfChange " + selfChange);
			lastTrackChangeTime = SystemClock.uptimeMillis();
			handle.removeCallbacks(AndroidEventListener.this.trackRun);			
			handle.postDelayed(AndroidEventListener.this.trackRun, TRACK_RELOAD_DELAY);
		}
		
		public synchronized void shutdown() {
			handle.removeCallbacks(AndroidEventListener.this.trackRun);
		}
	}


	
	private void registerPlaylistChangeObserver() {
		// register to be notified when playlists change
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver != null) {
				Uri uri = MediaStore.Audio.Playlists.getContentUri(SdCardHandler.getVolume());
				playlistObserver = new PlaylistContentObserver(playlistHandler = new Handler());
				resolver.registerContentObserver(uri, true, playlistObserver);
			}
		}
		catch (UnsupportedOperationException e) {
			log.error("StorePlaylistManagerImpl.register:", e);
		}
	}

	private class PlaylistContentObserver extends ContentObserver {
		protected Handler handle;
		public PlaylistContentObserver(Handler handler) {
			super(handler);
			this.handle = handler;
		}

		@Override
		public void onChange(boolean selfChange) {
			if(Logger.IS_DEBUG_ENABLED)
				log.debug("PlaylistContentObserver.onChange: selfChange " + selfChange);
			lastPlaylistChangeTime = SystemClock.uptimeMillis();
			handle.removeCallbacks(AndroidEventListener.this.playlistRun);			
			handle.postDelayed(AndroidEventListener.this.playlistRun, PLAYLIST_RELOAD_DELAY);			
		}
		
		public synchronized void shutdown() {
			handle.removeCallbacks(AndroidEventListener.this.playlistRun);
		}
	}



	protected boolean reloadTracksIfNeeded() {
		if(this.lastTrackChangeTime > trackManager.getLastValidLoadTime()) {			
			try {
				return trackManager.reloadTracks();
			} catch (EmptyQueryException e) {
			}
		}
		return false;
	}
	
	
	public class PlaylistChangeHandler implements Runnable {
		public void run() {
			if(Logger.IS_DEBUG_ENABLED)
				log.debug("PlaylistChangeHandler.run: ");

			boolean isPlaylistLoad = false;
			if(srcMgr.isInited()) {
				try {
					if(reloadTracksIfNeeded()) {
						srcMgr.setUpdated(true);
					}
					isPlaylistLoad = true;
					if (playlistManager.reloadPlaylists()) {
						srcMgr.setUpdated(true);
						if (Logger.IS_DEBUG_ENABLED)
							log.debug("PlaylistChangeHandler.run: updated");
					} 
				}catch (EmptyQueryException e) {
					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("PlaylistChangeHandler got empty query rescheduling in "  + EMPTY_CHECK_DELAY);
					}
					if(isPlaylistLoad) {
						if(e.isScannerReturnedEmpty() && e.getPrevCount() > 0) {
							if (Logger.shouldSelectivelyLog(100)) {
								log.error("PlaylistChangeHandler.run: all playlists appear deleted, previous count = " + e.getPrevCount());
							}
							srcMgr.markSavePlaylistsForRestore(true);
						}
					}
					playlistHandler.removeCallbacks(AndroidEventListener.this.playlistRun);			
					playlistHandler.postDelayed(AndroidEventListener.this.playlistRun, EMPTY_CHECK_DELAY);	
				}
			} else {
				if (Logger.IS_DEBUG_ENABLED) {
					log.debug("PlaylistChangeHandler srcmgr not completely inited : requeue "  + NON_INIT_DELAY);
				}
				// These never get called since the handler runs on MixZingMain - which queues up the events after init is done
				// but we will leave them here in case the processing thread changes
				playlistHandler.removeCallbacks(AndroidEventListener.this.playlistRun);			
				playlistHandler.postDelayed(AndroidEventListener.this.playlistRun, NON_INIT_DELAY);	
			}
		}
	}
	
	public class TrackChangeHandler implements Runnable {
		

		public void run() {
			if(Logger.IS_DEBUG_ENABLED)
				log.debug("TrackChangeHandler.run ");

			if(srcMgr.isInited()) {
				try {
					if (trackManager.reloadTracks()) {
						srcMgr.setUpdated(true);
						if (Logger.IS_DEBUG_ENABLED)
							log.debug("TrackChangeHandler.run: updated");
					}
				} catch (EmptyQueryException e) {
					trackHandler.removeCallbacks(AndroidEventListener.this.trackRun);			
					trackHandler.postDelayed(AndroidEventListener.this.trackRun, EMPTY_CHECK_DELAY);
				}	
			} else {
				// These never get called since the handler runs on MixZingMain - which queues up the events after init is done
				// but we will leave them here in case the processing thread changes
				if(Logger.IS_DEBUG_ENABLED)
					log.debug("TrackChangeHandler.run srcmgr not completely inited : requeue" + NON_INIT_DELAY);
				trackHandler.removeCallbacks(AndroidEventListener.this.trackRun);			
				trackHandler.postDelayed(AndroidEventListener.this.trackRun, NON_INIT_DELAY);				
			}
		}
	}

}
