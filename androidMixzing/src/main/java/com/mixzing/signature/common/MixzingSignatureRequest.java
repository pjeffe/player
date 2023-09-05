package com.mixzing.signature.common;

import java.util.List;

public interface MixzingSignatureRequest {

	public String getFileName();
	
	public List<MixzingSignatureWindow> getWindows();
	
}
