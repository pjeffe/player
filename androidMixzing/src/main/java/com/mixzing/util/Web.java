package com.mixzing.util;

import static com.mixzing.MixzingConstants.ONE_SECOND;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import android.net.Uri;
import android.os.SystemClock;

import com.mixzing.android.AndroidUtil;
import com.mixzing.android.Preferences;
import com.mixzing.log.Logger;


public class Web {
	private static Logger log = Logger.getRootLogger();
	public static final int BUFSIZE = 128 * 1024;
	protected static final int RESPONSE_TIMEOUT = 60000; // 60 seconds

	private static final String LIB_PARAM = "&lib=&";

	private static final String GOOGLE_SHORTEN_URL = "https://www.googleapis.com/urlshortener/v1/url";
	private static final String GOOGLE_SHORTEN_KEY = "\", \"key\": \"AIzaSyBNetdz1F5HW9P_gMUh5lFnlYCsbfKDNzQ\"}";

	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_USER_AGENT = "User-Agent";
	public static final String HEADER_ACCEPT = "Accept";

	public static final String CONTENT_JSON = "application/json";

	public static final String UTF8_ENCODING = "UTF-8";
	public static final String ISO_8859_ENCODING = "ISO-8859-1";
	public static final String US_ASCII_ENCODING = "US-ASCII";

	public static final String RESULT_ERROR_URL = "Web.errorUrl";
	public static final String RESULT_EXCEPTION = "Web.exception";
	public static final String RESULT_NO_REDIRECT = "Web.noRedirect";
	public static final String RESULT_ERROR = "Web.error";

	public static final int ERR_NO_SPACE = 1000;
	public static final int ERR_NO_NETWORK = 2000;

	public enum StatusType {
		UNKNOWN,
		INFORMATIONAL,  // 100s
		SUCCESS,        // 200s
		REDIRECTION,    // 300s
		CLIENT_ERROR,   // 400s
		SERVER_ERROR;   // 500s

		private static int max = values().length - 1;

		public static StatusType getType(int status) {
			final int type = status == 0 ? 0 : status / 100;
			if (type >= 0 && type <= max) {
				return values()[type];
			}
			return UNKNOWN;
		}
	}

	public static class Response {
		public int status;
		public StatusType statusType;
		public byte[] content;
	}

	public static Response getWebContent(Uri uri) {
		return getWebContent(uri, new HttpGet(uri.toString()), null, RESPONSE_TIMEOUT, 0, null, false, false, null);
	}

	public static Response getWebContent(Uri uri, String userAgent) {
		return getWebContent(uri, new HttpGet(uri.toString()), null, RESPONSE_TIMEOUT, 0, userAgent, false, false, null);
	}

	public static Response getWebContent(Uri uri, int timeout, int retries) {
		return getWebContent(uri, new HttpGet(uri.toString()), null, timeout, retries, null, false, false, null);
	}

	public static Response getWebContent(Uri uri, HttpClient client, String[][] headers, int timeout, int retries) {
		return getWebContent(uri, new HttpGet(uri.toString()), client, timeout, retries, null, false, false, headers);
	}

