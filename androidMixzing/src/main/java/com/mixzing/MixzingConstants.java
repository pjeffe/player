package com.mixzing;

import android.os.Build;

import com.mixzing.android.AndroidUtil;

public class MixzingConstants {

	public static final boolean USE_SERIALIZING_MARSHALLER = false;
	
	public static final String INTENT_FROM_UPGRADE = "fromUpgrade";
	public static final String BASIC_PACKAGE_NAME = "com.mixzing.basic";
	public static final String UPGRADE_PACKAGE_NAME = "com.mixzing.upgrade";
	public static final String LICENSE_PROXY_SERVICE_NAME = "com.mixzing.upgrade.MarketLicenseService";
	public static final String MARKET_LICENSING_SERVICE = "com.android.vending.licensing.ILicensingService";
	public static final String UPGRADE_NON_MARKET_PACKAGE_NAME = "com.mixzing.upgradenm";
	public static final String UPGRADE_MARKET_1_5_PACKAGE_NAME = "com.mixzing.upgrade.old";
	public static final String BASIC_LAUNCH = "com.mixzing.music.BasicLaunch";
	public static final String UPGRADE_LAUNCH = "com.mixzing.music.UpgradeLaunch";
	public static final int UPGRADE_MARKET_ENABLED_VERSION_CODE = 7;

	public static final String MIXZING_NETWORK_LOG_FILE = "mixzing_error.log";
	public static final String MIXZING_LOG_TAG = "MixZing";
	public static final String LOCAL_LOG_FILE = "debug.log";
	public static final String ANALYTICS_LOG_FILE = "analytics.log";

	public static final String MEDIA_PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
	public static final String MEDIA_META_CHANGED = "com.android.music.metachanged";
	public static final String MEDIA_SOURCE = "com.mixzing.basic.mediaSource";

	public static final String ALARM_CLOCK_ALERT_CLASS = "AlarmAlertFullScreen";

	public static final int BASIC_MIN_UPGRADE_VERSION = 16;

	public static final String OS_TYPE = "com.mixzing.ostype";
	public static final long PLAYLIST_ID_FOR_SCROBBLES = -100;
	public static final long PLAYLIST_ID_FOR_WAKEUP = -200;
	 
	public static final long ONE_SECOND = 1000; 
	public static final long ONE_MINUTE = 60 * ONE_SECOND; 
	public static final long FIVE_MINUTE = 5 * ONE_MINUTE; 
	public static final long ONE_HOUR = 60 * ONE_MINUTE; 
	public static final long ONE_DAY = ONE_HOUR * 24;
	public static final long ONE_WEEK = ONE_DAY * 7;
	
	public static final String RECO_POSSIBLE = "RECOPOSSIBLE: "; // XXX : Referenced by commo thread
		
	public static final boolean ALLOW_RATINGS_WITH_LSID = false;
	public static final boolean ALLOW_LIB_ID_AS_POST_PARAM = false;

	public static final String MIXZING_SCROBBLE_ACTION = "com.mixzing.intent.action.SCROBBLE";
	public static final String MIXZING_RESOLVE_ACTION  = "com.mixzing.intent.action.RESOLVE";
	public static final String MIXZING_STATS_ACTION    = "com.mixzing.intent.action.STATS";
	public static final String MIXZING_REMOUNT_ACTION = "com.mixzing.intent.action.REMOUNT";
	
	public static final long STARTUP_RESOLVE_CHECK_DELAY     = MixzingConstants.ONE_MINUTE *  30;    // 30 minutes	
	public static final long STARTUP_SCROBBLE_DELAY = MixzingConstants.ONE_MINUTE *  40;    // 40 minutes
	public static final long STARTUP_STAT_CHECK_DELAY   = MixzingConstants.ONE_MINUTE *  50;    // 50 minutes

	public static final long INTERSTITIAL_DELAY = MixzingConstants.ONE_MINUTE * 15;

	public static final long LICENSE_GRACE_PERIOD_DAYS = 3;
	public static final long LICENSE_GRACE_PERIOD = LICENSE_GRACE_PERIOD_DAYS * ONE_DAY;
	public static final long LICENSE_TRIAL_PERIOD = 3 * ONE_DAY;
	public static final long LICENSE_REMINDER_PERIOD = ONE_DAY;
	
