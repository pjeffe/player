package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.message.messageobject.impl.TrackRating;
import com.mixzing.message.messages.ClientMessage;

/**
 * TODO: To be defined and implemented
 * @author sandeep
 *
 */
//@XmlRootElement
public class ClientRatings implements ClientMessage {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<TrackRating> rating_vector;
    public ClientRatings () {
        this.rating_vector = new ArrayList<TrackRating>();
    }

    public void setRating_vector(List<TrackRating> rating_vector) {
        this.rating_vector = rating_vector;
    }

    public void addRating(TrackRating tr) {
        rating_vector.add(tr);
    }
    //@XmlElement(name = "rating_vector")
    public List<TrackRating> getRating_vector() {
        return rating_vector;
    }


    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	
    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientRatings);
    	
    	stringer.key("rating_vector");
    	stringer.array();
    	for(TrackRating tr : rating_vector) {
    		tr.toJson(stringer);
    	}
    	stringer.endArray();
    	
    	stringer.endObject();
    }
    
    
    public ClientRatings(JSONObject json) throws JSONException {
    	this.rating_vector = new ArrayList<TrackRating>();
    	
    	JSONArray jar = json.getJSONArray("rating_vector");
    	for(int i=0;i<jar.length();i++) {
    		TrackRating tr = new TrackRating(jar.getJSONObject(i));
    		rating_vector.add(tr);
    	}
    	
    	
    }
}
