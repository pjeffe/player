package com.mixzing.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Handler;

import com.mixzing.log.Logger;


public class Scanner {
	private static final Logger log = Logger.getRootLogger();
	private MediaScannerConnection scanner;
	private ArrayList<String> pendingFiles;
	private HashMap<String, Uri> results;
	private Handler handler;
	private int msgnum;

	private static final long WAIT_TIMEOUT = 60 * 1000;


	public Scanner(Context context) {
		this(context, null, 0);
	}

	public Scanner(Context context, Handler handler, int msgnum) {
		this.handler = handler;
		this.msgnum = msgnum;
		pendingFiles = new ArrayList<String>();
		results = new HashMap<String, Uri>();
		scanner = new MediaScannerConnection(context, scannerClient);
		try {
			scanner.connect();
		}
		catch (Exception e) {
			log.error("Scanner.ctor:", e);
		}
	}

	// scan a file and wait until it completes, returning the uri result
	// NB this can't run on the main thread since it will block trying to connect to the scanner service
	//
	public Uri scanFile(String file, boolean block) {
		Uri result = null;

		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("Scanner.scanFile: " + file);
		}

		synchronized (results) {
			// if we're connected then request a scan, otherwise enqueue it
			if (scanner.isConnected()) {
				scanner.scanFile(file, null);
			}
			else {
				pendingFiles.add(file);
				try {
					scanner.connect();
				}
				catch (Exception e) {
					log.error("Scanner.scanFile:", e);
				}
			}


			// if we're in blocking mode wait for result or until we time out
			if (block) {
				do {
					try {
						results.wait(WAIT_TIMEOUT);
					}
					catch (InterruptedException e) {
						continue;
					}

					result = results.remove(file);

					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("Scanner.scanFile: result = " + result);
					}
				} while (false);
			}
		}

		return result;
	}

	private MediaScannerConnectionClient scannerClient = new MediaScannerConnectionClient() {

		public void onMediaScannerConnected() {
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("Scanner.onScannerConnected");
			}

			// process any queued requests
			synchronized (results) {
				if (scanner != null) {
					while (pendingFiles.size() != 0) {
						final String file = pendingFiles.remove(0);
						try {
							scanner.scanFile(file, null);
						}
						catch (Exception e) {
							log.error("Scanner.onScannerConnected:", e);
						}
					}
				}
			}
		}

		public void onScanCompleted(String path, Uri uri) {
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("Scanner.onScanCompleted: path = " + path + ", uri = " + uri);
			}

			// store result and wake any observer
			synchronized (results) {
				results.put(path, uri);
				results.notify();
			}

			// notify async listener if any
			if (handler != null) {
				handler.sendMessage(handler.obtainMessage(msgnum, uri));
			}
		}
	};


	public static void rescan(Context context, String dir) {
		final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
		intent.setData(Uri.parse("file://" + dir));
		context.sendBroadcast(intent);
	}
}
