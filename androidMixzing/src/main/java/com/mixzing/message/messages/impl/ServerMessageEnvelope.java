package com.mixzing.message.messages.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mixzing.message.messages.ServerMessage;



//@XmlRootElement
public class ServerMessageEnvelope  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//@XmlElement
    public List<ServerMessage> getMessages() {
        return messages;
    }

	public ServerMessageEnvelope() {

    }

    /*
     * Library id copied from the client request. Included in the protocol for debugging
     * purpose to ensure that the server is not mixing up the clients.
     *
     */
    private String library_id;

    /*
     * Zero or more ServerMessages to be sent to the client. If there is no response available
     * Server could respond with a PingMe message with a time delay in it.
     *
     */
    
    private List<ServerMessage> messages;
    private ServerParameters server_params;

    
    //@XmlElement
    public ServerParameters getServer_params() {
        return server_params;
    }
    
    public void setServer_params(ServerParameters p) {
        server_params = p;
    }
    
    //@XmlAttribute(name = "lib_id")
    public String getLib_id() {
        return library_id;
    }
    public void setLib_id(String library_id) {
        this.library_id = library_id;
    }
    
    public void setMessages(List<ServerMessage> messages) {
        this.messages = messages;
    }

    private ServerParametersAndroid server_params_android;
    
	public ServerParametersAndroid getServer_params_android() {
		return server_params_android;
	}

	public void setServer_params_android(
			ServerParametersAndroid server_params_android) {
		this.server_params_android = server_params_android;
	}
	
    public ServerMessageEnvelope(JSONObject json) throws JSONException {
    	this.messages = new ArrayList<ServerMessage>();
    	library_id = json.getString("lib_id");
    	
    	JSONObject params = null;
    	try {
    		params = json.getJSONObject("server_params");
    	} catch (JSONException e) {
    		// No params should not be an error
    	}
    	
    	if(params != null) {
    		server_params_android = new ServerParametersAndroid(params);
    	}
    	
    	JSONArray srvrmsgs = json.getJSONArray("messages");
    	
    	
    	for(int i=0;i<srvrmsgs.length();i++) {
    		JSONObject srvrmsg = srvrmsgs.getJSONObject(i);
    		ServerMessage msg = null;
    		int mtype = srvrmsg.getInt(JSONMap.JSON_TYPE);
    		switch(mtype) {
    			case JSONMap.ServerFileResponse:
    				//msg = new ServerFileResponse(srvrmsg);
    				break;
    			case JSONMap.ServerGenreBasisVectors:
    				//msg = new ServerGenreBasisVectors(srvrmsg);
    				break;
    			case JSONMap.ServerNewLibraryResponse:
    				msg = new ServerNewLibraryResponse(srvrmsg);
    				break;
    			case JSONMap.ServerPingMe:
    				msg = new ServerPingMe(srvrmsg);
    				break;
    			case JSONMap.ServerRecommendations:
    				msg = new ServerRecommendations(srvrmsg);
    				break;
    			case JSONMap.ServerRequestSignature:
    				//msg = new ServerRequestSignature(srvrmsg);
    				break;
    			case JSONMap.ServerResponseDelayed:
    				msg = new ServerResponseDelayed(srvrmsg);
    				break;    				
    			case JSONMap.ServerTagResponse:
    				// msg = new ServerTagResponse(srvrmsg);
    				break;
    			case JSONMap.ServerTrackEquivalence:
    				// msg = new ServerTrackEquivalence(srvrmsg);
    				break;
    			case JSONMap.ServerTrackMapping:
    				msg = new ServerTrackMapping(srvrmsg);
    				break;
    			default:
    				break;
    				
    		}
    	
    		messages.add(msg);
    	}
    	
    }

}
