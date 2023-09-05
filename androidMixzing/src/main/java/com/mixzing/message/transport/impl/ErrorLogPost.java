package com.mixzing.message.transport.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpStatus;

import com.mixzing.android.AndroidUtil;
import com.mixzing.log.Logger;

public class ErrorLogPost {
	private static final Logger log = Logger.getRootLogger();


	public static boolean post(String url, String libId, String level, String msg) {
		try {
			final String encodedData = "id=" + libId + "&level=" + level + "&msg=" + URLEncoder.encode(msg,"UTF-8" ); // user-supplied
			return post(url, encodedData);
		}
		catch (Exception e) {
			log.error("ErrorLogPost.post:", e);
			return false;
		}
	}

	public static boolean post(String url, String encodedData) {
		// if no network available
		if (!AndroidUtil.hasNetwork()) {
			return false;
		}  
		boolean sent = false;
		try {
			final URL serverUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setConnectTimeout(60000);
			conn.setReadTimeout(60000);

			String agent = "Mozilla/4.0";
			String type = "application/x-www-form-urlencoded";

			conn.setRequestMethod("POST");
			conn.setRequestProperty( "User-Agent", agent );
			conn.setRequestProperty( "Content-Type", type );
			conn.setRequestProperty( "Content-Length", 
					encodedData.length() + "" );	
			conn.setDoOutput(true);
			conn.setDoInput(true);
	        OutputStream os = conn.getOutputStream();
	        os.write(encodedData.getBytes());
	        os.close();
	        int responseCode = conn.getResponseCode();
	        if (responseCode == HttpStatus.SC_OK) {
	            sent = true;	        
	            final InputStream responseStream = conn.getInputStream();
	            final byte[] buffer = new byte[256];
	            while (responseStream.read(buffer) > 0);
	            responseStream.close();
	        }
            conn.disconnect();

            if (Logger.IS_DEBUG_ENABLED) {
				log.debug("ErrorLogPost.post: response code = " + responseCode);
			}

		} catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("ErrorLogPost.post:", e);
			}
        } 

        return sent;
	}

	public static void main(String[] args) {
		boolean sent = post("","123456:789","ERROR","Simple test");
		System.out.println("Sent : " + sent);
	}
}
