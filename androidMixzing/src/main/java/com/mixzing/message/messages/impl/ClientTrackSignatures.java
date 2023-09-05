package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.message.messageobject.impl.TrackSignature;
import com.mixzing.message.messages.ClientMessage;
//@XmlRootElement
public class ClientTrackSignatures  implements ClientMessage{

    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<TrackSignature> signatures;

    public ClientTrackSignatures() {
        signatures = new ArrayList<TrackSignature>();
    }

    public ClientTrackSignatures(List<TrackSignature> sig) {
        signatures = sig;
    }

    //@XmlElement
    public List<TrackSignature> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<TrackSignature> signatures) {
        this.signatures = signatures;
    }

    public void addTrackSignature (TrackSignature ts) {
        signatures.add(ts);
    }
    
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	
    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientTrackSignatures);

    	stringer.key("signatures");
    	stringer.array();
    	for(TrackSignature tr : signatures) {
    		tr.toJson(stringer);
    	}
    	stringer.endArray();
    	
    	stringer.endObject();
    }
    
    public ClientTrackSignatures(JSONObject json) throws JSONException {
    	this.signatures = new ArrayList<TrackSignature>();
    	
    	JSONArray jar = json.getJSONArray("signatures");
    	for(int i=0;i<jar.length();i++) {
    		TrackSignature tr = new TrackSignature(jar.getJSONObject(i));
    		signatures.add(tr);
    	}
    	
    	
    }
    
}
