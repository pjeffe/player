package com.mixzing.musicobject.dao;

public interface CleanupResourceDAO {

	public abstract void deleteUnusedNonGlobalSongObjects();

	public abstract void deleteUnreferencedGlobalSongs();

}