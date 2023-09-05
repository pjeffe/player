package com.mixzing.servicelayer;

import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.musicobject.GlobalSong;

public interface GlobalSongService {

	public GlobalSong getSong(long globalSongId);
	
	public void addTrackEquivalence(long lsid, long gsid);

	public GlobalSong createGlobalSong(GlobalSongSpec gss);

	public GlobalSong getGlobalSongByGsid(long gsid);

	public void updateGsidUsingOldGsid(long oldGsid, long newGsid);
	
	public int tracksWithGsid();

}