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
public class ClientPing implements ClientMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientPing () {

	}

	public void toJson(JSONStringer stringer) throws JSONException  {
		stringer.object();

    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientPing);


		stringer.endObject();
	}

    public ClientPing(JSONObject json) throws JSONException {
    	
    }
}
