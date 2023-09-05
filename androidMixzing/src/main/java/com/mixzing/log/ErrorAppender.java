package com.mixzing.log;

public interface ErrorAppender {

	public abstract void init();

	public abstract void logError(String level, String msg);

	public abstract void onUpdate();
}