package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mixzing.message.messageobject.impl.TrackSignatureRequest;
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
public class ServerRequestSignature implements ServerMessage {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<TrackSignatureRequest> Signature_requests;
    public ServerRequestSignature() {
    }

    public String getType() {
        return ServerMessageEnum.REQUEST_SIGNATURE.toString();
    }

    public void setType(String s) {

    }

    public void setSignature_requests(List<TrackSignatureRequest> Signature_requests) {
        this.Signature_requests = Signature_requests;
    }

    //@XmlElement(name = "signature_requests")
    public List<TrackSignatureRequest> getSignature_requests() {
        return Signature_requests;
    }
    
    
    public ServerRequestSignature(JSONObject json) throws JSONException {
        this.Signature_requests = new ArrayList<TrackSignatureRequest>();
        
    	JSONArray recos = json.getJSONArray("signature_requests");
    	for(int i=0;i<recos.length();i++) {
    		TrackSignatureRequest tm = new TrackSignatureRequest(recos.getJSONObject(i));
    		Signature_requests.add(tm);
    	}
    }
}
