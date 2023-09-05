package com.mixzing.decoder;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TagQueryResponse {

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getOnebitScore() {
		return onebitScore;
	}

	public void setOnebitScore(int onebitScore) {
		this.onebitScore = onebitScore;
	}

	public ArrayList<TagSong> getArtSongs() {
		return artSongs;
	}

	public void setArtSongs(ArrayList<TagSong> artSongs) {
		this.artSongs = artSongs;
	}

	protected boolean found;

	protected int score;
	
	protected int onebitScore;
	
	protected ArrayList<TagSong> artSongs = new ArrayList<TagSong>();
	
	public TagQueryResponse(JSONObject jobj) throws JSONException {
		score = jobj.getInt("score");
		int ifound = jobj.getInt("found");
		if(ifound != 0) {
			found = true;
		} 
		onebitScore = jobj.getInt("onebitmatch");
		
		JSONArray artsongs = jobj.getJSONArray("artsongs");
		if(artsongs != null) {
			int len = artsongs.length();
			for(int i=0;i<len;i++) {
				JSONObject tags = artsongs.getJSONObject(i);
				TagSong artsong = new TagSong(tags);
				artSongs.add(artsong);
			}
		}
		
		if(artSongs.size() > 1) {
			Collections.sort(artSongs);
		}
	}
	
	public String toString() {
		return score + "|" + onebitScore + "|" + ((found && artSongs.size() > 0) ? artSongs.toString() : "null");
	}
}
