package com.mixzing.source.impl.android;

import android.content.Context;

import com.mixmoxie.source.dao.SourceLibraryManager;
import com.mixmoxie.source.dao.SourceTrackManager;
import com.mixzing.servicelayer.TrackService;


public class SourceLibraryManagerImpl implements SourceLibraryManager {
	private SourceTrackManagerImpl trackMgr;
	private Context context;
	
	public SourceLibraryManagerImpl(Context context, StoreUtils utils, SourceManagerImpl srcMgr, TrackService ts) {
		this.context = context;
		this.trackMgr = new SourceTrackManagerImpl(context, utils, srcMgr, ts);
	}

	public SourceTrackManager getSourceTrackManager() {
		return trackMgr;
	}
	
	public void clearNonRetainedResources() {
		trackMgr.clearNonRetainedResources();
	}
}
