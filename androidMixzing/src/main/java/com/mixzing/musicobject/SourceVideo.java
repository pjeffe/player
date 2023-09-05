package com.mixzing.musicobject;

public interface SourceVideo {

	public  String getLocation();

	public  int getSize();
	
	public  String getMimeType();
	
	public long getDateAdded();
	
	public long getDateModified();
	
	public  String getTitle();	
	
	public int getDuration();
	
	public  String getArtist();
	
	public  String getAlbum();
	
	public  String getResolution();
	
	public  String getDescription();
	
	public  String getTags();
	
	public  String getCategory();
	
	public  String getLanguage();
	
	public float getLatitude();
	
	public float getLongitude();
	
	public long getDateTaken();
	
}