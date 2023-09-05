package com.mixzing.message.messages.impl;

import org.json.JSONException;
import org.json.JSONObject;

import com.mixzing.message.messages.ServerMessage;
import com.mixzing.message.messages.ServerMessageEnum;

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
public class ServerResponseDelayed implements ServerMessage {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long Retry_time;
    public ServerResponseDelayed() {
    }

    public void setRetry_time(long Retry_time) {
        this.Retry_time = Retry_time;
    }

    //@XmlAttribute(name = "retry_time")
    public long getRetry_time() {
        return Retry_time;
    }

    public String getType() {
        return ServerMessageEnum.RESPONSE_DELAYED.toString();
    }

    public void setType(String s) {

    }
    
    public ServerResponseDelayed(JSONObject json) throws JSONException {    	
    	Retry_time = json.getInt("retry_time");
    }
}
