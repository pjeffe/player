package com.mixzing.servicelayer;

import java.util.ArrayList;
import java.util.List;

import com.mixzing.message.messages.impl.ServerRecommendations;
import com.mixzing.musicobject.EnumPlaylistType;
import com.mixzing.musicobject.EnumRatingValue;
import com.mixzing.musicobject.Playlist;
import com.mixzing.musicobject.Recommendation;
import com.mixzing.musicobject.Track;

public interface PlaylistService {


	public Playlist addSourcePlaylist(Playlist play);
	
	public Playlist addMagicPlaylist(String name, EnumPlaylistType type);

	public void deleteSourcePlaylist(long plid);

	public void addPlaylistTrack(Playlist play, Track track);

	public void deletePlaylistTrack(Playlist play, Track track);

	public List<Playlist> getPlaylists();

	public Playlist getPlaylistById(long plid);

	public List<Playlist> getVirtualClientPlaylists();
	
	public void addVirtualClientPlaylist(String name, ArrayList<Recommendation> recos);
	
	public void processUserRating(long plid, EnumRatingValue value,
			long recoId, long recoAltId, boolean implied);
	
	public void addPositiveRating(long plid, long lsid);

	public void processRecommendations(ServerRecommendations recos);
	
	public void commitMagicPlaylist(long plid);

	public void deleteRating(long plid, long ratingId);

	public void updateWishlist(long plid, long wishlistId, WISHLIST_OPERATION b);
	
	public enum WISHLIST_OPERATION {
		ADDTOCART,
		REMOVEFROMCART,
		DELETE
	}

	public void updatePlaylistName(Playlist p);
	
	public void updatePlaylistType(Playlist p);
	
	public boolean areAllGsidsReceived();
	
	public Playlist getPlaylistBySourceId(String source);

	public void setSourceService(SourceService srcService);

	public void saveDeletedPlaylistForRestore(Playlist play);
}