package com.mixzing.musicobject;

import com.mixzing.musicobject.dto.RecoAlternateDTO;

public interface RecoAlternate extends RecoAlternateDTO {

	void setGlobalSong(GlobalSong g);
	
	public GlobalSong getGlobalSong();


	
}