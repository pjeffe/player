package com.mixzing.signature.common;

public interface MixzingLagIterator {

	public int size();
	
	public boolean hasNext();
	
	public int next();
	
	public void restart();

	public void restart(int offset);
	
	public int sampleFrequency();
}
