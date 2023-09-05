package com.mixzing.android;

import java.util.Map;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;

//import com.flurry.android.Constants;
import com.flurry.android.FlurryAgent;
import com.mixzing.ads.MixZingAdInterface.Gender;
import com.mixzing.log.Logger;
import com.mixzing.util.LowPriThread;

public class Flurry {
	private static final Logger log = Logger.getRootLogger();
	private static String key = "QJ3P5JWF8E7DALTYSNKL";
	private static Context sessionContext;
	private static Context globalContext;


	public static void init(Context context) {
		try {
			globalContext = context;
			FlurryAgent.setContinueSessionMillis(Analytics.SESSION_TIMEOUT);
			FlurryAgent.setCaptureUncaughtExceptions(false);
			FlurryAgent.setUserId(AndroidUtil.getUserId());

			// if the only location provider available is gps then disable location, otherwise
			// set its criteria to only use a coarse provider
			//
			final Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_COARSE);
			crit.setPowerRequirement(Criteria.POWER_LOW);
			crit.setCostAllowed(false);
			crit.setAltitudeRequired(false);
			crit.setBearingRequired(false);
			crit.setSpeedRequired(false);

			LocationManager locmgr = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			final String provider = locmgr.getBestProvider(crit, true);
			if (Logger.IS_DEBUG_ENABLED)
				log.debug("Flurry.init: provider = " + provider);

			if ("gps".equalsIgnoreCase(provider)) {
				FlurryAgent.setReportLocation(false);
			}
			else {
				FlurryAgent.setLocationCriteria(crit);
			}

			setPriThread();
		}
		catch (Exception e) {
			log.error("Flurry.init:", e);
		}
	}

	protected static void setPriThread() {
		new LowPriThread() {
			public void run() {
				try {
					ThreadGroup tg = Thread.currentThread().getThreadGroup();
					int size = tg.activeCount();
					Thread[] threads;
					int num;
					do {
						size += 5;
						threads = new Thread[size];
						num = tg.enumerate(threads, true);
					} while (num >= size && size < 100);
					final int thnum = num;
					for (int i = 0; i < thnum; ++i) {
						Thread th = threads[i];
						final String name = th.getName();
						if (Logger.IS_DEBUG_ENABLED)
							log.debug("Flurry.init: checking thread " + th + "/" + th.getId());
						if (name.equals("FlurryAgent")) {
							th.setPriority(Thread.MIN_PRIORITY);
							if (Logger.IS_DEBUG_ENABLED)
								log.debug("Flurry.init: found and set low pri for " + th);
							break;
						}
					}
				}
				catch (Exception e) {
					log.error("Exception in flurry pri thread", e);
				}
			}
		}.start();
	}

	public static void init(Context context, String key) {
		Flurry.key = key;
		init(context);
	}

	public static void startSession(Context context) {
		if (Logger.IS_DEBUG_ENABLED)
			log.debug("Flurry.startSession: sessionContext = " + sessionContext);
		try {
			FlurryAgent.onStartSession(context, key);
			FlurryAgent.onPageView();
			sessionContext = context;
		}
		catch (Exception e) {
			log.error("Flurry.startSession:", e);
		}
	}

	public static void endSession(Context context) {
		if (Logger.IS_DEBUG_ENABLED)
			log.debug("Flurry.endSession: sessionContext = " + sessionContext);
		try {
			if (sessionContext == context) {
				sessionContext = null;
			}
			FlurryAgent.onEndSession(context);
		}
		catch (Exception e) {
			log.error("Flurry.endSession:", e);
		}
	}

	public static void event(String event) {
		if (Logger.IS_DEBUG_ENABLED)
			log.debug("Flurry.event: sessionContext = " + sessionContext + ", event = " + event);
		try {
			// if there is no current session then create a temporary one
			if (sessionContext == null) {
				FlurryAgent.onStartSession(globalContext, key);
			}
			FlurryAgent.onEvent(event);
			if (sessionContext == null) {
				FlurryAgent.onEndSession(globalContext);
			}
		}
		catch (Exception e) {
			log.error("Flurry.event:", e);
		}
	}

	public static void event(String event, Map<String, String> args) {
		try {
			FlurryAgent.onEvent(event, args);
		}
		catch (Exception e) {
			log.error("Flurry.event:", e);
		}
	}

	public static void setAge(int age) {
//		if (age >= 1 && age <= 109) {
//			try {
//				FlurryAgent.setAge(age);
//			}
//			catch (Exception e) {
//				log.error("Flurry.setAge:", e);
//			}
//		}
	}

	public static void setGender(Gender gender) {
//		try {
//			if (gender == Gender.MALE) {
//				FlurryAgent.setGender(Constants.MALE);
//			}
//			else if (gender == Gender.FEMALE) {
//				FlurryAgent.setGender(Constants.FEMALE);
//			}
//		}
//		catch (Exception e) {
//			log.error("Flurry.setGender:", e);
//		}
	}
}
