package com.mixzing.decoder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import com.mixzing.log.Logger;

class MixZingFingerServerComm {

	protected static final Logger log = Logger.getRootLogger();
	
	private static int CONNECT_TIMEOUT = 60 * 1000; // 60 seconds ?
	private static int READ_TIMEOUT_INTERVAL = 90000;

	public static String sendMessage(URL server, byte[] s) {
		String retval = null;
		byte[] buffer = new byte[256];
		int len;
		int responseCode = 0;

		try {
			HttpURLConnection uc = (HttpURLConnection) server.openConnection();
			uc.setRequestMethod("POST");
			uc.setRequestProperty("Content-Type","application/octet-stream");
			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setConnectTimeout(CONNECT_TIMEOUT);
			uc.setReadTimeout(READ_TIMEOUT_INTERVAL);
			uc.setRequestProperty("Content-Length", s.length + "");
			uc.connect();
			OutputStream os = uc.getOutputStream();
			os.write(s);
			os.close();
			responseCode = uc.getResponseCode();
			if(responseCode == HttpStatus.SC_OK) {
				InputStream responseStream = uc.getInputStream();
				ByteArrayOutputStream fullResponse = new ByteArrayOutputStream(256);
				int dataSz = 0;
				int expectedLen = uc.getContentLength();
				while ((len = responseStream.read(buffer)) > 0) {
					fullResponse.write(buffer,0,len);
					dataSz += len;
				}
				responseStream.close();
				fullResponse.close();
				uc.disconnect();					
				if(dataSz == expectedLen) {
					retval = new String(fullResponse.toByteArray());
				} 
			} else {
				uc.disconnect();
			}
		} catch (Exception e) {

		} finally {
		}

		return retval;
	}

}
