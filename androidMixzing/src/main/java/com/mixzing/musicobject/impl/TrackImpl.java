package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.Track;
import com.mixzing.musicobject.dto.impl.TrackDTOImpl;

public class TrackImpl extends TrackDTOImpl implements Track {

	protected long androidId;
	

	public TrackImpl(long anId) {
		super();
		setAndroidId(anId);
	}
	public TrackImpl(ResultSet rs) {
		super(rs);
	}

	public long getAndroidId() {
		return androidId;
	}
	
	public void setAndroidId(long anId) {
		androidId = anId;
	}

}
