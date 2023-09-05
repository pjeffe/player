package com.mixzing.musicobject.impl;

import java.lang.ref.SoftReference;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.mixmoxie.source.sourceobject.SourcePlaylist;
import com.mixmoxie.source.sourceobject.SourcePlaylist.Signature;
import com.mixmoxie.util.StackTrace;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.EnumPlaylistType;
import com.mixzing.musicobject.Playlist;
import com.mixzing.musicobject.RatingSong;
import com.mixzing.musicobject.Recommendation;
import com.mixzing.musicobject.Track;
import com.mixzing.musicobject.Wishlist;
import com.mixzing.musicobject.dto.impl.PlaylistDTOImpl;
import com.mixzing.servicelayer.RecommendationService;

public class PlaylistImpl extends PlaylistDTOImpl implements Playlist, Parcelable {

	protected static RecommendationService recoService;
	
	protected static Logger lgr = Logger.getRootLogger();
	
	private ArrayList<Wishlist> wishList = new ArrayList<Wishlist>() ;

	private ArrayList<RatingSong> songRatings = new ArrayList<RatingSong>();

	private ArrayList<Track> tracks = new ArrayList<Track>();
	
	private HashMap<String, String> ignoredArtists = new HashMap<String, String>();

	private SourcePlaylist splay;

	protected static final int MIN_ARTISTS_BEFORE_RECYCLE = 5;
	private RecentArtistsMap<String,Object> recentPositivesMap = new RecentArtistsMap<String, Object>(MIN_ARTISTS_BEFORE_RECYCLE);

	SoftReference<ArrayList<Recommendation>> recoRef;
	
	//protected static HashSet<Long> getCallCount = new HashSet<Long>();
	
	protected ArrayList<Recommendation> fetchOrLoadRecos() {
		ArrayList<Recommendation> rs = null;
		
		if(recoRef != null) {
			rs = recoRef.get();
		}
		
		if(rs == null) {
			if(Logger.IS_TRACE_ENABLED)
				lgr.debug("***** Possibly Finalized Recos for playlist, refetching from db " + id);
			rs = recoService.getAllRecommendations(this.getId());
			this.setRecommendation(rs);
		}
		
		return rs;
	}
	
	public static void setRecoService(RecommendationService rs) {
		recoService = rs;
	}

	
	protected class RecentArtistsMap<String,Object> extends LinkedHashMap<String,Object> {

	    protected int maxSize;
	    
	    public RecentArtistsMap(int size) {
	        super(size, 0.75f, true);
	        maxSize = size;
	    }

	    protected boolean removeEldestEntry(final Map.Entry eldest) {
	        return size() > maxSize;
	    }

	}
	
	private SourcePlaylist.Signature defaultSig = new SourcePlaylist.Signature() {

		
		
		public float getDuration() {
			return 0;
		}

		public int getSize() {
			return 0;
		}

		public int getTrackCount() {
			return 0;
		}
		
	};
	
	public PlaylistImpl() {
		super();

	}

	public PlaylistImpl(ResultSet rs) {
		super(rs);

	}

	// NOTE that we don't pass all fields to the client
	public PlaylistImpl(Parcel parcel) {
		id = parcel.readLong();
		name = parcel.readString();
		sourceSpecificId = parcel.readString();
		playlistType = EnumPlaylistType.valueOf(EnumPlaylistType.class, parcel.readString());
		isDeleted = parcel.createBooleanArray()[0];  // XXX correct?
	}

	public ArrayList<RatingSong> getRatings() {

		return songRatings;
	}

	public ArrayList<Recommendation> getRecommendation() {

/*		getCallCount.add(id);
		if(getCallCount.size() == 3) {
			try {
				Debug.dumpHprofData("/sdcard/hprof-data-" + System.currentTimeMillis() + ".hprof");
			} catch (IOException e) {
				Log.i("MixZing", "Unable to dump hprof - " + e);
			}
		}*/
		return fetchOrLoadRecos();
	}

	public ArrayList<Wishlist> getWishList() {

		return wishList;
	}

	public void setRatings(ArrayList<RatingSong> ratings) {
		songRatings = ratings;

	}

	protected class RecoSorter implements Comparator<Recommendation> {

		public int compare(Recommendation o1, Recommendation o2) {
			if(o1.getScore() > o2.getScore()) {
				return -1;
			}
			if(o1.getScore() < o2.getScore()) {
				return 1;
			}
			return 0;
		}
	}
	
	protected RecoSorter sorter = new RecoSorter();
	

	
	/*
	 * Index is the entry in the list that needs to be placed first in the list
	 */
	protected void bubbleUp(ArrayList<Recommendation> localRecos, int idx, int stepNum) {
		if(idx > 0 && idx > stepNum) {
			float topScore = localRecos.get(0).getScore();
			float newScore = topScore + NUM_OF_NEW_ARTISTS + 1 - stepNum;
			localRecos.get(idx).setScore(newScore);
			if(Logger.IS_TRACE_ENABLED) {
				lgr.trace("Playlist :" + this.name + ": Moving reco up to spot: " + 
						" " +  stepNum +
						" " + localRecos.get(idx).getArtsongArtist() +  
						" from spot: " + idx +  
						" " + localRecos.get(idx).getArtsongTitle() +
						" " + newScore + 
						" original song: " + localRecos.get(stepNum).getArtsongArtist() + 
						" " + localRecos.get(stepNum).getArtsongTitle()
						);
			}
		}	
	}
	
