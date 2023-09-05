package com.mixzing.message.messageobject.impl;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

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
public class TrackSignatureRequest  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long Lsid;
	
	private long Is_long;
	
	private long Is_high_priority;
	
	private int skip;
	
	private int duration;
    
	private int super_win;
	
    public TrackSignatureRequest() {
    }

    public void setLsid(long Lsid) {
        this.Lsid = Lsid;
    }

    //@XmlAttribute
    public long getLsid() {
        return Lsid;
    }

    public void setIs_long(long Is_long) {
        this.Is_long = Is_long;
    }

    //@XmlAttribute(name = "is_long")
    public long getIs_long() {
        return Is_long;
    }

    
    public void setIs_high_priority(long Is_high_priority) {
        this.Is_high_priority = Is_high_priority;
    }

    //@XmlAttribute(name = "is_high_priority")
    public long getIs_high_priority() {
        return Is_high_priority;
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

	//@XmlAttribute(name = "super_win")
	public int getSuper_win() {
		return super_win;
	}

	public void setSuper_win(int super_win_ms) {
		this.super_win = super_win_ms;
	}
	
    public TrackSignatureRequest(JSONObject json) throws JSONException {
  
    }	
}
