package com.mixzing.musicobject;

import java.util.ArrayList;

import com.mixzing.musicobject.dto.RecommendationDTO;


public interface Recommendation  extends  RecommendationDTO{

	void setAlternates(ArrayList<RecoAlternate> als);

	public ArrayList<RecoAlternate> getAlternates();
	
}