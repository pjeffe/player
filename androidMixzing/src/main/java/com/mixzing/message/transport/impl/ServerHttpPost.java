package com.mixzing.message.transport.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.os.SystemClock;

import com.mixmoxie.util.StackTrace;
import com.mixzing.android.AndroidUtil;
import com.mixzing.log.Logger;
import com.mixzing.message.transport.ServerStateNotifier;
import com.mixzing.message.transport.ServerTransport;


/**
 *
 * Communicate with server using http post
 *
 * @author sandeep
 *
 */
public class ServerHttpPost implements ServerTransport {


	private static final int LAST_NETWORK_UNKNOWN = 1;
	private static final int LAST_NETWORK_UP = 2;
	private static final int LAST_NETWORK_DOWN = 3;

	private static Logger lgr = Logger.getRootLogger();

	private String serverURL;

	protected int responseCode;
	protected URL server;

	private boolean isShuttingDown = false;
	private boolean isNetworkAvailable = true;
	private boolean isListenerHookedUp = false;
	private int lastNetworkStatus = LAST_NETWORK_UNKNOWN;

	private static int MAX_RETRY_INTERVAL = 3 * 60 * 1000; // 3 minutes 

	private static int CONNECT_TIMEOUT = 60 * 1000; // 60 seconds ?

	private ServerStateNotifier notifier;
	/*
	 * We are slow so use a larger read time out
	 */
	private int READ_TIMEOUT_INTERVAL = 60000;

	private boolean isSleepForNetworkEnabled = true;

	public void shutDown() {
		this.isShuttingDown  = true;
		synchronized(this) {
			this.notifyAll();
		}
	}

	public ServerHttpPost (String serverStr, ServerStateNotifier not, boolean networkSleep) {
		serverURL = serverStr;
		notifier = not;
		lastNetworkStatus = LAST_NETWORK_UNKNOWN;

		if(Logger.IS_TRACE_ENABLED)
			lgr.debug("Server = " + serverURL);
		try {
			server = new URL(serverURL);
		} catch (java.net.MalformedURLException e) {
			lgr.fatal("Our Server URL is malformed, " + serverURL );
		}
		initNetworkAvailability();
		isSleepForNetworkEnabled = networkSleep;
	}

	public ServerHttpPost (String serverStr, ServerStateNotifier not) {
		this(serverStr, not, true);
	}    

	protected void initNetworkAvailability() {
		synchronized(this) {
			isNetworkAvailable = AndroidUtil.hasNetwork();
		}
	}
	protected int getBackoffDelay(int prev) {
		if(prev <= 0) {
			prev = 1000;
		}
		int delay = prev * 2;
		if(delay > MAX_RETRY_INTERVAL) {
			delay = MAX_RETRY_INTERVAL;
		}
		if(Logger.IS_TRACE_ENABLED)
			lgr.debug("Seeting backoff delay = " + delay);
		return delay;
	}

	public InputStream sendMessage(ByteArrayOutputStream s) {
		return sendMessage(s.toByteArray());
	}

	public int getResponseCode() {
		return responseCode;
	}

	public InputStream sendMessage(byte[] s) {
		return sendMessage(s,Integer.MAX_VALUE);
	}

