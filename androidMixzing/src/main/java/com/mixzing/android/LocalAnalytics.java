package com.mixzing.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.mixmoxie.util.StackTrace;
import com.mixzing.MixzingConstants;
import com.mixzing.log.Logger;

public class LocalAnalytics {
	private static final Logger log = Logger.getRootLogger();
	private static Context context;
	private static String logFile;
	private static ArrayList<String> queue;
	private static Object queueLock = new Object();
	private static Object fileLock = new Object();
	private static boolean fileLocked;
	private static Handler handler;
	private static Object sessionLock = new Object();
	private static long lastSessionStart;
	private static int sessionStarts;

	private static final int MAX_QUEUE_SIZE = 20;
	private static final int MAX_LOG_FILE_SIZE = 1024 * 1024;
	private static final long LOG_WRITE_DELAY = 10 * MixzingConstants.ONE_SECOND;

	private static final int MSG_END_SESSION = 1;

	private static final String KEY_VERS = "vers";
	private static final String KEY_OSVERS = "osvers";
	private static final String KEY_TIME = "time";
	private static final String KEY_NAME = "name";
	private static final String KEY_ARGS = "args";


	public static void init(Context context) {
		LocalAnalytics.context = context;
		logFile = MixzingConstants.ANALYTICS_LOG_FILE;
		try {
			handler = new SessionHandler();
		}
		catch (Throwable t) {
			log.error("LocalAnalytics.init:", t);
		}
		queue = new ArrayList<String>(MAX_QUEUE_SIZE);
		new Thread(writer, "AnalyticsWriter").start();
	}

