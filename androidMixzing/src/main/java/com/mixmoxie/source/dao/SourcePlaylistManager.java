package com.mixmoxie.source.dao;

import java.util.ArrayList;

import com.mixmoxie.source.sourceobject.SourcePlaylist;

public interface SourcePlaylistManager {

    ArrayList<SourcePlaylist> getPlaylists();
    
    SourcePlaylist createPlaylist(String name);
    
    void deletePlaylist(SourcePlaylist playlist);
    
    SourcePlaylist renamePlaylist(SourcePlaylist playlist, String name);
    
    public SourcePlaylist findbyId(String id);

}
