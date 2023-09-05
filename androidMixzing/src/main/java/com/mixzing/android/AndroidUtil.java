package com.mixzing.android;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.mixmoxie.util.XProperties;
import com.mixzing.MixzingAppProperties;
import com.mixzing.MixzingConstants;
import com.mixzing.android.Preferences.Keys;
import com.mixzing.data.DeletedPlaylists;
import com.mixzing.log.Logger;

public class AndroidUtil {
	private static final Logger log = Logger.getRootLogger();
	private static String uuid;
	private static String deviceId = "";
	private static String imei;
	private static String pkgName = "";
	private static PackageInfo pkgInfo;
	private static int versionCode = -1;
	private static String versionName = null;
	private static Context app;
	private static XProperties props;
	private static String errorLogFile;
	private static String logTag;
	private static int prevVers;
	private static boolean updated;
	private static boolean installed;
	private static boolean tablet;
	protected static Handler scrobbleHandler;
	protected static Runnable scrobbleRunnable;
	protected static ServerParamHandler srvrParams;
	protected static String[] musicDirsCache;
	protected static Object dirLock = new Object();
	protected static RecommendCache recommendCache;
	private static boolean useDeprecatedGetWallpaper = false;
	private static Boolean externalInstall;
	private static SharedPreferences prefs;
	private static DeletedPlaylists dp;
	private static ConnectivityManager conMgr;
	private static Boolean amazonDevice;

	public static final String PLAYED_SONG_DELIM = ",";
	public static final String PLAYED_SONG_SEP = ":";
	public static final long SCROBBLE_DELAY = 20000; // wait this long to consider song as being played
	private static final String MNT = File.separator + "mnt" + File.separator;

	private static final int SYNC_BIT = 1 << 7;
	private static final int ID3v2_HEADER_LENGTH = 10;

	public static final int PREFS_OK = 0;
	public static final int PREFS_MISSING = 1;
	public static final int PREFS_WRONG_USER = 2;
	public static final int PREFS_WRONG_MODE = 3;

	public static final String MUSIC_DIR_DELIMITER = ":"; // NB used in RE patterns to split string

	private static final String MARKET_GOOGLE = "market://details?id=";
	private static final String MARKET_AMAZON = "amzn://apps/android?p=";


	public static void init(Context context, XProperties xprops, String errLogFile, String tag) {
		app = context;
		props = xprops;
		logTag = tag;
		errorLogFile = errLogFile;
	}

	public static String getErrorLogFile() {
		return errorLogFile;
	}

	public static String getLogTag() {
		return logTag;
	}

	public static XProperties getProperties() {
		assert(props != null);
		return props;
	}

	public static Context getAppContext() {
		return app;
	}

	public static String getUuid() {
		String uuid = AndroidUtil.uuid;
		if (uuid == null) {
			uuid = getStringPref(app, Keys.UUID, null);
			if (uuid == null) {
				uuid = UUID.randomUUID().toString();
				setStringPref(app, Keys.UUID, uuid);
			}
			AndroidUtil.uuid = uuid;
		}
		return uuid;
	}

