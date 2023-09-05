package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.musicobject.GlobalSongSource;

public interface GlobalSongSourceDAO extends MusicObjectDAO<GlobalSongSource> {

	public long insert(GlobalSongSource gss);

	public ArrayList<GlobalSongSource> readAll();

	public ArrayList<GlobalSongSource> findByGlobalSong(long id);

}