	protected void adjustScores(ArrayList<Recommendation> rawRecos, int numAdjust) {
		boolean hasLocal = false;
		ArrayList<Recommendation> localRecos = new ArrayList<Recommendation>();
		for(Recommendation r : rawRecos) {
			if(r.getAlternates().get(0).isLocal()) {
				localRecos.add(r);
				hasLocal = true;
			}
		}
		if(hasLocal) {
			Collections.sort(localRecos,sorter);
			int[] idx = new int[numAdjust];
			for(int i=0;i<numAdjust;i++) {
				idx[i]=-1;
			}
			int numFound = 0;
			for(int i=0;i<localRecos.size();i++) {
				String artist = localRecos.get(i).getArtsongArtist();
				boolean found = recentPositivesMap.containsKey(artist);
				if(!found) {
					idx[numFound++] = i;
					if(numFound == numAdjust)
						break;
				} else {
					if(Logger.IS_TRACE_ENABLED) {
						lgr.trace("Playlist :" + this.name + " Moving down candidate song for artist " + artist + " at position " + i);
					}
				}
			}
			if(numFound > 0) {
				// We need to move the recs leading upto idx below it
				for(int k=0;k<numFound;k++) {
					bubbleUp(localRecos, idx[k], k);
				}
			}
		}
		
	}
	
	protected static final int NUM_OF_NEW_ARTISTS = 3;
	public void setRecommendation(ArrayList<Recommendation> recos) {
		adjustScores(recos, NUM_OF_NEW_ARTISTS);
		recoRef = new SoftReference<ArrayList<Recommendation>>(recos);
	}

	
	public void setWishList(ArrayList<Wishlist> list) {
		wishList = list;

	}

	public HashMap<String, String> getIgnoredArtists() {
		return ignoredArtists;
	}

	public void setIgnoredArtists(HashMap<String, String> artists) {
		this.ignoredArtists = artists;
	}

	public ArrayList<Track> getTracks() {
		return tracks;
	}

	public void setTracks(ArrayList<Track> tracks) {
		this.tracks = tracks;
	}

	public boolean addRecommendation(Recommendation reco) {
		ArrayList<Recommendation> recos = fetchOrLoadRecos();
 		for(Recommendation rec : recos) {
			assert(rec.getId() != reco.getId());
		}
		
		return recos.add(reco);
		
	}

	public boolean deleteRecommendation(Recommendation reco) {
		ArrayList<Recommendation> recos = fetchOrLoadRecos();
		return recos.remove(reco);
	}

	public Recommendation locateRecommendtion(long recoId) {
		ArrayList<Recommendation> recos = fetchOrLoadRecos();
		for(Recommendation rec : recos) {
			if(rec.getId() == recoId) {
				return rec;
			}
		}
		return null;
	}

	public Recommendation locateRecommendtionByAsid(long asid) {
		ArrayList<Recommendation> recos = fetchOrLoadRecos();
		for(Recommendation rec : recos) {
			if(rec.getArtsongId() == asid) {
				return rec;
			}
		}
		return null;
	}
	
	public boolean hasNewerRecs(Long timestamp) {
		ArrayList<Recommendation> recos = fetchOrLoadRecos();
		if(recos.isEmpty()) {
			return true;
		}
		if(recos.get(0).getTimeReceived() > timestamp) {
			return true;
		}
		
		return false;	
	}

	public Signature getSourceSignature() {
		if(this.playlistType.equals(EnumPlaylistType.SOURCE_USER)
				|| this.playlistType.equals(EnumPlaylistType.SOURCE_GENIUS)) {
			if(splay != null) {
				return splay.getSignature();
			} else {
				lgr.error("Could not find source playlist for : " + this.getSourceSpecificId() + " : " + this.getName() + StackTrace.getStackTrace());
			}
		}
		return defaultSig;
	}

	public void setSourcePlaylist(SourcePlaylist play) {
		splay = play;
	}

	public SourcePlaylist getSourcePlaylist() {
		return splay;
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(name);
		parcel.writeString(sourceSpecificId);
		parcel.writeString(playlistType.toString());
		parcel.writeBooleanArray(new boolean[] { isDeleted });
	}

	public static final Parcelable.Creator<PlaylistImpl> CREATOR = new Parcelable.Creator<PlaylistImpl>() {
		public PlaylistImpl createFromParcel(Parcel source) {
			return new PlaylistImpl(source);
		}

		public PlaylistImpl[] newArray(int size) {
			return new PlaylistImpl[size];
		}
	};

	
	public void setPositiveRatedArtist(String posArtist) {
		recentPositivesMap.put(posArtist, posArtist);
	}
		
}
