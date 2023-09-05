package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.message.messages.ClientMessage;
import com.mixzing.musicobject.Track;

/**
 * TODO: To be defined and implemented
 * @author sandeep
 *
 */
//@XmlRootElement
public class ClientTagRequest implements ClientMessage {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Long> gsids;

    public ClientTagRequest() {

    }

    public void setGsids(List<Long> gsids) {
        this.gsids = gsids;
    }

    public void addTrack (Track tr) {
        this.gsids.add(Long.valueOf(tr.getId()));
    }

    //@XmlElement
    public List<Long> getGsids() {
        return gsids;
    }
    
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	
    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientTagRequest);

    	stringer.key("gsids");
    	stringer.array();
    	for(Long tr : gsids) {
    		stringer.value(tr);
    	}
    	stringer.endArray();
    	
    	stringer.endObject();
    }
    
    public ClientTagRequest(JSONObject json) throws JSONException {
    	this.gsids = new ArrayList<Long>();
    	
    	JSONArray jar = json.getJSONArray("gsids");
    	for(int i=0;i<jar.length();i++) {
    		Long tr = jar.getLong(i);
    		gsids.add(tr);
    	}
    	
    }   
}
