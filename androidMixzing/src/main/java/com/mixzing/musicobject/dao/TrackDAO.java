package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.musicobject.Track;

public interface TrackDAO extends MusicObjectDAO<Track>{

	public long insert(Track track);

	public ArrayList<Track> readAll();

	public ArrayList<Track> findAllTracks();

	public ArrayList<Track> findTracksInRecommendation();

	public ArrayList<Track> findTracksInAPlaylist(long plid);

	public void delete(Track t);

}