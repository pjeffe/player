package com.mixzing.musicobject.dao.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.TrackEquivalenceSongs;
import com.mixzing.musicobject.dao.TrackEquivalenceSongsDAO;

public class TrackEquivalenceSongsDAOImpl extends
		BaseDAO<TrackEquivalenceSongs> implements TrackEquivalenceSongsDAO {

	@Override
	protected TrackEquivalenceSongs createInstance(ResultSet rs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String tableName() {
		return "track_equivalence_songs";
	}

}
