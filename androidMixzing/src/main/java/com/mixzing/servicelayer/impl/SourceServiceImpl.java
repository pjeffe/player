package com.mixzing.servicelayer.impl;

import java.util.ArrayList;
import java.util.List;

import com.mixmoxie.source.dao.SourceLibraryManager;
import com.mixmoxie.source.dao.SourceManager;
import com.mixmoxie.source.dao.SourcePlaylistManager;
import com.mixmoxie.source.dao.SourceTrackManager;
import com.mixmoxie.source.player.SourceSoundPlayer;
import com.mixmoxie.source.sourceobject.SourcePlaylist;
import com.mixmoxie.source.sourceobject.SourceTrack;
import com.mixzing.log.Logger;
import com.mixzing.servicelayer.SourceService;

public class SourceServiceImpl extends BaseServiceImpl implements SourceService {

	protected static Logger lgr = Logger.getRootLogger();

	private SourceTrackManager trkManager;
	private SourcePlaylistManager playManager;
	private SourceSoundPlayer player;
	private SourceLibraryManager srcLibraryManager;
	private SourceManager srcManager;

	private long playingID;


	public SourceServiceImpl(SourceManager srcManager) {
		super();
		this.srcManager = srcManager;

		srcManager.sourceLibraryInit();
		srcLibraryManager = srcManager.getSourceLibraryManager();
		trkManager = srcLibraryManager.getSourceTrackManager();
		playManager = srcManager.getSourcePlaylistManager();
		player = srcManager.getPlayer();

	}

	public void addTrackToPlaylist(SourcePlaylist play, SourceTrack track) {
		play.addTrack(track);
	}

	public SourcePlaylist createPlaylist(String name) {
		return playManager.createPlaylist(name);
	}

	public ArrayList<SourcePlaylist> getPlayLists() {
		return playManager.getPlaylists();
	}

	public java.util.List<SourceTrack> getPlaylistTracks(SourcePlaylist play) {
		return play.getTracks();
	}

	public List<SourceTrack> getTracks() {
		return trkManager.getTracks();
	}

	public List<SourceTrack> getAllPlaylistTracks() {
		return trkManager.getTracksInPlaylists();
	}

	public void addTrackToPlaylist(String srcPlaylistId, String trackLocation) {
		SourcePlaylist play = playManager.findbyId(srcPlaylistId); // lookup based on id
		SourceTrack track = trkManager.findByTrackDbId(trackLocation);   // lookup based on location
		addTrackToPlaylist(play, track);
	}


	public long playFile(String filename) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Queueing to play file (not used):" + filename);
		player.playFile(filename);
		playingID = System.currentTimeMillis();
		return playingID;
	}

	public long playTrack(String sourceId) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Queueing to play srcId:" + sourceId);
		SourceTrack st = trkManager.findByTrackDbId(sourceId);
		return playTrack(st);
	}

	public long playURL(String location) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Queueing to play:" + location);
		long playID = System.currentTimeMillis();
		playSongURL(location, playID);
		return playID;
	}

	public void pausePlaying(long playId) {
		stopPlaying(playId);
	}

	public void stopPlaying(long playId) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Queueing to stop:" + playId);
		stopPlayingSong(playId);
	}

	protected long playSongURL(String location, long playId) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Attempting to play:" + location);	
		player.playURL(location);
		playingID = playId;
		return playingID;
	}

	protected long playTrack(SourceTrack track) {
		player.playTrack(track);
		playingID = System.currentTimeMillis();
		return playingID;
	}

	protected void stopPlayingSong(long playId) {
		if(this.playingID == playId) {
			player.stop();
		}
	}



	public void shutdown() {
		srcManager.shutDown();
	}

	private boolean iShuttingDown;

	public void attach() {
		srcManager.attach();
	}

	public void detach() {
		srcManager.detach();
	}

	public boolean reSync() {
		return srcManager.reSync();
	}
	
	public boolean shouldSavePlaylists() {
		return srcManager.shouldSavePlaylists();
	}

	public void updateItunesFileLocation(String fileLoc) {
		srcManager.updateItunesFileName(fileLoc);

	}

	public void clearNonRetainedResources() {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Clearing up non retained resources. " + Runtime.getRuntime().freeMemory());
		srcManager.clearNonRetainedResources();
		Runtime.getRuntime().gc();
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Cleaned up non retained resources. " + Runtime.getRuntime().freeMemory());

	}




}
