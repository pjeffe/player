package com.mixzing.message.messages;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONStringer;

public interface ClientMessage extends Serializable {

    /*
     * A marker interface to be implemented by all client messages
     */
	
	public void toJson(JSONStringer stringer) throws JSONException;
	
}
