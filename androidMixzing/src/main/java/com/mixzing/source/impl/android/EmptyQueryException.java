package com.mixzing.source.impl.android;

public class EmptyQueryException extends Exception {

	public boolean scannerReturnedEmpty;
	public int prevCount;
	
	public boolean isScannerReturnedEmpty() {
		return scannerReturnedEmpty;
	}
	
	public int getPrevCount() {
		return prevCount;
	}
	
	public EmptyQueryException(boolean shutd, int preCnt) {
		super();
		scannerReturnedEmpty = shutd;
		prevCount = preCnt;
	}

	public EmptyQueryException() {
		this(false,0);
	}
	
}
