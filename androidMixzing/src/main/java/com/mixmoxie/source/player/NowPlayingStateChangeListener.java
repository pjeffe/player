package com.mixmoxie.source.player;

public interface NowPlayingStateChangeListener {
    public void stateChanged (NowPlaying nowPlaying, NowPlaying.NowPlayingState state);
}
