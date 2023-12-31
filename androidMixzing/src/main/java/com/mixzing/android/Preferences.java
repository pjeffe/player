package com.mixzing.android;


public class Preferences {
	public static final String scrobbledroid = "net.jjc1138.android.scrobbler";

	public static final String ACTION_MUSIC_DIRS = "com.mixzing.android.MusicDirs";
	public static final String INTENT_MUSIC_DIRS = "value";

	// safe character to use to separate values--passed to server, so don't change lightly!
	public static final String PREF_DELIM = "\u2063";

	public interface Keys {
		public static final String RECOMMENDATIONS = "recommendations";
		public static final String DYNAMIC_PLAYLISTS = "dynamic_playlists";
		public static final String AUTO_LIKE_RATINGS = "auto_like_ratings";
		public static final String AUTO_DISLIKE_RATINGS = "auto_dislike_ratings";
		public static final String START_ON_BOOT = "bootstart";
		public static final String ENABLE_HEADSET = "headset";
		public static final String PAUSE_ON_HEADSET_REMOVAL = "headset_pause";
		public static final String PLAY_ON_HEADSET_PLUGGED = "headset_play";
		public static final String SURPRISE_FACTOR = "surprise_factor";
		public static final String USE_INTERNAL_STORAGE = "internal_storage";
		public static final String MUSIC_DIRS = "music_dirs";
		public static final String ALBUM_DIRS = "album_dirs";
		public static final String SCROBBLE = "scrobble";
		public static final String STATUS = "status";
		public static final String ABOUT = "about";
		public static final String PERSISTENT_NOTIFICATIONS = "persistent_notifications";
		public static final String REMOTE_CONTROL = "remote_control";
		public static final String FACEBOOK_ID = "facebook_id";
		public static final String FACEBOOK_DISCONNECT = "facebook_disconnect";
		public static final String FACEBOOK_TOKEN = "facebook_token";
		public static final String FAV_ARTIST = "favourite_artist";
		public static final String SHARE_LIB = "share_lib";
		public static final String HOME_ACTIVITY = "home_activity";
		public static final String CURRENT_THEME = "current_theme";
		public static final String KEY_MAPPING = "key_map";
		public static final String RESTORE_PLAYLIST = "restore_playlist";
		public static final String RHAPSODY_LOGIN = "rhapsody_login";
		public static final String DOWNLOAD_ART = "download_art";  // defunct
		public static final String DOWNLOAD_ALBUM_ART = "download_album_art";
		public static final String DOWNLOAD_BATTERY = "download_battery";
		public static final String DOWNLOAD_WIFI = "download_wifi";
		public static final String ALBUM_ART_FNAME = "album_art_fname";
		public static final String ALBUM_ART_FILE = "album_art_file";
		public static final String CLEAR_ART_CACHE = "clear_art_cache";
		public static final String VIDEO_BOOKMARKS = "video_bookmarks";
		public static final String LEAVE_SERVICE = "leave_service";
		public static final String LOCK_WIDGET_SCREEN = "pref_screen_lock_widget";
		public static final String LOCK_WIDGET = "lock_widget";
		public static final String LOCK_WIDGET_PLAYING = "lock_widget_playing";
		public static final String LOCK_WIDGET_WAKE = "lock_widget_wake";
		public static final String ENTER_LICENSE_KEY = "license_key";
		public static final String UPGRADE_NOW = "upgrade_now";
		public static final String AMAZON_PURCHASE = "amazon_purchase";
		public static final String REMOVE_ADS = "remove_ads";
		public static final String FREE_DOWNLOADS = "free_downloads";
		public static final String SHOW_NOW_PLAYING_MUSIC = "show_now_playing_music";
		public static final String SHOW_NOW_PLAYING_SOCIAL = "show_now_playing_social";
		public static final String SHOW_NOW_PLAYING_PORT_ONLY = "show_now_playing_port_only";
		public static final String SHOW_SEARCH_BOX_PORT = "search_box_portrait";
		public static final String SHOW_SEARCH_BOX_LAND = "search_box_landscape";
		public static final String SHOW_SEARCH_BOX_KBD_OPEN = "search_box_kbd_open";
		public static final String ENABLE_EQ = "enable_eq";
		public static final String ENABLE_EQ_ALWAYS = "enable_eq_always";
		public static final String RESUME_PLAY = "resume_play";
		public static final String BUFFER_SIZE = "buffer_size";
		public static final String ENABLE_ALL_EQ_BANDS = "enable_all_eq_bands";
		public static final String ENABLE_GESTURES = "enable_gestures";
		public static final String SYSTEM_EVENTS = "system_events";
		public static final String DUMP_DB = "dump_db";
		public static final String DUMP_TRACES = "dump_traces";
		public static final String DUMP_LOG = "dump_log";
		public static final String EMAIL_SUPPORT = "email_support";
		public static final String EXPLORER_BACK_NORMAL = "explorer_back";
		public static final String ADD_DEFAULT_MAPPING = "add_default_mapping";
		public static final String NEXT_UPDATE_CHECK = "next_update_check";
		public static final String NEXT_REC_FEATURE_CHECK = "next_rec_feature_check";
		public static final String NEXT_ART_FEATURE_CHECK = "next_art_feature_check";
		public static final String HELP_SCALE = "helpScale";
		public static final String INFO_SCALE = "infoScale";
		public static final String LAST_CODE_VERSION = "lastCodeVersion";
		public static final String LAST_UI_VERSION = "lastUIVersion";
		public static final String SHOW_WELCOME = "showWelcome";
		public static final String SHOW_UPGRADE_WELCOME = "showUpgrade";
		public static final String SHOW_EXTERNAL = "showExternal";
		public static final String SHOW_RATE = "showRate";
		public static final String SHOW_UNTAGGED = "showUntagged";
		public static final String CHECK_SCROBBLEDROID = "showScrobble";
		public static final String CHECK_PREFS = "checkPrefs";
		public static final String SHOW_FACEBOOK_PROMPT = "facebookPrompt";
		public static final String SHOW_DEMO_PROMPT = "demoPrompt";
		public static final String SHOW_DOWNLOADS_PROMPT = "downloadsPrompt";
		public static final String SHOW_STORAGE_DEFAULT_PROMPT = "storageDefaultPrompt";
		public static final String SHOW_STORAGE_CHANGE_PROMPT = "storageChangePrompt";
		public static final String SHOW_CLIPPING_PROMPT = "clippingPrompt";
		public static final String SHOW_BACKGROUND_PROMPT = "backgroundPrompt";
		public static final String SHOW_EXPLORER_BACK_PROMPT = "expBackPrompt";
		public static final String SHOW_SD_MOVE_PROMPT = "sdMovePrompt";
		public static final String SHOW_FAVE_ARTISTS_PROMPT = "faveArtistsPrompt";
		public static final String SHOW_TIP_OVERLAY = "tipOverlayNew";
		public static final String SHOW_TIP_EQ = "tipEq";
		public static final String SHOW_TIP_LYRICS = "tipLyrics";
		public static final String SHOW_TIP_CONTESTS = "tipContests";
		public static final String SHOW_TIP_BROADJAM = "tipBroadjam";
		public static final String USER_ENABLED_RECS = "userEnabledRecs";
		public static final String LIB_ID = "libId";
		public static final String SERVER_USER_ID = "serverUserId";
		public static final String LIB_LOADED = "libLoaded";
		public static final String START_COUNT = "startCount";
		public static final String RATING_COUNT = "playCount";
		public static final String USERID = "userId";
		public static final String DB_SPACE_CLEANUP_NEEDED = "dbSpaceCleanup";
		public static final String LAST_ERROR = "lastError";
		public static final String LAST_ERROR_DATA = "lastErrorData";
		public static final String DELETE_ART_DIR = "deleteArtDir";
		public static final String BUILD_PROP_MTIME = "buildPropMtime";
		public static final String IMEI = "imei";
		public static final String GENERATED_IMEI = "generatedImei";
		public static final String DEVICE_ID = "deviceId";
		public static final String LICENSE_CODE = "licenseCode";
		public static final String LICENSE_KEY = "licenseKey";
		public static final String SET_LICENSE_KEY = "setLicenseKey";
		public static final String NOTIFY_INVALID_LICENSE = "notifyInvalidLicense";
		public static final String NOTIFY_UNCONFIRMED_LICENSE = "notifyUnconfirmedLicense";
		public static final String NOTIFY_MARKET_ERROR = "notifyMarketError";
		public static final String LICENSE_GRACE_START = "licenseGraceStart";
		public static final String TRIAL_START = "trialStart";
		public static final String LICENSE_LAST_REMINDER = "licenseReminder";
		public static final String UPGRADE_STATUS = "upgrade";
		public static final String UPGRADE_PACKAGE_TYPE = "upgradePackageType";
		public static final String LAST_MARKET_RESPONSE_TIME = "lastMarketResponseTime";
		public static final String MARKET_LICENSE_STATUS = "marketLicenseStatus";
		public static final String MARKET_LICENSE_VALIDITY = "marketLicenseValidity";
		public static final String LICENSE_UPDATE_PACKAGE_STATES = "licenseUpdatePackageStates";
		public static final String MARKET_LICENSE_LAST_DEFINITIVE_STATUS = "lastDefinitiveMarketLicenseStatus";
		public static final String MARKET_UPGRADER_UNINSTALLED = "marketUpgraderUninstalled";
		public static final String LAST_FRIEND_RESPONSE = "lastFriendResponse";
		public static final String PLAYED_SONGS = "played_songs";
		public static final String LAST_PLAYED_SONG = "lastPlayedSong";
		public static final String ADD_FACEBOOK = "addFacebook";
		public static final String LAST_SCROBBLE_TIME = "lastScrobbleTime";
		public static final String LIB_RESOLVED = "libResolved";
		public static final String LIB_RESOLVED_TIME = "libResolvedTime";
		// Concatenated string of when last location was sent and the fix time with it
		public static final String LAST_LOC_TIMES = "lastLocTimes";
		public static final String LAST_DATE_USAGE_STATS = "usageStatsLastDate";
		public static final String LAST_STAT_TIME = "lastStatSendTime";
		public static final String LAST_ERROR_LOG_SENT_TIME = "lastErrorLogSentAt";
		public static final String LAST_ANALYTICS_LOG_SENT_TIME = "lastAnalytics";
		public static final String VIDEO_RESOLVE_TIME = "lastVideoResolveTime";
		public static final String PKG_RESOLVE_TIME = "lastPkgResolveTime";
		public static final String ANR_FILE_MODTIME = "anrFileLastMod";
		public static final String TRACKS_TAGS_CHANGED = "tracksTagsChanged";
		public static final String GENDER = "gender";
		public static final String YEAR_OF_BIRTH = "birthyear";
		public static final String SEND_DEMOGRAPHICS = "sendDemo";
		public static final String SEND_UPGRADE = "sendUpgrade";
		public static final String SEND_SHARE_LIB = "sendShareLib";
		public static final String LAST_SPLASH_DISPLAY_TIME = "lastSplashDisplayTime";
		public static final String DAILY_SPLASH_COUNT = "splashCount";
		public static final String DAILY_PLAY_SPLASH_COUNT = "playSplashCount";
		public static final String LAST_INTERSTITIAL_DISPLAY_TIME = "lastInterstitialTime";
		public static final String DAILY_INTERSTITIAL_COUNT = "interstitialCount";
		public static final String SPONSORED_TRACKS = "sponsoredTracks";
		public static final String SPONSORED_VIDEOS = "sponsoredVideos";
		public static final String LAST_SPONSORED_AUDIO_CHECK = "lastSpAudioCheck";
		public static final String LAST_SPONSORED_VIDEO_CHECK = "lastSpVideoCheck";
		public static final String RECENTLY_ADDED_NUM = "recentNum";
		public static final String RECENTLY_ADDED_UNITS = "recentUnits";
		public static final String RE_ENABLE_EQ = "reenableEq";
		public static final String LOG_INTERNAL_STORAGE_MISSING = "logIntStorageMissing";
		public static final String LOG_INTERNAL_STORAGE_WAS_MISSING = "logIntStorageWasMissing";
		public static final String ACTIVE_TAB = "activeTab";
		public static final String ACTIVE_MUSIC_TAB = "activeMusicTab";
		public static final String EXPLORER_SHOW_META = "showMeta";
		public static final String DASHBOARD_UPDATE = "dashboard_update";
		public static final String NO_SPACE = "noSpace";
		public static final String PLAYLISTS_DELETED = "plsDeleted";
		public static final String PLAYLISTS_PROMPT_COUNT = "plsPrompts";
		public static final String SLEEP_TIMER_MINUTES = "sleepMins";
		public static final String PREF_SCREEN_SOCIAL = "pref_screen_social";
		public static final String RADIO_STATION_GENRE = "radioGenre";
		public static final String RADIO_STATION_BITRATE = "radioBitrate";
		public static final String RADIO_STATION_SORT = "radioSort";
		public static final String RHAPSODY_USERNAME = "rhu";
		public static final String RHAPSODY_PASSWORD = "rhp";
		public static final String RHAPSODY_HQ_STREAMING = "hqStream";
		public static final String UUID = "uuid";
		public static final String SHOW_RHAPSODY_LAUNCH_PROMPT = "rhapLaunch";
		public static final String SHOW_RATE_MSG = "rateMsg";
		public static final String CITY = "city";
		public static final String COUNTRY = "country";
		public static final String CURLONGITUDE = "clongitude";
		public static final String CURLATITUDE = "clatitude";
		public static final String LAST_GEOCODED_LONGITUDE = "ge_longitude";
		public static final String LAST_GEOCODED_LATITUDE  = "ge_latitude";
		public static final String LAST_INFO_SELECTION = "last_info_selection";
		public static final String LAST_RADIO_SELECTION = "lastRadioSelection";
		public static final String LAST_GALLERY_SELECTION = "lastGallerySelection";
		public static final String SHOW_RECENTS = "showRecents";
		public static final String BUILD_VERSION_RELEASE = "build_vers_rel";
		public static final String DB_BUILD_VERSION_PROCESSED = "db_build_vers_processed";
		public static final String FOLDERS_CUR_DIRECTORY = "foldersCurDir";
		public static final String FOLDERS_CUR_SORT_ORDER = "foldersSortOrder";
		public static final String KINDLE_TOKEN = "kindleToken";

