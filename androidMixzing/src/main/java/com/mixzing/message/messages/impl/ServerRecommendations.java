package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mixzing.message.messageobject.impl.TrackRecommendation;
import com.mixzing.message.messages.ServerMessage;
import com.mixzing.message.messages.ServerMessageEnum;
//@XmlRootElement
public class ServerRecommendations implements ServerMessage {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<TrackRecommendation> recommendation_list;
	
	private HashMap<Long,List<TrackRecommendation>> server_recommendations;
    
    
    public ServerRecommendations() {
        this.recommendation_list = new ArrayList<TrackRecommendation>();
        this.server_recommendations = new HashMap<Long, List<TrackRecommendation>>();
    }

    public String getType() {
        return ServerMessageEnum.RECOMMENDATIONS.toString();
    }

    public void setType(String s) {

    }

    public void setServer_recommendations(HashMap<Long, List<TrackRecommendation>> server_recommendations) {
    	this.server_recommendations = server_recommendations;
        for(Long k : server_recommendations.keySet()) {
        	this.recommendation_list.addAll(server_recommendations.get(k));
        }
    }

    public void addServer_recommendation(Long plid, List<TrackRecommendation> recos) {
        this.server_recommendations.put(Long.valueOf(plid), recos);
    }

    public List<TrackRecommendation> findTrackRecommendations (Long plid) {
        return server_recommendations.get(Long.valueOf(plid));
    }

    //@XmlTransient
    public HashMap<Long, List<TrackRecommendation>> getServer_recommendations() {
    	if(server_recommendations.isEmpty()) {
    		populateRecos();
    	}
        return server_recommendations;
    }
    
    private void populateRecos() {
    	for(TrackRecommendation tr : recommendation_list) {
    		long plid = tr.getPlid();
    		List<TrackRecommendation> trp = server_recommendations.get(plid);
    		if(trp == null) {
    			trp = new ArrayList<TrackRecommendation>();
    			server_recommendations.put(plid, trp);
    		}
    		trp.add(tr);
    	}
	}

	//@XmlElement(name="recommendation_list")
    public List<TrackRecommendation> getRecommendationList() {
    	return recommendation_list;
    }

    public void setRecommendationList(List<TrackRecommendation> r) {
    	recommendation_list = r;
    	populateRecos();
    }
    
    public ServerRecommendations(JSONObject json) throws JSONException {
        this.recommendation_list = new ArrayList<TrackRecommendation>();
        this.server_recommendations = new HashMap<Long, List<TrackRecommendation>>();
        
    	JSONArray recos = json.getJSONArray("recommendation_list");
    	for(int i=0;i<recos.length();i++) {
    		TrackRecommendation tm = new TrackRecommendation(recos.getJSONObject(i));
    		recommendation_list.add(tm);
    	}
    	populateRecos();
    }
}
