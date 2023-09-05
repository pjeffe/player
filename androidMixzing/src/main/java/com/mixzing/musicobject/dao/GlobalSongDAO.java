package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.musicobject.GlobalSong;

public interface GlobalSongDAO extends MusicObjectDAO<GlobalSong> {

    public GlobalSong createInstance(GlobalSongSpec gss);
    
	public long insert(GlobalSong gss);

	public ArrayList<GlobalSong> readAll();

	public GlobalSong findByServerGsid(long gsid);

	public void updateGsidByLsid(long oldGsid, long newGsid);
	
	public int tracksWithGsid();
    
    public GlobalSong findUnwrappedById(long id); 

}