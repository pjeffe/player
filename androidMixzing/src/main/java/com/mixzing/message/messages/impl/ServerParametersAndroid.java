package com.mixzing.message.messages.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerParametersAndroid implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, String> otherAttributes;
    
    public ServerParametersAndroid() {
        super();
    }



    public void setOtherAttributes(Map<String, String> map) {
       otherAttributes = map;
    }
    
    public Map<String,String> getOtherAttributes() {
    	return otherAttributes;
    }
    
    public ServerParametersAndroid(JSONObject json) throws JSONException {    	
    	otherAttributes = new HashMap<String, String>();
    	Iterator<String> keys = json.keys();
    	while(keys.hasNext()) {
    		String key = keys.next();
    		otherAttributes.put(key, json.getString(key));
    	}
    }
}
