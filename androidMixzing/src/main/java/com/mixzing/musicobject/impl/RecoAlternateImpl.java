package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.RecoAlternate;
import com.mixzing.musicobject.dto.impl.RecoAlternateDTOImpl;

public class RecoAlternateImpl extends RecoAlternateDTOImpl implements
		RecoAlternate {

	public RecoAlternateImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RecoAlternateImpl(GlobalSongSpec gss, long recoId, long globalSongId, int rank) {
		super(gss, recoId, globalSongId, rank);
		// TODO Auto-generated constructor stub
	}

	public RecoAlternateImpl(ResultSet rs) {
		super(rs);
		// TODO Auto-generated constructor stub
	}

	protected GlobalSong globalSong;
	
	public GlobalSong getGlobalSong() {
		// TODO Auto-generated method stub
		return globalSong;
	}

	public void setGlobalSong(GlobalSong g) {
		globalSong = g;
		
	}
}
