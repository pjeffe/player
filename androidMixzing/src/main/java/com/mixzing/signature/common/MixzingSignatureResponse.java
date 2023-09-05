package com.mixzing.signature.common;

import java.util.List;


public interface MixzingSignatureResponse {

	public MixzingAudioInfo getAudioInfo();
	
	public List<MixzingSignatureData> getSignatures();
    
    public MixzingSignatureData getSignatureForWindow(int skip, int dur, int superWin);
    
    public MixzingSignatureData getSignatureForWindow(MixzingSignatureWindow win);
	
}
