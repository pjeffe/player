package com.mixzing.message.messageobject.impl;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * <p>Title: MixMoxie Java Client</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: MixMoxie</p>
 * @author G.Miller S Mathur.
 * @version 1.0
 */

public class TrackSignature  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TrackSignature() {

    }    
    
    private long lsid;
    
    private String signature;
    
    private int skip;
    
    private int duration;
    
    private int version;
    
    private String audioFileData;

    //@XmlAttribute
	public long getLsid() {
        return lsid;
    }
    public void setLsid(long lsid) {
        this.lsid = lsid;
    }
    //@XmlAttribute
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }

    //@XmlAttribute
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	//@XmlAttribute
	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}

	//@XmlAttribute
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	//@XmlAttribute
	public String getAudioFileData() {
		return audioFileData;
	}

	public void setAudioFileData(String audioFileData) {
		this.audioFileData = audioFileData;
	}
	

    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
   
    	stringer.key("lsid");
    	stringer.value(lsid);
    	
    	stringer.key("signature");
    	stringer.value(signature);
    	
       	stringer.key("skip");
    	stringer.value(skip);
    	
    	stringer.key("duration");
    	stringer.value(duration);

    	stringer.key("version");
    	stringer.value(version);
    	
    	stringer.key("audioFileData");
    	stringer.value(audioFileData);

    	stringer.endObject();
    }
    
    public TrackSignature(JSONObject json) throws JSONException {
    	lsid = json.getLong("lsid");
    	signature = json.getString("signature");
    	skip = json.getInt("skip");
    	duration = json.getInt("duration");
    	audioFileData= json.getString("audioFileData");
    }
}
