package com.mixzing.decoder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mixzing.log.Logger;

public class TagSong implements Comparable<TagSong>{
	private static final Logger log = Logger.getRootLogger();
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getTrackNum() {
		return trackNum;
	}

	public void setTrackNum(String trackNum) {
		this.trackNum = trackNum;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getAlbumArtUrl() {
		return albumArtUrl;
	}

	public void setAlbumArtUrl(String albumArtUrl) {
		this.albumArtUrl = albumArtUrl;
	}

	public double getAlbumScore() {
		return albumScore;
	}

	public void setAlbumScore(double albumScore) {
		this.albumScore = albumScore;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getAlbumIndex() {
		return albumIndex;
	}

	public void setAlbumIndex(int albumIndex) {
		this.albumIndex = albumIndex;
	}

	public ArrayList<Image> getImages() {
		return images;
	}

	public void setImages(ArrayList<Image> images) {
		this.images = images;
	}

	protected String title;
	
	protected String artist;
	
	protected String album;
	
	protected String trackNum;
	
	protected String genre;
	
	protected String albumArtUrl;
	
	protected double albumScore;
	
	protected int duration;
	
	protected int albumIndex;
	
	protected ArrayList<Image> images = new ArrayList<Image>();
	

	protected class Image {

		/*
		 * 
		 *    'albumarts' => [
	                              {
	                                'url' => 'http://image.listen.com/img/70x70/3/9/3/0/2100393_70x70.jpg',
	                                'type' => 70
	                               },
	                               {
	                                 'url' => 'http://image.listen.com/img/170x170/3/9/3/0/2100393_170x170.jpg',
	                                  'type' => 170
	                               },
	                               {
	                                 'url' => 'http://image.listen.com/img/500x500/3/9/3/0/2100393_500x500.jpg',
	                                 'type' => 500
	                                }
	                           ]
		 */
		public Image(JSONObject j) {
			try {
				size = j.getInt("size");
				url = j.getString("url");
			} catch (JSONException e) {
				
			}
		}
		int size;
		String url;
		
		public String toString() {
			return size + ":" + url;
		}
	}

	
	public TagSong(JSONObject tags) {
		try {
			albumScore = tags.getDouble("albumscore");
			title = tags.getString("title");
			artist = tags.getString("artist");
			album = tags.getString("album");
			duration = tags.getInt("duration");
			albumIndex = tags.getInt("albumindex");
			JSONArray imgs = tags.getJSONArray("albumarts");
			if(imgs != null) {
				int len = imgs.length();
				for(int i=0;i<len;i++) {
					JSONObject j = imgs.getJSONObject(i);
					Image img = new Image(j);
					images.add(img);
				}
			}
		}
		catch (JSONException e) {
			log.error("TagSong.ctor: malformed json: " + tags + ":", e);
		}
		catch (Exception e) {
			log.error("TagSong.ctor: " + tags + ":", e);
		}
	}
	
	public String toString() {
		return title + "|" + artist + "|" + album + "|" + albumScore + "|" + (images.size() > 0 ? images.get(0) : "no image");
	}

	public int compareTo(TagSong another) {
		return albumScore <= another.albumScore ?  1 : -1;
	}
}
