package com.mixzing.message.messageobject.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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

public class TrackRecommendation  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<GlobalSongSpec> alternates;
    
    private String as_artist;
    
    private String as_title;
    
    private float score;
    
    private long asid;
    
    private long plid;

    private String genre;
    
    public TrackRecommendation() {
        alternates = new ArrayList<GlobalSongSpec>();
    }

    public void setAlternates(List<GlobalSongSpec> alternates) {
        this.alternates = alternates;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setAs_title(String as_title) {
        this.as_title = as_title;
    }

    public void setAs_artist(String as_artist) {
        this.as_artist = as_artist;
    }

    public void setAsid(long asid) {
        this.asid = asid;
    }
    //@XmlElement
    public List<GlobalSongSpec> getAlternates() {
        return alternates;
    }
    //@XmlAttribute
    public float getScore() {
        return score;
    }
    //@XmlAttribute(name = "as_title")
    public String getAs_title() {
        return as_title;
    }
    //@XmlAttribute(name = "as_artist")
    public String getAs_artist() {
        return as_artist;
    }
    //@XmlAttribute
    public long getAsid() {
        return asid;
    }

    //@XmlAttribute
	public long getPlid() {
		return plid;
	}
    
    //@XmlAttribute(name = "genre")
    public String getGenre() {
        return genre;
    }
    
	public void setPlid(long plid) {
		this.plid = plid;
	}
	public void setGenre(String g) {
		this.genre = g;
	}
	
    public TrackRecommendation(JSONObject json) throws JSONException {
    	
    	alternates = new ArrayList<GlobalSongSpec>();
    	
    	
    	as_title = json.optString("as_title");
    	as_artist = json.optString("as_artist");
    	genre = json.optString("genre");
    	asid = json.getLong("asid");
    	plid = json.getLong("plid");
    	score = (float) json.getDouble("score");
    	
    	
    	JSONArray jar = json.getJSONArray("alternates");
    	for(int i=0;i<jar.length();i++) {
    		GlobalSongSpec tr = new GlobalSongSpec(jar.getJSONObject(i));
    		alternates.add(tr);
    	}   	    	
    }
    
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	stringer.key("as_title");
    	stringer.value(as_title);
    	stringer.key("as_artist");
    	stringer.value(as_artist);
    	stringer.key("asid");
    	stringer.value(asid);
    	stringer.key("plid");
    	stringer.value(plid);
    	stringer.key("score");
    	stringer.value(score);
       	stringer.key("genre");
    	stringer.value(genre);
       	stringer.key("alternates");
    	stringer.array();
    	for(GlobalSongSpec tr : alternates) {
    		tr.toJson(stringer);
    	}
    	stringer.endArray();  	
     	stringer.endObject();
    }
}
