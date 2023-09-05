package com.mixzing.message.messageobject.impl;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;


/**
 * <p>Title: MixMoxie Java Client</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: MixMoxie</p>
 * @author G.Miller S Mathur.
 * @version 1.0
 */
public class MPXTags  implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String title;
	
	private String album;
	
	private float duration;
	
	private String artist;
	
	private String track_number;
	
	private String year;
	
	private String genre;

    public MPXTags() {
        // need this for YAML
    }

/*    public MPXTags(SourceTrack src) {
        duration = src.duration();
        title = src.getTag(SourceTrackTag.NAME);
        album = src.getTag(SourceTrackTag.ALBUM);
        artist = src.getTag(SourceTrackTag.ARTIST);
        track_number = src.getTag(SourceTrackTag.TRACKNUMBER);
        year = src.getTag(SourceTrackTag.YEAR);
        genre = src.getTag(SourceTrackTag.GENRE);
    }*/
    
    public MPXTags(float duration,
    		String title,		
    		String album,
    		String artist,
    		String trackNumber,
    		String year,
    		String genre) 
    {
    
    	this.duration = duration;
    	this.title = title;
    	this.album = album;
    	this.artist = artist;
    	this.track_number = trackNumber;
    	this.year = year;
    	this.genre = genre;
    }

    //@XmlAttribute
    public String getAlbum() {
        return album;
    }

    //@XmlAttribute
    public String getArtist() {
        return artist;
    }

    //@XmlAttribute
    public String getGenre() {
        return genre;
    }

    //@XmlAttribute(name = "track_number")
    public String getTrack_number() {
        return track_number;
    }

    //@XmlAttribute
    public String getTitle() {
        return title;
    }

    //@XmlAttribute
    public float getDuration() {
        return duration;
    }

    //@XmlAttribute
    public String getYear() {
        return year;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setTrack_number(String trackNumber) {
        this.track_number = trackNumber;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();

    	stringer.key("duration");
    	stringer.value(duration);
    	stringer.key("title");
    	stringer.value(title);

    	
    	stringer.key("album");
    	stringer.value(album);
    	stringer.key("artist");
    	stringer.value(artist);
    	
    	stringer.key("track_number");
    	stringer.value(track_number);
    	stringer.key("year");
    	stringer.value(year);
    	stringer.key("genre");
    	stringer.value(genre);
    	

    	stringer.endObject();
    }
    
    public MPXTags(JSONObject json) throws JSONException {
    	
      	try {
      		duration = (float) json.getDouble("duration");
      	} catch (JSONException e) {
      		// XXX : should we send a error message ?
      	}
    	
    	title = json.optString("title");

    	album = json.optString("album");
    	
    	artist = json.optString("artist");    	
    	
    	track_number = json.optString("track_number");
    	
    	year = json.optString("year");
    	
    	genre = json.optString("genre");
    	
    	    	    	
    }
    
}
