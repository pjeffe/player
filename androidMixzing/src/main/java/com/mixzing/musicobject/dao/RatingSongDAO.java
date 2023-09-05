package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.musicobject.RatingSong;

public interface RatingSongDAO extends MusicObjectDAO<RatingSong>{

	public long insert(RatingSong rat);

	public ArrayList<RatingSong> readAll();

	public ArrayList<RatingSong> findCurrentRatings(long plid);

	public RatingSong is_already_rated(long plid, long globalsong_id);

	public void deleteRatingsForPlaylist(long plid) ;

	public void delete(RatingSong rat);

}