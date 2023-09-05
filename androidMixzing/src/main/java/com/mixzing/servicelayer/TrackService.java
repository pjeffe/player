package com.mixzing.servicelayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mixmoxie.source.sourceobject.SourceTrack;
import com.mixzing.musicobject.PlaylistTrack;
import com.mixzing.musicobject.Track;


public interface TrackService {

	public ArrayList<Track> getTracksForPlaylist(long plid);
	
	public ArrayList<Track> getAllTracks();
	
	public Track addSourceTrack(SourceTrack srcTrack);
	
	public void deleteTrack(Track t);
	
	public Track locateBySourceId(String l);
	
	public Track locateByAndroidSourceId(int tid);
	
	public void deletePlaylistTrack(long plid, Track track);

	public void addPlaylistTrack(long plid, Track track);

	public Track findByGlobalSongId(long globalSongId);
	
	public ArrayList<PlaylistTrack> mapTrackLsidGsid(long lsid, long gsid);

	public void deleteAllTracksFromPlaylist(long plid);

	public PlaylistTrack findPlaylistTrack(long plid, long lsid);
	
	public ArrayList<Track> getTracksWithLocationForPlaylist(long plid);
	
	public List<Track> locateLocalSongByLCD_ArtistTitle(String art, String tit);

	public File getLocation(long lsid);

    public Track findByLsid(long lsid);
    
    public int tracksWithGsid();

    public void reindexAndroidIds();

	public void processTagModified(String fileLocation, String title,
			String album, String artist, String trackNumber, int releaseYear,
			String genre, float duration);
	
	public void resolveEditedTagTracks();
	
	/*
	 * 
	 * Let the resolver know we have some tag data to send to the server
	 */
	public boolean haveTagsToProcess();
	
}
