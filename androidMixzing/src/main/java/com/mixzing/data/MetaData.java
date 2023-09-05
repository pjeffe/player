package com.mixzing.data;

import android.os.Parcel;
import android.os.Parcelable;


public class MetaData implements Parcelable {
	private CoreMetaData core;
	private String pathname;
	private int artistId;
	private long artistAlbumId;
	private long trackId;
	private long gsid;
	private boolean albumChanged;
	private boolean defaultArt;
	private boolean downloaded;
	private boolean missingTags;
	private boolean streaming;


	public MetaData(String artist, String album, String title, int artistId, long artistAlbumId, int trackId, long gsid, long duration,
		int year, int tracknum, String pathname, boolean albumChanged, boolean defaultArt, boolean downloaded) {
			init(artist, album, title, artistId, artistAlbumId, trackId, gsid, duration, year, tracknum, pathname, albumChanged,
				defaultArt, downloaded, false, false);
	}

	public MetaData(String artist, String album, String title, int artistId, long artistAlbumId, int trackId, long gsid, long duration,
		int year, int tracknum, String pathname, boolean albumChanged, boolean defaultArt, boolean downloaded, boolean missingTags,
		boolean streaming) {
			init(artist, album, title, artistId, artistAlbumId, trackId, gsid, duration, year, tracknum, pathname,
				albumChanged, defaultArt, downloaded, missingTags, streaming);
	}

	private void init(String artist, String album, String title, int artistId, long artistAlbumId, int trackId, long gsid, long duration,
			int year, int tracknum, String pathname, boolean albumChanged, boolean defaultArt, boolean downloaded, boolean missingTags,
			boolean streaming) {
		this.core = new CoreMetaData(artist, album, title, null, duration, year, tracknum, null);
		this.pathname = pathname;
		this.artistId = artistId;
		this.artistAlbumId = artistAlbumId;
		this.trackId = trackId;
		this.gsid = gsid;
		this.albumChanged = albumChanged;
		this.defaultArt = defaultArt;
		this.downloaded = downloaded;
		this.missingTags = missingTags;
		this.streaming = streaming;
	}

	public MetaData() {
		init("", "", "", -1, -1, -1, -1, 0, 0, 0, "", false, false, false, false, false);
	}

	public MetaData(Parcel parcel) {
		core = CoreMetaData.CREATOR.createFromParcel(parcel);
		pathname = parcel.readString();
		artistId = parcel.readInt();
		artistAlbumId = parcel.readLong();
		trackId = parcel.readLong();
		gsid = parcel.readLong();
		albumChanged = parcel.readInt() == 1;
		defaultArt = parcel.readInt() == 1;
		downloaded = parcel.readInt() == 1;
		missingTags = parcel.readInt() == 1;
		streaming = parcel.readInt() == 1;
	}

	public void writeToParcel(Parcel parcel, int flags) {
		core.writeToParcel(parcel, 0);
		parcel.writeString(pathname);
		parcel.writeInt(artistId);
		parcel.writeLong(artistAlbumId);
		parcel.writeLong(trackId);
		parcel.writeLong(gsid);
		parcel.writeInt(albumChanged ? 1 : 0);
		parcel.writeInt(defaultArt ? 1 : 0);
		parcel.writeInt(downloaded ? 1 : 0);
		parcel.writeInt(missingTags ? 1 : 0);
		parcel.writeInt(streaming ? 1 : 0);
	}

	public static final Parcelable.Creator<MetaData> CREATOR = new Parcelable.Creator<MetaData>() {
		public MetaData createFromParcel(Parcel source) {
			return new MetaData(source);
		}

		public MetaData[] newArray(int size) {
			return new MetaData[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public String getArtist() {
		return core.artist;
	}

	public void setArtist(String artist) {
		core.artist = artist;
	}

	public String getAlbum() {
		return core.album;
	}

	public void setAlbum(String album) {
		core.album = album;
	}

	public String getTitle() {
		return core.title;
	}

	public void setTitle(String title) {
		core.title = title;
	}

	public String getGenre() {
		return core.genre;
	}

	public void setGenre(String genre) {
		core.genre = genre;
	}

	public String getPathname() {
		return pathname;
	}

	public void setPathname(String pathname) {
		this.pathname = pathname;
	}

	public int getArtistId() {
		return artistId;
	}

	public void setArtistId(int artistId) {
		this.artistId = artistId;
	}

	public long getArtistAlbumId() {
		return artistAlbumId;
	}

	public void setArtistAlbumId(long artistAlbumId) {
		this.artistAlbumId = artistAlbumId;
	}

	public long getTrackId() {
		return trackId;
	}

	public void setTrackId(long trackId) {
		this.trackId = trackId;
	}

	public long getGsid() {
		return gsid;
	}

	public void setGsid(long gsid) {
		this.gsid = gsid;
	}

	public long getDuration() {
		return core.duration;
	}

	public void setDuration(long duration) {
		core.duration = duration;
	}

	public int getYear() {
		return core.year;
	}

	public void setYear(int year) {
		core.year = year;
	}

	public int getTracknum() {
		return core.trackNum;
	}

	public void setTracknum(int tracknum) {
		core.trackNum = tracknum;
	}

	public boolean isAlbumChanged() {
		return albumChanged;
	}

	public void setAlbumChanged(boolean albumChanged) {
		this.albumChanged = albumChanged;
	}

	public boolean isDefaultArt() {
		return defaultArt;
	}

	public void setDefaultArt(boolean defaultArt) {
		this.defaultArt = defaultArt;
	}

	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	public boolean isMissingTags() {
		return missingTags;
	}

	public void setMissingTags(boolean missingTags) {
		this.missingTags = missingTags;
	}

	public boolean isStreaming() {
		return streaming;
	}

	public String getArtworkUrl() {
		return core.getArtworkUrl();
	}

	public void setArtworkUrl(String artworkUrl) {
		core.setArtworkUrl(artworkUrl);
	}

	public CoreMetaData getCore() {
		return core;
	}

	@Override
	public String toString() {
		return String.format(
			"%s | %s | %s: artistId = %d, artistAlbumId = %d, trackId = %d, gsid = %d, duration = %d, albumChanged = %s, defaultArt = %s, downloaded = %s, missingTags = %s, streaming = %s, path = %s, artworkUrl = %s",
			core.artist, core.album, core.title, artistId, artistAlbumId, trackId, gsid, core.duration, albumChanged, defaultArt, downloaded, missingTags, streaming, pathname, core.getArtworkUrl());
	}
}
