package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.Video;
import com.mixzing.musicobject.dto.impl.VideoDTOImpl;

public class VideoImpl extends VideoDTOImpl implements Video {

	public VideoImpl(ResultSet rs) {
		super(rs);
	}
	
	public VideoImpl() {
		
	}

}
