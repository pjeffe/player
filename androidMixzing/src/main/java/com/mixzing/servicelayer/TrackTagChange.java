package com.mixzing.servicelayer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class TrackTagChange {

	public TrackTagChange(long seqno, long lsid, String fileLocation, String title,
			String album, String artist, String trackNumber, int releaseYear,
			String genre, float duration) {
		this.seqno = seqno;
		this.lsid = lsid;
		this.fileLocation = fileLocation;
		this.title = title;
		this.album  = album;
		this.artist = artist;
		this.trackNumber = trackNumber;
		this.releaseYear = releaseYear;
		this.genre = genre;
		this.duration = duration;
	}
	
	public long getSeqno() {
		return seqno;
	}

	public long getLsid() {
		return lsid;
	}

	public String getFileLocation() {
		return fileLocation;
	}


	public String getTitle() {
		return title;
	}


	public String getAlbum() {
		return album;
	}


	public String getArtist() {
		return artist;
	}


	public String getTrackNumber() {
		return trackNumber;
	}


	public int getReleaseYear() {
		return releaseYear;
	}


	public String getGenre() {
		return genre;
	}


	public float getDuration() {
		return duration;
	}

	protected long seqno;
	protected long lsid;
	protected String fileLocation;
	protected String title;
	protected String album; 
	protected String artist; 
	protected String trackNumber;
	protected int releaseYear;
	protected String genre; 
	protected float duration;
	
	public TrackTagChange(JSONObject json) throws JSONException {
		seqno = json.getLong("seqno");
		lsid = json.getLong("lsid");
		fileLocation = json.getString("fileLocation");
		title = json.getString("title");
		album = json.getString("album");
		artist = json.getString("artist");
		trackNumber = json.getString("trackNumber");
		releaseYear = json.getInt("releaseYear");
		duration = (float) json.getDouble("duration");
		genre = json.getString("genre");
	}


	public void toJson(JSONStringer stringer) throws JSONException  {
		stringer.object();
		stringer.key("seqno");
		stringer.value(seqno);
		stringer.key("lsid");
		stringer.value(lsid);
		stringer.key("fileLocation");
		stringer.value(fileLocation);
		stringer.key("title");
		stringer.value(title);
		stringer.key("album");
		stringer.value(album);
		stringer.key("artist");
		stringer.value(artist);
		stringer.key("trackNumber");
		stringer.value(trackNumber);
		stringer.key("releaseYear");
		stringer.value(releaseYear);
		stringer.key("duration");
		stringer.value(duration);
		stringer.key("genre");
		stringer.value(genre);
		stringer.endObject();
	}
	
}
