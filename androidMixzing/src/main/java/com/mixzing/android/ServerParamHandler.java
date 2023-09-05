package com.mixzing.android;

import java.util.HashMap;
import java.util.Map;

import com.mixzing.MixzingConstants;
import com.mixzing.log.Logger;

public class ServerParamHandler {

	protected static Logger lgr = Logger.getRootLogger();

	public static final String SERVER_PARAM_PREFIX = "srvr_prms.";

	protected HashMap<String, String> serverParamsString = new HashMap<String, String>();
	protected HashMap<String, Long>   serverParamsLong = new HashMap<String, Long>();
	protected static HashMap<String, ParamDef>   persistedKeys = new HashMap<String, ParamDef>();
	
	protected static enum ParamType {
		STRING,
		LONG
	}

	protected static class ParamDef {
		ParamDef(String n, String ds) {
			name = n;
			type = ParamType.STRING;
			defValueStr = ds;
		}
		ParamDef(String n, long dl, long ml) {
			name = n;
			type = ParamType.LONG;
			defValueLong = dl;
			minValue = ml;
		}
		String name;
		ParamType type;
		String defValueStr;
		long defValueLong;
		long minValue;
	}

	static {
		persistedKeys.put(MixzingConstants.SERVER_PARAM_LOCDEL, 
				          new ParamDef(MixzingConstants.SERVER_PARAM_LOCDEL,  
						               2 * MixzingConstants.ONE_HOUR,     // default
						               MixzingConstants.FIVE_MINUTE));    // minimum allowed
		
		persistedKeys.put(MixzingConstants.SERVER_PARAM_STATS_SEND_INTERVAL, 
				          new ParamDef(MixzingConstants.SERVER_PARAM_STATS_SEND_INTERVAL,  
						               MixzingConstants.ONE_DAY,          // default
						               MixzingConstants.ONE_HOUR * 4));   // minimum allowed
		
		persistedKeys.put(MixzingConstants.SERVER_PARAM_SCROBBLE_SEND_INTERVAL, 
				          new ParamDef(MixzingConstants.SERVER_PARAM_SCROBBLE_SEND_INTERVAL,  
				                       MixzingConstants.ONE_MINUTE * 40,  // defualt
				                       MixzingConstants.FIVE_MINUTE));    // minimum allowed
		
		
		persistedKeys.put(MixzingConstants.SERVER_PARAM_ERRORLOG_SEND_INTERVAL, 
				          new ParamDef(MixzingConstants.SERVER_PARAM_ERRORLOG_SEND_INTERVAL,  
				        		       MixzingConstants.ONE_MINUTE * 10,  // default
				        		       MixzingConstants.FIVE_MINUTE));    // minimum allowed
		
		persistedKeys.put(MixzingConstants.SERVER_PARAM_AUTORESOLVE_INTERVAL, 
		                  new ParamDef(MixzingConstants.SERVER_PARAM_AUTORESOLVE_INTERVAL, 
		        		               MixzingConstants.ONE_DAY * 7,       // default
		        		               MixzingConstants.ONE_HOUR * 2));    // minimum allowed
		
		persistedKeys.put(MixzingConstants.SERVER_PARAM_VIDEORESOLVE_INTERVAL, 
                new ParamDef(MixzingConstants.SERVER_PARAM_VIDEORESOLVE_INTERVAL, 
      		               MixzingConstants.ONE_DAY,       // default
      		               MixzingConstants.FIVE_MINUTE * 2));    // minimum allowed

		persistedKeys.put(MixzingConstants.SERVER_PARAM_AD_HIDE_TIME, 
                new ParamDef(MixzingConstants.SERVER_PARAM_AD_HIDE_TIME, 
      		               MixzingConstants.ONE_MINUTE,          // default
      		               0));   // minimum allowed

		persistedKeys.put(MixzingConstants.SERVER_PARAM_AD_DISPLAY_TIME,
                new ParamDef(MixzingConstants.SERVER_PARAM_AD_DISPLAY_TIME, 0, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_INTERSTITIAL_DISPLAY_TIME, 
                new ParamDef(MixzingConstants.SERVER_PARAM_INTERSTITIAL_DISPLAY_TIME, MixzingConstants.ONE_SECOND * 8, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_INTERSTITIAL_INTERVAL,
			new ParamDef(MixzingConstants.SERVER_PARAM_INTERSTITIAL_INTERVAL, MixzingConstants.ONE_MINUTE * 2, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_INTERSTITIAL_MAX,
			new ParamDef(MixzingConstants.SERVER_PARAM_INTERSTITIAL_MAX, 4, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_PLAY_SPLASH_MAX,
			new ParamDef(MixzingConstants.SERVER_PARAM_PLAY_SPLASH_MAX, 3, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_SPLASH_DISPLAY_TIME, 
                new ParamDef(MixzingConstants.SERVER_PARAM_SPLASH_DISPLAY_TIME,
                			MixzingConstants.ONE_SECOND * 8,
                			0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_SPLASH_INTERVAL,
                new ParamDef(MixzingConstants.SERVER_PARAM_SPLASH_INTERVAL, 2 * MixzingConstants.ONE_HOUR, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_SPLASH_MAX,
				new ParamDef(MixzingConstants.SERVER_PARAM_SPLASH_MAX, 2, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_SPLASH_TYPE,
				new ParamDef(MixzingConstants.SERVER_PARAM_SPLASH_TYPE,
							MixzingConstants.SERVER_PARAM_SPLASH_TYPE_DEFAULT));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_SPONSORED_AUDIO,
                new ParamDef(MixzingConstants.SERVER_PARAM_SPONSORED_AUDIO, 0, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_SPONSORED_VIDEO,
                new ParamDef(MixzingConstants.SERVER_PARAM_SPONSORED_VIDEO, 0, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_SPONSORED_REFRESH_INTERVAL, 
                new ParamDef(MixzingConstants.SERVER_PARAM_SPONSORED_REFRESH_INTERVAL,
                			MixzingConstants.ONE_DAY, // default
                			0));                      // minimum

		persistedKeys.put(MixzingConstants.SERVER_PARAM_PKGRESOLVE_INTERVAL, 
                new ParamDef(MixzingConstants.SERVER_PARAM_PKGRESOLVE_INTERVAL, 
                		   MixzingConstants.ONE_HOUR,       // default
      		               MixzingConstants.FIVE_MINUTE * 2));    // minimum allowed
		
		persistedKeys.put(MixzingConstants.SERVER_PARAM_LOCATION_PROVIDER_TYPE, 
				          new ParamDef(MixzingConstants.SERVER_PARAM_LOCATION_PROVIDER_TYPE,  
				        		       MixzingConstants.COARSE_LOCATION_PROVIDER_ON)); // default

		persistedKeys.put(MixzingConstants.SERVER_PARAM_AD_ORDER, 
		          new ParamDef(MixzingConstants.SERVER_PARAM_AD_ORDER,  
		        		       MixzingConstants.AD_DEFAULT_ORDER_VALUE)); // default
		
		persistedKeys.put(MixzingConstants.SERVER_PARAM_CURVERS,
			new ParamDef(MixzingConstants.SERVER_PARAM_CURVERS, -1, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_MINVERS,
			new ParamDef(MixzingConstants.SERVER_PARAM_MINVERS, -1, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_USAGE_STATS_ENABLED,
				new ParamDef(MixzingConstants.SERVER_PARAM_USAGE_STATS_ENABLED, MixzingConstants.USAGE_STATS_DISABLED, 0));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_FB_PERMS, new ParamDef(MixzingConstants.SERVER_PARAM_FB_PERMS, null));

		persistedKeys.put(MixzingConstants.SERVER_PARAM_RADIUM_APP_ID,
			new ParamDef(MixzingConstants.SERVER_PARAM_RADIUM_APP_ID, MixzingConstants.SERVER_PARAM_DEFAULT_RADIUM_APP_ID));
	}



	public ServerParamHandler() {
		for(ParamDef def : persistedKeys.values()) {
			String key = def.name;
			if(def.type == ParamType.STRING) {
				String value = AndroidUtil.getStringPref(null, SERVER_PARAM_PREFIX + key, def.defValueStr);
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("Loaded Server Param: " + key + "=" + value);
				}
				serverParamsString.put(key, value);	
			}
			if(def.type == ParamType.LONG) {
				Long value = null;

				value = AndroidUtil.getLongPref(null, SERVER_PARAM_PREFIX + key, def.defValueLong);

				serverParamsLong.put(key, value);	
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("Loaded Server Param: " + key + "=" + value);
				}

			}			
		}
	}

	/*
	 * 
	 * Merge the incoming server parameters with what we had, updating our map in 
	 * process.
	 * 
	 * We also persist the server parameters that we know about.
	 * 
	 */
	public synchronized void updateServerParams(Map<String, String> newParams) {

		for(String key : newParams.keySet()) {			
			String value = newParams.get(key);
			ParamDef def = persistedKeys.get(key);
			if(def != null) {
				if(def.type == ParamType.LONG) { // if long deal with minimum value and defaults 
					long lvalue;
					try {
						lvalue = Long.valueOf(value);
					} catch (Exception e) {
						lvalue = def.defValueLong;
					}
					// at this point lvalue should have the value we got from server or 
					// default value if not a valid long

					// make sure that the value we set is not smaller than the minimum
					if (def.minValue != 0) {
						lvalue = Math.max(lvalue, def.minValue);
					}

					Long oldValue = serverParamsLong.get(key);
					serverParamsLong.put(key, lvalue);
					if(lvalue != oldValue) {
						if(Logger.IS_DEBUG_ENABLED) {
							lgr.debug("Updated Server Param: " + key + "=" + lvalue);
						}
						readOnlyCopy = null;
						AndroidUtil.setLongPref(null, SERVER_PARAM_PREFIX + key, lvalue);
					}
				} else { // Its a String - check for null before setting
					String sValue = value;
					if(sValue == null) {		
						sValue = def.defValueStr;
					}
					String oldValue = serverParamsString.get(key);
					if(!sValue.equalsIgnoreCase(oldValue)) {
						if(Logger.IS_DEBUG_ENABLED) {
							lgr.debug("Updated Server Param: " + key + "=" + sValue);
						}
						readOnlyCopy = null;
						AndroidUtil.setStringPref(null, SERVER_PARAM_PREFIX + key, sValue);
					}
					serverParamsString.put(key, sValue);
				}
			} else {
				String oldVal = serverParamsString.get(key);
				if(!value.equalsIgnoreCase(oldVal)) {
					readOnlyCopy = null;
				}
				serverParamsString.put(key, value);
			}
		}
	}

	public synchronized String getServerParam(String key) {
		ParamDef def = persistedKeys.get(key);
		if(def != null) {
			if(def.type == ParamType.LONG) {
				return serverParamsLong.get(key) == null ? null : serverParamsLong.get(key).toString();
			}
		} 
		return serverParamsString.get(key);
	}

	public synchronized long getLongValue(String key) {
		Long l =  serverParamsLong.get(key);
		if(l == null) {
			ParamDef def = persistedKeys.get(key);
			if(def != null && def.type == ParamType.LONG) {
				return def.defValueLong;
			}
			return 0;
		} else {
			return l.longValue();
		}
	}

	public synchronized String getStringValue(String key) {
		return serverParamsString.get(key);
	}

	protected HashMap<String, String> readOnlyCopy = null;
	public synchronized Map<String, String> getServerParameters() {
		if(readOnlyCopy == null) {
			readOnlyCopy = new HashMap<String, String>(serverParamsString);
			for(String key : serverParamsLong.keySet()) {
				readOnlyCopy.put(key, serverParamsLong.get(key).toString());
			}
		}
		return readOnlyCopy;
	}


}
