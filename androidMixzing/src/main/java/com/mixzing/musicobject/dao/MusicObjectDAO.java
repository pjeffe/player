package com.mixzing.musicobject.dao;

public interface MusicObjectDAO<T> {

	public T readOne(String sql, Long... longs);

	public T readOne(String sql);

	public T findById(long id);

}