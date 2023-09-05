package com.mixzing.android;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.Environment;

import com.mixzing.android.Preferences.Defaults;
import com.mixzing.android.Preferences.Keys;
import com.mixzing.log.Logger;


// TODO instead of internal/external dichotomy need to support arbitrary list of volumes
// TODO find way to discover volume names: put media files in top mounted dirs and scan them?

public class SdCardHandler {
	private static final Logger log = Logger.getRootLogger();
	private static final String externalRoot = Environment.getExternalStorageDirectory().getPath();
	private static String internalRoot;
	private static boolean hasInternal;
	private static boolean useInternal;
	private static String rootdir;
	private static String volume;
	private static int cardId;
	private static boolean cardIdSet;
	private static String internalVolume = "phoneStorage";
	private static String externalVolume = "external";
	private static Method getInternalState;
	private static String dataDir;
	private static Context context;

	private static final int INTERNAL_CARD_ID = 1;  // XXX
	public static final String DATA_DIR = File.separator + ".mixzing";
	private static final String CARD_ID_FILE = "cardid";
	public static final byte DEFAULT_VOLUME = (byte) 0xFF;

	private static final String[][] volData = new String[][] {
		{ "Phone", "phoneStorage" },  // HTC
		{ "Sdcard", "external" },     // Haipad
		{ "SCSI", "external" },
		{ "SATA", "external" },
		{ "Nand", "external" },
		{ "INand", "external" },
		{ "Default", "phoneStorage" }
	};

