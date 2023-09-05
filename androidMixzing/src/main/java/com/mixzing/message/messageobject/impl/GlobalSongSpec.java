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
 *
 * @author G.Miller S Mathur.
 * @version 1.0
 */
public class GlobalSongSpec  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long gsid;
	
	private long lsid;
	
	private String audition_url;
	
	private String purchase_url;
	
	private MPXTags mpx_info;
    public GlobalSongSpec() {
    }

    public void setGsid(long gsid) {
        this.gsid = gsid;
    }

    public void setLsid(Long lsid) {
        if (lsid == null) {
            this.lsid = -1;
        } else {
            this.lsid = lsid.longValue();
        }
    }

    public void setLsid(long lsid) {
            this.lsid = lsid;        
    }
    
    public void setAudition_url(String audition_url) {
        this.audition_url = audition_url;
    }

    public void setPurchase_url(String purchase_url) {
        this.purchase_url = purchase_url;
    }

    public void setMpx_info(MPXTags mpx_info) {
        this.mpx_info = mpx_info;
    }

    //@XmlAttribute
    public long getGsid() {
        return gsid;
    }

    //@XmlAttribute(name = "lsid")
    public long getLsid() {
        return lsid;
    }

    //@XmlAttribute(name = "audition_url")
    public String getAudition_url() {
        return audition_url;
    }
    //@XmlAttribute(name = "purchase_url")
    public String getPurchase_url() {
        return purchase_url;
    }

    //@XmlElement(name = "mpx_info")
    public MPXTags getMpx_info() {
        return mpx_info;
    }
    
    public GlobalSongSpec(JSONObject json) throws JSONException {
    	
    	purchase_url = json.optString("purchase_url");
    	audition_url = json.optString("audition_url");
    	gsid = json.getLong("gsid");

    	/* 
    	 * Either lsid or the mpxinfo will exist
    	 */
    	
    	JSONObject mpxi = null;
    	try {
    		mpxi = json.getJSONObject("mpx_info");
    	} catch (Exception e) {
    		
    	}
    	if(mpxi != null) {
    		mpx_info = new MPXTags(mpxi);	
    	} else {
    		lsid = json.getLong("lsid");	
    	}
    	    	    	
    }
    
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	stringer.key("audition_url");
    	stringer.value(audition_url);
    	stringer.key("purchase_url");
    	stringer.value(purchase_url);
    	
    	stringer.key("lsid");
    	stringer.value(lsid);

    	stringer.key("gsid");
    	stringer.value(gsid);

    	
    	stringer.key("mpx_info");
    	mpx_info.toJson(stringer);
    	stringer.endObject();
    }
}
