package com.mixzing.signature.common;

import java.util.List;


public interface MixzingSignatureData {


	public int getCodeVersion();
	
	public List<Long> getSignature();
	
	public MixzingSignatureWindow getWindow();
	
}
