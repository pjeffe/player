package com.mixzing.source.impl.android;

import java.util.Properties;

import android.content.Context;

import com.mixmoxie.source.dao.SourceLibraryManager;
import com.mixmoxie.source.dao.SourceManager;
import com.mixmoxie.source.dao.SourcePlaylistManager;
import com.mixmoxie.source.player.SourceSoundPlayer;
import com.mixzing.log.Logger;
import com.mixzing.servicelayer.TrackService;
import com.mixzing.source.android.AndroidSourceManager;


public class SourceManagerImpl implements SourceManager, AndroidSourceManager {
	private Properties props;
	protected SourceLibraryManagerImpl libMgr;
	protected SourcePlaylistManagerImpl playlistMgr;
	protected SourceSoundPlayer player;
	protected Context context;
	private boolean updated;
	private Object syncLock = new Object();
	protected StoreUtils utils;
	protected boolean isInited = false;
	protected static Logger lgr = Logger.getRootLogger();
	protected AndroidEventListener listener;
	private boolean savePlaylists;
	protected TrackService trkSvc;
	
	public SourceManagerImpl(Properties appProperties, Context context, StoreUtilImpl stru, TrackService ts) {
		this.props = appProperties;
		this.context = context;
		this.utils = stru;
		this.trkSvc = ts;
    }

	// XXX: This method can now block indefinitely reading track nad playlist information
	public void sourceLibraryInit() {
		if (libMgr == null) {
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Source library init *****************************************************************************************");
			}
			listener = new AndroidEventListener(this,context);
			libMgr = new SourceLibraryManagerImpl(context, utils, this, trkSvc);
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Source playlist init *****************************************************************************************");
			}
			playlistMgr = new SourcePlaylistManagerImpl(context, this, libMgr.getSourceTrackManager(),utils, this);
			
			player = new SoundPlayer();
			listener.addManagers(playlistMgr,(SourceTrackManagerImpl)libMgr.getSourceTrackManager());
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Source manager complete init *****************************************************************************************");
			}
			isInited = true;
		}
	}

	public SourceSoundPlayer getPlayer() {
		return player;
	}

	public SourceLibraryManager getSourceLibraryManager() {
		return libMgr;
	}

	public SourcePlaylistManager getSourcePlaylistManager() {
		return playlistMgr;
	}

	public boolean reSync() {
		synchronized (syncLock) {
			boolean ret = updated;
			updated = false;
			return ret;
		}
	}
	
	public void setUpdated(boolean val) {
		synchronized (syncLock) {
			updated = val;
		}
	}

	public boolean shouldSavePlaylists() {
		synchronized (syncLock) {
			boolean ret = savePlaylists;
			savePlaylists = false;
			return ret;
		}
	}
	
	public void markSavePlaylistsForRestore(boolean val) {
		synchronized (syncLock) {
			savePlaylists = val;
		}		
	}
	
	public void shutDown() {
		if(utils != null) {
			utils.shutdown();
		}
		if(listener != null) {
			listener.shutdown();
		}
	}

	public void attach() {
	}

	public void detach() {
	}

	public void clearNonRetainedResources() {
		libMgr.clearNonRetainedResources();
	}

	public SourceType getSourceType() {
		return SourceType.FileSystem;
	}

	public void updateItunesFileName(String location) {
	}

	public boolean isInited() {
		return isInited;
	}
	
	public boolean isResolvingEnabled() {
		return true;
	}
}