	public static void startSession() {
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("LocalAnalytics.startSession: starts = " + sessionStarts + ", lastStart = " + lastSessionStart);
		}
		if (handler != null) {
			synchronized (sessionLock) {
				// bump start count, set start time if it hasn't already been, and clear any session-end messages
				++sessionStarts;
				if (lastSessionStart == 0) {
					lastSessionStart = System.currentTimeMillis();
				}
				handler.removeMessages(MSG_END_SESSION);
			}
		}
	}

	public static void endSession() {
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("LocalAnalytics.endSession: starts = " + sessionStarts + ", lastStart = " + lastSessionStart);
		}
		if (handler != null) {
			synchronized (sessionLock) {
				// if this was the last end in the stack then queue up a message to log the session
				if (--sessionStarts <= 0) {
					final Message msg = handler.obtainMessage(MSG_END_SESSION);
					final long now = System.currentTimeMillis();
					final long start = lastSessionStart;
					msg.arg1 = (int)(start / 1000);  // log time
					msg.arg2 = (int)(now - start);  // session length
					handler.sendMessageDelayed(msg, Analytics.SESSION_TIMEOUT);
					if (sessionStarts < 0) {
						sessionStarts = 0;
						log.error("LocalAnalytics.endSession: end without start: " + StackTrace.getStackTrace(false));
					}
				}
			}
		}
	}

	private static class SessionHandler extends Handler {
		private static HashMap<String, String> sessionArgs = new HashMap<String, String>(1);

		public void handleMessage(Message msg) {
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("LocalAnalytics.handleMessage: msg = " + msg);
			}
			if (msg.what == MSG_END_SESSION) {
				synchronized (sessionLock) {
					if (sessionStarts == 0) {
						// log the session end
						sessionArgs.put(Analytics.DATA_SESSION_LENGTH, Integer.toString(msg.arg2));
						event(Analytics.EVENT_SESSION, msg.arg1, sessionArgs);

						// clear the start time
						lastSessionStart = 0;
					}
				}
			}
		}
	}

	/**
	 * Writes an event to the analytics log.
	 * @param name The name of the event
	 * @param args Name/value pairs of args for the event or null if none
	 */
	public static void event(String name, Map<String, String> args) {
		event(name, (int)(System.currentTimeMillis() / 1000), args);
	}

	private static void event(String name, int time, Map<String, String> args) {
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("LocalAnalytics.event: queue size = " + queue.size());
		}
		try {
			// convert event to JSON string
			final JSONObject json = new JSONObject();
			json.put(KEY_VERS, AndroidUtil.getVersionCode());
			json.put(KEY_OSVERS, Build.VERSION.RELEASE);
			json.put(KEY_TIME, time);
			json.put(KEY_NAME, name);
			if (args != null && args.size() != 0) {
				json.put(KEY_ARGS, new JSONObject(args));
			}

			synchronized (queueLock) {
				// write it to the event queue and wake up the writer if the queue was empty or is full
				queue.add(json.toString());
				final int size = queue.size();
				if (size == 1 || size >= MAX_QUEUE_SIZE) {
					queueLock.notify();
				}
			}
		}
		catch (Exception e) {
			log.error("LocalAnalytics.event: event = " + name + ", args = " + args, e);
		}
	}

	// writes events to the log file on a background thread
	private static Runnable writer = new Runnable() {
		@Override
		public void run() {
			while (true) {
				final ArrayList<String> events;
				final int size;
				final long delay;

				// wait for events to be logged
				synchronized (queueLock) {
					// wait until notified if the queue is empty, otherwise time out in a bit
					delay = queue.size() == 0 ? 0 : LOG_WRITE_DELAY;
					try {
						queueLock.wait(delay);
					}
					catch (Exception e) {
					}

					// check the event queue
					events = queue;
					size = events.size();

					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("LocalAnalytics.writer: queue size = " + size + ", delay = " + delay);
					}

					// keep waiting if we haven't timed out yet and the queue isn't full
					if (delay == 0 && size < MAX_QUEUE_SIZE) {
						continue;
					}
					queue = new ArrayList<String>(MAX_QUEUE_SIZE);
				}

				// write events to the log file if it's not too big
				synchronized (fileLock) {
					// if the file is being read then wait until it's done
					while (fileLocked) {
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("LocalAnalytics.writer: file locked");
						}
						try {
							fileLock.wait(1000);
						}
						catch (Exception e) {
						}
					}

					// append the events to the log file
					final File file = context.getFileStreamPath(logFile);
					final long len = file.length();
					if (len < MAX_LOG_FILE_SIZE) {
						OutputStream out = null;
						try {
							out = new BufferedOutputStream(new FileOutputStream(file, true), 4096);
							for (int i = 0; i < size; ++i) {
								if (i > 0 || len != 0) {
									out.write(',');
								}
								out.write(events.get(i).getBytes("UTF-8"));
							}
						}
						catch (Exception e) {
							if (Logger.shouldSelectivelyLog(100)) {
								log.error("LocalAnalytics.writer:", e);
							}
						}
						finally {
							if (out != null) {
								try {
									out.close();
								}
								catch (Exception e) {
									if (Logger.shouldSelectivelyLog()) {
										log.error("LocalAnalytics.writer:", e);
									}
								}
							}
							if (Logger.IS_DEBUG_ENABLED) {
								log.debug("LocalAnalytics.writer: file size = " + file.length());
							}
						}
					}
					else if (Logger.shouldSelectivelyLog(100)) {
						log.error("LocalAnalytics.writer: " + file.getAbsolutePath() + " size = " + file.length());
					}
				}
			}
		}
	};

	private static String readLog() {
		String data = null;
		synchronized (fileLock) {
			try {
				final File file = context.getFileStreamPath(logFile);
				if (Logger.IS_DEBUG_ENABLED) {
					log.debug("LocalAnalytics.readLog: exists = " + file.exists() + ", readable = " + file.canRead());
				}
				if (file.exists()) {
					if (file.canRead()) {
						InputStream inp = null;
						try {
							final int len = (int)file.length();
							if (len > 0) {
								inp = new BufferedInputStream(new FileInputStream(file), len);
								final byte[] buf = new byte[len];
								final int read = inp.read(buf);
								if (read == len) {
									data = new String(buf, "UTF-8");
								}
								else {
									log.error("LocalAnalytics.readLog: read " + read + ", expecting " + len + " from " + file.getAbsolutePath());
								}
							}
						}
						catch (Exception e) {
							log.error("LocalAnalytics.readLog:", e);
						}
						finally {
							if (inp != null) {
								try {
									inp.close();
								}
								catch (Exception e) {
									if (Logger.IS_DEBUG_ENABLED) {
										log.error("LocalAnalytics.readLog:", e);
									}
								}
							}
						}
					}

					// if there was a problem reading data try deleting the file
					if (data == null) {
						try {
							file.delete();
						}
						catch (Exception e) {
							if (Logger.shouldSelectivelyLog(100)) {
								log.error("LocalAnalytics.readLog:", e);
							}
						}
					}
				}
			}
			catch (Exception e) {
				log.error("LocalAnalytics.readLog:", e);
			}
		}
		return data;
	}

	public static void releaseData(boolean delete) {
		synchronized (fileLock) {
			fileLocked = false;
			if (delete) {
				try {
					context.getFileStreamPath(logFile).delete();
				}
				catch (Exception e) {
					if (Logger.shouldSelectivelyLog(100)) {
						log.error("LocalAnalytics.releaseData:", e);
					}
				}
			}
		}
	}

	public static String getPostData(boolean lock) {
		if (lock) {
			synchronized (fileLock) {
				fileLocked = true;
			}
		}
		String data = null;
		final String logData = readLog();
		if (logData != null) {
			try {
				final StringBuilder sb = new StringBuilder("libid=");
				sb.append(AndroidUtil.getLibraryId());
				sb.append("&devid=");
				sb.append(AndroidUtil.getDeviceId());
				sb.append("&imei=");
				sb.append(AndroidUtil.getImei());
				sb.append("&time=");
				sb.append(System.currentTimeMillis() / 1000);
				sb.append("&data=[");
				sb.append(URLEncoder.encode(logData, "UTF-8"));
				sb.append(']');
				data = sb.toString();
			}
			catch (Exception e) {
				log.error("LocalAnalytics.getPostData:", e);
			}
		}
//		if (Logger.IS_DEBUG_ENABLED) {
//			log.debug("LocalAnalytics.getPostData: returning <" + data + ">");
//		}
		return data;
	}
}
