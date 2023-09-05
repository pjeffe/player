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

public class ServerPingMe implements ServerMessage {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int Delay_time;
    public ServerPingMe() {
    }

    public void setDelay_time(int Delay_time) {
        this.Delay_time = Delay_time;
    }

    //@XmlAttribute(name="delay_time")
    public int getDelay_time() {
        return Delay_time;
    }
     
    public String getType() {
        return ServerMessageEnum.PING_ME.toString();
    }

    public void setType(String s) {

    }
    
    public ServerPingMe(JSONObject json) throws JSONException {    	
    	Delay_time = json.getInt("delay_time");
    }
    

}
