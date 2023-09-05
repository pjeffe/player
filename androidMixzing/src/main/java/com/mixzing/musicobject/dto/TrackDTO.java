package com.mixzing.musicobject.dto;

public interface TrackDTO {

	public long getGlobalSongId();

	public void setGlobalSongId(long gsid);

	public boolean isDeleted();

	public void setDeleted(boolean isDeleted);

	public String getLocation();

	public void setLocation(String location);

	public long getId();

	public void setId(long lsid);

	public String getSourceId();

	public void setSourceId(String location);
}