package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.musicobject.Recommendation;

public interface RecommendationDAO extends MusicObjectDAO<Recommendation>{

	public long insert(Recommendation reco) ;

	/*
	 * READ
	 */
	public ArrayList<Recommendation> findAllRecommendationForPlaylist(long plid);

	public ArrayList<Recommendation> findCurrentRecommendations(long plid);

	public ArrayList<Recommendation> findCurrentRecommendations();

	public ArrayList<Recommendation> findRatedRecoByPlidAsid(long plid,
			long artsongId);

	public ArrayList<Recommendation> readAll();

	public int invalidatePreviousRecos(long plid, long time);
			

	public int invalidatePreviousRecos(Recommendation reco);

	public int updateRecoAsRated(long recoId) ;

	public int softDelete(Recommendation reco);

	public int softDelete(long recoId);

	/*
	 * DELETE
	 */
	public int hardDelete(Recommendation reco);

	public int hardDelete(long recoId);

}