package com.mixzing.decoder;

import java.io.FileDescriptor;

import android.util.Log;

import com.mixmoxie.util.StackTrace;
import com.mixzing.log.Logger;

/**
 * 
 * Decode mp3 files using mpg123 library
 * 
 * 
 */
public class MusicDecoder {
	protected static final Logger log = Logger.getRootLogger();
	private static final boolean DEBUG = false;  //Logger.IS_DEBUG_ENABLED;
	public long handle = 0;
	private static MusicDecoder instance;

	public static final int FILTER_TYPE_PKING      = 1;
	public static final int FILTER_TYPE_LOW_SHELF  = 2;
	public static final int FILTER_TYPE_HIGH_SHELF = 3;
	public static final int FILTER_TYPE_BAND_PASS = 5;
	public static final int FILTER_TYPE_LOW_PASS  = 6;
	public static final int FILTER_TYPE_HIGH_PASS = 7;
	
	public static final int FILTER_TYPE_BW_OLD     = 4; // Used by the old impl

	public static final String MSG_OPEN_ERROR = "couldn't open file code = ";


	public static synchronized MusicDecoder getInstance() {
		if (instance == null) {
			instance = new MusicDecoder();
			System.loadLibrary("stagefrightmz");
			instance.init();
		}
		return instance;
	}

	private MusicDecoder() {
	}

	public long[] getInfoForFile(String filename) {
		long[] data = new long[4];
		openFileAndGetInfo(filename, data);
		return data;
	}
	/**
	 * Opens the given file for mp3 decoding. Throws an IllegalArugmentException
	 * in case the file could not be opened.
	 * 
	 * @param filename the filename
	 */
	public synchronized long openMusicFile(String filename) {
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native openFile - using 3 libs - " + filename);
		}

		if (handle != 0) {
			dispose();
			log.error("MusicDecoder.openMusicFile: open called with open handle: thread = " +
				Thread.currentThread().getName() + ":\n" + StackTrace.getStackTrace(false));
		}

		handle = openFile(filename);
		if (DEBUG) {
			log.debug("MusicDecoder: Called native openfile " + handle);
		}
/*
		#define MZ_ERROR_CANNOT_OPEN -1
		#define MZ_ERROR_MEM_ALLOC   -2
		#define MZ_ERROR_NO_FIRSTFR  -3
		#define MZ_ERROR_DECODE      -4
		#define MZ_ERROR_SEEKSET     -5
*/		
		if (handle  >= -15 && handle <= 0) {
			long exc = handle;
			handle = 0;
			throw new IllegalArgumentException(MSG_OPEN_ERROR + exc);
		}

