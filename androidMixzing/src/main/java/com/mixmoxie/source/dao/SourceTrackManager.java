package com.mixmoxie.source.dao;

import java.util.List;

import com.mixmoxie.source.sourceobject.SourceTrack;
import com.mixmoxie.source.sourceobject.SourceTrackId;

public interface SourceTrackManager {

    public List<SourceTrack> getTracks();

    public List<SourceTrack> createTracksForPlaylist(List playlistITTracks);

    public List<SourceTrack> getTracksInPlaylists();

    public void removeTrack(SourceTrack track);

    public void changeTrackTag(SourceTrackId id, String tag, String value);

    public int getTrackCount();

	//public SourceTrack findByLocation(String trackLocation);
	
	public SourceTrack findByTrackDbId(String dbid);
}
