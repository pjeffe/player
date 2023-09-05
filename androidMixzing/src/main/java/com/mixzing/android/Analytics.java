package com.mixzing.android;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.mixzing.MixzingConstants;
import com.mixzing.ads.MixZingAdInterface.Gender;
import com.mixzing.log.Logger;

// TODO refactor to use interface if/when we need to support multiple analytics packages

public class Analytics {
	private static final Logger log = Logger.getRootLogger();

	public static final String EVENT_ART_DOWNLOAD = "ArtDownload";
	public static final String EVENT_SPONSORED_SONG_DISPLAY = "SponsoredSongDisplay";
	public static final String EVENT_SPONSORED_SONG_CLICK = "SponsoredSongClick";
	public static final String EVENT_SPONSORED_VIDEO_DISPLAY = "SponsoredVideoDisplay";
	public static final String EVENT_SPONSORED_VIDEO_CLICK = "SponsoredVideoClick";
	public static final String EVENT_AD_CALL = "AdCall";
	public static final String EVENT_AD_FETCH = "AdFetch";
	public static final String EVENT_AD_CACHE_START= "AdCacheStart";
	public static final String EVENT_AD_RESULT = "AdResult";
	public static final String EVENT_AD_CLICK = "AdClick";
	public static final String EVENT_AD_LAUNCH = "AdLaunch";
	public static final String EVENT_BUY_CLICK = "BuyClick";
	public static final String EVENT_CONTEST_CLICK = "ContestClick";
	public static final String EVENT_CONTEST_PLAY = "ContestPlay";
	public static final String EVENT_ITEM_CLICK = "ItemClick";
	public static final String EVENT_RADIO_PLAY = "RadioPlay";
	public static final String EVENT_INFO_SELECTION = "InfoSelection";
	public static final String EVENT_INFO_SONG_CHANGE = "InfoSongChange";
	public static final String EVENT_INFO_RESULT = "InfoResult-";
	public static final String EVENT_LYRIC_FIND_API_CALL = "LyricFindApiCall";
	public static final String EVENT_RATING = "Rating";
	public static final String EVENT_NOW_PLAYING_RATING = "NowPlayingRating";
	public static final String EVENT_BROWSER_RATING = "BrowserRating";
	public static final String EVENT_NEW_MUSIC = "NewMusic";
	public static final String EVENT_INSTALL_UPGRADE = "InstallUpgrade";
	public static final String EVENT_INSTALL_UPDATE = "InstallUpdate";
	public static final String EVENT_DEVICE_INFO = "DeviceInfo";
	public static final String EVENT_SESSION = "Session";
	public static final String EVENT_PREVIEW = "Preview";
	public static final String EVENT_DOWNLOAD = "Download";
	public static final String EVENT_WIDGET = "Widget";
	public static final String EVENT_SHARE = "Share";
	public static final String EVENT_UPDATE = "Update";
	public static final String EVENT_INSTALL = "Install";
	public static final String EVENT_KINDLE_BUY_PROMPT = "KindleBuyPrompt";
	public static final String EVENT_KINDLE_BUY_YES = "KindleBuyYes";
	public static final String EVENT_KINDLE_BUY_NO = "KindleBuyNo";

