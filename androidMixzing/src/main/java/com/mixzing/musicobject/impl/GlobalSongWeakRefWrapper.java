package com.mixzing.musicobject.impl;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mixzing.log.Logger;
import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.GlobalSongSource;
import com.mixzing.musicobject.dao.GlobalSongDAO;
import com.mixzing.musicobject.dao.GlobalSongSourceDAO;
import com.mixzing.musicobject.dao.impl.GlobalSongCache;

public class GlobalSongWeakRefWrapper implements GlobalSong {

	public static final int GLOBAL_SONG_CACHE_SZ = 512;
	protected static Logger lgr = Logger.getRootLogger();
	protected static GlobalSongCache<Long,GlobalSong> cache = new GlobalSongCache<Long,GlobalSong>(GLOBAL_SONG_CACHE_SZ);

	protected static GlobalSongSourceDAO gssDAO;
	protected static GlobalSongDAO dao;
	
	
	protected WeakReference<GlobalSong> gsRef;
	protected long id;
	protected GlobalSong strongRef;
	
	public static void setDAOs(GlobalSongDAO pgsDao, GlobalSongSourceDAO pgssDAO) {
		dao = pgsDao;
		gssDAO = pgssDAO;		
	}


	protected GlobalSong fetchObject() {
		GlobalSong gs = gsRef.get();
		if(gs == null) {
			if(Logger.IS_TRACE_ENABLED)
				lgr.debug("***** Possibly Finalized Global Song, refetching from db " + id);
			strongRef = gs = dao.findUnwrappedById(id);
			cacheit();
			gsRef = new WeakReference<GlobalSong>(gs);
		} else {
			//  access the strong reference cache so that the object stays around
			cache.get(gs.getId());
		}    
		return gs;
	}

	protected void cacheit() {
		cache.put(id,strongRef);
		strongRef = null;
	}

	public static void emptyCache() {
		cache.clear();
	}
	
	protected GlobalSongWeakRefWrapper(GlobalSong gs) {
		strongRef = gs;
		id = gs.getId();
		gsRef = new WeakReference<GlobalSong>(gs);

		// Keep a strong ref in strongRef around if we are adding with a negative id
		// it will get reset once a proper id is set
		if(id > 0) {
			cacheit();
			//lgr.trace("********** Adding gs weak ref with id : " + id);
		}

	}

	public GlobalSongWeakRefWrapper(GlobalSongSpec gss) {
		this(new GlobalSongImpl(gss));
	}

	public GlobalSongWeakRefWrapper(ResultSet rs) {
		this(new GlobalSongImpl(rs));
	}

	public void setGlobalSongSources(ArrayList<GlobalSongSource> gsss) {
		fetchObject().setGlobalSongSources(gsss);

	}

	public ArrayList<GlobalSongSource> getGlobalSongSources() {
		GlobalSong gs = fetchObject();
		ArrayList<GlobalSongSource> sources = gs.getGlobalSongSources();
		if(sources == null) {
			sources = gssDAO.findByGlobalSong(id);
			gs.setGlobalSongSources(sources);
		}
		return sources;
	}

	public boolean isLocal() {
		// TODO Auto-generated method stub
		return fetchObject().isLocal();
	}

	public String getAlbum() {
		// TODO Auto-generated method stub
		return fetchObject().getAlbum();
	}

	public void setAlbum(String album) {
		fetchObject().setAlbum(album);

	}

	public String getArtist() {
		// TODO Auto-generated method stub
		return fetchObject().getArtist();
	}

	public void setArtist(String artist) {
		fetchObject().setArtist(artist);

	}

	public float getDuration() {
		return fetchObject().getDuration();
	}

	public void setDuration(float duration) {
		fetchObject().setDuration(duration);

	}

	public String getGenre() {
		// TODO Auto-generated method stub
		return fetchObject().getGenre();
	}

	public void setGenre(String genre) {
		fetchObject().setGenre(genre);

	}

	public long getGsid() {
		// TODO Auto-generated method stub
		return fetchObject().getGsid();
	}

	public void setGsid(long gsid) {
		fetchObject().setGsid(gsid);

	}

	public long getId() {
		// TODO Auto-generated method stub
		return fetchObject().getId();
	}

	public void setId(long id) {
		this.id = id;
		GlobalSong gs = gsRef.get();
		if(gs != null) {
			gs.setId(id);
			strongRef = gs;
			cacheit();
		} else {
			fetchObject().setId(id);
		}  
	}

	public int getReleaseYear() {
		// TODO Auto-generated method stub
		return fetchObject().getReleaseYear();
	}

	public void setReleaseYear(int releaseYear) {
		fetchObject().setReleaseYear(releaseYear);

	}

	public long getTimeUpdated() {
		// TODO Auto-generated method stub
		return fetchObject().getTimeUpdated();
	}

	public void setTimeUpdated(long timeUpdated) {
		fetchObject().setTimeUpdated(timeUpdated);

	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return fetchObject().getTitle();
	}

	public void setTitle(String title) {
		// TODO Auto-generated method stub
		fetchObject().setTitle(title);
	}

	public String getTrackNumber() {
		// TODO Auto-generated method stub
		return fetchObject().getTrackNumber();
	}

	public void setTrackNumber(String trackNumber) {
		fetchObject().setTrackNumber(trackNumber);
	}

}
