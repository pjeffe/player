package com.mixzing.signature.common;


public interface MixzingAudioFactory {

    public MixzingPcmDataStream createPcmStream(MixzingSignatureWindow win);

    public MixzingWaveSig createWaveSig(MixzingLagIterator it, double sf, int superWin);
    
    public MixzingAudioFileAdapter createAdapter(String fileName, MixzingPcmDataStream stream, MixzingSignatureWindow window);

    /*
     * This is the version of the packaging
     */
    public String getVersion();
    
    /*
     * This is the version that determines if there is sufficient change in logic to warrant another
     * regeneration of the signature data.
     */
    public String getSignatureVersion();
}