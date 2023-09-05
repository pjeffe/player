package com.mixzing.log;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;

import android.content.Context;
import android.os.Process;
import android.os.StatFs;
import android.util.Log;

import com.mixzing.MixzingConstants;

import de.mindpipe.android.logging.log4j.LogConfigurator;


public class Logger {
	private static Logger instance;
	private Context context;
	private String tag;
	private ErrorAppender errorLogger;
	private String localLogFname;
	private org.apache.log4j.Logger localLogger;
	private Formatter header;
	private StringBuilder headersb;
	private static final Random rand = new Random();

	public static final String LEVEL_ERROR = "error";
	public static final String LEVEL_FATAL = "fatal";

	private static final boolean DEBUG_MODE = false;      // true for internal or external debug builds
	private static final boolean INTERNAL_BUILD = false;  // true for internal builds only
	public static final boolean IS_DEBUG_ENABLED = DEBUG_MODE;
	public static final boolean IS_TRACE_ENABLED = DEBUG_MODE;
	private static final boolean NON_ERROR_TO_SYSLOG = true;
	public static final boolean IS_CRYPTIC_MESSAGES_DISABLED = INTERNAL_BUILD;
	public static final boolean IS_INTERNAL_DEBUG = DEBUG_MODE && INTERNAL_BUILD;

	private static final long MIN_FREE_SPACE = 8 * 1024 * 1024;         // don't do local logging if < this free
	private static final long MAX_LOCAL_LOG_SIZE = MIN_FREE_SPACE / 2;  // max size of local log
	private static final int MAX_LOCAL_LOG_FILES = 8;                   // number of log files to keep
	private static final float PERCENT_FREE_SPACE = 0.2f;               // use no more than this much of free space
	private static final String LOCAL_HEADER_FMT = "%tm-%<td %<tH:%<tM:%<tS.%<tL %5d %5d %c %s:%s: ";

	private enum LogLevel {
		DEBUG(Level.DEBUG),
		INFO(Level.INFO),
		WARN(Level.WARN),
		ERROR(Level.ERROR),
		FATAL(Level.FATAL);

		private Priority priority;
		private char letter;

		private LogLevel(Priority priority) {
			this.priority = priority;
			this.letter = name().charAt(0);
		}
	};

	static {
		instance = new Logger();
	}

	private Logger() {
	}

	public void init(Context context, ErrorAppender errorLogger, String tag) {
		this.context = context;
		this.errorLogger = errorLogger;
		this.tag = tag;
		errorLogger.init();
		if (IS_DEBUG_ENABLED) {
			openLocalLog();
		}
	}

	public static Logger getRootLogger() {	
		return instance;
	}

	public void onUpdate() {
		if (errorLogger != null) {
			errorLogger.onUpdate();
		}
	}

	// log to system log and/or our logs
	private void log(LogLevel level, String msg) {
		final String tag = getTag();

		// only log to system log if >= error or we're syslogging all
		if (level == LogLevel.DEBUG) {
			if (NON_ERROR_TO_SYSLOG) {
				// debug level sometimes gets stripped so we log at info
				Log.i(tag, msg);
			}
		}
		else if (level == LogLevel.INFO) {
			if (NON_ERROR_TO_SYSLOG) {
				Log.i(tag, msg);
			}
		}
		else if (level == LogLevel.WARN) {
			if (NON_ERROR_TO_SYSLOG) {
				Log.w(tag, msg);
			}
		}
		else {  // error or fatal
			Log.e(tag, msg);
			logError(level, msg);
		}

		// everything gets logged locally in debug mode
		if (IS_DEBUG_ENABLED) {
			logLocal(level, msg);
		}
	}

	private void log(LogLevel level, Object...objs) {
		log(level, getMessage(objs));
	}

	// use log4j to write rolling log files to internal cache dir
	private void openLocalLog() {
		final LogConfigurator cfg = new LogConfigurator();
		final String dir = context.getCacheDir().getAbsolutePath();
		long free;
		try {
			final StatFs stat = new StatFs(dir);
			final long bs = stat.getBlockSize();
			final long avail = stat.getAvailableBlocks();
			free = avail * bs;
			if (IS_DEBUG_ENABLED) {
				final long total = (long)stat.getBlockCount() * bs;
				Log.i(tag, "Logger.openLocalLog: " + dir + ": " + free + " / " + total);
			}
		}
		catch (Exception e) {
			Log.e(tag, "Logger.openLocalLog: " + e);
			free = MIN_FREE_SPACE + 1;  // assume OK
		}
		if (free > MIN_FREE_SPACE) {
			long max = (long)(free * PERCENT_FREE_SPACE);
			if (max > MAX_LOCAL_LOG_SIZE) {
				max = MAX_LOCAL_LOG_SIZE;
			}
			try {
				cfg.setMaxFileSize(max / MAX_LOCAL_LOG_FILES);
				cfg.setMaxBackupSize(MAX_LOCAL_LOG_FILES);
				localLogFname = dir + File.separator + MixzingConstants.LOCAL_LOG_FILE;
				cfg.setFileName(localLogFname);
				cfg.setFilePattern("%m");
				cfg.setUseLogCatAppender(false);
				cfg.configure();
				localLogger = org.apache.log4j.Logger.getRootLogger();
			}
			catch (Exception e) {
				if (IS_DEBUG_ENABLED) {
					Log.e(tag, "Logger.openLocalLog: " + e);
				}
			}

			headersb = new StringBuilder();
			header = new Formatter(headersb, Locale.US);
		}
		else {
			Log.e(tag, "Logger.openLocalLog: " + dir + " has only " + free + " free");
		}
	}

