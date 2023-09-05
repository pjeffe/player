package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.musicobject.RecoAlternate;

public interface RecoAlternateDAO extends MusicObjectDAO<RecoAlternate>{

	/*
	 * READ
	 */
	public ArrayList<RecoAlternate> findAllAlternatesForReco(long recoid);

	public long insert(RecoAlternate alt);

	public ArrayList<RecoAlternate> readAll();

}