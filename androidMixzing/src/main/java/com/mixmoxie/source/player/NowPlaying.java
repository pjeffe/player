package com.mixmoxie.source.player;

public interface NowPlaying {
    public enum NowPlayingState {
        PLAYING,
        PAUSED,
        FORWARD,
        REWIND,
        STOPPED,
        DISPOSE;
    };

    void playPause();

    void stop();

    String getLocation();
    
    void dispose();
}
