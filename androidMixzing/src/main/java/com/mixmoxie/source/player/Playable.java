package com.mixmoxie.source.player;

public interface Playable {
    // remember to implement toString()!

    // selects track and plays self, or resumes from pause
    void play();

    // selects track pauses. no-op if currently paused.
    void pause();

    // selects track and sets play position. 
    // may be called when paused. in either case,
    // player remains in its previous state (playing or paused)
    void cue(int milliSeconds);
}
