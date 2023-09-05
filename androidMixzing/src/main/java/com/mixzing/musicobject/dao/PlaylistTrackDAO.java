package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.musicobject.PlaylistTrack;

public interface PlaylistTrackDAO extends MusicObjectDAO<PlaylistTrack>{

	public long insert(PlaylistTrack gss);

	public ArrayList<PlaylistTrack> readAll();

	public ArrayList<PlaylistTrack> findbyPlid(long plid);

	public ArrayList<PlaylistTrack> findbyLsid(long plid);
	
	public void delete(PlaylistTrack pltrk);

	public void deleteAllPlaylistTracks(long plid);

}