		public interface Social {
			public static final String LAST_STATE = "lastState";
			public static final String LAST_USER_STATE = "lastUserState";
			public static final String FIRST_APP_START = "firstAppStart";
			public static final String FIRST_UI_START = "firstUIStart";
			public static final String CRITERIA_SHARED = "critShared";
			public static final String CRITERIA_NOW_PLAYING = "nowPlaying";
			public static final String CRITERIA_MALE = "critMale";
			public static final String CRITERIA_FEMALE = "critFemale";
			public static final String CRITERIA_START_AGE = "critStartAge";
			public static final String CRITERIA_END_AGE = "critEndAge";
			public static final String CRITERIA_GROUP = "critGroup";
			public static final String NOW_PLAYING_STATE = "nowPlayingState";
			public static final String TOP_100_MODE = "top100Mode";
			public static final String MOST_SONG_SELECTION = "mostSongSelection";
			public static final String MOST_USER_SELECTION = "mostUserSelection";
			public static final String DASHBOARD_MODE = "dashboardMode";
			public static final String DASHBOARD_USER_MODE ="dashboardUserMode";
		}
	}

	public interface Defaults {
		public static final boolean ALBUM_ART_FILE = false;
		public static final boolean ALBUM_DIRS = true;
		public static final boolean LEAVE_SERVICE = false;
		public static final boolean LOCK_WIDGET = false;
		public static final boolean LOCK_WIDGET_PLAYING = false;
		public static final boolean PERSISTENT_NOTIFICATIONS = true;
		public static final boolean REMOTE_CONTROL = true;
		public static final boolean LOCK_WIDGET_WAKE = true;
		public static final boolean SHOW_SEARCH_BOX_PORTRAIT = false;
		public static final boolean SHOW_SEARCH_BOX_LANDSCAPE = false;
		public static final boolean SHOW_SEARCH_BOX_KBD_OPEN = true;
		public static final boolean SHOW_NOW_PLAYING_MUSIC = true;
		public static final boolean SHOW_NOW_PLAYING_SOCIAL = false;
		public static final boolean SHOW_NOW_PLAYING_PORT_ONLY = false;
		public static final boolean USE_INTERNAL_STORAGE = false;
		public static final boolean RECOMMENDATIONS = true;
		public static final boolean ENABLE_EQ = false;
		public static final boolean ENABLE_EQ_ALWAYS = false;
		public static final boolean ENABLE_GESTURES = true;
		public static final boolean SYSTEM_EVENTS = false;
		public static final int RECENTLY_ADDED_NUM = 2;
		public static final int RECENTLY_ADDED_UNITS = Values.RECENTLY_ADDED_WEEKS;
		public static final String BUFFER_SIZE = Values.BUFFER_SIZE_NORMAL;
		public static final boolean ENABLE_ALL_EQ_BANDS = false;
		public static final boolean PAUSE_ON_HEADSET_REMOVAL = true;
		public static final boolean PLAY_ON_HEADSET_PLUGGED = false;
		public static final boolean EXPLORER_SHOW_META = true;
		public static final boolean EXPLORER_BACK_NORMAL = true;
		public static final String DASH_BOARD_UPDATE = "5";
		public static final boolean SHARE_LIB = true;
	}

	public interface Values {
		public static final int DOWNLOAD_ART_ALWAYS = 2;
		public static final int DOWNLOAD_ART_CHARGING = 3;
		public static final int DOWNLOAD_ART_NEVER = 4;
		public static final int RECENTLY_ADDED_HOURS = 0;
		public static final int RECENTLY_ADDED_DAYS = 1;
		public static final int RECENTLY_ADDED_WEEKS = 2;
		public static final String BUFFER_SIZE_NORMAL = "0";
	}
}