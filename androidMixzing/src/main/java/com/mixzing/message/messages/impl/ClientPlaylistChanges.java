package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.message.messageobject.impl.ClientPlaylist;
import com.mixzing.message.messages.ClientMessage;

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
//@XmlRootElement
public class ClientPlaylistChanges implements ClientMessage {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ClientPlaylistChanges() {
        added = new ArrayList<ClientPlaylist>();
        deleted = new ArrayList<Long>();
    }

    public void setAdded(List<ClientPlaylist> added) {
        this.added = added;
    }

    public void setDeleted(List<Long> deleted) {
        this.deleted = deleted;
    }

    public void addPlaylist (ClientPlaylist pl) {
        added.add(pl);
    }

    public void deletePlaylist (ClientPlaylist pl) {
        deleted.add(pl.getPlid());
    }    
    
    //@XmlElement
    public List<ClientPlaylist> getAdded() {
        return added;
    }

    //@XmlElement
    public List<Long> getDeleted() {
        return deleted;
    }

    
    private List<ClientPlaylist> added;
    private List<Long> deleted;
    
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	
    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientPlaylistChanges);
    	
    	stringer.key("added");
    	stringer.array();
    	for(ClientPlaylist tr : added) {
    		tr.toJson(stringer);
    	}
    	stringer.endArray();

    	stringer.key("deleted");
    	stringer.array();
    	for(Long del : deleted) {
    		stringer.value(del);
    	}
    	stringer.endArray();
    	
    	stringer.endObject();
    }
    
    public ClientPlaylistChanges(JSONObject json) throws JSONException {
        added = new ArrayList<ClientPlaylist>();
        deleted = new ArrayList<Long>();
    	
    	JSONArray jar = json.getJSONArray("added");
    	for(int i=0;i<jar.length();i++) {
    		ClientPlaylist tr = new ClientPlaylist(jar.getJSONObject(i));
    		added.add(tr);
    	}
 
    	jar = json.getJSONArray("deleted");
    	for(int i=0;i<jar.length();i++) {
    		Long tr = jar.getLong(i);
    		deleted.add(tr);
    	}

    	
    }    

}
