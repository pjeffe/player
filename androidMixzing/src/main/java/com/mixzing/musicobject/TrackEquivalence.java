package com.mixzing.musicobject;

import java.util.ArrayList;

import com.mixzing.musicobject.dto.TrackEquivalenceDTO;

public interface TrackEquivalence extends TrackEquivalenceDTO {

	public ArrayList<TrackEquivalenceSongs> getTrackEquivalenceSongs();
	
	public void setTrackEquivalenceSongs(ArrayList<TrackEquivalenceSongs> songs);
	
}