	// server params
	public static final String SERVER_PARAM_ERRORLOG_SEND_INTERVAL = "errdel";
	public static final String SERVER_PARAM_SCROBBLE_SEND_INTERVAL = "scrdel";
	public static final String SERVER_PARAM_STATS_SEND_INTERVAL = "stadel";
	public static final String SERVER_PARAM_USAGE_STATS_ENABLED = "staena";
	public static final String SERVER_PARAM_AUTORESOLVE_INTERVAL = "autres";
	public static final String SERVER_PARAM_VIDEORESOLVE_INTERVAL = "vidres";
	public static final String SERVER_PARAM_PKGRESOLVE_INTERVAL = "pkgres";
	public static final String SERVER_PARAM_LOCDEL = "locdel";
	public static final String SERVER_PARAM_AD_HIDE_TIME = "adhide";
	public static final String SERVER_PARAM_AD_DISPLAY_TIME = "adtime";
	public static final String SERVER_PARAM_AD_ORDER = "adordr";
	public static final String SERVER_PARAM_INTERSTITIAL_INTERVAL = "isint";      // mininum period between ads
	public static final String SERVER_PARAM_INTERSTITIAL_MAX = "ismax";           // maximum ads per day
	public static final String SERVER_PARAM_INTERSTITIAL_DISPLAY_TIME = "istime"; // time to show ad
	public static final String SERVER_PARAM_SPLASH_INTERVAL = "splint";           // minimum period between splash displays
	public static final String SERVER_PARAM_SPLASH_MAX = "splmax";                // maximum splash displays per day
	public static final String SERVER_PARAM_SPLASH_DISPLAY_TIME = "spltime";
	public static final String SERVER_PARAM_PLAY_SPLASH_MAX = "psplmax";          // maximum play splash displays per day
	public static final String SERVER_PARAM_SPLASH_TYPE = "spltype";              // type of splash ads to show
	public static final String SERVER_PARAM_SPONSORED_AUDIO = "spaud";
	public static final String SERVER_PARAM_SPONSORED_VIDEO = "spvid";
	public static final String SERVER_PARAM_SPONSORED_REFRESH_INTERVAL = "sprefr";
	public static final String SERVER_PARAM_LOCATION_PROVIDER_TYPE = "coarse";
	public static final String SERVER_PARAM_LICENSE_STATE = "licsta";
	public static final String SERVER_PARAM_LICENSE_KEY = "lickey";
	public static final String SERVER_PARAM_LICENSE = "licval";
	public static final String SERVER_PARAM_CURVERS = "curvers";
	public static final String SERVER_PARAM_MINVERS = "minvers";
	public static final String SERVER_PARAM_MARKET_LICENSE = "licmgd";
	public static final Object SERVER_PARAM_REINIT_LIBRARY = "rEiNiTlIb";
	public static final String SERVER_PARAM_FB_PERMS = "fbperm";
	public static final String SERVER_PARAM_RADIUM_APP_ID = "radid";
	// param values
	public static final String COARSE_LOCATION_PROVIDER_ON = "on";
	public static final String COARSE_LOCATION_PROVIDER_OFF = "off";
	public static final long   USAGE_STATS_DISABLED = 0;
	public static final long   USAGE_STATS_ENABLED = 1;
	public static final Object REINIT_LIB_TRUE = "TrUe";
	public static final String SERVER_PARAM_SPLASH_TYPE_STATIC = "s";
	public static final String SERVER_PARAM_SPLASH_TYPE_DEFAULT = SERVER_PARAM_SPLASH_TYPE_STATIC;
	public static final String SERVER_PARAM_DEFAULT_RADIUM_APP_ID = "dc9c983c0a1a4f9c9ef14081c5b19abe";

	public static final String MOBCLIX = "0";
	public static final String SMAATO = "1";
	public static final String ADMOB = "2";
	public static final String MILLENNIAL = "3";
	public static final String INHOUSE_UPGRADE = "4";
	public static final String INHOUSE_REMOTE = "5";
	// public static final String GOOGLE_ADSENSE = "6"; #6 Reserved for ADSENSE, which is now disabled, do not reuse for any other provider
	public static final String PONTIFLEX = "7";
	public static final String RHYTHM = "8";
	public static final String TRIBALFUSION = "9";
	public static final String AD_DEFAULT_ORDER_VALUE;

	// Service start flags, here for compatibility builds against older SDK
	// see http://developer.android.com/reference/android/app/Service.html#START_CONTINUATION_MASK
	public static final int START_NOT_STICKY = 2;

	static {
		// banner providers in priority order followed by static and video interstitial providers
		// Millennial requires Eclair
		final String interstitial = AndroidUtil.getSDK() >= Build.VERSION_CODES.ECLAIR ?
			MILLENNIAL + "," + MILLENNIAL :
			RHYTHM + "," + RHYTHM;

		AD_DEFAULT_ORDER_VALUE =
			MILLENNIAL + ":" + SMAATO + ";" + interstitial;
	}
}
