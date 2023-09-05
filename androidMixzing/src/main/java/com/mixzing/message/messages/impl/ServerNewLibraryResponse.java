package com.mixzing.message.messages.impl;

import org.json.JSONException;
import org.json.JSONObject;

import com.mixzing.message.messages.ServerMessage;
import com.mixzing.message.messages.ServerMessageEnum;
//@XmlRootElement
public class ServerNewLibraryResponse implements ServerMessage {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerNewLibraryResponse() {

    }

    /*
     * Server responds to a new library request with a unique library id.
     *
     */
    private String library_id;
    private String user_id;

    //@XmlAttribute(name = "library_id")
    public String getLibrary_id() {
        return library_id;
    }

    public void setLibrary_id(String library_id) {
        this.library_id = library_id;
    }

    //@XmlAttribute(name = "user_id")
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    
    public String getType() {
        return ServerMessageEnum.NEW_LIBRARY.toString();
    }

    public void setType(String s) {

    }
    
    public ServerNewLibraryResponse(JSONObject json) throws JSONException {    	
    	library_id = json.getString("library_id");
//    	user_id = json.getString("user_id");
    }
}
