package com.mixzing.servicelayer;

import java.util.ArrayList;
import java.util.HashMap;

import com.mixzing.musicobject.RatingArtist;
import com.mixzing.musicobject.RatingSong;

public interface RatingService {

	public ArrayList<RatingSong> findCurrentRatingSongs(long plid);
	
	public HashMap<String, String> findCurrentHatedArtists();

	public HashMap<String, String> findCurrentIgnoredArtists(long plid);

	public void addRating(RatingSong rat);

	public void addRating(RatingArtist rat);
	
	public void deleteRatingsForPlaylist(long plid);

	public void deleteSongRating(RatingSong rat);

	public boolean hasPositiveRatingForGsid(long id, long gsid);
	
}