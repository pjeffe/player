package com.mixzing.musicobject.dto;


public interface RecommendationDTO {

	public enum RecoSource {
		INFERRED,
		SERVER
	}
	public String getArtsongArtist();

	public void setArtsongArtist(String artsongArtist);

	public long getArtsongId();

	public void setArtsongId(long artsongId);

	public String getArtsongTitle();

	public void setArtsongTitle(String artsongTitle);

	public long getId();

	public void setId(long id);

	public boolean isDeleted();

	public void setDeleted(boolean isDeleted);

	public boolean isRated();

	public void setRated(boolean isRated);

	public long getPlid();

	public void setPlid(long plid);

	public RecoSource getRecoSource();

	public void setRecoSource(RecoSource recoSource);

	public float getScore();

	public void setScore(float score);

	public long getTimeReceived();

	public void setTimeReceived(long timeReceived);

}