package com.mixzing.musicobject.impl;

import java.util.ArrayList;

import com.mixzing.musicobject.TrackEquivalence;
import com.mixzing.musicobject.TrackEquivalenceSongs;
import com.mixzing.musicobject.dto.impl.TrackEquivalenceDTOImpl;

public class TrackEquivalenceImpl extends TrackEquivalenceDTOImpl implements
		TrackEquivalence {

	ArrayList<TrackEquivalenceSongs> songs = new ArrayList<TrackEquivalenceSongs>();
	public TrackEquivalenceImpl() {

	}

	public ArrayList<TrackEquivalenceSongs> getTrackEquivalenceSongs() {
		return songs;
	}

	public void setTrackEquivalenceSongs(ArrayList<TrackEquivalenceSongs> songs) {
		this.songs = songs;
	}

}
