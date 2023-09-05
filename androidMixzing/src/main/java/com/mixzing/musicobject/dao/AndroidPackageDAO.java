package com.mixzing.musicobject.dao;

import java.util.List;

import com.mixzing.musicobject.AndroidPackage;

public interface AndroidPackageDAO extends MusicObjectDAO<AndroidPackage>{

	public long insert(AndroidPackage pkg);

	public List<AndroidPackage> readAll();

	public List<AndroidPackage> findAllPackages();

	public void delete(AndroidPackage t);
	
	public void deleteAll();

}