	public static void init(Context context) {
		SdCardHandler.context = context;

		// check if there is internal storage on this device
		// check all the known non-standard classes in Environment to see if any of them are present
		// TODO change this from undocumented vendor-specific methods when official support is available
		// XXX some of these should be external?
		//
		try {
			final Class<?> cls = Class.forName("android.os.Environment");

			if (Logger.IS_DEBUG_ENABLED) {
				final Method[] meths = cls.getDeclaredMethods();
				StringBuilder sb = new StringBuilder("SdCardHandler.init: Environment methods:");
				for (final Method emeth : meths) {
					sb.append("\n");
					sb.append(emeth.toString());
				}
				log.debug(sb.toString());
			}

			for (String[] data : volData) {
				final String name = data[0];
				try {
					final Method meth = cls.getMethod("get" + name + "StorageDirectory");
					final File file = (File)meth.invoke(null);
					if (file != null) {
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("SdCardHandler.init: " + name + " returned " + file.getAbsolutePath());
						}

						// check for get state method
						final String stateMeth = "get" + name + "StorageState";
						try {
							getInternalState = cls.getMethod(stateMeth);
							internalRoot = file.getCanonicalPath();
							internalVolume = data[1];
							hasInternal = true;
						}
						catch (Exception e) {
							// include Environment's methods in the logged error
							try {
								final Method[] meths = cls.getDeclaredMethods();
								StringBuilder sb = new StringBuilder("SdCardHandler.init: internal storage but no " + stateMeth + ":");
								for (final Method emeth : meths) {
									sb.append("\n");
									sb.append(emeth.toString());
								}
								log.error(sb.toString(), e);
							}
							catch (Throwable t) {
								log.error("SdCardHandler.init:", t);
							}
						}

						break;
					}
				}
				catch (NoSuchMethodException e) {
					if (Logger.IS_DEBUG_ENABLED) {
						log.debug("SdCardHandler.init: no " + name + " on this device");
					}
				}
			}
		}
		catch (Exception e) {
			log.error("SdCardHandler.init:", e);
		}
	}

	// set the current state based on whether the device has internal storage and the user's pref
	public static void setState() {
		final boolean internal = hasInternal && AndroidUtil.getBooleanPref(null, Keys.USE_INTERNAL_STORAGE, Defaults.USE_INTERNAL_STORAGE);
		setInternal(internal);
	}

	// set the useInternal flag and the proper root dir
	public static void setInternal(boolean internal) {
		// reset cached card id flag if we're changing volumes
		if (useInternal != internal) {
			cardIdSet = false;
		}

		useInternal = internal;
		if (internal) {
			rootdir = internalRoot;
			volume = internalVolume;
		}
		else {
			rootdir = externalRoot;
			volume = externalVolume;
		}

		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("SdCardHandler.setInternal: set rootdir to " + rootdir);
		}

		dataDir = rootdir + DATA_DIR;

		if (isMounted()) {
			// ensure data dirs exist
			final String[] dirs = new String[] { dataDir };
			for (String dir : dirs) {
				try {
					final File file = new File(dir);
					if (!file.exists()) {
						if (file.mkdir()) {
							if (Logger.IS_DEBUG_ENABLED) {
								log.debug("SdCardHandler.setInternal: made dir " + dir);
							}
			
							if (dir == dataDir) {
								// create nomedia file to prevent scanner from scanning us
								final File nomedia = new File(dir + File.separator + ".nomedia");
								if (!nomedia.exists()) {
									try {
										nomedia.createNewFile();
									}
									catch (Exception e) {
										if (Logger.IS_DEBUG_ENABLED) {
											log.debug("SdCardHandler.setInternal:", e);
										}
									}
								}
							}
						}
						else if (Logger.shouldSelectivelyLog()) {
							try {
								final Method meth = internal ? getInternalState : Class.forName("android.os.Environment").getMethod("getExternalStorageState");
								log.error("SdCardHandler.setInternal: failed to mkdir " + file.getAbsolutePath() + " - " + file.getCanonicalPath() +
									", " + meth.getDeclaringClass().getName() + "." + meth.getName() + " = " + (String)meth.invoke(null));
							}
							catch (Throwable t) {
								if (Logger.IS_DEBUG_ENABLED) {
									log.error("SdCardHandler.setInternal:", t);
								}
							}
						}
					}
				}
				catch (Exception e) {
					log.error("SdCardHandler.setInternal:", e);
				}
			}
		}
	}

	// if the current volume isn't the desired one then set it, return false if volume isn't supported
	public static boolean setVolume(String newvol) {
		boolean ret = true;
		if (!volume.equals(newvol)) {
			// check if we're changing to a supported volume
			final boolean toExternal = externalVolume.equals(newvol);
			if (toExternal || (hasInternal && internalVolume.equals(newvol))) {
				// set the pref, which triggers all the necessary changes
				AndroidUtil.setBooleanPref(null, Keys.USE_INTERNAL_STORAGE, !toExternal);
			}
			else {
				log.error("SdCardHandler.setVolume: unsupported volume change attempt to " + newvol);
				ret = false;
			}
		}
		return ret;
	}

	// return true if desired storage is mounted
	public static boolean isMounted() {
		return isMounted(useInternal);
	}

	public static boolean isMounted(boolean internal) {
		final String state = getStorageState(internal);
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	public static String getStorageState() {
		return getStorageState(useInternal);
	}

	public static String getStorageState(boolean internal) {
		String state = null;
		if (internal) {
			try {
				state = (String)getInternalState.invoke(null);
			}
			catch (Throwable t) {
				log.error("SdCardHandler.getStorageState:", t);
			}
		}
		else {
			state = Environment.getExternalStorageState();
		}
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("SdCardHandler.getStorageState: internal = " + internal + ", state = " + state);
		}
		return state;
	}

	// return true if we should use internal storage by default on this system
	public static boolean defaultToInternalStorage() {
		// XXX assume true if the device has internal storage and no external storage is currently mounted
		return hasInternal && !isMounted(false);
	}

	public static String getRootDir() {
		return rootdir;
	}

	// get the volume ID for the currently used card
	// NB this will return -1 for pre-2.2 systems if the card isn't available, but a valid ID for 2.2 regardless of mount
	// state, so always use isMounted() as well to check for the storage availability
	//
	public static int getCardId() {
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("SdCardHandler.getCardId: cardIdSet = " + cardIdSet + ", cardId = " + cardId);
		}
		if (!cardIdSet) {
			int id;
			if (useInternal) {
				id = INTERNAL_CARD_ID;
			}
			else {
				// check if we've already created an id file for this volume
				id = -1;
				final String fname = getRootDir() + DATA_DIR + File.separator + CARD_ID_FILE;
				final File file = new File(fname);
				if (file.exists()) {
					// read id from file
					FileReader in = null;
					try {
						in = new FileReader(file);
						final char[] chars = new char[20];
						final int num = in.read(chars);
						if (num > 0) {
							id = Integer.parseInt(new String(chars, 0, num));
						}
						else if (Logger.IS_DEBUG_ENABLED) {
							log.error("SdCardHandler.getCardId: read returned " + num);
						}
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("SdCardHandler.getCardId: read file " + fname);
						}
					}
					catch (Exception e) {
						id = -1;
						if (Logger.shouldSelectivelyLog()) {
							log.error("SdCardHandler.getCardId:", e);
						}
					}
					finally {
						try {
							if (in != null) {
								in.close();
							}
						}
						catch (Exception e) {
						}
					}
				}

				if (id == -1) {
					// create new id and write to file
					id = (int)(System.currentTimeMillis() / 1000);
					FileWriter out = null;
					try {
						out = new FileWriter(file);
						out.write(Integer.toString(id));
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("SdCardHandler.getCardId: wrote file " + fname);
						}
					}
					catch (Exception e) {
						// this happens frequently when the volume is being unmounted but still says it's mounted,
						// and maybe in other cases as well; since we couldn't save the created volume ID we need
						// to fail the operation so that the caller knows we don't have a valid ID
						//
						id = -1;
						if (Logger.IS_DEBUG_ENABLED) {
							log.error("SdCardHandler.getCardId:", e);
						}
					}
					finally {
						if (out != null) {
							try {
								out.close();
							}
							catch (Exception e) {
							}
						}
					}
				}
	
