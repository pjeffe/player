package com.mixzing.musicobject.dao.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.TrackEquivalence;
import com.mixzing.musicobject.dao.TrackEquivalenceDAO;

public class TrackEquivalenceDAOImpl extends BaseDAO<TrackEquivalence> implements
		TrackEquivalenceDAO {

	@Override
	protected TrackEquivalence createInstance(ResultSet rs) {
		return null;
	}

	@Override
	protected String tableName() {
		return "track_equivalence";
	}

}
