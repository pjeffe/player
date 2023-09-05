package com.mixzing.message.messages.impl;

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
public class ClientNewLibrary implements ClientMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientNewLibrary () {
		// empty no arg constructor
	}

	public void toJson(JSONStringer stringer) throws JSONException  {
		stringer.object();

    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientNewLibrary);


		stringer.endObject();
	}

    public ClientNewLibrary(JSONObject json) throws JSONException {
    	
    }
}
