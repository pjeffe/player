package com.mixzing.musicobject.impl;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.GlobalSongSource;
import com.mixzing.musicobject.dto.impl.GlobalSongDTOImpl;

public class GlobalSongImpl extends GlobalSongDTOImpl implements GlobalSong {

	public GlobalSongImpl(GlobalSongSpec gss) {
		super(gss);
	}

	public GlobalSongImpl(ResultSet rs) {
		super(rs);
	}

	public GlobalSongImpl() {
		super();
	}

	protected ArrayList<GlobalSongSource> globalSongSources;
	
	public ArrayList<GlobalSongSource> getGlobalSongSources() {
		// TODO Auto-generated method stub
		return globalSongSources;
	}

	public void setGlobalSongSources(ArrayList<GlobalSongSource> gsss) {
		globalSongSources = gsss;
	}

	public boolean isLocal() {
		for(GlobalSongSource gss : globalSongSources) {
			if (!gss.getAuditionUrl().startsWith("http:")) {
				return true;
			}
		}
		return false;
	}
}
