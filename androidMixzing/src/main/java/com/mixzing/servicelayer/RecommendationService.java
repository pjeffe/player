package com.mixzing.servicelayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mixzing.message.messageobject.impl.TrackRecommendation;
import com.mixzing.musicobject.Recommendation;

public interface RecommendationService {

	public ArrayList<Recommendation> getAllRecommendations();

	public ArrayList<Recommendation> getAllRecommendationsSince(long time);
	
	public ArrayList<Recommendation> getAllRecommendations(long plid);

	public ArrayList<Recommendation> getAllRecommendationsSince(long plid, long time);

	public void deleteRecommendationsForPlaylist(long plid);
	
	public ArrayList<Recommendation> processRecommendations(long plid, List<TrackRecommendation> trl, long time ) ;

	public Recommendation loadRecoById(long recoId);

	public void setRated(Recommendation reco);

	public Collection<RecoStats> getRecoStats();
	
	public interface RecoStats {
		public long getPlid();
		public long getLastUserRatingTime();
		public float getHighScore();
	}
}