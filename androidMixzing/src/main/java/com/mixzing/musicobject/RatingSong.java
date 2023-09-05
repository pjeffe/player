package com.mixzing.musicobject;

import com.mixzing.musicobject.dto.RatingSongDTO;


public interface RatingSong  extends  RatingSongDTO{

	public void setGlobalSong(GlobalSong song);
	
	public GlobalSong getGlobalSong();

}