	private static Response getWebContent(Uri uri, HttpRequest req, HttpClient client, int timeout, int retries,
			String userAgent, boolean returnErrors, boolean waitForLib, String[][] headers) {
		final Response ret = new Response();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (headers != null) {
			for (String[] header : headers) {
				req.setHeader(header[0], header[1]);
			}
		}
		ret.status = getWebContent(uri, req, client, out, timeout, retries, userAgent, returnErrors, waitForLib);
		ret.statusType = StatusType.getType(ret.status);
		if (ret.statusType == StatusType.SUCCESS || returnErrors) {
			ret.content = out.toByteArray();
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("Web.getWebContent: total bytes = " + ret.content.length);
			}
		}
		return ret;
	}

	public static int getWebContent(Uri uri, OutputStream out) {
		return getWebContent(uri, new HttpGet(uri.toString()), null, out, RESPONSE_TIMEOUT, 1, null, false, false);
	}

	private static int getWebContent(Uri uri, HttpRequest req, HttpClient client, OutputStream out, int timeout,
			int retries, String userAgent, boolean returnErrors, boolean waitForLib) {
		HttpEntity entity = null;
		int status = 0;

		final long start;
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("Web.getWebContent: uri = " + uri + ", timeout = " + timeout + ", retries = " + retries +
				", headers = " + Arrays.asList(req.getAllHeaders()));
			start = SystemClock.uptimeMillis();
		}

		try {
			if (timeout == -1) {
				timeout = RESPONSE_TIMEOUT;
			}
			if (client == null) {
				client = new DefaultHttpClient();
			}
			final HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, timeout);
			HttpConnectionParams.setSoTimeout(params, timeout);
			if (userAgent != null) {
				params.setParameter("http.useragent", userAgent);
			}

			final HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
			do {
				try {
					final HttpResponse resp = client.execute(target, req);
					status = resp.getStatusLine().getStatusCode();
					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("Web.getWebContent: got status " + status + " in " +
							(SystemClock.uptimeMillis() - start) + "ms for " + uri);
					}
					if (status == HttpStatus.SC_OK || returnErrors) {
						entity = resp.getEntity();
						entity.writeTo(out);
					}
				}
				catch (IOException ioe) {
					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("Web.getWebContent: got exception: " + ioe.getMessage() + ", retries = " + retries);
					}
					if ("No space left on device".equals(ioe.getMessage())) {
						status = ERR_NO_SPACE;
					}
					else {
						status = ERR_NO_NETWORK;
					}
				}
			} while (status == ERR_NO_NETWORK && retries-- > 0);
		}
		catch (Throwable t) {
			status = 0;
			if (Logger.shouldSelectivelyLog(1000)) {
				log.debug("Web.getWebContent: error getting content from " + uri.toString(), t);
			}
		}
		finally {
			try {
				if (entity != null) {
					entity.consumeContent();
				}
				if (client != null) {
					client.getConnectionManager().shutdown();
				}
			}
			catch (Exception e) {
			}
		}

		return status;
	}

	private static ResponseHandler<String> redirRespHandler = new ResponseHandler<String>() {
		public String handleResponse(HttpResponse resp) throws ClientProtocolException, IOException {
			String url = null;
			int status = resp.getStatusLine().getStatusCode();

			if (Logger.IS_DEBUG_ENABLED)
				log.debug("Web.resolve: status = " + status);

			if (status == HttpStatus.SC_OK) {
				// XXX assumes we'll always stop after final redirect
				url = RESULT_NO_REDIRECT;
			}
			else if (status == 301 || status == 302) {
				// return redirected location
				Header[] headers = resp.getHeaders("Location");
				int num = headers.length;
				if (num >= 0) {
					url = headers[0].getValue();

					if (Logger.IS_DEBUG_ENABLED) {
						// log.debug("Web.resolve: redirected to " + url);
						if (num > 1) {
							log.error("Web.resolve: got " + num + " location headers");
						}
					}
				}
				else {
					log.error("Web.resolve: redirect with no location header");
				}
			}

			return url;
		}
	};

	private static class RedirectClient extends DefaultHttpClient {
		private RedirectHandler redirHandler = new RedirectHandler() {
			public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
				return null;
			}

			public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
				return false;
			}

		};

		@Override
		protected RedirectHandler createRedirectHandler() {
			return redirHandler;
		}
	}

	// follow redirects and return the final url if sucessful, otherwise null on error or FOUND_ERROR_URL if
	// one of the errorUrls was found
	//
	public static String resolve(Uri uri, String[] stopUrls, String[] errorUrls) {
		HttpClient client = new RedirectClient();
		String url = uri.toString();
		try {
			for (;;) {
				HttpUriRequest req = new HttpGet(url);
				String loc = client.execute(req, redirRespHandler, null);

				if (Logger.IS_DEBUG_ENABLED)
					log.debug("Web.resolve: loc = " + loc);

				if (loc == null) {
					return RESULT_ERROR;
				}
				else if (loc == RESULT_NO_REDIRECT) {
					// got valid response
					return url; // XXX need return object that includes status
				}
				else {
					// got redirected: check if we hit an error or stop url
					if (errorUrls != null) {
						for (String errorUrl : errorUrls) {
							if (loc.contains(errorUrl)) {
								if (Logger.IS_DEBUG_ENABLED)
									log.debug("Web.resolve: found error url " + errorUrl);
								return RESULT_ERROR_URL;
							}
						}
					}

					if (stopUrls != null) {
						for (String stopUrl : stopUrls) {
							if (loc.contains(stopUrl)) {
								if (Logger.IS_DEBUG_ENABLED)
									log.debug("Web.resolve: found stop url " + stopUrl);
								return loc;
							}
						}
					}

					// keep resolving
					url = loc;
				}
			}
		}
		catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug("Web.resolve: error on request to " + url + ":", e);
			return RESULT_EXCEPTION;
		}
		finally {
			try {
				if (client != null) {
					client.getConnectionManager().shutdown();
				}
			}
			catch (Exception e) {
			}
		}
	}

	public static String getCompressedContent(String uri, int timeout, int retries) {
		String ret = null;
		Reader rdr = null;
		try {
			Response resp = getWebContent(Uri.parse(uri), new HttpGet(uri.toString()), null, timeout, retries, null, false, false, null);
			byte[] content = resp.content;
			if (resp.status == HttpStatus.SC_OK && content != null) {
				InputStream gzip = new GZIPInputStream(new ByteArrayInputStream(resp.content));
				rdr = new InputStreamReader(gzip, Web.UTF8_ENCODING); // XXX assume utf-8
				char[] buf = new char[1024];
				StringWriter sw = new StringWriter();
				int num;
				do {
					num = rdr.read(buf);
					if (num > 0) {
						sw.write(buf, 0, num);
					}
				}
				while (num != -1);
				ret = sw.toString();
			}
			else {
				if (Logger.IS_DEBUG_ENABLED)
					log.debug(String.format("Web.getCompressedContent: status %d getting '%s'", resp.status, uri));
			}
		}
		catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug(String.format("Web.getCompressedContent: error getting '%s': ", uri), e);
		}
		finally {
			if (rdr != null) {
				try {
					rdr.close();
				}
				catch (Exception e) {
					log.error("Web.getCompressedContent: error closing stream: ", e);
				}
			}
		}
		return ret;
	}

	public static String uriDecode(String url) {
		return Uri.decode(url);
	}

	private static HashMap<String, String> parseParams(String query) {
		final HashMap<String, String> map = new HashMap<String, String>();
		if (query != null) {
			final String params[] = query.split("&");
			for (String param : params) {
				String arr[] = param.split("=");
				map.put(arr[0], Uri.decode(arr[1]));
			}
		}
		return map;
	}

	public static HashMap<String, String> getParams(String urlstr) {
		HashMap<String, String> params = null;
		try {
			final URL url = new URL(urlstr);
			params = parseParams(url.getQuery());
			params.putAll(parseParams(url.getRef()));
		}
		catch (MalformedURLException e) {
			log.error("Web.getParams: url = " + urlstr + ":", e);
		}
		return params;
	}

	public static JSONObject getJSONObject(String url, int timeout, int retries, boolean returnErrors) {
		JSONObject json = null;

		url = waitForLibId(url);

		final Response resp = getWebContent(Uri.parse(url), new HttpGet(url), null, timeout, retries, null, returnErrors, true, null);
		if (resp.status == HttpStatus.SC_OK || returnErrors) {
			try {
				final String str = new String(resp.content, Web.UTF8_ENCODING);
				json = new JSONObject(str);
			}
			catch (Exception e) {
				// TODO try to detect HTML response, presumably from public WiFi authorization screens and allow client to prompt user
				if (Logger.IS_DEBUG_ENABLED) {
					log.error("Web.getJSON:", e);
				}
			}
		}
		return json;
	}

	public static JSONObject getJSONFromPost(String url, String data, String contentType, int timeout, int retries, boolean returnErrors) {
		JSONObject json = null;

		url = waitForLibId(url);

		final String[][] headers = { { HEADER_CONTENT_TYPE, contentType } };
		final Response resp = post(url, data, headers, timeout, retries);
		if (resp.status == HttpStatus.SC_OK || returnErrors) {
			try {
				final String str = new String(resp.content, Web.UTF8_ENCODING);
				json = new JSONObject(str);
			}
			catch (Exception e) {
				// TODO try to detect HTML response, presumably from public WiFi authorization screens and allow client to prompt user
				if (Logger.IS_DEBUG_ENABLED) {
					log.error("Web.getJSON:", e);
				}
			}
		}
		return json;
	}

	public static Response post(String url, String data, String[][] headers, int timeout, int retries) {
		Response resp;
		final HttpPost req = new HttpPost(url);

		if (headers != null) {
			for (String[] header : headers) {
				req.setHeader(header[0], header[1]);
			}
		}

		try {
			req.setEntity(new StringEntity(data, UTF8_ENCODING));
			resp = getWebContent(Uri.parse(url), req, null, timeout, retries, null, true, false, null);
		}
		catch (Exception e) {
			log.error("Web.post:", e);
			resp = new Response();
		}
		return resp;
	}

	/**
	 * Try to shorten a URL using goo.gl.
	 * @return Shortened URL or null if failed
	 */
	public static String shortenUrl(String url) {
		String ret = null;
		final String data = "{\"longUrl\": \"" + url + GOOGLE_SHORTEN_KEY;
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("Web.shortenUrl: data = " + data);
		}
		final String[][] headers = { { HEADER_CONTENT_TYPE, CONTENT_JSON } };
		final Response resp = post(GOOGLE_SHORTEN_URL, data, headers, 20 * (int)ONE_SECOND, 2);
		if (resp.status == HttpStatus.SC_OK) {
			try {
				final String str = new String(resp.content, Web.UTF8_ENCODING);
				final JSONObject json = new JSONObject(str);
				ret = json.optString("id", null);
			}
			catch (Exception e) {
				if (Logger.shouldSelectivelyLog()) {
					log.error("Web.shortenUrl:", e);
				}
			}
		}
		else if (resp.status == HttpStatus.SC_BAD_REQUEST && Logger.shouldSelectivelyLog()) {
			try {
				log.error("Web.shortenUrl: bad req: " + new String(resp.content, Web.UTF8_ENCODING));
			}
			catch (Exception e) {
			}
		}

		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("Web.shortenUrl: returning = " + ret);
		}
		return ret;
	}

	public static String waitForLibId(String url) {
		// if the url has no lib param then replace it with a valid one
		final int i = url.indexOf(LIB_PARAM);
		if (i >= 0) {
			final String libId = getLibId();
			if (libId != null) {
				url = url.replace(LIB_PARAM, "&lib=" + libId + "&");
			}
			else {
				// if we have no lib id then we provide params that the server uses to find or create one,
				// since the manager may never create a lib if there is no mounted external storage
				//
				final StringBuilder sb = new StringBuilder("&appid=");
				sb.append(AndroidUtil.getUuid());
				sb.append("&devid=");
				sb.append(AndroidUtil.getDeviceId());
				sb.append("&imei=");
				sb.append(AndroidUtil.getImei());

				final Locale locale = Locale.getDefault();
				sb.append("&language=");
				sb.append(locale.getLanguage());
				sb.append("&country=");
				sb.append(locale.getCountry());

				sb.append("&");
				url = url.replace(LIB_PARAM, sb.toString());
			}
		}
		return url;
	}

	private static String getLibId() {
		final String libId = AndroidUtil.getStringPref(null, AndroidUtil.getCardSpecificPrefKey(Preferences.Keys.LIB_ID), null);
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("Web.hasLibId: libId = " + libId);
		}
		return libId == null || libId.startsWith("-") ? null : libId;
	}
}