	public InputStream sendMessage(byte[] s, int maxRetries) {
		if (server == null) {
			lgr.fatal("Send message to server failed, bad server URL");
			return null;
		}

		int num500s = 0;
		byte[] buffer = new byte[256];
		int len;
		ByteArrayInputStream rval = null;
		boolean sent = false;
		responseCode = 0;

		int retryIntervalOnError = 1000;
		int retries = 0;

		while (!sent) {
			retries++;
			try {
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("Send message to server");
				rval = null;
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
				if (Logger.IS_DEBUG_ENABLED)
					lgr.debug("Got response code " + responseCode);
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
					rval = new ByteArrayInputStream(fullResponse.toByteArray());
					uc.disconnect();					

					if(Logger.IS_TRACE_ENABLED)
						lgr.trace("Got length: " + dataSz +  " expected length: " + expectedLen);

					if(dataSz == expectedLen) {
						sent = true;
					} else {
						lgr.warn("Network error: got length: " + dataSz + " expected: " + expectedLen );
						sleepOnException(retryIntervalOnError = getBackoffDelay(retryIntervalOnError), null);
					}
				} else {
					if(responseCode == 500 && ++num500s > 10) {
						if(Logger.IS_DEBUG_ENABLED) {
							lgr.error("Got 500s while sending message");
						}
						num500s=0;
					}
					// retry
					uc.disconnect();
					sleepOnException(retryIntervalOnError = getBackoffDelay(retryIntervalOnError), null);
				}
			} catch (Exception e) {
				sleepOnException(retryIntervalOnError = getBackoffDelay(retryIntervalOnError), e);               
			} finally {
				if(this.isShuttingDown || retries >= maxRetries) {
					break;
				}           	
			}
		}
		if (rval != null) {
			if(notifier != null) {
				if(lastNetworkStatus != LAST_NETWORK_UP) {
					notifier.notifyAboutServerConnection(true);
					lastNetworkStatus = LAST_NETWORK_UP;
				}
			}
			if(Logger.IS_TRACE_ENABLED)
				lgr.debug("Done Send message to server, using notification code");
		} else {
			if(!isShuttingDown)
				if(Logger.IS_TRACE_ENABLED)
					lgr.trace("Failed to send a message to the server. Retries = " + retries + " \n"+ StackTrace.getStackTrace());
		}

		return rval;
	}


	private static final long MAX_SLEEP = 5 * 60 * 1000;

	private void sleepForNetwork() {
		if(isSleepForNetworkEnabled) {
			if(isListenerHookedUp) {
				synchronized(this) {
					while(!isNetworkAvailable) {
						try {
							if(Logger.IS_TRACE_ENABLED) {
								lgr.trace("No network available: waiting for network, with listener hooked up");
							}
							this.wait();
						} catch (InterruptedException e) {

						}
						if(Logger.IS_TRACE_ENABLED) {
							lgr.trace("Woken up: network state is: " + isNetworkAvailable);
						}
						if(isShuttingDown) {
							break;
						}
					}
				}			
			} else {
				while(!AndroidUtil.hasNetwork()) {
					try {
						if(isShuttingDown) {
							break;
						} else {
							if(Logger.IS_TRACE_ENABLED) {
								lgr.trace("No network available: sleeping some more, using network poll method");
							}
							synchronized(this) {
								this.wait(MAX_SLEEP);
							}
						} 
					} catch (InterruptedException e1) {
					}
				}
				if(Logger.IS_TRACE_ENABLED) {
					lgr.trace("Woken up: from poll, network should be availabe");
				}			
			}
		}
	}

	private long lastWarnTime = Long.MIN_VALUE;
	private static long MIN_TIME_TO_REPEAT_WARN = 5 * 60 * 1000;

	private void sleepOnException(long sleepTime, Exception e) {
		if(e != null) {
			long now = SystemClock.uptimeMillis();
			if(now > (lastWarnTime + MIN_TIME_TO_REPEAT_WARN)) {
				if(isSleepForNetworkEnabled) {
					if (Logger.IS_DEBUG_ENABLED)
						lgr.debug("Got exception while sending to server: ", e);
				}
				lastWarnTime = now;
			}
		}
		if(isShuttingDown) {
			return;
		}
		if(Logger.IS_TRACE_ENABLED) {
			lgr.trace("Attempting sleep for : " + sleepTime);
		}
		try {
			if(notifier != null) {
				if(lastNetworkStatus != LAST_NETWORK_DOWN) {
					notifier.notifyAboutServerConnection(false);
					lastNetworkStatus = LAST_NETWORK_DOWN;
				}
			}
			synchronized(this) {
				this.wait(sleepTime);
			}
		} catch (InterruptedException e1) {
		} finally {
			sleepForNetwork();
		}
	}

	public void networkStateChanged(boolean available) {
		synchronized(this) {
			isNetworkAvailable = available;
			isListenerHookedUp = true;
			if(isNetworkAvailable) {
				this.notifyAll();
			}					
		}
	}

}
