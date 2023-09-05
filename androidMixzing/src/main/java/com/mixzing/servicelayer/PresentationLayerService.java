package com.mixzing.servicelayer;

import java.util.HashMap;
import java.util.List;

import com.mixzing.musicobject.EnumRatingValue;
import com.mixzing.musicobject.Playlist;
import com.mixzing.musicobject.RatingSong;
import com.mixzing.musicobject.Recommendation;
import com.mixzing.musicobject.Wishlist;

public interface PresentationLayerService {

	
	public HashMap<Playlist, List<Recommendation>> getRecommendations(long plid);
	
	public void processUserRating(long plid, EnumRatingValue value, long recoId, long recoAltId, boolean implied);

	public void processTagModified(String fileLocation, String title, String album, String artist, String trackNumber, int releaseYear, String genre, float duration);

	public void createVirtualPlaylist(long plid);

	
	public HashMap<Playlist, List<RatingSong>> getRatings();
	
	public void deleteRating(long plid, long ratingId);
	
	
	public HashMap<Playlist, List<Wishlist>> getWishlist();
	
	public void deleteFromShoppingCart(long plid, long wishlistId);

	public void addToShoppingCart(long plid, long wishlistId);
	
	public void deleteFromWishlist(long plid, long wishlistId);
	
		
	public void attachGui(String url, String handle, String iTunesLoc);
	
	public void detachGUI(String url);

	
	public void pauseSong(long globalSongId);

	public void playSong(long globalSongId);

	public void buySong(long id);	
	
	// public Library getLibraryStatus();
	
}
