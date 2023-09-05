package com.mixzing.signature.common.impl;

import java.util.ArrayList;
import java.util.List;

import com.mixzing.signature.common.MixzingPeak;
import com.mixzing.signature.common.MixzingSignatureData;
import com.mixzing.signature.common.MixzingSignatureWindow;

public class MixzingSignatureDataImpl implements MixzingSignatureData {

	private List<Long> signature;
	private MixzingSignatureWindow window;
	private int version;

	public MixzingSignatureDataImpl(List<Long> sig, MixzingSignatureWindow w, int version) {
		this.signature = sig;
		this.window = w;
		this.version = version;
	}
	public List<Long> getSignature() {
		return signature;
	}

	public MixzingSignatureWindow getWindow() {
		return window;
	}
	public int getCodeVersion() {
		return version;
	}

}
