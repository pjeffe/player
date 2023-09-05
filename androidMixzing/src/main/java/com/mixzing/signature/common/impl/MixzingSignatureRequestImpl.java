package com.mixzing.signature.common.impl;

import java.util.List;

import com.mixzing.signature.common.MixzingSignatureRequest;
import com.mixzing.signature.common.MixzingSignatureWindow;

public class MixzingSignatureRequestImpl implements MixzingSignatureRequest {

	private String fileName;
	private List<MixzingSignatureWindow> windows;

	public MixzingSignatureRequestImpl(String f, List<MixzingSignatureWindow> w) {
		windows = w;
		fileName = f;
	}
	
	public String getFileName() {
		return fileName;
	}

	public List<MixzingSignatureWindow> getWindows() {
		return windows;
	}

}
