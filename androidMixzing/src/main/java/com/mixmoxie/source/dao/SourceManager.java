package com.mixmoxie.source.dao;

import com.mixmoxie.source.player.SourceSoundPlayer;

public interface SourceManager {

    public enum SourceType {
        iTunes,
        FileSystem,
        WindowsMedia
    }

    public SourceType getSourceType();

    public SourceLibraryManager getSourceLibraryManager();

    public SourcePlaylistManager getSourcePlaylistManager();

    public SourceSoundPlayer getPlayer();

    public void sourceLibraryInit();
    
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
	
	public boolean shouldSavePlaylists();
	
    public void shutDown();
    
    public void updateItunesFileName(String location);

    public void clearNonRetainedResources();

}