	public static synchronized String getDeviceId() {
		String deviceId = AndroidUtil.deviceId;
		if (deviceId == "") {
			// ideally we would try SERIAL first for all devices, but that would orphan installs that already use ANDROID_ID
			final boolean isAmazonDevice = isAmazonDevice();
			if (isAmazonDevice && Build.VERSION.SDK_INT >= 9) {
				deviceId = Build.SERIAL;
				if (Logger.IS_DEBUG_ENABLED) {
					log.debug("AndroidUtil.getDeviceId: serial = " + deviceId);
				}
			}

			if (deviceId == null || deviceId.length() == 0) {
				deviceId = Settings.Secure.getString(app.getContentResolver(), Settings.Secure.ANDROID_ID);
				if (Logger.IS_DEBUG_ENABLED) {
					log.debug("AndroidUtil.getDeviceId: ANDROID_ID = " + deviceId);
				}
				if (deviceId == null || deviceId.length() == 0) {
					// try serial number if available
					if (!isAmazonDevice && Build.VERSION.SDK_INT >= 9) {
						deviceId = Build.SERIAL;
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("AndroidUtil.getDeviceId: serial = " + deviceId);
						}
					}

					if (deviceId == null || deviceId.length() == 0) {
						// no id on this device: see if we have a saved generated one in prefs
						deviceId = getStringPref(app, Preferences.Keys.DEVICE_ID, null);
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("AndroidUtil.getDeviceId: from prefs = " + deviceId);
						}
						if (deviceId == null) {
							// new install: generate one and save it in prefs
							deviceId = "U-" + UUID.randomUUID();
							setStringPref(app, Preferences.Keys.DEVICE_ID, deviceId);
						}
					}
				}
			}
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("AndroidUtil.getDeviceId: deviceId = " + deviceId);
			}
		}

		// cache and return it
		AndroidUtil.deviceId = deviceId;
		return deviceId;
	}

	public static String getImei() {
		String imei = AndroidUtil.imei;
		if (imei == null) {
			if (isAmazonDevice()) {
				imei = AndroidUtil.imei = getDeviceId();
			}
			else {
				try {
					// try to get it from the telephone manager
					imei = ((TelephonyManager)app.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("AndroidUtil.getImei: imei from mgr = " + imei);
					}
					if (imei != null) {
						if ("000000000000000".equals(imei)) {
							imei = null;
						}
						else {
							// cache valid imei and save in prefs
							AndroidUtil.imei = imei;
							setStringPref(app, Preferences.Keys.IMEI, imei);
						}
					}
				}
				catch (Exception e) {
					log.error("AndroidUtil.getImei: error getting imei:", e);
				}
	
				if (imei == null) {
					// emulator, non-phone or other failure (e.g. no network since boot): see if we have a saved one in prefs
					imei = getStringPref(app, Preferences.Keys.IMEI, null);
					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("AndroidUtil.getImei: imei from prefs = " + imei);
					}
					if (imei == null) {
						// see if we have saved a generated one
						imei = getStringPref(app, Preferences.Keys.GENERATED_IMEI, null);
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("AndroidUtil.getImei: generated imei from prefs = " + imei);
						}
						if (imei == null) {
							// new install: generate one and save it in prefs
							imei = "U-" + UUID.randomUUID();
							setStringPref(app, Preferences.Keys.GENERATED_IMEI, imei);
						}
					}
				}
			}

			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("AndroidUtil.getImei: imei = " + imei);
			}
		}
		return imei;
	}

	public static int getLibraryId() {
		int id = -1;
		final String libid = getStringPref(null, getCardSpecificPrefKey(Keys.LIB_ID), null);
		if (libid != null) {
			final String[] toks = libid.split(":");
			try {
				id = Integer.parseInt(toks[0]);
			}
			catch (Exception e) {
				log.error("AndroidUtil.getLibraryId: libid = " + libid + ":", e);
			}
		}
		return id;
	}

	public static synchronized DeletedPlaylists getDeletedPlaylistHandler() {
		if (dp == null) {
			dp = new DeletedPlaylists(app, true);
		}
		return dp;
	}

	// userid for internal use (e.g. for analytics) is deviceId + imei
	public static String getUserId() {
		String userid = getStringPref(app, Preferences.Keys.USERID, null);
		if (userid == null) {
			userid = getDeviceId() + "-" + getImei();

			if (Logger.IS_DEBUG_ENABLED)
				log.debug("AndroidUtil.getUserId: userid = " + userid);

			setStringPref(app, Preferences.Keys.USERID, userid);
		}

		return userid;
	}

	public static String getServerUserId() {
		final String libid = getStringPref(null, getCardSpecificPrefKey(Keys.LIB_ID), null);
		return libid + "|" + getUserId();
	}

	public static String getAdUserId() {
		String userId = null;
		final TelephonyManager telManager = (TelephonyManager)app.getSystemService(Context.TELEPHONY_SERVICE);
		if (telManager != null) {
			userId = telManager.getDeviceId();
		}
		if (userId != null && (userId.length() == 0 || userId.equals("0") || userId.equals("000000000000000"))) {
			userId = null;
		}
		if (userId == null) {
			userId = Secure.getString(app.getContentResolver(), Secure.ANDROID_ID);
		}
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("AndroidUtil.getAdUserId: userid = " + userId);
		}
		return userId;
	}

	public static PackageInfo getPackageInfo(Context context) {
		if (pkgInfo == null) {
			try {
				if (pkgName == "") {
					pkgName = context.getPackageName();
				}
				if (pkgName != null) {
					pkgInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
				}
				else {
					pkgName = "";
				}
			}
			catch (Exception e) {
				Log.e("MixZing", "AndroidUtil.getPackageInfo: exception getting package info:" + e);
			}
		}
		return pkgInfo;
	}

	public static int getVersionCode() {
		return getVersionCode(app);
	}

	public static int getVersionCode(Context context) {
		if (versionCode != -1 || context == null) {
			return versionCode;
		}
		if (pkgInfo == null) {
			getPackageInfo(context);
		}
		if (pkgInfo != null) {
			versionCode = pkgInfo.versionCode;
		}
		return versionCode;
	}

	public static String getVersionName() {
		return getVersionName(app);
	}

	public static String getVersionName(Context context) {
		if (versionName != null || context == null) {
			return versionName;
		}
		if (pkgInfo == null) {
			getPackageInfo(context);
		}
		if (pkgInfo != null) {
			versionName = pkgInfo.versionName;
		}
		return versionName;
	}

	public static String getPackageName() {
		return getPackageName(app);
	}

	public static String getPackageName(Context context) {
		if (pkgName == "") {
			getPackageInfo(context);
		}
		return pkgName;
	}

	public static int getSDK() {
		return Build.VERSION.SDK_INT;
	}

	public static SharedPreferences getPrefs() {
		if (prefs == null) {
			prefs = PreferenceManager.getDefaultSharedPreferences(app);
		}
		return prefs;
	}

	public static Editor getPrefsEditor() {
		return getPrefs().edit();
	}

	public static void registerPrefsListener(OnSharedPreferenceChangeListener listener) {
		getPrefs().registerOnSharedPreferenceChangeListener(listener);
	}

	public static void unregisterPrefsListener(OnSharedPreferenceChangeListener listener) {
		getPrefs().unregisterOnSharedPreferenceChangeListener(listener);
	}

	public static void setStringPref(Context context, String key, String val) {
		final Editor editor = getPrefsEditor();
		editor.putString(key, val);
		editor.commit();
	}

	public static void setFloatPref(Context context, String key, float val) {
		final Editor editor = getPrefsEditor();
		editor.putFloat(key, val);
		editor.commit();
	}

	public static void setIntPref(Context context, String key, int val) {
		final Editor editor = getPrefsEditor();
		editor.putInt(key, val);
		editor.commit();
	}

	public static void setLongPref(Context context, String key, long val) {
		final Editor editor = getPrefsEditor();
		editor.putLong(key, val);
		editor.commit();
	}

	public static void setBooleanPref(Context context, String key, boolean val) {
		final Editor editor = getPrefsEditor();
		editor.putBoolean(key, val);
		editor.commit();
	}

	public static void removePref(Context context, String key) {
		final Editor editor = getPrefsEditor();
		editor.remove(key);
		editor.commit();
	}

	public static String getStringPref(Context context, String key, String def) {
		return getPrefs().getString(key, def);
	}

	public static int getIntPref(Context context, String key, int def) {
		return getPrefs().getInt(key, def);
	}

	public static long getLongPref(Context context, String key, long def) {
		return getPrefs().getLong(key, def);
	}

	public static float getFloatPref(Context context, String key, float def) {
		return getPrefs().getFloat(key, def);
	}

	public static boolean getBooleanPref(Context context, String key, boolean def) {
		return getPrefs().getBoolean(key, def);
	}

	public static boolean prefsContain(Context context, String key) {
		return getPrefs().contains(key);
	}

	// set web view to all black
	public static void blankWebView(WebView wv) {
		String blankPage = "<html><body style=\"background-color: black;\"></body></html>";
		wv.loadData(blankPage, "text/html", "utf-8");
		wv.invalidate();
	}

	public static boolean isMediaReady() {
		boolean isMounted = SdCardHandler.isMounted();
		boolean mediaScanning = isMediaScanning(app);
		boolean ready = isMounted && !mediaScanning;
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("AndroidUtil.isMediaReady: ready=" + ready + " mounted=" + isMounted + " mediaScanning=" + mediaScanning);	
		}
		return ready;
	}

	public static boolean isMediaScanning(Context context) {
		boolean result = false;
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver != null) {
				Uri uri = MediaStore.getMediaScannerUri();
				Cursor cursor = resolver.query(uri, new String[] { MediaStore.MEDIA_SCANNER_VOLUME }, null, null, null);
				if (cursor != null) {
					if (cursor.getCount() == 1) {
						cursor.moveToFirst();
						final String vol = SdCardHandler.getVolume();
						result = vol.equals(cursor.getString(0));
					}
					else {
						if (Logger.IS_DEBUG_ENABLED)
							log.debug("AndroidUtil.isMediaScanning: cursor count = " + cursor.getCount());
					}
					cursor.close();
				}
				else {
					if (Logger.IS_DEBUG_ENABLED)
						log.debug("AndroidUtil.isMediaScanning: null cursor");
				}
			}
			else {
				log.error("AndroidUtil.isMediaScanning: null resolver");
			}
		}
		catch (Exception e) {
			log.error("AndroidUtil.isMediaScanning: exception:", e);
		}

		return result;
	}

	public static void dumpView(View view) {
		dumpView(view, "  ");
	}

	public static void dumpView(View view, String prefix) {
		log.debug(String.format("%s%08x: %s %d", prefix, view.getId(), view, view.getVisibility()));
		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup)view;
			int num = vg.getChildCount();
			String pfx = prefix + "  ";
			for (int i = 0; i < num; ++i) {
				dumpView(vg.getChildAt(i), pfx);
			}
		}
	}

	public static String getViewState(View view) {
		StringBuilder sb = new StringBuilder();
		String delim = "";
		int[] state = view.getDrawableState();
		for (int i : state) {
			sb.append(delim);
			sb.append(Integer.toHexString(i));
			delim = " | ";
		}
		return sb.toString();
	}

	public static NetworkInfo[] getAllNetWorkInfo() {
		if (conMgr == null) {
			conMgr = (ConnectivityManager)app.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		return conMgr.getAllNetworkInfo();
	}

	public static NetworkInfo getNetWorkInfo() {
		if (conMgr == null) {
			conMgr = (ConnectivityManager)app.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		return conMgr.getActiveNetworkInfo();
	}

	public static boolean hasNetwork() {
		final NetworkInfo ni = getNetWorkInfo();
		return ni != null && ni.getState() == NetworkInfo.State.CONNECTED;
	}

	public static PackageInfo getPackageInfo(Context context, String pkg, int flags) {
		try {
			return context.getPackageManager().getPackageInfo(pkg, flags);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static boolean isPackageInstalled(Context context, String pkg) {
		return getPackageInfo(context, pkg, 0) != null;
	}

	public static int getOrientation() {
		return app.getResources().getConfiguration().orientation;
	}

	public static DisplayMetrics getDisplayMetrics() {
		return app.getResources().getDisplayMetrics();
	}

	public static String getCardSpecificPrefKey(String key) {
		return getCardSpecificPrefKey(key, SdCardHandler.getCardId());
	}

	public static String getCardSpecificPrefKey(String key, int cardId) {
		return key + "-" + cardId;
	}

	public static String getAndZeroPlayedSongList() {
		String list;
		synchronized (log) {
			String key = getCardSpecificPrefKey(Preferences.Keys.PLAYED_SONGS);
			list = getStringPref(app, key, "");
			if (!list.equals("")) {
				setStringPref(app, key, "");
			}
		}
		return list;
	}

	public static String getPlayedSongList() {
		String list = null;
		synchronized (log) {
			String key = getCardSpecificPrefKey(Preferences.Keys.PLAYED_SONGS);
			list = getStringPref(app, key, "");
		}
		return list;
	}

	public static boolean isPlayedSongListPopulated() {
		String list;
		synchronized (log) {
			String key = getCardSpecificPrefKey(Preferences.Keys.PLAYED_SONGS);
			list = getStringPref(app, key, "");
			if (!list.trim().equals("")) {
				return true;
			}
		}
		return false;
	}

	public static class Scrobble {
		public Scrobble(String i, long t) {
			id = i;
			time = t;
		}

		String id;
		long time;

		public String toString() {
			return time + PLAYED_SONG_SEP + id;
		}
	}

	private static long MIN_DISTANCE_BETWEEN_SONGS = 45 * 1000;
	private static long MIN_DISTANCE_BETWEEN_SCROBBLES = MixzingConstants.ONE_MINUTE * 10;
	private static long MAX_SCROBBLE_LIST_LENGTH = 2048;

	public static ArrayList<Scrobble> getScrobblesFromPlayedList(String playedSongs) {
		StringTokenizer tok = new StringTokenizer(playedSongs, PLAYED_SONG_DELIM);
		ArrayList<Scrobble> scrobbles = new ArrayList<Scrobble>();

		long lastTimePlayed = 0L;

		while (tok.hasMoreTokens()) {

			String token = tok.nextToken();
			if (token == null || token.equals("")) {
				continue;
			}

			StringTokenizer tok2 = new StringTokenizer(token, PLAYED_SONG_SEP);

			String time = null;
			if (tok2.hasMoreTokens()) {
				time = tok2.nextToken();
			}
			String id = null;
			if (tok2.hasMoreTokens()) {
				id = tok2.nextToken();
			}

			long timePlayed = lastTimePlayed;

			try {
				timePlayed = Long.valueOf(time);
			}
			catch (Exception e) {
				log.error("AndroidUtil.addNewSongAndRemoveSkips: bad time value " + time);
				timePlayed = lastTimePlayed;
			}
			scrobbles.add(new Scrobble(id, timePlayed));
		}
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("AndroidUtil.getScrobblesFromPlayedList: returning size " + scrobbles.size());
		}
		return scrobbles;
	}

	private static String addNewSongAndRemoveSkips(String playedsongs, int newSong) {

		ArrayList<Scrobble> scrobbles = getScrobblesFromPlayedList(playedsongs);
		long curTime = System.currentTimeMillis();

		for (int i = scrobbles.size() - 1; i >= 0; i--) {
			Scrobble scrob = scrobbles.get(i);
			if (curTime - scrob.time < MIN_DISTANCE_BETWEEN_SONGS) {
				scrobbles.remove(i);
			}
			else {
				break;
			}
		}
		scrobbles.add(new Scrobble("" + newSong, curTime));
		StringBuffer ret = new StringBuffer();
		ret.append("");
		for (Scrobble scrob : scrobbles) {
			ret.append(scrob.toString());
			ret.append(PLAYED_SONG_DELIM);
		}
		return ret.toString();
	}

	public static boolean addPlayedSong(int newsong, boolean ignoreDups) {
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("AndroidUtil.addPlayedSong: " + newsong);
		}
		boolean isAdded = false;
		synchronized (log) {
			final String lastPlayedKey = getCardSpecificPrefKey(Preferences.Keys.LAST_PLAYED_SONG);
			if (ignoreDups) {
				// ignore repeated plays of the same song
				final int lastSong = getIntPref(null, lastPlayedKey, -1);
				if (lastSong == newsong) {
					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("AndroidUtil.addPlayedSong: ignoring repeated song");
					}
					return false;
				}
			}
			setIntPref(null, lastPlayedKey, newsong);

			final String key = getCardSpecificPrefKey(Preferences.Keys.PLAYED_SONGS);
			String playedSongs = getStringPref(app, key, "");
			if (playedSongs.length() < MAX_SCROBBLE_LIST_LENGTH) {
				setStringPref(app, key, addNewSongAndRemoveSkips(playedSongs, newsong));
				isAdded = true;
			}
		}
		return isAdded;
	}

	// TODO: remove dead code; add source param to scrobble messages
	public static void registerScrobbleService(Runnable svc, Handler handler) {
		scrobbleRunnable = svc;
		scrobbleHandler = handler;
		if (scrobbleHandler != null && scrobbleRunnable != null) {
			scrobbleHandler.postDelayed(scrobbleRunnable, MIN_DISTANCE_BETWEEN_SCROBBLES);
		}
	}

	public static void unRegisterScrobbleService() {
		if (scrobbleHandler != null && scrobbleRunnable != null) {
			scrobbleHandler.removeCallbacks(scrobbleRunnable);
		}
		scrobbleRunnable = null;
		scrobbleHandler = null;
	}

	public static void processScrobble() {
		if (scrobbleHandler != null && scrobbleRunnable != null) {
			long lastScrobTime = getLongPref(app, Preferences.Keys.LAST_SCROBBLE_TIME, 0);
			long curTime = System.currentTimeMillis();
			long nextScrobTime = Math.max(lastScrobTime + MIN_DISTANCE_BETWEEN_SCROBBLES,
					curTime + MIN_DISTANCE_BETWEEN_SONGS);
			long delay = nextScrobTime - curTime;
			delay = Math.max(delay, MIN_DISTANCE_BETWEEN_SONGS);
			scrobbleHandler.removeCallbacks(scrobbleRunnable);
			scrobbleHandler.postDelayed(scrobbleRunnable, delay);
		}
	}

	// check if we've been freshly installed or updated, return true if we were updated
	public static boolean checkUpdate() {
		prevVers = getIntPref(null, Preferences.Keys.LAST_CODE_VERSION, -1);
		int curVers = getVersionCode();

		if (Logger.IS_DEBUG_ENABLED)
			log.debug("AndroidUtil.checkUpdate: last version = " + prevVers + ", current = " + curVers);

		if (curVers != prevVers) {
			final String event;
			if (prevVers == -1) {
				installed = true;
				event = Analytics.EVENT_INSTALL;
			}
			else {
				updated = true;
				event = Analytics.EVENT_UPDATE;
			}
			Analytics.event(event, Analytics.DATA_VERSION, Integer.toString(curVers));

			// update prefs with current version
			setIntPref(null, Preferences.Keys.LAST_CODE_VERSION, curVers);
		}

		return updated;
	}

	public static boolean isUpdated() {
		return updated;
	}

	public static boolean isInstalled() {
		return installed;
	}

	public static int getPrevVersion() {
		return prevVers;
	}

	public static void logDeviceInfo() {
		final HashMap<String, String> args = DeviceInfo.getDeviceInfo();
		Analytics.event(Analytics.EVENT_DEVICE_INFO, args);
	}

	public static long fmtLocation(StringBuilder sb, Location loc, boolean encode) {
		final String delim = encode ? Uri.encode("|") : "|";
		long now = System.currentTimeMillis();
		long fixTime = loc.getTime();
		long stale = now - fixTime; // how old is this fix wrt curtime

		sb.append(loc.getLatitude());
		sb.append(delim);
		sb.append(loc.getLongitude());
		sb.append(delim);
		sb.append(loc.getAltitude());
		sb.append(delim);
		sb.append(loc.getAccuracy());
		sb.append(delim);
		sb.append(fixTime);
		sb.append(delim);
		sb.append(loc.getProvider());
		sb.append(delim);
		sb.append(stale);

		if (Logger.IS_DEBUG_ENABLED)
			log.debug("AndroidUtil.fmtLocation: now = " + now + ", loc = " + sb.toString());

		return now;
	}

	public static String getMd5Hash(String input) {
		try {
			return getMd5Hash(input.getBytes());
		}
		catch (Exception e) {
			return null;
		}
	}

	public static String getMd5Hash(byte[] input) {
		try {

			final MessageDigest md = MessageDigest.getInstance("MD5");
			final byte[] messageDigest = md.digest(input);
			final BigInteger number = new BigInteger(1, messageDigest);
			final String str = number.toString(16);
			int len = str.length();
			final StringBuilder md5 = new StringBuilder();
			while (len < 32) {
				md5.append('0');
				++len;
			}
			md5.append(str);
			return md5.toString();
		}
		catch (NoSuchAlgorithmException e) {
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("Could not gen md5" + e.getMessage());
			}
		}
		return null;
	}

	public static final byte[] readArray(RandomAccessFile ra, int length) throws IOException {
		byte result[] = new byte[length];
		int total = 0;
		while (total < length) {
			int read = ra.read(result, total, length - total);
			if (read < 0)
				throw new IOException("bad read");
			total += read;
		}
		return result;
	}

	public static final byte[] readArray(RandomAccessFile ra, int length, int chunk, long skip) throws IOException {
		byte result[] = new byte[length];
		int total = 0;

		long cur = ra.getFilePointer();

		int j = 1;
		while (total < length) {
			int thisChunk = 0;
			while (thisChunk < chunk) {
				int read = ra.read(result, total, chunk - thisChunk);
				if (read < 0)
					throw new IOException("bad read");
				thisChunk += read;
				total += read;
			}
			// System.out.println("null:Seeking_to_" + (cur + total + skip * j));
			ra.seek(cur + total + skip * j);
			j++;
		}

		return result;
	}

	protected static int isMp3FileSkippingZeros(byte[] data) {
		byte one = (byte)0xff;
		byte twoOne = (byte)0xfa;
		byte twoTwo = (byte)0xfb;
		byte twoThree = (byte)0xf3;
		byte twoFour = (byte)0xf2;
		byte zero = (byte)0x00;
		for (int i = 0; i < data.length - 1; i++) {
			if (data[i] == zero) {
				continue;
			}
			if (data[i] != one) {
				break;
			}
			else {
				byte second = data[i + 1];
				if (second != twoOne && second != twoTwo && second != twoThree && second != twoFour) {
					break;
				}
				return i;
			}
		}
		return -1;
	}

	public static Number readSynchsafeInt(byte bytes[], int start) {
		if ((start + 3) >= bytes.length) {
			return null;
		}

		int index = start;
		int array[] = { 0xff & bytes[index++], //
				0xff & bytes[index++], //
				0xff & bytes[index++], //
				0xff & bytes[index++], //
		};

		for (int i = 0; i < array.length; i++) {
			if ((array[i] & SYNC_BIT) > 0) {
				array[i] &= SYNC_BIT;
			}
		}
		int result = (array[0] << 21) | (array[1] << 14) | (array[2] << 7)
				| (array[3] << 0);
		return Integer.valueOf(result);
	}

	/*
	 * And ID3v2 Encapsulated file starts with bytes 
	 * ID3 followed by 2 bytes of version, 1 byte of flags and 4 bytes of length 
	 * 
	 */
	protected static int getId3v2Length(byte[] header, long fileSize) {
		if (header[0] != 0x49) // I
			return 0;
		if (header[1] != 0x44) // D
			return 0;
		if (header[2] != 0x33) // 3
			return 0;

		int flags = header[5];
		boolean has_footer = (flags & (1 << 4)) > 0;

		Number tagLength = readSynchsafeInt(header, 6);
		if (tagLength == null)
			return 0;

		int bodyLength = tagLength.intValue();
		if (has_footer)
			bodyLength += ID3v2_HEADER_LENGTH;

		if (ID3v2_HEADER_LENGTH + bodyLength > fileSize)
			return 0;

		return bodyLength;

	}

	/*
	 * 
	 * Check if the mp3 header pattern of 0xfff (SYNC) followed by
	 * 0xA or 0xB is at the start of the file.
	 * 
	 */
	protected static boolean isMp3FileWithoutId3V2Tags(byte[] dest) {
		byte one = (byte)0xff;
		byte twoOne = (byte)0xfa;
		byte twoTwo = (byte)0xfb;

		if (dest[0] != one) {
			return false;
		}
		if (dest[1] != twoOne && dest[1] != twoTwo) {
			return false;
		}

		return true;
	}

	public static boolean isMp3File(String name) {
		if (name != null && name.toLowerCase(Locale.US).endsWith(".mp3")) {
			final File file = new File(name);
			return file.exists();
		}
		return false;
	}

	public static String getMp3DataChecksum(String file) {
		if (file != null)
			return getMp3DataChecksum(new File(file));
		else
			return null;
	}

	public static String getMp3DataChecksum(String file, int dataLen) {
		return getMp3DataChecksum(new File(file), dataLen);
	}

	public static String getMp3DataChecksum(File file) {
		return getMp3DataChecksum(file, 4096 * 8);
	}

	/*
	 * 
	 * Create a checksum for MP3 files skipping over any ID3v2 tags that may be at the start of
	 * the file. 
	 * 
	 */
	/*
	 * 
	 * Create a checksum for MP3 files skipping over any ID3v2 tags that may be at the start of
	 * the file. 
	 * 
	 */
	public static String getMp3DataChecksum(File file, int dataLen) {

		// InputStream is = null;
		RandomAccessFile is = null;
		try {

			if (file == null || !file.exists())
				return null;

			long fileLen = file.length();

			int NUMCHUNKS = 8;
			int SKIP = 8 * 4096;
			int chunk = dataLen / NUMCHUNKS;
			int bodyLengthNeeded = dataLen + (NUMCHUNKS - 1) * SKIP;

			if (fileLen < bodyLengthNeeded + 8192)
				return null;

			// is = new FileInputStream(file);
			// is = new BufferedInputStream(is);

			is = new RandomAccessFile(file, "r");

			byte header[];
			header = readArray(is, ID3v2_HEADER_LENGTH);

			int bodyLength = getId3v2Length(header, fileLen);
			if (bodyLength > 0) {
				if (fileLen < bodyLengthNeeded + ID3v2_HEADER_LENGTH + bodyLength) {
					return null;
				}
				is.seek(bodyLength + ID3v2_HEADER_LENGTH);
			}
			else {
				is.seek(0L);
			}

			long curPos = is.getFilePointer();
			byte[] data = readArray(is, 1024);

			if (data == null) {
				return null;
			}

			int startpos = isMp3FileSkippingZeros(data);

			if (startpos == -1) {
				return null;
			}
			else {
				is.seek(curPos + startpos);
			}

			byte[] dest = readArray(is, dataLen, chunk, SKIP);

			/*
				String data1 = "";
				for (int i = 0; i < 128;) {
					String word = "";
					for (int j = 0; j < 4; j++, i++) {
						byte b = dest[i];
						word += String.format("%02x", b);
					}
					data1 = data1 + " " + word;
				}
				// System.out.println(isMp3FileWithoutId3V2Tags(dest) + " : " + data);
			 */

			return getMd5Hash(dest);

		}
		catch (Exception e) {

			return null;

		}
		finally {
			try {
				if (is != null)
					is.close();
			}
			catch (IOException e) {

			}
		}
	}

	protected final static String[] idCols;
	protected final static String idWhere;
	static {
		idCols = new String[] { Media.DATA };
		idWhere = Media.IS_MUSIC + "=1 AND " + Media._ID + "= ";
	}

	public static String getSourceIdForAndroidId(Context context, int id) {
		final Cursor cur = MediaStoreUtil.query(context, Media.getContentUri(SdCardHandler.getVolume()), idCols,
				idWhere + id, null, null);
		if (cur != null) {
			try {
				if (cur.moveToFirst()) {
					String s = cur.getString(0);
					if (s != null) {
						return getMd5Hash(s);
					}
				}
				if (Logger.IS_DEBUG_ENABLED)
					log.debug("AndroidUtil.getSourceIdForAndroidId: got " + cur.getCount() + " tracks from query");
			}
			catch (Exception e) {
			}
			finally {
				cur.close();
			}
		}
		else {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug("AndroidUtil.getSourceIdForAndroidId: null cursor");
		}
		return null;
	}

	public static void zeroOutPlayedSongsThroughTime(long lastScrobbleEventTime) {
		synchronized (log) {
			final String key = getCardSpecificPrefKey(Preferences.Keys.PLAYED_SONGS);
			String playedsongs = getStringPref(app, key, "");

			ArrayList<Scrobble> scrobbles = getScrobblesFromPlayedList(playedsongs);
			StringBuffer ret = new StringBuffer();
			ret.append("");
			for (Scrobble scrob : scrobbles) {
				if (scrob.time > lastScrobbleEventTime) {
					ret.append(scrob.toString());
					ret.append(PLAYED_SONG_DELIM);
				}
			}
			setStringPref(app, key, ret.toString());
		}
	}

	public static ServerParamHandler getParamHandler() {
		if (srvrParams == null) {
			srvrParams = new ServerParamHandler();
		}
		return srvrParams;
	}

	public static long getStartupUsageStatsDelay() {
		return MixzingConstants.STARTUP_STAT_CHECK_DELAY;
	}

	public static long getStartupScrobbleDelay() {
		return MixzingConstants.STARTUP_SCROBBLE_DELAY;
	}

	public static long getStartupResolveDelay() {
		return MixzingConstants.STARTUP_RESOLVE_CHECK_DELAY;
	}

	public static long getMinDelayBetweenScrobbles() {
		return getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_SCROBBLE_SEND_INTERVAL);
	}

	public static long getMinDelayBetweenAutoResolves() {
		return getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_AUTORESOLVE_INTERVAL);
	}

	public static long getMinDelayBetweenStats() {
		return getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_STATS_SEND_INTERVAL);
	}

	public static boolean isUsageStatsEnabled() {
		return  getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_USAGE_STATS_ENABLED) == 1;
	}

	public static long getMinDelayBetweenVideoResolves() {
		return getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_VIDEORESOLVE_INTERVAL);
	}

	public static long getMinDelayBetweenPkgResolves() {
		return getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_PKGRESOLVE_INTERVAL);
	}

	public static long getAdDisplayTime() {
		return getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_AD_DISPLAY_TIME);
	}

	public static long getAdHideTime() {
		return getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_AD_HIDE_TIME);
	}

	public static String getAdDisplayOrder(boolean banner) {
		String param = getParamHandler().getStringValue(MixzingConstants.SERVER_PARAM_AD_ORDER);
		if (param != null) {
			final String[] toks = param.split(";");
			if (toks.length == 2) {
				param = toks[banner ? 0 : 1];
			}
		}
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("AndroidUtil.getAdDisplayOrder: banner = " + banner + ", returning <" + param + ">");
		}
		return param;
	}

	public static boolean isSponsoredContent(boolean audio) {
		final String param = audio ? MixzingConstants.SERVER_PARAM_SPONSORED_AUDIO : MixzingConstants.SERVER_PARAM_SPONSORED_VIDEO;
		return getParamHandler().getLongValue(param) == 1;
	}

	public static long getSponsoredRefreshInterval() {
		return getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_SPONSORED_REFRESH_INTERVAL);
	}

	public static int getCurVers() {
		return (int)getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_CURVERS);
	}

	public static int getMinVers() {
		return (int)getParamHandler().getLongValue(MixzingConstants.SERVER_PARAM_MINVERS);
	}

	public static long getLongServerParam(String name) {
		return getParamHandler().getLongValue(name);
	}

	public static String getStringServerParam(String name) {
		return getParamHandler().getStringValue(name);
	}

	public static void checkAndQueueIntents() {
		final Context context = app;
		if (doesIntentExist(context, MixzingConstants.MIXZING_SCROBBLE_ACTION)) {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug("AndroidUtil.checkAndQueueIntents: a pending intent exists, so we are probably ok");
		}
		else {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug("AndroidUtil.checkAndQueueIntents: no pending intent found ... trying to hook up");
			AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			scheduleIntent(alarm, context, MixzingConstants.MIXZING_RESOLVE_ACTION, getStartupResolveDelay());
			long delay = getStartupScrobbleDelay();
			// For test debug set the initial load at 5 min
			// delay = MixzingConstants.FIVE_MINUTE;
			scheduleIntent(alarm, context, MixzingConstants.MIXZING_SCROBBLE_ACTION, delay);

			if(isUsageStatsEnabled()) {
				delay = getStartupUsageStatsDelay();
				scheduleIntent(alarm, context, MixzingConstants.MIXZING_STATS_ACTION, delay);
			}
		}
	}

	public static void scheduleIntent(AlarmManager alarm, Context context, String action, long delay) {
		// Create an IntentSender that will launch our service, to be scheduled
		// with the alarm manager.
		Intent alarmIntent = new Intent();
		alarmIntent.setAction(action);

		PendingIntent operation = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
		alarm.cancel(operation);
		alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + delay, operation);
		if (Logger.IS_DEBUG_ENABLED)
			log.debug("AndroidUtil.scheduleIntent: action = " + action + " delay = " + delay);
	}

	public static boolean doesIntentExist(Context context, String action) {
		Intent alarmIntent = new Intent();
		alarmIntent.setAction(action);
		PendingIntent operation = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE);
		if (operation == null) {
			return false;
		}
		else {
			return true;
		}
	}

	public static void cancelIntent(AlarmManager alarm, Context context, String action) {
		// Create an IntentSender that will launch our service, to be scheduled
		// with the alarm manager.
		Intent alarmIntent = new Intent();
		alarmIntent.setAction(action);
		PendingIntent operation = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
		alarm.cancel(operation);
	}

	public static String getMusicDirs() {
		return getStringPref(null, getCardSpecificPrefKey(Preferences.Keys.MUSIC_DIRS), null);
	}

	// convert dirs string array into preferences format
	// returns null if no dirs were selected, otherwise list of selected dirs separated by delimiter
	// we interpret all dirs selected the same as none and return null
	//
	public static String musicDirsToPref(String[] dirs, int total) {
		String pref = null;
		final int num = dirs.length;
		if (num != 0 && num != total) {
			final StringBuilder sb = new StringBuilder();
			final String delim = MUSIC_DIR_DELIMITER;
			for (int i = 0; i < num; ++i) {
				if (i > 0) {
					sb.append(delim);
				}
				sb.append(dirs[i]);
			}
			pref = sb.toString();
		}
		return pref;
	}

	// convert dirs from preferences format into string array
	//
	public static String[] getMusicDirsFromPref() {
		synchronized (dirLock) {
			if (musicDirsCache == null) {
				final String[] dirs = musicDirsFromPref(getMusicDirs(), false, true);
				if (dirs == null) {
					musicDirsCache = new String[0];
				}
				else {
					musicDirsCache = dirs;
				}
			}
			return musicDirsCache;
		}
	}

	public static void setCacheCleaner(RecommendCache recoCache) {
		recommendCache = recoCache;
	}

	public static void clearMusicDirsCache() {
		synchronized (dirLock) {
			musicDirsCache = null;
			if (recommendCache != null) {
				recommendCache.clearCache();
			}
		}
	}

	public static String[] musicDirsFromPref(String pref, boolean stripMnt, boolean addSep) {
		String[] dirs = null;
		if (pref != null) {
			dirs = pref.split(MUSIC_DIR_DELIMITER);
			if (stripMnt || addSep) {
				final int start = MNT.length() - 1;
				final String sep = File.separator;
				final int num = dirs.length;
				for (int i = 0; i < num; ++i) {
					String dir = dirs[i];
					if (stripMnt && dir.startsWith(MNT)) {
						dir = dir.substring(start);
					}
					if (addSep) {
						dir = dir + sep;
					}
					dirs[i] = dir;
				}
			}
		}
		return dirs;
	}

	public static void validateMusicDirsPref() {
		final String key = getCardSpecificPrefKey(Preferences.Keys.MUSIC_DIRS);
		final String pref = getStringPref(null, key, null);
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("AndroidUtil.validateMusicDirsPref: pref = " + pref);
		}
		if (pref != null) {
			final String[] dirs = pref.split(MUSIC_DIR_DELIMITER);
			final int num = dirs.length;
			final ArrayList<String> newdirs = new ArrayList<String>(num);
			try {
				boolean changed = false;
				for (int i = 0; i < num; ++i) {
					final String dir = dirs[i];
					final File file = new File(dir);
					if (file.exists() && file.isDirectory()) {
						final String newdir = file.getCanonicalPath();
						newdirs.add(newdir);
						if (!newdir.equals(dir)) {
							changed = true;
						}
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("AndroidUtil.validateMusicDirsPref: old = " + dir + ", new = " + newdir);
						}
					}
				}
				final int newnum = newdirs.size();
				if (changed || newnum != num) {
					final String newpref = musicDirsToPref(newdirs.toArray(new String[newnum]), 0);
					setStringPref(null, key, newpref);
					clearMusicDirsCache();
					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("AndroidUtil.validateMusicDirsPref: new pref = " + newpref);
					}
				}
			}
			catch (Exception e) {
				log.error("AndroidUtil.validateMusicDirsPref:", e);
			}
		}
	}

	public static boolean newOSVersion(boolean update) {
		final String curRel = Build.VERSION.RELEASE;
		final String key = getCardSpecificPrefKey(Keys.BUILD_VERSION_RELEASE);
		final String prevRel = getStringPref(null, key, null);
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("AndroidUtil.newOSVersion: prev = " + prevRel + ", cur = " + curRel);
		}
		final boolean changed = prevRel == null || !prevRel.equals(curRel);
		if (changed && update) {
			setStringPref(null, key, curRel);
		}
		return changed;
	}

	public static String getEmail() {
		// try to get user's google account username
		return getAccountName("com.google");
	}

	public static String getAccountName(String desiredType) {
		String name = null;
		try {
			Object[] accts = getAccounts();
			if (accts != null) {
				final Class<?> cls = Class.forName("android.accounts.Account");
				final Field nameField = cls.getField("name");
				final Field typeField = cls.getField("type");
				for (Object acct : accts) {
					final String type = (String)typeField.get(acct);
					if (type.equals(desiredType)) {
						name = (String)nameField.get(acct);
						break;
					}
				}
			}
		}
		catch (UnsupportedOperationException e) {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug("AndroidUtil.getAccountName:", e);
		}
		catch (Exception e) {
			log.error("AndroidUtil.getAccountName:", e);
		}

		if (Logger.IS_DEBUG_ENABLED)
			log.debug("AndroidUtil.getAccountName: returning " + name + " for type " + desiredType);

		return name;
	}

	public static Object[] getAccounts() throws UnsupportedOperationException {
		try {
			final Class<?> cls = Class.forName("android.accounts.AccountManager");
			final Method get = cls.getMethod("get", new Class[] { Context.class });
			final Object am = get.invoke(null, new Object[] { app });
			final Method getAccounts = cls.getMethod("getAccounts", new Class[] {});
			final Object accts = getAccounts.invoke(am);
			return (Object[])accts;
		}
		catch (ClassNotFoundException e) {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug("AndroidUtil.getAccounts:", e);
			throw new UnsupportedOperationException("Account Manager not present");
		}
		catch (Exception e) {
			log.error("AndroidUtil.getAccounts:", e);
		}
		return null;
	}

	private static String getDownloadURL(String pkg) {
		if (isAmazonDevice()) {
			return null;
		}
		else {
			final String libId = getStringPref(null, getCardSpecificPrefKey(Keys.LIB_ID), "");
			return props.getProperty(MixzingAppProperties.DOWNLOAD_URL) + pkg + "&lib=" + libId;
		}
	}

	// take the user to either the market or the web download for the basic mixzing package
	//
	public static boolean installMixZing(Context context) {
		Analytics.event(Analytics.EVENT_INSTALL_UPDATE);
		final String pkg = MixzingConstants.BASIC_PACKAGE_NAME;
		return findPackage(context, pkg, getDownloadURL(pkg));
	}

	// take the user to either the market or the web download for the mixzing upgrade package
	//
	public static boolean installMixZingUpgrade(Context context) {
		Analytics.event(Analytics.EVENT_INSTALL_UPGRADE);
		final String pkg = MixzingConstants.UPGRADE_PACKAGE_NAME;
		return findPackage(context, pkg, getDownloadURL(pkg));
	}

	public static boolean findPackage(Context context, String pkg, String url) {
		boolean ret = true;

		// try the market app first
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(getMarketLink(pkg));

		// if the url is set then check if the market intent resolves and fall back to url if not
		if (url != null) {
			final PackageManager pm = context.getPackageManager();
			try {
				if (pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
					intent.setData(Uri.parse(url));
				}
			}
			catch (Exception e) {
				if (Logger.shouldSelectivelyLog()) {
					log.error("AndroidUtil.findPackage:", e);
				}
				intent.setData(Uri.parse(url));
			}
		}

		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("AndroidUtil.findPackage: starting intent: " + dumpIntent(intent, "  "));
		}

		try {
			context.startActivity(intent);
		}
		catch (Exception e) {
			if (Logger.shouldSelectivelyLog()) {
				log.debug("AndroidUtil.findPackage: intent = " + dumpIntent(intent, "  "), e);
			}
			ret = false;
		}

		return ret;
	}

	private static Uri getMarketLink(String pkg) {
		final String market = isAmazonDevice() ? MARKET_AMAZON : MARKET_GOOGLE;
		return Uri.parse(market + pkg);
	}

	public static boolean isAmazonDevice() {
		if (amazonDevice == null) {
			final String vendor = Build.MANUFACTURER;
			amazonDevice = Boolean.valueOf(vendor != null ? vendor.toLowerCase(Locale.US).equals("amazon") : false);
		}
		return amazonDevice;
	}

	public static boolean hasTelephony() {
		return !isAmazonDevice();  // TODO improve logic when needed
	}

	/**
	 * context.getWallpaper has been deprecated but we still need to support 1.5 so we must use reflection to
	 * try to obtain the wallpaper using the newer WallpaperManager. The VM class loader won't let us load the
	 * class if the WallpaperManager object is not defined.
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static BitmapDrawable getWallpaper (Context context) {
		BitmapDrawable ret = null;
		if (!useDeprecatedGetWallpaper) {
			try {
				final Class<?>[] parameterTypes = new Class[]{Context.class};
				final Object[] args = new Object[] {context};
				final Class<?> wallpaperManagerDef = Class.forName("android.app.WallpaperManager");
				final Method getInstance = wallpaperManagerDef.getMethod("getInstance", parameterTypes);
				final Object wallpaperManager = getInstance.invoke(null, args);
				final Method getDrawable =  wallpaperManagerDef.getMethod("getDrawable", (Class<?>[])null);
				ret = (BitmapDrawable) getDrawable.invoke(wallpaperManager, (Object[])null);
			}
			catch (Exception e) {
				useDeprecatedGetWallpaper = true;
			}
		}
		if (useDeprecatedGetWallpaper) {
			ret = (BitmapDrawable) context.getWallpaper();
		}
		return ret;
	}

	public static BitmapDrawable createBitmapDrawable(Resources res, Bitmap bmp) {
		try {
			return new BitmapDrawable(res, bmp);
		}
		catch (Throwable t) {
		}
		return null;
	}

	public static BitmapDrawable viewToBitmap(Resources res, View view) {
		BitmapDrawable ret = null;
		try {
			Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bmp);
			view.draw(canvas);
			ret = createBitmapDrawable(res, bmp);
		}
		catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug("AndroidUtil.viewToBitmap:", e);
		}
		return ret;
	}

	// set zoom controls properly for device
	//
	public static void setZoom(WebView web) {
		// try the HTC-specific smart zoom mode first
		//		try {
		//			final Class<?> cls = Class.forName("android.webkit.WebView");
		//			@SuppressWarnings("unused")
		//			Method meth = cls.getMethod("enableSmartZoom");
		//			// TODO set zoom mode properly, for now leave disabled
		//			return;
		//			meth.invoke(web);
		//			meth = cls.getMethod("disableMultiTouch");
		//			meth.invoke(web);
		//			return;
		//		}
		//		catch (ClassNotFoundException e) {
		//			log.error("AndroidUtil.setZoom:", e);
		//		}
		//		catch (NoSuchMethodException e) {
		//			if (Logger.IS_DEBUG_ENABLED)
		//				log.debug("AndroidUtil.setZoom: no smart zoom");
		//		}
		//		catch (Exception e) {
		//			log.error("AndroidUtil.setZoom:", e);
		//		}

		// otherwise use the normal call
		final WebSettings webSettings = web.getSettings();
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
	}

	public static void rescan() {
		final String[] dirs = musicDirsFromPref(getMusicDirs(), false, false);
		if (dirs != null && dirs.length != 0) {
			for (String dir : dirs) {
				Scanner.rescan(app, dir);
			}
		}
		else {
			Scanner.rescan(app, SdCardHandler.getRootDir());
		}
	}

	private static Address getAddress(double lon, double lat) {
		Geocoder gc = new Geocoder(getAppContext(), Locale.ENGLISH);
		try {
			List<Address> addresses = gc.getFromLocation(lat, lon, 1);
			if(addresses!=null && addresses.size() >0) {
				Address add = addresses.get(0);
				if(Logger.IS_DEBUG_ENABLED) {
					log.debug("Got geo locations city and country = " + add.getLocality() + " : " + add.getCountryCode());
				}
				return add;
			} else {
				log.debug("Unable to get geocoded location from fix lon=" + lon + " lat=" + lat);
			}
		} catch (IOException e) {
			log.debug("Exception " + e);
		}
		return null;
	}

	/*
	 * Computes distance in miles between two Locations
	 */	
	private static double computeDistance(double lat1, double long1, double lat2, double long2) {
		double x = 69.1 * (lat2 - lat1);
		double y = 69.1 * (long2 - long1) * Math.cos(lat1/57.3); 
		double miles = Math.sqrt(x * x + y * y);
		return miles;
	}

	private static synchronized void setGeoLocations(Address add) {
		setStringPref(null, Preferences.Keys.CITY, add.getLocality());
		setStringPref(null, Preferences.Keys.COUNTRY, add.getCountryCode());
		setStringPref(null, Preferences.Keys.LAST_GEOCODED_LATITUDE, Double.toString(add.getLatitude()));
		setStringPref(null, Preferences.Keys.LAST_GEOCODED_LONGITUDE, Double.toString(add.getLongitude()));
	}

	public static void persistLastLocation(Location loc) {
		final String lat = Double.toString(loc.getLatitude());
		final String lon = Double.toString(loc.getLongitude());
		setStringPref(null, Preferences.Keys.CURLATITUDE, lat);
		setStringPref(null, Preferences.Keys.CURLONGITUDE, lon);
		// TODO listener.setLocation(lat, lon);
		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("AndroidUtil.persistLastLocation: " + lat + ":" + lon);
		}
	}

	public static String getCountry() {
		return getStringPref(null, Preferences.Keys.COUNTRY, null);
	}

	public static  String getCity() {
		return getStringPref(null, Preferences.Keys.CITY, null);
	}

	public static boolean refreshLocations() {
		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("Refresh Locations called");
		}
		String lons = getStringPref(null, Preferences.Keys.CURLONGITUDE, null);
		String lats = getStringPref(null, Preferences.Keys.CURLATITUDE, null);



		boolean refreshed = false;
		if(lons != null && lats != null) {

			double lon = Double.valueOf(lons);
			double lat = Double.valueOf(lats);
			String geolons = getStringPref(null, Preferences.Keys.LAST_GEOCODED_LONGITUDE, null);
			String geolats = getStringPref(null, Preferences.Keys.LAST_GEOCODED_LATITUDE, null);

			double dist  = -10;
			// If we do not know the city and address or the distance is > 25 miles from curloc recompute city/country 
			if(geolons == null || geolats == null || 
					(dist = computeDistance(lat, lon, Double.valueOf(geolats), Double.valueOf(geolons))) > 25) {
				Address ad = getAddress(lon, lat);
				if(ad != null) {
					setGeoLocations(ad);
					refreshed = true;
				} else {
					refreshed = false;
				}
			} else {
				if(dist >= 0) {
					refreshed = true;
				}					
			}
		}

		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("Refresh Locations Returned : " + refreshed);
		}
		return refreshed;
	}

	public static void resetPrefs() {
		setStringPref(null, getCardSpecificPrefKey(Keys.LIB_ID), "");
		setBooleanPref(null, getCardSpecificPrefKey(Keys.LIB_LOADED), false);
		setBooleanPref(null, getCardSpecificPrefKey(Keys.LIB_RESOLVED), false);
		setLongPref(null, getCardSpecificPrefKey(Keys.LIB_RESOLVED_TIME), 0);
		setLongPref(null, getCardSpecificPrefKey(Keys.PKG_RESOLVE_TIME), 0);
		setLongPref(null, getCardSpecificPrefKey(Keys.VIDEO_RESOLVE_TIME), 0);
		setLongPref(null, getCardSpecificPrefKey(Keys.LAST_SCROBBLE_TIME), 0);
		setStringPref(null, Keys.TRACKS_TAGS_CHANGED, null);
	}

	public static void terminateProcess(String reason) {
		log.error("Process terminated for: " + reason);
		int pid = Process.myPid();
		Process.killProcess(pid);		
	}

	public static boolean reinitWithFileDelete() {
		Context context = AndroidUtil.getAppContext();
		String dbName = SdCardHandler.getDbNameOrNull();
		boolean isDeleted = false;
		if (dbName != null) {
			File f = context.getDatabasePath(dbName);
			isDeleted = f.delete();
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("AndroidUtil.reinitWithFileDelete: delete of " + f.getPath() + " returned " + isDeleted);
			}
			if (isDeleted) {
				resetPrefs();
				terminateProcess("Reinit of database");
			}
		}
		return isDeleted;
	}

	public static String dumpPrefs(SharedPreferences prefs, String prefix) {
		final Map<String, ?> map = prefs.getAll();
		final ArrayList<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		final StringBuilder sb = new StringBuilder();
		for (String key : keys) {
			final Object o = map.get(key);
			if (prefix != null) {
				sb.append(prefix);
			}
			sb.append(key);
			sb.append(" = ");
			sb.append(o == null ? "<null>" : o.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public static String dumpIntent(Intent intent, String prefix) {
		final StringBuilder sb = new StringBuilder();
		sb.append(intent.toString());
		sb.append(", data = ");
		sb.append(intent.getData());
		final Bundle b = intent.getExtras();
		if (b != null) {
			sb.append("\n");
			for (String key : b.keySet()) {
				if (prefix != null) {
					sb.append(prefix);
				}
				sb.append(key);
				sb.append(" = ");
				final Object o = b.get(key);
				sb.append(o == null ? "null" : o.toString());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public static String dumpLooper(Looper looper, String prefix) {
		final StringBuilder sb = new StringBuilder();
		if (looper != null) {
			final StringBuilderPrinter sbp = new StringBuilderPrinter(sb);
			looper.dump(sbp, prefix);
		}
		return sb.toString();
	}

	public static boolean isInstalledOnExternalStorage() {
		if (externalInstall == null) {
			if (pkgInfo == null) {
				getPackageInfo(app);
			}
			if (pkgInfo != null) {
				externalInstall = (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0;
			}
			else {
				externalInstall = false;
			}
		}
		return externalInstall;
	}

	public static ComponentName getTopActivity(Context context) {
		ComponentName comp = null;
		final ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		if (am != null) {
			final List<RunningTaskInfo> tasks = am.getRunningTasks(1);
			if (tasks.size() > 0) {
				comp = tasks.get(0).topActivity;
			}
			else {
				log.error("AndroidUtil.getTopActivity: no tasks returned");
			}
		}
		else {
			log.error("AndroidUtil.getTopActivity: no activity manager");
		}
		return comp;
	}

	// check state of default shared prefs file, assuming it should exist
	//
	@SuppressLint("SdCardPath")
	public static int getPrefsState(StringBuilder sb) {
		int state = PREFS_OK;
		try {
			final PackageInfo pkgInfo = getPackageInfo(app);
//			FileStatus stat = null;
			final String[] dirs = {
					app.getFilesDir() + "/..",
					"/data/data/" + pkgName,
					"/dbdata/databases/" + pkgName
			};
			state = PREFS_MISSING;
			final String fname = "/shared_prefs/" + pkgName + "_preferences.xml";
			for (String dir : dirs) {
				final File file = new File(dir + fname);
				if (Logger.IS_DEBUG_ENABLED) {
					log.debug("AndroidUtil.getPrefsState: " + (file.exists() ? "found " : "tried ") + file.getAbsolutePath());
				}
				if (file.exists()) {
					state = PREFS_OK;
//					// check its uid and mode
//					stat = new FileStatus();
//					FileUtils.getFileStatus(file.getAbsolutePath(), stat);
//					if (pkgInfo != null && stat.uid != pkgInfo.applicationInfo.uid) {
//						state = PREFS_WRONG_USER;
//					}
//					else {
//						final int mode = FileUtils.S_IRUSR | FileUtils.S_IWUSR;  // read-write by user
//						if ((stat.mode & mode) != mode) {
//							state = PREFS_WRONG_MODE;
//						}
//					}

					// return file status if desired
					if (sb != null) {
//						sb.append(String.format("%s: mode %o, uid %d, gid %d, size %d, mtime %d, ctime %d, pkg %s uid = %d",
//								file.getCanonicalPath(), stat.mode, stat.uid, stat.gid, stat.size, stat.mtime, stat.ctime, pkgName,
//								pkgInfo != null ? pkgInfo.applicationInfo.uid : 0));
						sb.append(String.format("%s: pkg %s uid = %d",
								file.getCanonicalPath(), pkgName,
								pkgInfo != null ? pkgInfo.applicationInfo.uid : 0));
					}

					break;
				}
			}

//			if (stat == null) {
//				state = PREFS_MISSING;
//			}
		}
		catch (Exception e) {
			log.error("AndroidUtil.getPrefsState:", e);
		}
		return state;
	}

	/**
	 * 
	 * @param string
	 * @return boolean
	 * <br />Returns true if string is not null and not empty.
	 * <br />Returns false if it is either null or empty.
	 * 
	 */
	public static boolean isNotNullNorEmpty(String string){
		return string != null && string.length() != 0;
	}

	public static boolean onMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	public static void assertOnMainThread() {
		if (!onMainThread()) {
			throw new RuntimeException("Called off main thread");
		}
	}

	public static void assertOffMainThread() {
		if (onMainThread()) {
			throw new RuntimeException("Called on main thread");
		}
	}

	public static boolean equals(Bundle b1, Bundle b2) {
		if (b1 == b2) {
			return true;
		}
		else if (b1 != null && b2 != null) {
			if (b1.size() != b2.size()) {
				return false;
			}
			else {
				final Set<String> b1keys = b1.keySet();
				final Set<String> b2keys = b2.keySet();
				if (!b1keys.equals(b2keys)) {
					return false;
				}
				for (String key : b1keys) {
					if (!b1.get(key).equals(b2.get(key))) {
						return false;
					}
				}
				return true;
			}
		}
		else {
			return false;
		}
	}

	public static boolean isTablet() {
		return tablet;
	}
}
