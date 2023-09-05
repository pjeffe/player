package com.mixzing.musicobject.dao;

import com.mixzing.musicobject.dao.impl.AndroidPackageDAOImpl;
import com.mixzing.musicobject.dao.impl.CleanupResourcesDAOImpl;
import com.mixzing.musicobject.dao.impl.GlobalSongDAOImpl;
import com.mixzing.musicobject.dao.impl.GlobalSongSourceDAOImpl;
import com.mixzing.musicobject.dao.impl.LibraryDAOImpl;
import com.mixzing.musicobject.dao.impl.OutboundMsgQDAOImpl;
import com.mixzing.musicobject.dao.impl.PlaylistDAOImpl;
import com.mixzing.musicobject.dao.impl.PlaylistTrackDAOImpl;
import com.mixzing.musicobject.dao.impl.RatingArtistDAOImpl;
import com.mixzing.musicobject.dao.impl.RatingSongDAOImpl;
import com.mixzing.musicobject.dao.impl.RecoAlternateDAOImpl;
import com.mixzing.musicobject.dao.impl.RecommendationDAOImpl;
import com.mixzing.musicobject.dao.impl.SignatureRequestDAOImpl;
import com.mixzing.musicobject.dao.impl.TrackDAOImpl;
import com.mixzing.musicobject.dao.impl.TrackSignatureValueDAOImpl;
import com.mixzing.musicobject.dao.impl.VideoDAOImpl;
import com.mixzing.musicobject.dao.impl.WishlistDAOImpl;

public class DAOFactory {

	
	public static GlobalSongDAO createGlobalSongDAO(GlobalSongSourceDAO gssDAO) {
		return new GlobalSongDAOImpl(gssDAO);
	}

	public static GlobalSongSourceDAO createGlobalSongSourceDAO() {
		return new GlobalSongSourceDAOImpl();
	}

	public static OutboundMsgQDAO createOutboundMsgQDAO() {
		return new OutboundMsgQDAOImpl();
	}

	public static PlaylistDAO createPlaylistDAO() {
		return new PlaylistDAOImpl();
	}
	
	public static PlaylistTrackDAO createPlaylistTrackDAO() {
		return new PlaylistTrackDAOImpl();
	}
	
	public static RatingArtistDAO createRatingArtistDAO() {
		return new RatingArtistDAOImpl();
	}
	
	public static RatingSongDAO createRatingSongDAO() {
		return new RatingSongDAOImpl();
	}
	
	public static RecoAlternateDAO createRecoAlternateDAO() {
		return new RecoAlternateDAOImpl();
	}
	
	public static RecommendationDAO createRecommendationDAO() {
		return new RecommendationDAOImpl();
	}
	
	public static SignatureRequestDAO createSignatureRequestDAO() {
		return new SignatureRequestDAOImpl();
	}
	
	public static TrackDAO createTrackDAO() {
		return new TrackDAOImpl();
	}
	
	public static WishlistDAO createWishlistDAO() {
		return new WishlistDAOImpl();
	}

	public static LibraryDAO createLibraryDAO() {
		return new LibraryDAOImpl();
	}

    public static TrackSignatureValueDAO createTrackSignatureValueDAO() {
        return new TrackSignatureValueDAOImpl();
    }
    
    public static CleanupResourceDAO createCleanupResourceDAO() {
    	return new CleanupResourcesDAOImpl();
    }

	public static AndroidPackageDAO createPackageDAO() {
		return new AndroidPackageDAOImpl();
	}

	public static VideoDAO createVideoDAO() {
		return new VideoDAOImpl();
	}
}
