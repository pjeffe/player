package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.message.messages.ClientMessage;

/**
 * TODO: To be defined and implemented
 * @author sandeep
 *
 */
//@XmlRootElement
public class ClientRequestRecommendations implements ClientMessage {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Long> plids;
    public ClientRequestRecommendations() {
        this.plids = new ArrayList<Long>();
    }

    public void setPlids(List<Long> plids) {
        this.plids = plids;
    }

    ////@XmlElement
    public List<Long> getPlids() {
        return plids;
    }
    
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	
    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientRequestRecommendations);

    	stringer.key("plids");
    	stringer.array();
    	for(Long tr : plids) {
    		stringer.value(tr);
    	}
    	stringer.endArray();
    	
    	stringer.endObject();
    }
    
    public ClientRequestRecommendations(JSONObject json) throws JSONException {
    	this.plids = new ArrayList<Long>();
    	
    	JSONArray jar = json.getJSONArray("plids");
    	for(int i=0;i<jar.length();i++) {
    		Long tr = jar.getLong(i);
    		plids.add(tr);
    	}
    	
    }    
}