	// write to local log file
	private void logLocal(LogLevel level, String msg) {
		final org.apache.log4j.Logger logger = localLogger;
		if (logger != null) {
			final Thread thread = Thread.currentThread();
			final int pid = Process.myPid();
			final int tid = Process.myTid();
			synchronized (logger) {
				final Calendar cal = Calendar.getInstance();
				headersb.setLength(0);
				header.format(LOCAL_HEADER_FMT, cal, pid, tid, level.letter, tag, thread.getName());
				final String hdr = headersb.toString();
				final StringBuilder sb = new StringBuilder();
				final String[] lines = msg.split("\n");
				for (final String line : lines) {
					sb.append(hdr);
					sb.append(line);
					sb.append("\n");
				}
				logger.log(level.priority, sb.toString());
			}
		}
	}

	/**
	 * Concatenate any local log files to the output stream.
	 * @return true if succeeded
	 */
	public boolean writeLocalLog(OutputStream out) {
		boolean ret = true;

		final org.apache.log4j.Logger logger = localLogger;
		if (logger != null) {
			synchronized (logger) {
				InputStream last = null;
				for (int i = MAX_LOCAL_LOG_FILES - 1; i >= 0; --i) {
					final String fname = i > 0 ? localLogFname + "." + i : localLogFname;
					InputStream in = null;
					try {
						in = new BufferedInputStream(new FileInputStream(fname), 16 * 1024);
						last = in;
					}
					catch (FileNotFoundException e) {
						if (last != null) {
							Log.e(tag, "Logger.writeLocalLog: missing log file " + fname);
						}
					}
					catch (Exception e) {
						Log.e(tag, "Logger.writeLocalLog: error opening " + fname + ": " + e);
						ret = false;
					}

					if (in != null) {
						try {
							final byte[] buf = new byte[16 * 1024];
							int num;
							while ((num = in.read(buf)) >= 0) {
								out.write(buf, 0, num);
							}
						}
						catch (Exception e) {
							Log.e(tag, "Logger.writeLocalLog: error writing " + fname + ": " + e);
							ret = false;
						}
						finally {
							try {
								in.close();
							}
							catch (Exception e) {
							}
						}
					}
				}
			}
		}

		try {
			out.close();
		}
		catch (Exception e) {
			Log.e(tag, "Logger.writeLocalLog: error closing: " + e);
			ret = false;
		}

		return ret;
	}

	public String getMessage(Object... objs) {
		if (objs == null || objs.length == 0) {
			return "";
		}
		final String msg;
		if (objs.length > 1 || objs[0] instanceof Throwable) {
			final StringBuilder sb = new StringBuilder();
			boolean delim = false;
			for (Object o : objs) {
				if (delim) {
					sb.append("\n");
				}
				else {
					delim = true;
				}
				if (o instanceof Throwable) {
					Throwable e = (Throwable)o;
					do {
						final String emsg = e.getMessage();
						sb.append(e.getClass().getName());
						if (emsg != null) {
							sb.append(": ");
							sb.append(emsg);
						}
						sb.append("\n");
						for (StackTraceElement ste : e.getStackTrace()) {
							sb.append("  ");
							sb.append(ste.toString());
							sb.append("\n");
						}
						e = e.getCause();
						if (e != null) {
							sb.append("caused by:\n");
						}
					} while (e != null);
				}
				else if (o instanceof Class) {  // prefix message with class name
					Class<?> cls = (Class<?>)o;
					String name = cls.getSimpleName();
					if (name == null || name.length() == 0) {
						name = cls.getName();
					}
					sb.append(name);
					sb.append(": ");
					delim = false;
				}
				else if (o == null) {
					sb.append("<null>");
				}
				else {
					sb.append(o.toString());
				}
			}
			msg = sb.toString();
		}
		else {
			final Object o = objs[0];
			msg = o == null ? "<null>" : o.toString();
		}
		return msg;
	}

	public void debug(Object... objs) {
		log(LogLevel.DEBUG, objs);
	}

	public void debug(String msg) {
		log(LogLevel.DEBUG, msg);
	}

	public void info(Object... objs) {
		log(LogLevel.INFO, objs);
	}

	public void warn(Object... objs) {
		log(LogLevel.WARN, objs);
	}

	public void error(Object... objs) {
		log(LogLevel.ERROR, objs);
	}

	public void error(String msg) {
		log(LogLevel.ERROR, msg);
	}

	public void fatal(Object... objs) {
		log(LogLevel.FATAL, objs);
	}

	private void logError(LogLevel level, String msg) {
		if (errorLogger != null) {
			errorLogger.logError(level == LogLevel.ERROR ? LEVEL_ERROR : LEVEL_FATAL, msg);
		}
		else {
			Log.e(tag, " **** Error logger not initialized **** ");
			Log.e(tag, msg);
		}
	}

	public void trace(Object... objs) {
		log(LogLevel.INFO, objs);
	}

	public void trace(String msg) {
		log(LogLevel.INFO, msg);
	}

	private String getTag() {
		return tag + ":" + Thread.currentThread().getName();
	}

    /**
	 * Used for potentially noisy log messages to limit their frequency while still getting input from the field.
	 * 
	 * @return true if you should proceed with logging
	 */
	public static boolean shouldSelectivelyLog() {
		return shouldSelectivelyLog(500);
	}

	@SuppressWarnings("unused")
	public static boolean shouldSelectivelyLog(int i) {
		return IS_DEBUG_ENABLED || (rand.nextInt() % i == 0);
	}
}
