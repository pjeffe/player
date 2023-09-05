package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.RatingArtist;
import com.mixzing.musicobject.dto.impl.RatingArtistDTOImpl;

public class RatingArtistImpl extends RatingArtistDTOImpl implements
		RatingArtist {

	public RatingArtistImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RatingArtistImpl(ResultSet rs) {
		super(rs);
		// TODO Auto-generated constructor stub
	}

}
