package com.mixzing.data;

import com.mixzing.external.android.Image;
import com.mixzing.external.android.Images;

import android.os.Parcel;
import android.os.Parcelable;


public class CoreMetaData implements Parcelable, Cloneable {
	protected String artist;
	protected String album;
	protected String title;
	protected String genre;
	protected long duration;  // in ms
	protected int year;
	protected int trackNum;
	protected int trackCount;
	protected int posIndex;
	protected int posCount;
	protected String comment;
	protected Images images;


	public CoreMetaData(String artist, String album, String title, String genre, long duration, int year, int tracknum, String artworkUrl) {
		init(artist, album, title, genre, duration, year, tracknum, 0, 0, 0, null, null);
		setArtworkUrl(artworkUrl);
	}

	public CoreMetaData(String artist, String album, String title, String genre, long duration, int year, int tracknum, int trackCount,
			int posIndex, int posCount, String comment) {
		init(artist, album, title, genre, duration, year, tracknum, trackCount, posIndex, posCount, comment, null);
	}

	public CoreMetaData(String artist, String album, String title, String genre, int year, int tracknum, String comment) {
		init(artist, album, title, genre, 0, year, tracknum, 0, 0, 0, comment, null);
	}

	private void init(String artist, String album, String title, String genre, long duration, int year, int tracknum, int trackCount,
			int posIndex, int posCount, String comment, Images images) {
		this.artist = artist;
		this.album = album;
		this.title = title;
		this.genre = genre;
		this.duration = duration;
		this.year = year;
		this.trackNum = tracknum;
		this.trackCount = trackCount;
		this.posIndex = posIndex;
		this.posCount = posCount;
		this.comment = comment;
		this.images = images;
	}

	public CoreMetaData(CoreMetaData meta) {
		init(meta.artist, meta.album, meta.title, meta.genre, meta.duration, meta.year, meta.trackNum, meta.trackCount,
			meta.posIndex, meta.posCount, meta.comment, meta.images);
	}

	public CoreMetaData() {
	}

	public CoreMetaData(Parcel parcel) {
		artist = parcel.readString();
		album = parcel.readString();
		title = parcel.readString();
		genre = parcel.readString();
		duration = parcel.readLong();
		year = parcel.readInt();
		trackNum = parcel.readInt();
		trackCount = parcel.readInt();
		posIndex = parcel.readInt();
		posCount = parcel.readInt();
		comment = parcel.readString();
		if (parcel.readInt() == 1) {
			images = new Images(parcel);
		}
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(artist);
		parcel.writeString(album);
		parcel.writeString(title);
		parcel.writeString(genre);
		parcel.writeLong(duration);
		parcel.writeInt(year);
		parcel.writeInt(trackNum);
		parcel.writeInt(trackCount);
		parcel.writeInt(posIndex);
		parcel.writeInt(posCount);
		parcel.writeString(comment);
		if (images != null) {
			parcel.writeInt(1);
			images.writeToParcel(parcel, flags);
		}
		else {
			parcel.writeInt(0);
		}
	}

	public static final Parcelable.Creator<CoreMetaData> CREATOR = new Parcelable.Creator<CoreMetaData>() {
		public CoreMetaData createFromParcel(Parcel source) {
			return new CoreMetaData(source);
		}

		public CoreMetaData[] newArray(int size) {
			return new CoreMetaData[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getTrackNum() {
		return trackNum;
	}

	public void setTrackNum(int tracknum) {
		this.trackNum = tracknum;
	}

	public int getTrackCount() {
		return trackCount;
	}

	public void setTrackCount(int trackCount) {
		this.trackCount = trackCount;
	}

	public int getPosIndex() {
		return posIndex;
	}

	public void setPosIndex(int posIndex) {
		this.posIndex = posIndex;
	}

	public int getPosCount() {
		return posCount;
	}

	public void setPosCount(int posCount) {
		this.posCount = posCount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Images getImages() {
		return images;
	}

	public void setImages(Images images) {
		this.images = images;
	}

	public String getArtworkUrl() {
		return images == null ? null : images.largeImage.getUrl();
	}

	public void setArtworkUrl(String artworkUrl) {
		if (artworkUrl != null) {
			if (images == null) {
				final Image image = new Image(0, 0, artworkUrl);
				images = new Images(image, image);
			}
			else {
				final Image image = images.getLargeImage();
				image.setUrl(artworkUrl);
				image.setHeight(0);
				image.setWidth(0);
			}
		}
	}

	public String getImageURLSmall() {
		return images == null ? null : images.getSmallURL();
	}

	public String getImageURLLarge() {
		return images == null ? null : images.getLargeURL();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();  // shallow copy OK
	}

	@Override
	public String toString() {
		return artist + " | " + album + " | " + title + ": genre = " + genre + ", duration = " + duration + ", year = " + year +
			", tracknum = " + trackNum + "/" + trackCount + ", discnum = " + posIndex + "/" + posCount + ", artworkUrl = " + getArtworkUrl();
	}
}
