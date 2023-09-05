package com.mixzing.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import com.mixzing.log.Logger;


public class Util {
	private static final Logger log = Logger.getRootLogger();

	public static int getInt(String val) {
		try {
			return Math.round(Float.parseFloat(val));
		}
		catch (Exception e) {
			return 0;
		}
	}

	public static long getLong(String val) {
		try {
			return Math.round(Double.parseDouble(val));
		}
		catch (Exception e) {
			return 0;
		}
	}

	public static boolean getBoolean(String val) {
		return Boolean.parseBoolean(val);
	}

	// assumes YYYY-MM-DD format
	public static Date getDate(String val) {
		Calendar cal = Calendar.getInstance();
		String[] arr = val.split("-");
		if (arr.length >= 3) {
			int year = getInt(arr[0]);
			int month = getInt(arr[1]);
			int day = getInt(arr[2]);
			if (year >= 0 && month >= 0 && day >= 0) {
				try {
					cal.set(year, month - 1, day, 0, 0, 0);
				}
				catch (Exception e) {
					return null;
				}
				return cal.getTime();
			}
		}
		return null;
	}

	public static String readTextFile(String path) {
		return readTextFile(path, true, false);
	}

	public static String readTextFile(String path, boolean newlines, boolean logErrors) {
		String ret = null;
		BufferedReader rdr = null;
		final StringBuilder sb = new StringBuilder();
		try {
			rdr = new BufferedReader(new InputStreamReader(new FileInputStream(path)), 4096);
			String line = null;
			while ((line = rdr.readLine()) != null) {
				sb.append(line);
				if (newlines) {
					sb.append("\n");
				}
			}
			ret = sb.toString();
		}
		catch (Exception e) {
			if (logErrors) {
				log.error("Util.readTextFile(" + path + "):", e);
			}
		}
		finally {
			if (rdr != null) {
				try {
					rdr.close();
				}
				catch (IOException e) {
				}
			}
		}
		return ret;
	}
}
