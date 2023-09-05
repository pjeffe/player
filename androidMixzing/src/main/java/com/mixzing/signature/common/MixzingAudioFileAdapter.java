package com.mixzing.signature.common;

import java.io.FileNotFoundException;


public interface MixzingAudioFileAdapter {

	public MixzingAudioInfo getAudioInfo();
	
	public void extractPCMData() throws FileNotFoundException;
	
	public void registerStream(MixzingPcmDataStream stream);
	
}
