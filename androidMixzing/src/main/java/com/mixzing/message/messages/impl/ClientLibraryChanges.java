
package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.message.messageobject.impl.ClientTrack;
import com.mixzing.message.messages.ClientMessage;
//@XmlRootElement
public class ClientLibraryChanges  implements ClientMessage {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ClientLibraryChanges() {
        this.added = new ArrayList<ClientTrack>();
        this.deleted = new ArrayList<Long>();
    }

    public void setAdded(List<ClientTrack> added) {
        this.added = added;
    }

    public void setDeleted(List<Long> deleted) {
        this.deleted = deleted;
    }
    
    public void addTrack(ClientTrack track) {
        added.add(track);
    }

    public void deleteTrack(com.mixzing.musicobject.Track t) {
        deleted.add(t.getId());
    }
    
    //@XmlElement
    public List getAdded() {
        return added;
    }
    
    //@XmlElement
    public List<Long> getDeleted() {
        return deleted;
    }
    
	private List<ClientTrack> added;   
	private List<Long> deleted;
	
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	
    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientLibraryChanges);
    	
    	stringer.key("added");
    	stringer.array();
    	for(ClientTrack tr : added) {
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
    
    public ClientLibraryChanges(JSONObject json) throws JSONException {
        this.added = new ArrayList<ClientTrack>();
        this.deleted = new ArrayList<Long>();
    	
    	JSONArray jar = json.getJSONArray("added");
    	for(int i=0;i<jar.length();i++) {
    		ClientTrack tr = new ClientTrack(jar.getJSONObject(i));
    		added.add(tr);
    	}
 
    	jar = json.getJSONArray("deleted");
    	for(int i=0;i<jar.length();i++) {
    		Long tr = jar.getLong(i);
    		deleted.add(tr);
    	}

    	
    }
}
