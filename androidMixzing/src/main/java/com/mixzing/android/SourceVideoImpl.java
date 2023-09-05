package com.mixzing.android;



import com.mixzing.musicobject.SourceVideo;

public class SourceVideoImpl implements SourceVideo {

	protected String album;
	private String artist;
	private String category;
	private long dateAdded;
	private long dateModified;
	private long dateTaken;
	private String description;
	private int duration;
	private String language;
	private float latitude;
	private String location;
	private String mimeType;
	private float longitude;
	private String resolution;
	private int size;
	private String tags;
	private String title;


	public void setAlbum(String album) {
		this.album = album;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setDateAdded(long dateAdded) {
		this.dateAdded = dateAdded;
	}

	public void setDateModified(long dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateTaken(long dateTaken) {
		this.dateTaken = dateTaken;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setTitle(String title) {
		this.title = title;
	}



	public SourceVideoImpl() {
	}

	public String getAlbum() {
		return album;
	}

	public String getArtist() {
		return artist;
	}

	public String getCategory() {
		return category;
	}

	public long getDateAdded() {
		return dateAdded;
	}

	public long getDateModified() {
		return dateModified;
	}

	public long getDateTaken() {
		return dateTaken;
	}

	public String getDescription() {
		return description;
	}

	public int getDuration() {
		return duration;
	}

	public String getLanguage() {
		return language;
	}

	public float getLatitude() {
		return latitude;
	}

	public String getLocation() {
		return location;
	}

	public float getLongitude() {
		return longitude;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getResolution() {
		return resolution;
	}

	public int getSize() {
		return size;
	}

	public String getTags() {
		return tags;
	}

	public String getTitle() {
		return title;
	}
}

