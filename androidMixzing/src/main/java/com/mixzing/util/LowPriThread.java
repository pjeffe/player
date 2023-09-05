package com.mixzing.util;

import com.mixzing.log.Logger;


public class LowPriThread extends Thread {
	private static final Logger log = Logger.getRootLogger();
	protected boolean canceled;

	private static final int BASE_PRIORITY = MIN_PRIORITY;

	public LowPriThread() {
		super();
		setMinPriority();
	}

	public LowPriThread(Runnable target) {
		super(target);
		setMinPriority();
	}

	public LowPriThread(Runnable target, String name) {
		super(target, name);
		setMinPriority();
	}

	private void setMinPriority() {
		try {
			setPriority(BASE_PRIORITY);
		}
		catch (Exception e) {
			log.error("WorkerThread.setMinPriority: setting priority:", e);
		}
	}

	public void cancel() {
		canceled = true;
	}
}
