package com.mixzing.musicobject.dto;

import com.mixzing.musicobject.EnumRatingSource;
import com.mixzing.musicobject.EnumRatingValue;


public interface RatingSongDTO {
	
	public  long getGlobalSongId();

	public  void setGlobalSongId(long globalSongId);

	public  long getId();

	public  void setId(long id);

	public  boolean isDeleted();

	public  void setDeleted(boolean isDeleted);

	public  long getPlid();

	public  void setPlid(long plid);

	public  EnumRatingSource getRatingSource();

	public  void setRatingSource(EnumRatingSource ratingSource);

	public  EnumRatingValue getRatingValue();

	public  void setRatingValue(EnumRatingValue ratingValue);

	public  long getTimeRated();

	public  void setTimeRated(long timeRated);

}