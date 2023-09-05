package com.mixzing.signature.common;

import java.io.File;


public interface MixzingAudioFactoryLoader {

    /*
     * Called at startup
     */
    public MixzingAudioFactory loadAudioFactory();

    /*
     * 
     * Called when a new jar is downloaded while the manager is running.
     * 
     */
    public MixzingAudioFactory loadAudioFactory(File jar);

}