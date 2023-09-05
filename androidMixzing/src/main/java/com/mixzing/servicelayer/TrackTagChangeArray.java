package com.mixzing.servicelayer;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class TrackTagChangeArray {

	protected ArrayList<TrackTagChange> tagTracks;

	public ArrayList<TrackTagChange> getTagTracks() {
		return tagTracks;
	}

	public TrackTagChangeArray() {
		tagTracks = new ArrayList<TrackTagChange>();
	}
	public void toJson(JSONStringer stringer) throws JSONException  {
		stringer.object();    	
		stringer.key("tagTracks");
		stringer.array();
		for(TrackTagChange tr : tagTracks) {
			tr.toJson(stringer);
		}
		stringer.endArray();
		stringer.endObject();
	}

	public TrackTagChangeArray(JSONObject json) throws JSONException {
		tagTracks = new ArrayList<TrackTagChange>();

		try {
			JSONArray jar = json.getJSONArray("tagTracks");
			if(jar != null) {
				for(int i=0;i<jar.length();i++) {
					TrackTagChange tr = new TrackTagChange(jar.getJSONObject(i));
					tagTracks.add(tr);
				}
			}
		} catch (Exception e) {
		}
	}
}
