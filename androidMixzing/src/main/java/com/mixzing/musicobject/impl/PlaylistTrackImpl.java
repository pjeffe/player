package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.PlaylistTrack;
import com.mixzing.musicobject.dto.impl.PlaylistTrackDTOImpl;

public class PlaylistTrackImpl extends PlaylistTrackDTOImpl implements
		PlaylistTrack {

	public PlaylistTrackImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PlaylistTrackImpl(ResultSet rs) {
		super(rs);
		// TODO Auto-generated constructor stub
	}

}
