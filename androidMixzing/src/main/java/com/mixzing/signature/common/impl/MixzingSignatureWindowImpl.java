package com.mixzing.signature.common.impl;

import com.mixzing.signature.common.MixzingSignatureWindow;

public class MixzingSignatureWindowImpl implements MixzingSignatureWindow {

	private int skip, duration, superWinMs;
	
	private MixzingSignatureWindowImpl(int skip, int dur) {
		this(skip,dur,0);
	}

	public MixzingSignatureWindowImpl(int skip, int dur, int sWin) {
		this.skip = skip;
		this.duration = dur;
		this.superWinMs = sWin;
	}
	
	public int getDuration() {
		return duration;
	}

	public int getSkip() {
		return skip;
	}
	public int getSuperWindow() {
		// TODO Auto-generated method stub
		return superWinMs;
	}

}
