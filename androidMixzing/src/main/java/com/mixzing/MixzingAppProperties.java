package com.mixzing;

import com.mixmoxie.util.XProperties;

public class MixzingAppProperties {
	protected static XProperties props;

	public static final String SERVER_URL = "ServerURL";
	public static final String ERROR_LOG_URL = "ErrorLogURL";
	public static final String QUERY_URL = "QueryURL";
	public static final String ID_URL = "IdUrl";
	public static final String CONTEST_URL = "ContestUrl";
	public static final String RADIO_URL = "RadioUrl";
	public static final String BROADJAM_URL = "BroadjamUrl";
	public static final String SHARE_URL = "ShareURL";
	public static final String DOWNLOAD_URL = "DownloadURL";
	public static final String AD_URL = "AdURL";
	public static final String INFO_URL = "InfoBaseURL";
	public static final String HELP_URL = "HelpBaseURL";
	public static final String ANALYTICS_URL = "AnalyticsURL";
	public static final String FILE_URL = "ServerFilePort";

	private static final int PROD = 0;
	private static final int DEV = 1;
	private static final int STAGE = 2;
	private static final int PRODDEV = 3;
	private static final int PROD_STAGE = 4;

	private static final int BUILD = PROD;

	static {
		props = new XProperties();

		if (BUILD == PROD || BUILD == PROD_STAGE) {
			// App servers
			props.put("com.mixzing.appserver", "appsrv-android.mixzing.com");
			props.put("com.mixzing.proxyserver", "proxysrv-android-json.mixzing.com");
			props.put("com.mixzing.helpserver", "helpsrv-android.mixzing.com");
			props.put("com.mixzing.infoserver", "infosrv-android.mixzing.com");
			props.put("com.mixzing.adserver", "adsrv-android.mixzing.com");
			props.put("com.mixzing.shareserver", "music.mixzing.com");
			props.put("com.mixzing.idserver", "idsrv-android.mixzing.com");
			props.put("com.mixzing.contestserver", "contest.mixzing.com");
			props.put("com.mixzing.radioserver", "radio.mixzing.com");
			props.put("com.mixzing.broadjamserver", "appsrv-android.mixzing.com");
			props.put("com.mixzing.logserver", "logsrv-android.mixzing.com");
			props.put("com.mixzing.anaserver", "anasrv-android.mixzing.com");

			if (BUILD == PROD) {
				props.put("com.mixzing.server.release", "003");
				props.put("ProxyName", "ProxyServerJsonServletPerl");
				props.put("CGIPort", "80");
			}
			else if (BUILD == PROD_STAGE) {
				props.put("com.mixzing.server.release", "stage");
				props.put("ProxyName", "ProxyServerJsonStageServletPerl");
				props.put("CGIPort", "8050");
			}
		}

		else if (BUILD == DEV || BUILD == STAGE || BUILD == PRODDEV) {
			final String testServer = "dev.mixzing.com";
			props.put("com.mixzing.appserver", testServer);
			props.put("com.mixzing.proxyserver", testServer);
			props.put("com.mixzing.helpserver", testServer);
			props.put("com.mixzing.infoserver", testServer);
			props.put("com.mixzing.adserver", testServer);
			props.put("com.mixzing.shareserver", testServer);
			props.put("com.mixzing.idserver", "idsrv-android.mixzing.com");
			props.put("com.mixzing.contestserver", testServer);
			props.put("com.mixzing.radioserver", testServer);
			props.put("com.mixzing.broadjamserver", testServer);
			props.put("com.mixzing.logserver", testServer);
			props.put("com.mixzing.anaserver", testServer);

			if (BUILD == DEV) {
				props.put("com.mixzing.server.release", "dev");
				props.put("ProxyName", "ProxyServerJsonDevServletPerl");
				props.put("CGIPort", "8080");
			}
			else if (BUILD == STAGE) {
				props.put("com.mixzing.server.release", "stage");
				props.put("ProxyName", "ProxyServerJsonStageServletPerl");
				props.put("CGIPort", "8050");
			}
			else if (BUILD == PRODDEV) {
				props.put("com.mixzing.server.release", "prod");
				props.put("ProxyName", "ProxyServerJsonProdServletPerl");
				props.put("CGIPort", "8010");
			}
		}

		props.put("com.mixzing.proxyserver.port", "8000");

		// Web server
		props.put("com.mixzing.webserver", "www.mixzing.com");

		// CGIs
		props.put(SERVER_URL, "http://{com.mixzing.proxyserver}:{com.mixzing.proxyserver.port}/{ProxyName}/JSON-{com.mixzing.server.release}");

		props.put("PerlCGIBase", "http://{com.mixzing.appserver}:{CGIPort}/perl/{com.mixzing.server.release}");
		props.put(QUERY_URL, "{PerlCGIBase}/query.pl");
		props.put(DOWNLOAD_URL, "{PerlCGIBase}/download.pl?pkg=");
		props.put(ERROR_LOG_URL, "http://{com.mixzing.logserver}:{CGIPort}/perl/{com.mixzing.server.release}/logerror.pl");
		props.put(AD_URL, "http://{com.mixzing.adserver}:{CGIPort}/perl/{com.mixzing.server.release}/ad.pl");
		props.put(SHARE_URL, "http://{com.mixzing.shareserver}:{CGIPort}/perl/{com.mixzing.server.release}/share.pl");
		props.put(ID_URL, "http://{com.mixzing.idserver}:{CGIPort}/perl/{com.mixzing.server.release}/query.pl");
		props.put(CONTEST_URL, "http://{com.mixzing.contestserver}:{CGIPort}/perl/{com.mixzing.server.release}/query.pl");
		props.put(RADIO_URL, "http://{com.mixzing.radioserver}:{CGIPort}/perl/{com.mixzing.server.release}/query.pl");
		props.put(BROADJAM_URL, "http://{com.mixzing.broadjamserver}:{CGIPort}/perl/{com.mixzing.server.release}/query.pl");
		props.put(ANALYTICS_URL, "http://{com.mixzing.anaserver}:{CGIPort}/perl/{com.mixzing.server.release}/analytics.pl");

		// HTML
		props.put(INFO_URL, "http://{com.mixzing.infoserver}:{CGIPort}/{com.mixzing.server.release}/Android");
		props.put(HELP_URL, "http://{com.mixzing.helpserver}/help/Android");

		// Release independent stuff
		props.put(FILE_URL, "http://{com.mixzing.webserver}/updates");

		if (BUILD != PROD) {
			props.put(ID_URL, "http://idsrv-android.mixzing.com:80/perl/003/query.pl");
		}
	}
	
	public static XProperties getProperties() {
		return props;
	}
}
