package com.mixzing.message.transport.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import com.mixzing.log.Logger;
import com.mixzing.message.messages.impl.ClientMessageEnvelope;
import com.mixzing.message.messages.impl.ClientRequestFile;
import com.mixzing.message.messages.impl.ServerFileResponse;
import com.mixzing.message.messages.impl.ServerMessageEnvelope;
import com.mixzing.message.transport.ServerTransport;
import com.mixzing.message.transport.SimulateServerMessage;


/**
 *
 * Communicate with server using http post
 *
 * @author sandeep
 *
 */
public class ServerHttpGet implements ServerTransport {

	private static Logger lgr = Logger.getRootLogger();

	protected String webServerFilePort;

	protected int responseCode;

	private boolean isShuttingDown = false;
	private int MIN_RETRY_INTERVAL = 30000;

	protected SimulateServerMessage simulator;

	public void shutDown() {
		this.isShuttingDown  = true;
	}

	public ServerHttpGet (String webServerRoot, SimulateServerMessage sim) {
		webServerFilePort = webServerRoot;
		this.simulator = sim;
		if(webServerRoot == null) {
			lgr.fatal("Server URL is malformed, " + ":" + webServerFilePort);
			throw new RuntimeException("Null webserver root: " + webServerFilePort);
		}
		try {
			URL webServerFilePortURL = new URL(webServerFilePort);

		} catch (java.net.MalformedURLException e) {
			lgr.fatal("WebServer URL is malformed, " + ":" + webServerFilePort);
		}
	}    

	public InputStream sendMessage(ByteArrayOutputStream s) {
		return sendMessage(s.toByteArray());
	}

	public int getResponseCode() {
		return responseCode;
	}


	protected InputStream getFileFromServer(String url) {
		byte[] data = getFile(url);
		if(data == null) {
			data = new byte[0];
		}
		return simulateServerMessage(data);
	}

	protected InputStream simulateServerMessage(byte[] data) {
		ServerMessageEnvelope env = simulator.generateMessageEnvelope();
		ServerFileResponse resp = simulator.generateFileMessage(data,false);
		if(data.length > 0)
			resp.setError(false);
		else 
			resp.setError(true);
		env.getMessages().add(resp);
		byte[] msg = simulator.marshall(env);
		return new ByteArrayInputStream(msg);
	}

	protected byte[] getFile(String url) {
		String filePort = url;
		ByteArrayOutputStream out = new ByteArrayOutputStream(256);

		try {
			URL filePortURL = new URL(filePort);            

			byte[] buffer = new byte[256];
			int len;

			boolean received = false;
			responseCode = 0;
			long start = System.currentTimeMillis();

			while (!received) {
				try {
					start = System.currentTimeMillis();
					if(Logger.IS_TRACE_ENABLED)
						lgr.debug("Send message to server");

					HttpURLConnection uc = (HttpURLConnection) filePortURL.openConnection();
					// uc.setRequestMethod("POST");
					uc.setRequestProperty("Content-Type","application/octet-stream");
					uc.setDoOutput(true);
					uc.setDoInput(true);
					uc.setConnectTimeout(MIN_RETRY_INTERVAL);
					uc.setReadTimeout(MIN_RETRY_INTERVAL);
					uc.connect();
					responseCode = uc.getResponseCode();
					InputStream responseStream = uc.getInputStream();
					out = new ByteArrayOutputStream(256);

					while ((len = responseStream.read(buffer)) > 0) {
						out.write(buffer,0,len);
					}
					responseStream.close();
					out.close();

					uc.disconnect();
					received = true;
				} catch (UnknownHostException e) {
					lgr.warn("Got exception, retry in a bit:", e);
					try {
						Thread.sleep(MIN_RETRY_INTERVAL);
					} catch (InterruptedException e1) {
					}
				} catch (ConnectException e) {
					lgr.warn("Got exception ", e);
					try {
						Thread.sleep(MIN_RETRY_INTERVAL);
					} catch (InterruptedException e1) {
					}
				} catch (SocketTimeoutException e ) {
					lgr.warn("Got exception ", e);
					try {
						Thread.sleep(MIN_RETRY_INTERVAL);
					} catch (InterruptedException e1) {
					}
				} catch (IOException e) {
					lgr.warn("Got exception ", e);
					// if there was an error then no point in trying again with this message.
					if (responseCode == 500 || responseCode == 404) {
						lgr.error("Communication with server failed with response code " + responseCode, e);
						break;
					} else {
						lgr.warn("Communication with server failed with response code " + responseCode, e);
						//long elapsed = System.currentTimeMillis() - start;
						// Prevent looping with no wait, that chews up CPU
						long sleep = MIN_RETRY_INTERVAL;
						if(sleep > 0) {
							try {
								Thread.sleep(sleep);
							} catch (InterruptedException e1) {
							}
						}
					}                
				} finally {
					if(this.isShuttingDown) {
						break;
					}               
				}
			}

		} catch (java.net.MalformedURLException e) {
			lgr.error("Invalid file port: " + url, e);
		}

		return out.toByteArray();
	}


	public InputStream sendMessage(byte[] s) {
		Object o = simulator.unmarshall(s);
		ClientMessageEnvelope env = (ClientMessageEnvelope) o;
		ClientRequestFile req = (ClientRequestFile) env.getMessages().get(0);
		String fname = req.getFileName();
		return getFileFromServer(webServerFilePort + "/" + fname);
	}

	public void networkStateChanged(boolean available) {
		// TODO Auto-generated method stub
		
	}

	public InputStream sendMessage(byte[] s, int retries) {
		lgr.warn("Incorrect call to ServerHttpGet.sendMessage");
		return null;
	}

}
