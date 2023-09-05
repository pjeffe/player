package com.mixzing.musicobject.impl;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.mixzing.message.messageobject.impl.TrackRecommendation;
import com.mixzing.musicobject.RecoAlternate;
import com.mixzing.musicobject.Recommendation;
import com.mixzing.musicobject.dto.impl.RecommendationDTOImpl;

public class RecommendationImpl extends RecommendationDTOImpl implements Recommendation {

	public RecommendationImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RecommendationImpl(long plid, TrackRecommendation t, long time) {
		super(plid, t, time);
		// TODO Auto-generated constructor stub
	}

	public RecommendationImpl(ResultSet rs) {
		super(rs);
		// TODO Auto-generated constructor stub
	}

	protected ArrayList<RecoAlternate> alternates;
	
	public void setAlternates(ArrayList<RecoAlternate> als) {
		
		alternates = als;
	}

	public ArrayList<RecoAlternate> getAlternates() {
		return alternates;
	}
}
