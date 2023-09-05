package com.mixzing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.mixzing.android.AndroidUtil;
import com.mixzing.android.Preferences;
import com.mixzing.android.Preferences.Keys;
import com.mixzing.log.ErrorAppender;
import com.mixzing.log.Logger;

/**
 * 
 * Simple appender that opens the file every time and appends to it if it exists or 
 * creates a new one if it does not.
 * 
 * @author sandeepmathur
 *
 */
public class ErrorAppenderImpl implements ErrorAppender  {

	private int MAX_LOG_SZ = 512 * 1024;
	private String lastErrorHash;
	private String lastErrorThread;
	private int lastErrorCount;
	private GregorianCalendar lastErrorTime;
	private String tag;
	private String file;
	protected String info;
	private Context context;
	private int vers;
	private boolean current;
	private boolean currentVerified;

	private static final String LAST_ERROR_FILE = "lastError";


	public ErrorAppenderImpl(String fileName, String tg, Context context) {
		file = fileName;
		tag  = tg;
		this.context = context;
		lastErrorTime = new GregorianCalendar();
		try {
			// check if we are running the current release
			checkCurrent();
			final String pkg = AndroidUtil.getPackageName();
			final String name = AndroidUtil.getVersionName();
			final String os = Build.VERSION.RELEASE;
			final String model = Build.MODEL;
			final String prod = Build.PRODUCT;
			info = vers + "/" + name + "/" + os + "/" + pkg + "/" + model + "/" + prod;
		}
		catch (Exception e) {
			info = "exception/" + e.getMessage() + "//";
		}
	}

	private String makeHeader(String thread, String level) {
		return "\n" + thread + " " + level + " " + info + "\n";
	}

	private void checkCurrent() {
		final int vers = this.vers = AndroidUtil.getVersionCode();
		final int cur = AndroidUtil.getCurVers();
		if (vers == -1 || cur == -1) {
			current = true;  // assume true until we get valid data
		}
		else {
			currentVerified = true;
			if (vers >= cur) {
				current = true;
			}
			else {
				clearLastError();
			}
		}
	}

	protected void doAppend(String data) {
		try {
			File ff = context.getFileStreamPath(file);
			// Skip if we are full, since we probably have enough data that we need already
			if(ff.exists() && ff.length() > MAX_LOG_SZ) {
				return;
			}
			FileWriter wr = new FileWriter(ff, true);
			wr.write("\n" + new GregorianCalendar().getTime().toGMTString());
			wr.write(data);
			wr.write("\n");
			wr.close();
		} catch (Exception e) {
			if(Logger.IS_DEBUG_ENABLED) {
				Log.e(tag, "Got exception logging error - " + e);
			}
		}
	}

	public void init() {
		boolean valid = false;
		if (current) {
			try {
				// set last error time, count and hash from saved pref
				final String last = AndroidUtil.getStringPref(null, Keys.LAST_ERROR_DATA, null);
				if (last != null) {
					final String[] toks = last.split(" ", 5);
					if (toks.length == 5) {
						// discard if last error wasn't from current release
						final int lastVers = Integer.parseInt(toks[0]);
						if (vers == -1 || lastVers == -1 || lastVers >= vers) {
							valid = true;
							lastErrorTime.setTimeInMillis(Long.parseLong(toks[1]));
							lastErrorCount = Integer.parseInt(toks[2]);
							lastErrorHash = toks[3];
							lastErrorThread = toks[4];
	
							// if there was a repeated last error then check if it's time to log it
							if (lastErrorCount > 0) {
								checkLastError(makeHeader(lastErrorThread, Logger.LEVEL_ERROR));
							}
						}
					}
				}
			}
			catch (Exception e) {
				Log.e(tag, e.toString());
			}
		}

		if (!valid) {
			clearLastError();
		}
	}

	// on an update we clear out the previous state
	//
	public void onUpdate() {
		clearLastError();
	}

	private void clearLastError() {
		lastErrorHash = null;
		lastErrorThread = null;
		lastErrorCount = 0;
		lastErrorTime = new GregorianCalendar();
		AndroidUtil.removePref(null, Keys.LAST_ERROR_DATA);
		try {
			context.getFileStreamPath(LAST_ERROR_FILE).delete();
		}
		catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED) {
				Log.e(tag, e.toString());
			}
		}
	}

	public synchronized void logError(String level, String msg) {
		// check if we're recursing on an error
		if (msg.contains(getClass().getName())) {  // XXX assumes that this means we're in the stack trace
			msg = "logger recursing on error:\n" + msg;
			Log.e(tag, msg);
			doAppend("\n" + tag + ":<logger> error /error//\n" + msg);
			return;
		}

		try {
			// only log to server if we're running the current release
			if (!currentVerified) {
				checkCurrent();  // try again
			}
			if (current) {
				final String thr = tag + ":" + Thread.currentThread().getName();

				// check if this is a repeat of the last error
				// TODO strip instance-specific data before doing hash
				final String hash = AndroidUtil.getMd5Hash(msg);
				if (hash.equals(lastErrorHash)) {
					++lastErrorCount;
					// if it's time to log it then do so
					checkLastError(makeHeader(lastErrorThread, Logger.LEVEL_ERROR));
				}
				else {
					// not a repeat: log last error if set and log this one
					if (lastErrorCount > 0) {
						logLastError(makeHeader(lastErrorThread, Logger.LEVEL_ERROR));
					}
					doAppend(makeHeader(thr, level) + msg);

					// set last error data and save msg
					lastErrorHash = hash;
					lastErrorCount = 0;
					lastErrorTime = new GregorianCalendar();
					lastErrorThread = thr;
					saveLastError(msg);
				}
	
				// persist last error data
				final String data = vers + " " + lastErrorTime.getTimeInMillis() + " " + lastErrorCount + " " + hash + " " + thr;
				AndroidUtil.setStringPref(null, Preferences.Keys.LAST_ERROR_DATA, data);
			}
		}
		catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED) {
				Log.e(tag, "exception trying to log error: " + e + "\n" + msg);
			}
		}	
	}

	private void saveLastError(String msg) {
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(LAST_ERROR_FILE, Context.MODE_PRIVATE);
			final byte[] buf = msg.getBytes("UTF-8");
			fos.write(buf);
		}
		catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED) {
				Log.e(tag, e.toString());
			}
		}
		finally {
			if (fos != null) {
				try {
					fos.close();
				}
				catch (Exception e) {
				}
			}
		}
	}

	// log last error if it's time
	private void checkLastError(String header) {
		GregorianCalendar now = new GregorianCalendar();
		now.add(Calendar.DAY_OF_YEAR, -1);  // send once a day
		if (now.compareTo(lastErrorTime) > 0) {
			logLastError(header);
			clearLastError();
		}
	}

	private void logLastError(String header) {
		// read the last error message and log it
		FileInputStream fis = null;
		try {
			final File file = context.getFileStreamPath(LAST_ERROR_FILE);
			if (file.exists()) {
				fis = new FileInputStream(file);
				final int len = (int)file.length();
				final byte[] buf = new byte[len];
				if (fis.read(buf, 0, len) == len) {
					final String msg = new String(buf, "UTF-8");
					doAppend("\nrepeated " + lastErrorCount + " times since " + lastErrorTime.getTime().toGMTString() + header + msg);
				}
			}
		}
		catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED) {
				Log.e(tag, e.toString());
			}
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				}
				catch (Exception e) {
				}
			}
		}
	}
}	