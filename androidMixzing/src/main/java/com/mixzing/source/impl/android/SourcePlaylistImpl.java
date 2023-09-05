package com.mixzing.source.impl.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Playlists;

import com.mixmoxie.source.dao.SourceTrackManager;
import com.mixmoxie.source.sourceobject.SourcePlaylist;
import com.mixmoxie.source.sourceobject.SourceTrack;
import com.mixzing.android.SdCardHandler;
import com.mixzing.log.Logger;

public class SourcePlaylistImpl implements SourcePlaylist, SourcePlaylist.Signature {
	private Context context;
	private long id;
	private String name;
	private SourceTrackManagerImpl trackMgr;
	ArrayList<SourceTrack> tracks;
	HashMap<Long, SourceTrack> trackMap;
	private int size;
	private float duration;
	private Object playlistLock = new Object();
	private static final Logger log = Logger.getRootLogger();
	private StoreUtils utils;
	

	
	public SourcePlaylistImpl(StoreUtils utils, Context context, SourceTrackManager trackMgr, String id, String name) {
		this.context = context;
		this.id = Long.parseLong(id);
		this.name = name;
		this.trackMgr = (SourceTrackManagerImpl) trackMgr;
		this.utils = utils;
		tracks = new ArrayList<SourceTrack>();
		trackMap = new HashMap<Long, SourceTrack>();
		reInitTracks();
	}

	protected void updateName(String s) {
		this.name = s;
	}

	protected String[] playlistProjection = { Playlists.Members.AUDIO_ID };
	//	public void newTrack(SourceTrack track) {
	//		tracks.add(track);
	//		size += track.size();
	//		duration += track.duration();
	//	}

	protected boolean reInitTracks() {
		if(Logger.IS_TRACE_ENABLED)
			log.trace("Reinit tracks called for playlist. " + name);
		boolean isChanged = false;
		final String vol = SdCardHandler.getVolume();
		Cursor cur = utils.query(Playlists.Members.getContentUri(vol, id),
				playlistProjection, null, null, null);
		
		if(cur == null || cur.getCount() == 0) {
			if(cur != null) {
				cur.close();
			}
			return false;
		}
		
		synchronized(playlistLock) {
			HashSet<Long> sourceIds = new HashSet<Long>();

			
			if (cur != null) {
				cur.moveToFirst();
				for (int i = cur.getCount(); i > 0; --i) {
					Long tid = getTrackId(cur);
					sourceIds.add(tid);

					if(!containsTrack(tid)) {
						SourceTrackManagerImpl trackMgrImpl = (SourceTrackManagerImpl) trackMgr;
						SourceTrack track = trackMgrImpl.findByColumnId(tid);
						//log.debug(String.format("playlist member id %s: %s", tid, track.toString()));
						if (track == null) {
							// XXX should we reload tracks ?
						} else {
							isChanged = true;
							trackMap.put(track.id().getInternalId(), track);
							tracks.add(track);
						}
					}
					cur.moveToNext();
				}
				cur.close();
			}
			List<SourceTrack> tracksToRemove = new ArrayList<SourceTrack>();
			for(SourceTrack t : this.tracks) {
				if(!sourceIds.contains(t.id().getInternalId())) {
					if(Logger.IS_DEBUG_ENABLED)
						log.debug("Should remove track . " + t.id().getInternalId());
					tracksToRemove.add(t);
				}
			}
			for(SourceTrack t : tracksToRemove) {
				removePreviousTrack(t);
				isChanged = true;
			}
		}
		return isChanged;
	}

	protected void removePreviousTrack(SourceTrack t) {
		// XXX: Do we need to override the equals and hash methods in Track to ensure that we remove in case
		// the objects themselves get out of sync ?
		tracks.remove(t);
		trackMap.remove(t.id().getInternalId());
	}

	protected boolean containsTrack(Long tid) {
		if(trackMap.get(tid) != null) {
			return true;
		}
		return false;
	}

	protected static final Long ZERO = Long.valueOf(0);
	
	protected Long getTrackId(Cursor cur) {
		Long id = ZERO;
		String s = cur.getString(0);
		try {
			id = Long.valueOf(s);
		} catch (Exception e) {
			
		}
		return id;
	}

	public void addTrack(SourceTrack track) {
		synchronized (playlistLock) {
			ContentResolver resolver = context.getContentResolver();

			// find number of tracks in playlist
			// XXX if we don't specify play order will it just add the new track to the end anyway?
			String[] cols = new String[] { "count(*)" };
			final String vol = SdCardHandler.getVolume();
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(vol, id);
			Cursor cur = resolver.query(uri, cols, null, null, null);
			if (cur != null) {
				try {
					if (cur.moveToFirst()) {
						int num = cur.getInt(0);
						ContentValues val = new ContentValues(1);
						val.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(num));
						val.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, track.id().getInternalId());
						resolver.bulkInsert(uri, new ContentValues[] { val });
					}
				}
				catch (Exception e) {
				}
				finally {
					cur.close();
				}
			}
		}
	}

	public String getDbId() {
		return Long.toString(id);
	}

	public String getName() {
		return name;
	}

	public String getParent() {
		return null;
	}

	public Signature getSignature() {
		return null;
	}

	public List<SourceTrack> getTracks() {
		return tracks;
	}

	public boolean isGenius() {
		return false;
	}

	public float getDuration() {
		return duration;
	}

	public int getSize() {
		return size;
	}

	public int getTrackCount() {
		return tracks.size();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("playlist " + id + "  " +  name  + "  size " + size + " duration " + duration);
		for (SourceTrack track : tracks) {
			sb.append("\n  " + track.toString());
		}
		return sb.toString();
	}
}
