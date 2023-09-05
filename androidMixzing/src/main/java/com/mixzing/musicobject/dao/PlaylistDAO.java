package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.musicobject.Playlist;

public interface PlaylistDAO extends MusicObjectDAO<Playlist>{

	public long insert(Playlist play);

	public ArrayList<Playlist> readAll();

	public ArrayList<Playlist> findCurrentPlaylists();

	public void delete(Playlist play);

	public void updateName(Playlist p);

	public void updateType(Playlist p);
	
	public void updateFromMagicToSource(Playlist play);

}