		if (DEBUG) {
			log.debug("MusicDecoder.openMusicFile :  " + handle);
		}
		return handle;
	}

	/**
	 * Opens the given fd stream for mp3 decoding. Throws an IllegalArugmentException
	 * in case the file could not be opened.
	 * 
	 * @param filename the filename
	 */
	public synchronized long openMusicFile(FileDescriptor fd) {
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native openFilefd " + fd);
		}

		if (handle != 0) {
			throw new IllegalArgumentException("open called when previous handle is not closed " + handle);
		}

		handle = openFilefd(fd);
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native openFilefd " + handle);
		}

		if (handle == 0) {
			throw new IllegalArgumentException("couldn't open file");
		}

		if (DEBUG) {
			log.debug("MusicDecoder.openMusicFilefd :  " + handle);
		}
		return handle;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized int readSamples(short[] samples) {
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native readSamples ");
		}

		if (handle == 0) {
			if (Logger.shouldSelectivelyLog()) {
				log.error("MusicDecoder.readSamples: handle was 0 when called");
			}
			return 0;
		}

		int read = 0;
		if (handle != 0) {
			read = readSamples(handle, samples, samples.length);
		}
		if (DEBUG) {
			log.debug("MusicDecoder: Called native readSamples " + read);
		}
		return read;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized int getNumChannels() {
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native getNumChannels ");
		}
		if (handle == 0) {
			if (Logger.shouldSelectivelyLog()) {
				log.error("MusicDecoder.getNumChannels: handle was 0 when called");
			}
		}

		int channels = 2;
		if (handle != 0) {
			channels = getNumChannels(handle);
		}
		if (DEBUG) {
			log.debug("MusicDecoder: Called native getNumChannels " + channels);
		}
		return channels;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized int getRate() {
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native getRate ");
		}

		if (handle == 0) {
			if (Logger.shouldSelectivelyLog()) {
				log.error("MusicDecoder.getRate: handle was 0 when called");
			}
		}

		int rate = 44100;
		if (handle != 0) {
			rate = getRate(handle);
		}
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native getRate " + rate);
		}
		return rate;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized long getLength() {
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native getLength ");
		}

		if (handle == 0) {
			if (Logger.shouldSelectivelyLog()) {
				log.error("MusicDecoder.getLength: handle was 0 when called");
			}
		}

		long len = 0;
		if (handle != 0) {
			len = getLength(handle);
		}
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native getLength " + len);
		}
		return len;
	}

	private native int openFile(String filename);

	private native int openFileAndGetInfo(String filename, long[] buffer);
	
	private native int openFilefd(FileDescriptor fd);

	private native int readSamples(long handle, short[] buffer, int numSamples);

	private native int setEQ(long handle, float[] buffer);

	private native int initEQ(long handle, int bands, float[] cfs,  float[] qs, int[] types);

	private native int getNumChannels(long handle);

	private native int getRate(long handle);

	private native long getLength(long handle);

	private native void closeFile(long handle);

	private native long tell(long handle);

	private native long seek(long handle, long pos);

	private native void resetClipped(long handle);

	private native int getClipped(long handle);

	private native void setGain(long handle, float gain);

	public native int init();

	public native int exit();

	private native int generateFingerPrint(String file, int skipMs, int durMs, long[] buffer, int[] data);
	
	/*
	 * 
	 * See MixZingFingerPrinter class for how to call this
	 * 
	 */
	protected int generateFingerPrintForFile(String file, int skipMs, int durMs, long[] buffer, int[] data) {
		return generateFingerPrint(file,skipMs,durMs,buffer,data);
	}
	
	
	/**
	 * Allocates a direct buffer for the given number of samples and channels. The final
	 * number of samples is numSamples * numChannels.
	 * 
	 * @param numSamples the number of samples
	 * @param numChannels the number of channels
	 * @return the direct buffer
	 */
	public static short[] allocateShortBuffer(int numSamples, int numChannels) {
		return new short[numSamples * numChannels];
	}

	/**
	 * {@inheritDoc}
	 */

	public synchronized void dispose() {
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native closeFile ");
		}
		if (handle != 0) {
			closeFile(handle);
			handle = 0;
		}
		if (DEBUG) {
			log.debug("MusicDecoder: Called native closeFile ");
		}
	}

	public synchronized void setEQ(float[] eq) {
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native setEq ");
		}

		if (handle == 0) {
			if (Logger.shouldSelectivelyLog()) {
				log.error("MusicDecoder.setEQ: handle was 0 when called");
			}
		}

		if (handle != 0) {
			setEQ(handle, eq);
		}
		if (DEBUG) {
			log.debug("MusicDecoder: Called native setEq ");
		}
	}

	
	
	/*
	 * Set USE_PKIG_DBG to play around with the the Peaking and Shelving Filters
	 * 
	 * See the bottom of this file for sample configurations for the filter CFs and Qs
	 * 
	 * XXX:
	 * 
	 * This should be false for Productions 
	 * 
	 */
	private static final boolean USE_PKING_DBG = false;
	
	public synchronized void initEQ(float[] cfs, float q) {
		final int len = cfs.length;

		if(USE_PKING_DBG) {
			Log.d("MIXZING","WARNING .....USE_PKING_DBG=true in Decoder.java");
			if(len == 3) {
				initEQ(fcfs3,fqs3,fts3);
			} else if (len == 5) {
				initEQ(fcfs5,fqs5,fts5);
			} else {
				initEQ(fcfs10,fqs10,fts10);
			}
		} else {
			float[] qs = new float[len];
			int [] types = new int[len];
			for(int i=0;i<cfs.length;i++) {
				qs[i] = q;
				types[i] = FILTER_TYPE_BW_OLD;
			}
			initEQ(cfs,qs,types);
		}
	}
	

		
	public synchronized void initEQ(float[] cfs, float[] qs, int[] types) {
		if (handle == 0) {
			if (Logger.shouldSelectivelyLog()) {
				log.error("MusicDecoder.setEQ: handle was 0 when called");
			}
		}
		else if (handle != 0) {
			if(cfs.length != qs.length || cfs.length != types.length) {
				log.error("MusicDecoder.setEQ: length mismatch on input");
				return;
			}
			
			boolean bwFilter = false;
			boolean nonBwFilter = false;
			for(int typ : types) {
				if(typ == FILTER_TYPE_BW_OLD) {
					bwFilter = true;
				} else {
					nonBwFilter = true;
				}
				if(bwFilter && nonBwFilter) {
					log.error("MusicDecoder.setEQ: cannot mix BW filter with non BW filters");
					return;
				}
			}
			initEQ(handle, cfs.length, cfs, qs, types);
		}
	}

	public synchronized long tell() {
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native tell ");
		}

		if (handle == 0) {
			if (Logger.shouldSelectivelyLog()) {
				log.error("MusicDecoder.tell: handle was 0 when called");
			}
		}

		long rc = 0;
		if (handle != 0) {
			rc = tell(handle);
		}
		if (DEBUG) {
			log.debug("MusicDecoder: Called native tell " + rc);
		}
		return rc;
	}

	public synchronized long seek(long pos) {
		if (DEBUG) {
			log.debug("MusicDecoder: Calling native seek " + pos);
		}

		if (handle == 0) {
			if (Logger.shouldSelectivelyLog()) {
				log.error("MusicDecoder.seek: handle was 0 when called");
			}
		}

		long rc = 0;
		if (handle != 0) {
			rc = seek(handle, pos);
		}

		if (DEBUG) {
			log.debug("MusicDecoder: Called native seek " + rc);
		}
		return rc;
	}

	public synchronized int getClipped() {
		if (handle != 0) {
			return getClipped(handle);
		}
		return 0;
	}

	public synchronized void resetClipped() {
		if (handle != 0) {
			resetClipped(handle);
		}
	}

	public synchronized void setGain(float gain) {
		if (handle != 0) {
			setGain(handle, gain);
		}
	}

	/*
	 * 
	 * Sample cfg for TESTING out PKING and SHELVING filters
	 * 
	 * SET USE_PKING_DBG to true when testing
	 *      
	 */	
	private float[] fcfs10 = { 31.25f, 62.5f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f };
	private float[] fqs10 = { 0.67f,   0.67f, 0.67f,0.67f,0.67f,0.67f, 0.67f, 0.67f, 0.67f, 0.67f  };
	private int  [] fts10 = { 
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
	};
	
	private float[] fcfs5 = { 62.5f, 250f, 1000f, 4000f, 16000f };
	private float[] fqs5 = { 0.266667f, 0.266667f, 0.266667f, 0.266667f, 0.266667f};
	private int  [] fts5 = { 
				FILTER_TYPE_LOW_SHELF,
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
				FILTER_TYPE_PKING,
				FILTER_TYPE_HIGH_SHELF,
	};

	private float[] fcfs3 = { 62.5f, 1000f, 16000f };
	private float[] fqs3 = { 0.062745f, 0.062745f, 0.062745f};
	private int  [] fts3 = { 
				FILTER_TYPE_LOW_SHELF,
				FILTER_TYPE_PKING,
				FILTER_TYPE_HIGH_SHELF,
	};

}
