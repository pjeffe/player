package com.mixzing.android;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.mixzing.MixzingConstants;
import com.mixzing.log.Logger;
import com.mixzing.servicelayer.LibraryService;

public class MixzingLocationManagerImpl implements MixzingLocationManager, Runnable {

	protected static Logger lgr = Logger.getRootLogger();

	protected static final float SUFFICIENT_ACCURACY = 200.0f;                   // 200 meters is more than enough for us
	protected static final long MAX_TIME_TO_STAY_REGISTERED = MixzingConstants.FIVE_MINUTE / 10; // 30 seconds
	
	protected Handler handler;

	protected LocationManager mgr;
	protected Location lastLocation = null;
	protected MixZingLocationListener listener;
	protected boolean isRegistered = false;

	protected int registerCount;
	protected TimeoutHandler timeoutHandler;
	protected GeoCodeHandler geoHandler;
	protected boolean timeoutHandlerSet = false;
	
	protected static final boolean LOCATION_CODE_DISABLED = false;
	protected LibraryService libSvc;
	protected String lastParsedDevPoll;
	
	

	protected long   locationDelay = MixzingConstants.ONE_HOUR * 2;
	protected long   sufficientCurrency = locationDelay/2;
	
	public MixzingLocationManagerImpl(Context cont, LibraryService ls) {
		mgr = (LocationManager) cont.getSystemService(Context.LOCATION_SERVICE);
		listener = new MixZingLocationListener();
		timeoutHandler = new TimeoutHandler();
		geoHandler = new GeoCodeHandler();
		libSvc = ls;
	}

