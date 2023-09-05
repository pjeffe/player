package com.mixzing.message.messages.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.message.messages.ClientMessage;

//@XmlRootElement
public class ClientRequestFile implements ClientMessage {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileName;
    
    public ClientRequestFile() {

    }

    //@XmlElement
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	
    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientRequestFile);

    	stringer.key("fileName");
    	stringer.value(fileName);
    	
    	stringer.endObject();
    }
    
    public ClientRequestFile(JSONObject json) throws JSONException {    	
    	fileName = json.getString("fileName");
    }
}

