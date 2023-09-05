package com.mixzing.message.messages.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.message.messages.ClientMessage;

/**
 * <p>Title: MixMoxie Java Client</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: MixMoxie</p>
 *
 * @author G.Miller S Mathur.
 * @version 1.0
 */
//@XmlRootElement
public class ClientRequestDefaultRecommendations implements ClientMessage {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientRequestDefaultRecommendations() {
    }
	
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	
    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientRequestDefaultRecommendations);
    	
    	stringer.endObject();
    }
    
    public ClientRequestDefaultRecommendations(JSONObject json) throws JSONException {    	
    	
    }
}
