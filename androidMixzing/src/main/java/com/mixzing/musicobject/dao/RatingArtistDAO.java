package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.musicobject.RatingArtist;

public interface RatingArtistDAO extends MusicObjectDAO<RatingArtist>{

	public long insert(RatingArtist rat);

	public ArrayList<RatingArtist> readAll();

	public ArrayList<RatingArtist> findCurrentRatings(long plid);

	public ArrayList<RatingArtist> findCurrentHatedRatings();

	public void deleteRatingsForPlaylist(long plid);

}