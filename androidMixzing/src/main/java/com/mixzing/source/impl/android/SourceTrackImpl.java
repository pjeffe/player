package com.mixzing.source.impl.android;

import com.mixmoxie.source.sourceobject.SourceTrack;
import com.mixmoxie.source.sourceobject.SourceTrackId;
import com.mixmoxie.source.sourceobject.SourceTrackTag;


public class SourceTrackImpl implements SourceTrack {
	private SourceTrackId id;
	private String name;
	private String artist;
	private String album;
	private String genre;
	private String tracknum;
	private String year;
	private long createDate;
	private float duration;
	private String location;
	private int size;
	private boolean isCleared;
	
	public SourceTrackImpl(String id, String compositeId, String title,
			String artist, String album, String genre, String tracknum,
			String year, long createDate, int duration, String location,
			int size) {
		long lval = 0;
		try {
			lval = Long.valueOf(id);
		} catch (Exception e) {			
		}
		this.id = new SourceTrackId(lval,compositeId);
		this.name = title;
		this.artist = artist;
		this.album = album;
		this.genre = genre;
		this.tracknum = tracknum;
		this.year = year;
		this.createDate = createDate;
		this.duration = duration;
		this.location = location;
		this.size = size;
		this.isCleared = false;
	}

	public String name() {
		return name;
	}

	public String artist() {
		return artist;
	}

	public String album() {
		return album;
	}

	public long creationDate() {
		return createDate;
	}

	public String location() {
		return location;
	}

	public float duration() {
		return duration;
	}

	public int size() {
		return size;
	}

	public SourceTrackId id() {
		return id;
	}

	public String getTag(SourceTrackTag tag) {
		if (tag == SourceTrackTag.ARTIST) {
			return artist;
		}
		else if (tag == SourceTrackTag.ALBUM) {
			return album;
		}
		else if (tag == SourceTrackTag.GENRE) {
			return genre;
		}
		else if (tag == SourceTrackTag.LOCATION) {
			return location;
		}
		else if (tag == SourceTrackTag.NAME) {
			return name;
		}
		else if (tag == SourceTrackTag.PLAYCOUNT) {
			return "0";  // XXX
		}
		else if (tag == SourceTrackTag.RATING) {
			return "0";  // XXX
		}
		else if (tag == SourceTrackTag.TRACKNUMBER) {
			return tracknum;
		}
		else if (tag == SourceTrackTag.YEAR) {
			return year;
		}
		return null;
	}

	public void clearNonRetainedResources() {
		name = null;
		artist = null;
		album = null;
		genre = null;
		tracknum = null;
		year = null;
		location  = null;
		isCleared = true;
	}
	
	// stubs

	public void setLocation(String location) {
		assert(false);
	}

	public boolean isInPlaylist() {
		assert(false);
		return false;
	}

	public long getQuickKey() {
		assert(false);
		return 0;
	}

	public void setTag(SourceTrackTag tag, String val) {
		assert(false);
	}
	
	public boolean isCleared() {
		return isCleared;
	}
	
	public String toString() {
		return "track " + id.getCompositeId() + ": " + artist + " | " + name + " | " + album + " | " + genre +
			", tracknum " + tracknum + ", year " + year + ", create " + createDate + ", dur " + duration +
			", size " + size + ", loc " + location;
	}
}
