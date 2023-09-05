package com.mixzing;

import java.lang.Thread.UncaughtExceptionHandler;

import android.os.Process;

import com.mixzing.log.Logger;

public class ExceptionHandler implements UncaughtExceptionHandler {
	private static final Logger log = Logger.getRootLogger();
	private boolean kill;

	public ExceptionHandler(boolean kill) {
		this.kill = kill;
	}

	public void uncaughtException(Thread t, Throwable e) {
		if (Logger.IS_DEBUG_ENABLED)
			log.debug("ExceptionHandler.uncaughtException");
		t.setUncaughtExceptionHandler(null);  // to avoid loops if we fault
		Thread.setDefaultUncaughtExceptionHandler(null);
		log.fatal("Uncaught exception: " + log.getMessage(e));

		if (kill) {
			try {
				int pid = Process.myPid();
				if (Logger.IS_DEBUG_ENABLED)
					log.debug("ExceptionHandler.uncaughtException: killing proc " + pid);
				Process.killProcess(pid);
			}
			catch (Exception e2) {
			}
		}
	}
}
