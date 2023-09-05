package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.RatingSong;
import com.mixzing.musicobject.dto.impl.RatingSongDTOImpl;

public class RatingSongImpl extends RatingSongDTOImpl implements RatingSong {

	private GlobalSong globalSong;
	
	public RatingSongImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RatingSongImpl(ResultSet rs) {
		super(rs);
		// TODO Auto-generated constructor stub
	}

	public GlobalSong getGlobalSong() {
		// TODO Auto-generated method stub
		return globalSong;
	}

	public void setGlobalSong(GlobalSong song) {
		globalSong = song;
		
	}

}
