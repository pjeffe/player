package com.mixmoxie.source.player;


import com.mixmoxie.source.sourceobject.SourceTrack;

public interface SourceSoundPlayer {

    public enum PlayerType {
        iTunes,
        WinMedia,
        JMF,
        Android,
        Other
    }
    public PlayerType getPlayerType();
    public void playFile(String filename);
    public void playURL(String location);
    public void playTrack(SourceTrack track);
    public void stop();
}
