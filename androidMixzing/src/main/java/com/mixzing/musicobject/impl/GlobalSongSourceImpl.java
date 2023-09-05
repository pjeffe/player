package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.musicobject.GlobalSongSource;
import com.mixzing.musicobject.dto.impl.GlobalSongSourceDTOImpl;

public class GlobalSongSourceImpl extends GlobalSongSourceDTOImpl implements
		GlobalSongSource {

	public GlobalSongSourceImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GlobalSongSourceImpl(long globalSongId, GlobalSongSpec gss, String purchLib) {
		super(globalSongId, gss, purchLib);
		// TODO Auto-generated constructor stub
	}

	public GlobalSongSourceImpl(ResultSet rs) {
		super(rs);
		// TODO Auto-generated constructor stub
	}

}
