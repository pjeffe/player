package com.mixzing.musicobject;

import java.util.ArrayList;

import com.mixzing.musicobject.dto.GlobalSongDTO;

public interface GlobalSong extends GlobalSongDTO{

	void setGlobalSongSources(ArrayList<GlobalSongSource> gsss);
	
	public ArrayList<GlobalSongSource> getGlobalSongSources();
	
	public boolean isLocal();

}