package com.mixzing.servicelayer;

import java.util.List;

import com.mixmoxie.source.sourceobject.SourcePlaylist;
import com.mixmoxie.source.sourceobject.SourceTrack;

public interface SourceService {

	public List<SourceTrack> getTracks();
	
	public List<SourceTrack> getAllPlaylistTracks();
	
	public List<SourcePlaylist> getPlayLists();
	
	//public ArrayList<SourceTrack> getPlaylistTracks(SourcePlaylist play);
	
	public void addTrackToPlaylist(SourcePlaylist play, SourceTrack track);
	
	public void addTrackToPlaylist(String srcPlaylistId, String trackLocation);
	
	public SourcePlaylist createPlaylist(String name);
	
    public long playFile(String filename);
    
    public long playURL(String url);    
    
    public long playTrack(String sourceId);

    public void stopPlaying(long playId);
    
    public void pausePlaying(long playId);

	public void shutdown();
	
	/**
	 *  Attach to the Source
	 *
	 */
	public void attach();
	
	/**
	 * Detach from source
	 *
	 */
	public void detach();
	
	
	/**
	 * Resync with the source if it is not already
	 *
	 */
	public boolean reSync();
	
	/**
	 * Did the scanner tell us that it had no playlists  ?  
	 */
	public boolean shouldSavePlaylists();
	
	public void updateItunesFileLocation(String fileLoc);
	
    /*
     * Some of the data is only needed while resolving the tracks.
     * release that.
     */
    public void clearNonRetainedResources();
}
