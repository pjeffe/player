package com.mixzing.signature.common;


public interface MixzingPcmDataStream {

	public MixzingAudioInfo getAudioInfo();
	
	public MixzingLagIterator getIterator(MixzingSignatureWindow window);
	
	public void writePcmShort(short pcmData);

	public void clear();
	
	public void updateAudioFileInfo(MixzingAudioInfo info);

}