	protected long getDevicePollInterval() {
		long delay = libSvc.getServerParameterLong(MixzingConstants.SERVER_PARAM_LOCDEL);
		if(delay != locationDelay) {
			locationDelay = delay;
			sufficientCurrency = locationDelay;
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Got new location delays: " + locationDelay + ":" + sufficientCurrency); 
			}
		}
		return locationDelay;
	}
	
	/*
	 * 
	 * By default we use coarse unless server tells us to use fine(ps).
	 * 
	 */
	protected boolean useCoarseProvider() {
		String prv = libSvc.getServerParameterString(MixzingConstants.SERVER_PARAM_LOCATION_PROVIDER_TYPE); 
		if(prv != null && prv.equalsIgnoreCase(MixzingConstants.COARSE_LOCATION_PROVIDER_OFF) ) {
			return false;
		}		
		return true;
	}
	
	protected long getSufficientCurrency() {
		return sufficientCurrency;
	}


	/* (non-Javadoc)
	 * @see com.mixzing.android.MixzingLocationManager#getLocation()
	 */
	public Location getLocation() {
		if(LOCATION_CODE_DISABLED) {
			return null;
		}
		long now = System.currentTimeMillis();
		if(lastLocation != null) {
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Previous known location: " + lastLocation.getAccuracy() + ":" + (now - lastLocation.getTime())  + ":"  + lastLocation.getProvider()); 
			}
		}
		try {
			Location newLoc = getLastKnownLocation();
			if(newLoc != null) {
				lastLocation = newLoc;
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("New selected location: " + lastLocation.getAccuracy() + ":" + (now - lastLocation.getTime())  + ":"  + lastLocation.getProvider()); 
				}
			}
		} catch (Exception e) {
			lgr.error("Exception getting location: ",e);
		}

		// return data from lastLocation
		return lastLocation;
	}

	protected static final float MIN_ACCURACY = Float.MAX_VALUE - 100;

	
	protected float getAccuracy(Location loc) {
		float accuracy = loc.hasAccuracy() ? Math.abs(loc.getAccuracy()) : MIN_ACCURACY ;
		if(accuracy == 0) {
			accuracy = MIN_ACCURACY; 
		}
		return accuracy;
	}


	/*
	 * 
	 * Be a good citizen, try and get whatever last cached location is available for the device, 
	 * so that we do not use the battery to activate the hardware for our purpose.
	 * 
	 * The assumption here is that there are many other programs that would want real time location
	 * 
	 * We just scan through all the locations and get the one that was updated in our tolerance period
	 * (say 5 minutes) and use the one with the best accuracy.
	 * 
	 * Need to enhance the algorithm in a way that if the most accurate one is more than X minutes older
	 * than another more recent but less accurate fix, use the recent fix instead. 
	 * 
	 */
	protected Location getLastKnownLocation() {
		List<String> providers = mgr.getProviders(true);
		Location mostAccurate = null;
		Location mostCurrent = null;

		long mostCurrentFixTime = 0;
		float mostAccuracy = Float.MAX_VALUE;
		long lastFix = lastLocation != null ? lastLocation.getTime() : 0;
		long now = System.currentTimeMillis();

		if(providers != null) {
			for(String provider : providers) {
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("Asking mgr for location from provider: "  + provider); 
				}
				Location latestLoc = mgr.getLastKnownLocation(provider);
				if(latestLoc != null) {
					if(Logger.IS_DEBUG_ENABLED) {
						lgr.debug("Got fix candidate: " + latestLoc.getAccuracy() + ":" + (now - latestLoc.getTime())  + ":"  + provider); 
					}
				} else {
					if(Logger.IS_DEBUG_ENABLED) {
						lgr.debug("Did not get a location from provider: "  + provider);
					}
				}
				if(latestLoc != null && latestLoc.getTime() > lastFix) {
					float accuracy = getAccuracy(latestLoc);
					long fixTime = latestLoc.getTime();
					if(accuracy < mostAccuracy) {
						mostAccurate = latestLoc;
						mostAccuracy = accuracy;
					}
					if( mostCurrentFixTime < fixTime) {
						mostCurrent = latestLoc;
						mostCurrentFixTime = fixTime;
					}

					if(mostAccuracy < SUFFICIENT_ACCURACY && now < (fixTime + getSufficientCurrency()) ) {
						break;
					}
				}
			}
		} 

		return chooseBetweenAccurateAndCurrent(mostAccurate, mostCurrent);
	}


	protected Location chooseBetweenAccurateAndCurrent(Location accurate, Location current) {
		if(accurate == null ) {
			return current;
		}
		if(current == null ) {
			return accurate;
		}

		long accurateTime  = accurate.getTime();
		long curTime = current.getTime();

		/*
		 * If most accurate is also most current, or the difference between the two is less then the
		 * granularity of location messages we expect on the server, return most accurate
		 */
		if(accurateTime >= curTime || ((curTime - accurateTime) < getDevicePollInterval()) ) {
			return accurate;
		}

		// most accurate is more stale than our location granularity so return the most current one
		return current;
	}


	protected String getBestProvider(int criteria) {
		String provider = null;
		try {
			Criteria crit = new Criteria();
			crit.setAccuracy(criteria);
			crit.setAltitudeRequired(false);
			crit.setBearingRequired(false);
			crit.setPowerRequirement(Criteria.NO_REQUIREMENT);
			crit.setCostAllowed(false);
			crit.setSpeedRequired(false);
			provider = mgr.getBestProvider(crit, true);
		} catch (Exception e) {
			lgr.error("Exception in get best provider",e);
		}

		return provider;
	}

	protected boolean isRegistrationNeeded() {
		boolean isNeeded = true;
		if(lastLocation != null) {
			long now = System.currentTimeMillis();
			long lastFix = lastLocation.getTime();
			if(lastFix + getSufficientCurrency() > now) {
				isNeeded = false;
			}
		}
		return isNeeded;                      
	}
	
	/*
	 * If no-one else has requested a Location on this device, we will do it. But we do it 
	 * extremely conservatively, say once an hour.
	 *  
	 */
	protected void registerForLocationFromDevice() {
		if(Logger.IS_DEBUG_ENABLED) {
			lgr.debug("Location registration called.");
		}	
		boolean weRegistered = false;
		if(!isRegistered && isRegistrationNeeded()) {
			
			// Default is coarse, unless the server requests gps
			boolean useCoarse = useCoarseProvider();
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Use coarse location provider: " + useCoarse);
			}
			
			String provider = getBestProvider(useCoarse ? Criteria.ACCURACY_COARSE : Criteria.ACCURACY_FINE);
			if(provider == null)  {
				provider = getBestProvider(useCoarse ? Criteria.ACCURACY_FINE   : Criteria.ACCURACY_COARSE);
			}
			
			// XXX: we may need to work differently with socialmuse
			if("gps".equalsIgnoreCase(provider) && useCoarse) {
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("Punting on gps provider since we are supposed to use coarse");
				}	
				provider = null;
			}

			if(provider != null) {
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("Registering for Location Provider " + provider);
				}	
				synchronized(this) {
					if(!isRegistered) {
						mgr.requestLocationUpdates(provider, MAX_TIME_TO_STAY_REGISTERED/10, 0, listener);
						isRegistered = true;
						weRegistered = true;
						registerCount++;
						if(Logger.IS_DEBUG_ENABLED) {
							lgr.debug("Location listener registered for provider : " + provider + " " + timeoutHandlerSet);
						}
						handler.postDelayed(timeoutHandler, MAX_TIME_TO_STAY_REGISTERED);
						timeoutHandlerSet = true;
					}
				}
			} else {
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("No Location Provider found for registration");
				}			
			}
		} 
	
		// If we were unable to register in this call reschedule later
		if(!weRegistered) {
			handler.postDelayed(this, getDevicePollInterval() / 2);
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Location handler was not registered in this instance since we had a current loc. Will check in later");
			}	
		}
		
	}

	public void unRegisterForLocationFromDevice() {
		if(LOCATION_CODE_DISABLED) {
			if (Logger.IS_DEBUG_ENABLED)
				lgr.debug("Location code disabled...not doing anything while unregistering");
			return;
		}
		if (Logger.IS_DEBUG_ENABLED)
			lgr.debug("Attempt to Unregister location from device");
		unRegisterAndReschedule(false);
		handler.removeCallbacks(geoHandler);
	}
	
	protected void unRegisterAndReschedule(boolean reschedule) {
		synchronized(this) {
			if(isRegistered) {		
				mgr.removeUpdates(listener);
				isRegistered = false;
				registerCount--;
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("Unregistered location listener. ");
				}
				handler.removeCallbacks(timeoutHandler);
				timeoutHandlerSet = false;
				if(reschedule) {
					handler.postDelayed(MixzingLocationManagerImpl.this, getDevicePollInterval());
				} else {
					handler.removeCallbacks(MixzingLocationManagerImpl.this);
				}
			}
		}
	}
	
	protected void scheduleGeocodeHandler() {
		synchronized(this) {
			handler.removeCallbacks(geoHandler);
			handler.postDelayed(geoHandler, 5);
		}
	}

	/*
	 * 
	 * This is a no-op class, we just want the lastknownlocation to be populated
	 * 
	 */
	protected class MixZingLocationListener implements LocationListener {



		public void onLocationChanged(Location location) {
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Got a new location at " + location.getTime());
			}
			AndroidUtil.persistLastLocation(location);
			scheduleGeocodeHandler();
			unRegisterAndReschedule(true);
		}

		public void onProviderDisabled(String provider) {
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Location Provider " + provider);
			}			
		}

		public void onProviderEnabled(String provider) {
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Location Provider " + provider + " enabled ");
			}			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Location Provider " + provider + " new status " + status);
			}						
		}

	}

	public void run() {
		if(LOCATION_CODE_DISABLED) {
			if (Logger.IS_DEBUG_ENABLED)
				lgr.debug("Location code disabled...not doing anything while running");
			return;
		}
		registerForLocationFromDevice();
	}


	public void registerForLocationFromDevice(Handler handler) {
		if(LOCATION_CODE_DISABLED) {
			if (Logger.IS_DEBUG_ENABLED)
				lgr.debug("Location code disabled...not doing anything while registering");
			return;
		}
		this.handler = handler;
		registerForLocationFromDevice();
	}

	protected class GeoCodeHandler implements Runnable {
		public void run() {
			boolean handled = AndroidUtil.refreshLocations();
			if(!handled) {
				// If we did not get a geocode try again in 10 minutes, if we are still running then
				handler.postDelayed(geoHandler, MixzingConstants.ONE_MINUTE * 10);
			}
		}
	}

	protected class TimeoutHandler implements Runnable {
		public void run() {
			unRegisterAndReschedule(true);
		}
	}

}
