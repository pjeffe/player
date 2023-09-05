package com.mixzing.source.impl.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Playlists;

import com.mixmoxie.source.dao.SourcePlaylistManager;
import com.mixmoxie.source.dao.SourceTrackManager;
import com.mixmoxie.source.sourceobject.SourcePlaylist;
import com.mixzing.android.SdCardHandler;
import com.mixzing.log.Logger;
import com.mixzing.source.android.AndroidSourceManager;
import com.mixzing.source.impl.android.StoreUtilImpl.MyEmptyCursor;

public class SourcePlaylistManagerImpl implements SourcePlaylistManager {
	private HashMap<String, SourcePlaylist> playlistsById;
	private Object playlistLock = new Object();
	private Context context;
	private AndroidSourceManager srcMgr;
	private SourceTrackManager trackMgr;
	private static final Logger log = Logger.getRootLogger();
	private StoreUtils utils;
	private boolean inited  = false;
	protected SourceManagerImpl mzSrcMgr;
	protected boolean haveSignalledEmptySinceLastChange;
	
	private static String[] cols = {
		Playlists._ID,
		Playlists.NAME,
	};
	private static final int ID = 0;
	private static final int NAME = 1;

	public SourcePlaylistManagerImpl(Context context, AndroidSourceManager srcMgr, SourceTrackManager trackMgr, StoreUtils utils, SourceManagerImpl sourceManagerImpl) {
		this.context = context;
		this.srcMgr = srcMgr;
		this.trackMgr = trackMgr;
		this.playlistsById = new HashMap<String, SourcePlaylist>();
		this.utils = utils;
		this.mzSrcMgr = sourceManagerImpl;
		try {
			reloadPlaylists();
		} catch (EmptyQueryException e) {

		}
	}

	protected synchronized boolean reloadPlaylists() throws EmptyQueryException {
		boolean isChanged = false;

		if(!mzSrcMgr.isResolvingEnabled()) {
			return isChanged;
		}
		
		Cursor cur = null;

		try {
			cur = utils.query(Playlists.getContentUri(SdCardHandler.getVolume()), cols, null, null, Playlists.NAME);
		} catch (Exception e) {
			log.error("SourcePlaylistManagerImpl.reloadPlaylists: query exception:", e);
		}

		if(cur == null || cur.getCount() == 0) {
			if(cur != null) {
				cur.close();
			}			
			
			if(cur == null || (cur instanceof MyEmptyCursor) || haveSignalledEmptySinceLastChange) {
				throw new EmptyQueryException(false,playlistsById.size());
			}
			
			haveSignalledEmptySinceLastChange = true;
			throw new EmptyQueryException(true,playlistsById.size());
		}

		
		synchronized (playlistLock) {
			HashSet<String> sourceIds = new HashSet<String>();
			isChanged = true;
			cur.moveToFirst();
			for (int i = cur.getCount(); i > 0; --i) {
				String sourceId = this.getSourceId(cur);
				sourceIds.add(sourceId);
				SourcePlaylist spl = playlistsById.get(sourceId);
				if (spl == null) {
					spl = new SourcePlaylistImpl(utils, context, trackMgr,
							getSourceId(cur), cur.getString(NAME));
					newPlaylist(spl);
					if (Logger.IS_DEBUG_ENABLED)
						log.debug("SourcePlaylistManagerImpl.reloadPlaylists: added " + spl.toString());
					isChanged = true;
					haveSignalledEmptySinceLastChange = false;
				}
				else {
					SourcePlaylistImpl spli = (SourcePlaylistImpl)spl;
					spli.updateName(cur.getString(NAME));
					boolean res = spli.reInitTracks();
					if (res) {
						isChanged = true; // XXX : don't know how to accurately set this
					}
					if (Logger.IS_DEBUG_ENABLED)
						log.debug("SourcePlaylistManagerImpl.reloadPlaylists: reloaded " + spl.toString());
				}
				cur.moveToNext();
			}
			cur.close();

			List<SourcePlaylist> playlistsToRemove = new ArrayList<SourcePlaylist>();
			for (SourcePlaylist t : playlistsById.values()) {
				if (!sourceIds.contains(t.getDbId())) {
					playlistsToRemove.add(t);
				}
			}
			for (SourcePlaylist t : playlistsToRemove) {
				removeSourcePlaylist(t);
				haveSignalledEmptySinceLastChange = false;
				isChanged = true;
			}
		}
		
		inited = true;
		return isChanged;
	}

	protected void removeSourcePlaylist(SourcePlaylist src) {
		playlistsById.remove(src.getDbId());
	}

	private String getSourceId(Cursor cur) {
		return cur.getString(ID);
	}

	private void newPlaylist(SourcePlaylist spl) {
		playlistsById.put(spl.getDbId(), spl);
	}

	public SourcePlaylist findbyId(String id) {
		synchronized (playlistLock) {
			return playlistsById.get(id);
		}
	}

	public ArrayList<SourcePlaylist> getPlaylists() {
		ArrayList<SourcePlaylist> pls;
		synchronized (playlistLock) {
			pls = new ArrayList<SourcePlaylist>(playlistsById.values());
		}
		return pls;
	}

	public SourcePlaylist createPlaylist(String name) {
		assert (false);
		return null;
	}

	public SourcePlaylist renamePlaylist(SourcePlaylist playlist, String name) {
		assert (false);
		return null;
	}

	public void deletePlaylist(SourcePlaylist playlist) {
		assert (false);
	}
}
