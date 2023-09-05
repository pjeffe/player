package com.mixzing.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mixzing.log.Logger;

public class AnrCatcher  extends BroadcastReceiver {

	protected static final Logger lgr = Logger.getRootLogger();
	public static final String ANR_FILE_NAME = "/data/anr/traces.txt";

	public AnrCatcher() {
	}

	public void process() {
		try {
			File f = new File(ANR_FILE_NAME);
			long fileModified = f.lastModified();
			long lastSeen = AndroidUtil.getLongPref(null, Preferences.Keys.ANR_FILE_MODTIME, 0);
			if(fileModified <= lastSeen ) {
				return;
			} else {
				AndroidUtil.setLongPref(null, Preferences.Keys.ANR_FILE_MODTIME, fileModified);
			}

			final String anr = getAnr(f);
			if(anr != null) {
				lgr.error(anr);
			}	
		} catch (Exception e) {
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("AnrCatcher: Error processing ANR: " + e);
			}
		}
	}

	private static String getAnr(File f) {
		BufferedReader input = null;
		boolean first = true;
		StringBuffer anrData = null;
		try {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String line = null;
			String header = null;
			while((line=input.readLine()) != null) {
				if (line.startsWith("----- pid")) {
					header = line;
					if (anrData != null) {  // done with mixzing data
						break;
					}
				}
				else if(line.startsWith("Cmd line:")) {
					/*
					 * If this is the first instance of the magic pattern and the process running is ours
					 */
					if(first && line.contains("com.mixzing")) {
						first = false;
						anrData = new StringBuffer();
						anrData.append("ANR--->\n");
						if (header != null) {
							anrData.append(header);
							anrData.append("\n");
						}
						anrData.append(line);
						anrData.append("\n");
					} else {
						// Either we got the second command or this ANR is not ours
						break;
					}
				} else if (anrData != null) {
					anrData.append(line);
					anrData.append("\n");						
				}
			}
		} catch (Exception e) {

		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}

		return anrData != null ? anrData.toString() : null;
	}

	public static String getAnr() {
		return getAnr(new File(ANR_FILE_NAME));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		process();
	}
}
