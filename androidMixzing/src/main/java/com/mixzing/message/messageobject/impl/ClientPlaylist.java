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
public class ClientPlaylist implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long plid;
	
	private String name;

    public ClientPlaylist() {

    }

    public ClientPlaylist(com.mixzing.musicobject.Playlist play) {
        plid = play.getId();
        name = play.getName();
    }
    
    //@XmlAttribute(name="workaround_name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    //@XmlAttribute
    public long getPlid() {
        return plid;
    }
    public void setPlid(long plid) {
        this.plid = plid;
    }

    public ClientPlaylist(JSONObject play) throws JSONException {
        plid = play.getLong("plid");
        name = play.getString("name");
    }
    
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	stringer.key("plid");
    	stringer.value(plid);
    	stringer.key("name");
    	stringer.value(name);	
    	stringer.endObject();
    }
}
