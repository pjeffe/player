package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mixzing.message.messageobject.impl.TrackRecommendation;
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
public class ServerGenreBasisVectors implements ServerMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<TrackRecommendation> basis_vectors_list;
    private HashMap<String,List<TrackRecommendation>> basis_vectors;

    public ServerGenreBasisVectors() {
    	this.basis_vectors_list = new ArrayList<TrackRecommendation>();
        this.basis_vectors = new HashMap<String, List<TrackRecommendation>>();
    }

    public String getType() {
        return ServerMessageEnum.GENRE_BASIS_VECTORS.toString();
    }

    public void setType(String s) {

    }


    
    public void setBasis_vectors(HashMap<String,List<TrackRecommendation>> basis_vectors) {
        this.basis_vectors = basis_vectors;
        for(String k : basis_vectors.keySet()) {
        	this.basis_vectors_list.addAll(basis_vectors.get(k));
        }
    }

    public void addBasis_vector(String genre, List<TrackRecommendation> recos) {
        basis_vectors.put(genre, recos);
    }

    //@XmlTransient
    public HashMap<String,List<TrackRecommendation>> getBasis_vectors() {
    	if(basis_vectors.isEmpty()) {
    		populateRecos();
    	}
    	return basis_vectors;
    }
    
    private void populateRecos() {
    	for(TrackRecommendation tr : basis_vectors_list) {
    		String genre = tr.getGenre();
    		List<TrackRecommendation> trp = basis_vectors.get(genre);
    		if(trp == null) {
    			trp = new ArrayList<TrackRecommendation>();
    			basis_vectors.put(genre, trp);
    		}
    		trp.add(tr);
    	}
	}
    
	//@XmlElement(name="basis_vectors_list")
    public List<TrackRecommendation> getBasis_vectors_list() {
		return basis_vectors_list;
    }

    public void setBasis_vectors_list(List<TrackRecommendation> r) {
    	basis_vectors_list = r;
    	populateRecos();
    }
    
}
