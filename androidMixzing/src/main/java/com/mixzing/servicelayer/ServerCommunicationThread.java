package com.mixzing.servicelayer;

public interface ServerCommunicationThread {

	public void shutDown();

	public void wakeup();

	public void run();

}