//				if (Logger.IS_DEBUG_ENABLED) {
//					log.debug("SdCardHandler.getCardId: vol id = " + FileUtils.getFatVolumeId(getRootDir()));
//				}
			}
	
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("SdCardHandler.getCardId: id = " + id);
			}

			// save card id and state
			cardId = id;
			cardIdSet = id != -1;
		}

		return cardId;
	}

	public static String getVolume() {
		return volume;
	}

	public static String[] getVolumes() {
		return hasInternal ?
			new String[] { externalVolume, internalVolume } :
			new String[] { externalVolume };
	}

	public static byte getVolumeIndex() {
		return getVolumeIndex(volume);
	}

	// TODO make this more flexible when we need to support more/different vols
	public static byte getVolumeIndex(String vol) {
		return (byte)(externalVolume.equals(vol) ? 0 : internalVolume.equals(vol) ? 1 : -1);
	}

	// TODO make this more flexible when we need to support more/different vols
	public static String getVolume(byte index) {
		if (index == DEFAULT_VOLUME) {
			return volume;
		}
		return index == 0 ? externalVolume : index == 1 ? internalVolume : null;
	}

	public static boolean hasInternal() {
		return hasInternal;
	}

	public static boolean isInternal() {
		return useInternal;
	}

	public static String getDbNameOrNull() {
		return isMounted() ? getDbName(getCardId()) : null;
	}

	public static String getDbName(int volumeID) {
		String dbName = null;
		if (volumeID != -1) {
			// generate database name based on volume ID
			dbName = "mixzing-" + Integer.toHexString(volumeID) + ".db";
		}
		return dbName;
	}

	public static String getDataDir() {
		return dataDir;
	}

	public static String getExternalCacheDir() {
		if (AndroidUtil.getSDK() >= 8) {
			return SdCardHandlerFroyo.getExternalCacheDir(context).getAbsolutePath();
		}
		else {
			return rootdir;
		}
	}
}
