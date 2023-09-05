package com.mixzing.signature.common;

import java.util.List;


public interface MixzingWaveSig {

    public List<MixzingPeak> generateSignature();
    
    public List<MixzingPeak> generateSignature(int minPeaks, int maxPeaks);
    
    public void setDebugFile(String f);

    public boolean isDebug();
}