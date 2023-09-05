package com.mixzing.musicobject.dto;

public interface GlobalSongDTO {

	public  String getAlbum();

	public  void setAlbum(String album);

	public  String getArtist();

	public  void setArtist(String artist);

	public  float getDuration();

	public  void setDuration(float duration);

	public  String getGenre();

	public  void setGenre(String genre);

	public  long getGsid();

	public  void setGsid(long gsid);

	public  long getId();

	public  void setId(long id);

	public  int getReleaseYear();

	public  void setReleaseYear(int releaseYear);

	public  long getTimeUpdated();

	public  void setTimeUpdated(long timeUpdated);

	public  String getTitle();

	public  void setTitle(String title);

	public  String getTrackNumber();

	public  void setTrackNumber(String trackNumber);

}