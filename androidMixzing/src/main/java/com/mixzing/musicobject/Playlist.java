package com.mixzing.musicobject;

import java.util.ArrayList;
import java.util.HashMap;

import com.mixmoxie.source.sourceobject.SourcePlaylist;
import com.mixmoxie.source.sourceobject.SourcePlaylist.Signature;
import com.mixzing.musicobject.dto.PlaylistDTO;

public interface Playlist extends PlaylistDTO {

	public ArrayList<RatingSong> getRatings();
	
	public void setRatings(ArrayList<RatingSong> ratings);
	
	public ArrayList<Wishlist> getWishList();
	
	public void setWishList(ArrayList<Wishlist> list);
	
	public ArrayList<Recommendation> getRecommendation();
	
	public void setRecommendation(ArrayList<Recommendation> recos);
	
	//public ArrayList<String> getIgnoredArtists();
	
	//public void setIgnoredArtists(ArrayList<String> artists);

	public void setIgnoredArtists(HashMap<String, String> name);
	
	public HashMap<String, String> getIgnoredArtists();
	
	public ArrayList<Track> getTracks();
	
	public void setTracks(ArrayList<Track> tracks);
	
	public boolean addRecommendation(Recommendation reco);
	
	public boolean deleteRecommendation(Recommendation reco);

	public Recommendation locateRecommendtion(long recoId);

	public Recommendation locateRecommendtionByAsid(long asid);
	
	public boolean hasNewerRecs(Long timestamp);
	
	public Signature getSourceSignature();
	
	public void setSourcePlaylist(SourcePlaylist play);
	
	public SourcePlaylist getSourcePlaylist();

	public void setPositiveRatedArtist(String posArtist);
	
	
}