	public static final String DATA_AD_TYPE = "AdType";
	public static final String DATA_AD_RESULT = "AdResult";
	public static final String DATA_AD_REASON = "AdReason";
	public static final String DATA_AD_REQUEST = "AdRequest";
	public static final String DATA_AD_ID = "AdId";
	public static final String DATA_BUY_URL = "BuyUrl";
	public static final String DATA_CONTEST_ID = "ContestId";
	public static final String DATA_ITEM_TYPE = "ItemType";
	public static final String DATA_ITEM_ID = "ItemId";
	public static final String DATA_SOURCE = "Source";
	public static final String DATA_RESULT = "Result";
	public static final String DATA_INFO_TYPE = "InfoType";
	public static final String DATA_INFO_RESULT_STATUS = "InfoResultStatus";
	public static final String DATA_LYRICS_TYPE = "LyricsType";
	public static final String DATA_LYRIC_FIND_API = "LyricFindApiDesc";
	public static final String DATA_DISPLAY_METRICS = "DisplayMetrics";
	public static final String DATA_OS_VERSION = "OSVersion";
	public static final String DATA_FREQ = "Frequency";
	public static final String DATA_PACKAGE = "Package";
	public static final String DATA_COUNTRY = "Country";
	public static final String DATA_LANGUAGE = "Language";
	public static final String DATA_MODEL = "Model";
	public static final String DATA_PRODUCT = "Product";
	public static final String DATA_SESSION_LENGTH = "SessionLength";
	public static final String DATA_URL = "Url";
	public static final String DATA_WIDGET_NAME = "Name";
	public static final String DATA_WIDGET_ACTION = "Action";
	public static final String DATA_SPONSORED_ITEM = "SponsoredItem";
	public static final String DATA_NET_STATE = "NetworkState";
	public static final String DATA_SHARE_LABEL = "ShareLabel";
	public static final String DATA_SHARE_PACKAGE = "SharePackage";
	public static final String DATA_RATING = "Rating";
	public static final String DATA_VERSION = "Version";

	public static final String VALUE_LYRIC_FIND_SEARCH_TAGS = "SearchTags";
	public static final String VALUE_LYRIC_FIND_SEARCH_INFO = "SearchInfo";
	public static final String VALUE_LYRIC_FIND_QUERY_AMG = "DisplayAmg";
	public static final String VALUE_AD_DISPLAY = "Display";
	public static final String VALUE_AD_NO_DISPLAY = "NoDisplay";
	public static final String VALUE_AD_FILL = "Fill";
	public static final String VALUE_AD_FAIL = "Fail";
	public static final String VALUE_AD_TIMEOUT = "Timeout";
	public static final String VALUE_AD_FETCH_TIMEOUT = "FetchTimeout";
	public static final String VALUE_RATING_LOVE = "Love";
	public static final String VALUE_RATING_LIKE = "Like";
	public static final String VALUE_RATING_DISLIKE = "Dislike";
	public static final String VALUE_RESULT_SUCCESS = "Success";
	public static final String VALUE_RESULT_FAILURE = "Failure";
	public static final String VALUE_RESULT_NO_DATA = "NoData";
	public static final String VALUE_RESULT_NO_NETWORK = "NoNetwork";
	public static final String VALUE_NOTIFICATION = "Notification";

	public static final long SESSION_TIMEOUT = MixzingConstants.ONE_MINUTE;  // timeout of session after last endSession()


	public static void init(Context context) {
		LocalAnalytics.init(context);
		Flurry.init(context);
	}

	public static void startSession(Context context) {
		Flurry.startSession(context);
		LocalAnalytics.startSession();
		final String name = context.getClass().getName();
		event(name, null);
	}

	public static void endSession(Context context) {
		Flurry.endSession(context);
		LocalAnalytics.endSession();
	}

	public static void event(String event) {
		event(event, null);
	}

	public static void event(String event, String key, String value) {
		final HashMap<String, String> args = new HashMap<String, String>(1);
		args.put(key, value);
		event(event, args);
	}

	public static void event(String event, String key1, String value1, String key2, String value2) {
		final HashMap<String, String> args = new HashMap<String, String>(2);
		args.put(key1, value1);
		args.put(key2, value2);
		event(event, args);
	}

	public static void event(String event, String key1, String value1, String key2, String value2, String key3, String value3) {
		final HashMap<String, String> args = new HashMap<String, String>(3);
		args.put(key1, value1);
		args.put(key2, value2);
		args.put(key3, value3);
		event(event, args);
	}

	public static void event(String event, Map<String, String> args) {
		if (args == null) {
			Flurry.event(event);
		}
		else {
			Flurry.event(event, args);
		}

		LocalAnalytics.event(event, args);

		if (Logger.IS_DEBUG_ENABLED) {
			log(event, args);
		}
	}

	public static void setAge(int age) {
		Flurry.setAge(age);
	}

	public static void setGender(Gender gender) {
		Flurry.setGender(gender);
	}

	private static void log(String event, Map<String, String> args) {
		log.debug("Analytics: event = " + event + ", args = " + args);
	}
}
