package com.mixzing;

public interface ManagerStarter {

	public enum Status {
		NOTSTARTED,
		STARTING,
		STARTED,
		FAILED
	};
	
	public void startMZ(boolean isBound);

	public void notifyAboutServerConnection(boolean isConnected);

	public Status getManagerStatus();
	
	public ManagerStopper getStopper();
	
	public void updateStartupStatus(Status newStat, String broadcast);

	public void startBoundServices();

}