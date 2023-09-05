package com.mixzing.musicobject.dao;

import java.util.List;

import com.mixzing.musicobject.Video;

public interface VideoDAO extends MusicObjectDAO<Video>{

	public long insert(Video vid);

	public List<Video> readAll();

	public List<Video> findAllVideos();

	public void delete(Video t);
	
	public void deleteAll();

}