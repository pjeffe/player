package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mixzing.message.messageobject.impl.TrackMapping;
import com.mixzing.message.messages.ServerMessage;
import com.mixzing.message.messages.ServerMessageEnum;
//@XmlRootElement
public class ServerTrackMapping implements ServerMessage{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerTrackMapping() {
        this.mapped = new ArrayList<TrackMapping>();
    }

    private List<TrackMapping> mapped;

    public String getType() {
        return ServerMessageEnum.TRACK_MAPPING.toString();
    }

    public void setType(String s) {

    }

    //@XmlElement
    public List<TrackMapping> getMapped() {
        return mapped;
    }

    public void setMapped(List<TrackMapping> mapped) {
        this.mapped = mapped;
    }

    public void addTrackMapping(TrackMapping tm) {
        mapped.add(tm);
    }
    
    public ServerTrackMapping(JSONObject json) throws JSONException {
    	this.mapped = new ArrayList<TrackMapping>();
    	JSONArray mappings = json.getJSONArray("mapped");
    	for(int i=0;i<mappings.length();i++) {
    		TrackMapping tm = new TrackMapping(mappings.getJSONObject(i));
    		mapped.add(tm);
    	}
    }
}
