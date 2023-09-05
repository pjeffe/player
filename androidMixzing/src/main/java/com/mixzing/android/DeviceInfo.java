package com.mixzing.android;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import android.os.Process;
import android.util.DisplayMetrics;

import com.mixzing.basic.R;
import com.mixzing.log.Logger;
import com.mixzing.util.Util;

public class DeviceInfo {
	private static final Logger log = Logger.getRootLogger();
	private static int cpuSpeed;
	private static Properties deviceProps;
	private static HashMap<String, String> buildProps;
	private static int width;
	private static int height;
	private static float density;

	private static final float MAX_SHORT_DISPLAY_RATIO = 1.6f;  // aspect ratios <= this are considered short

	private static final String SCALING_MAX_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
	private static final String BACKGROUND_GROUP = "/bg_non_interactive";

	public static final int DEVICE_CLASS_LOW = 0;
	public static final int DEVICE_CLASS_MEDIUM = 1;
	public static final int DEVICE_CLASS_HIGH = 2;

	private static final String PROP_MODEL = "ro.product.model";
	private static final String PROP_PRODUCT = "ro.product.name";
	public static final String PROP_SERIAL = "ro.serialno";

	// from android.os.Build
	private final static String[][] buildFields = new String[][] {
		{ "BOARD", "ro.product.board" },
		{ "BOOTLOADER", "ro.bootloader" },
		{ "BRAND", "ro.product.brand" },
		{ "CODENAME", "ro.build.version.codename" },
		{ "CPU_ABI", "ro.product.cpu.abi" },
		{ "CPU_ABI2", "ro.product.cpu.abi2" },
		{ "DEVICE", "ro.product.device" },
		{ "DISPLAY", "ro.build.display.id" },
		{ "FINGERPRINT", "ro.build.fingerprint" },
		{ "HARDWARE", "ro.hardware" },
		{ "HOST", "ro.build.host" },
		{ "ID", "ro.build.id" },
		{ "INCREMENTAL", "ro.build.version.incremental" },
		{ "MANUFACTURER", "ro.product.manufacturer" },
		{ "MODEL", PROP_MODEL },
		{ "PRODUCT", PROP_PRODUCT },
		{ "RELEASE", "ro.build.version.release" },
		{ "SDK", "ro.build.version.sdk" },
		{ "SERIAL", PROP_SERIAL },
		{ "TAGS", "ro.build.tags" },
		{ "TYPE", "ro.build.type" },
		{ "USER", "ro.build.user" }
	};


	public static Properties getDeviceProps(boolean force) {
		if (deviceProps == null || force) {
			Properties props = new Properties();
			try {
				// read build.prop props if they've changed since last time or force is true
				FileInputStream fis = null;
				try {
					File file = new File("/system/build.prop");
					if (!force) {
						// check if the file has changed
						final long mtime = file.lastModified();
						final long lastTime = AndroidUtil.getLongPref(null, Preferences.Keys.BUILD_PROP_MTIME, 0);
						if (mtime > lastTime) {
							force = true;
							AndroidUtil.setLongPref(null, Preferences.Keys.BUILD_PROP_MTIME, mtime);
						}
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("DeviceInfo.getDeviceProps: build.prop mtime = " + mtime + ", lastTime = " + lastTime);
						}
					}
					if (force) {
						fis = new FileInputStream(file);
						props.load(fis);
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("DeviceInfo.getDeviceProps: loaded " + props.size() + " props from build.prop");
						}
					}
				}
				catch (Exception e) {
					log.error("DeviceInfo.getDeviceProps: error loading build props:", e);
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

				// try to fill in any missing props from Build class
				for (String[] buildProp : buildFields) {
					final String prop = buildProp[1];
					if (!props.containsKey(prop)) {
						final String value = getBuildProp(prop);
						if (value != null) {
							props.put(prop, value);
						}
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("DeviceInfo.getDeviceProps: build prop " + prop + " = " + value);
						}
					}
				}

				props.put("smallScreen", Boolean.toString(AndroidUtil.getAppContext().getResources().getBoolean(R.bool.smallScreen)));
			}
			catch (Exception e) {
				log.error("DeviceInfo.getDeviceProps:", e);
			}

			// add device id, imei and display size
			props.put("devid", AndroidUtil.getDeviceId());
			props.put("imei", AndroidUtil.getImei());
			props.put("display", getDisplayString());

			deviceProps = props;
		}
		return deviceProps;
	}

	public static String getBuildProp(String prop) {
		if (buildProps == null) {
			// get property values from Build class
			buildProps = new HashMap<String, String>(buildFields.length);
			try {
				final Class<?> cls = Class.forName("android.os.Build");
				for (String[] buildProp : buildFields) {
					final String fldName = buildProp[0];
					final String propName = buildProp[1];
					try {
						final Field field = cls.getField(fldName);
						final String value = (String)field.get(null);
						buildProps.put(propName, value);
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("DeviceInfo.getBuildProp: build prop " + propName + " = " + value);
						}
					}
					catch (Exception e) {
						if (Logger.IS_DEBUG_ENABLED) {
							log.debug("DeviceInfo.getBuildProp: " + fldName + ": " + e);
						}
					}
				}
			}
			catch (Exception e) {
				if (Logger.shouldSelectivelyLog()) {
					log.error("DeviceInfo.getBuildProp:", e);
				}
			}
		}
		return buildProps.get(prop);
	}

	public static String getDeviceProp(String prop) {
		if (deviceProps == null) {
			getDeviceProps(true);
		}
		return deviceProps.getProperty(prop);
	}

	public static HashMap<String, String> getDeviceInfo() {
		final HashMap<String, String> args = new HashMap<String, String>(5);
		try {
			args.put(Analytics.DATA_DISPLAY_METRICS, getDisplayString());
			args.put(Analytics.DATA_FREQ, Integer.toString(getCpuSpeed()));
			args.put(Analytics.DATA_PACKAGE, AndroidUtil.getPackageName());
			final Locale locale = Locale.getDefault();
			args.put(Analytics.DATA_COUNTRY, locale.getCountry());
			args.put(Analytics.DATA_LANGUAGE, locale.getLanguage());
			args.put(Analytics.DATA_MODEL, getDeviceProp(PROP_MODEL));
			args.put(Analytics.DATA_PRODUCT, getDeviceProp(PROP_PRODUCT));
		}
		catch (Exception e) {
			log.error(e);
		}
		return args;
	}

	private static String getDisplayString() {
		getDisplayMetrics();
		return width + "x" + height + "x" + density;
	}

	private static void getDisplayMetrics() {
		if (width == 0) {
			final DisplayMetrics dm = AndroidUtil.getDisplayMetrics();
			width = dm.widthPixels;
			height = dm.heightPixels;
			if (width > height) {
				final int temp = width;
				width = height;
				height = temp;
			}
			density = dm.density;
		}
	}

	public static int getCpuSpeed() {
		if (cpuSpeed == 0) {
			final String str = Util.readTextFile(SCALING_MAX_FREQ, false, false);
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("DeviceInfo.getCpuSpeed: str = " + str);
			}
			int speed = -1;
			if (str != null) {
				try {
					speed = Integer.parseInt(str);
				}
				catch (Exception e) {
					log.error("DeviceInfo.getCpuSpeed: str = " + str + ":", e);
				}
			}
			cpuSpeed = speed;
		}
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("DeviceInfo.getCpuSpeed: speed = " + cpuSpeed);
		}
		return cpuSpeed;
	}

	// simplistic division of devices into small number of classes based on cpu speed
	public static int getDeviceClass() {
		final int speed = getCpuSpeed() / 1000;
		return speed > 700 ? DEVICE_CLASS_HIGH : speed > 530 ? DEVICE_CLASS_MEDIUM : speed > 0 ? DEVICE_CLASS_LOW : DEVICE_CLASS_HIGH;
	}

	public static String getSchedulingGroup() {
		String cgroup = null;
		final String path = "/proc/" + Process.myPid() + "/cgroup";
		final String line = Util.readTextFile(path, false, false);
		if (line != null) {
			final String[] toks = line.split(":");
			cgroup = toks.length == 3 ? toks[2] : null;
		}
		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("DeviceInfo.getSchedulingGroup: cgroup = " + cgroup);
		}
		return cgroup;
	}

	// return true if we can positively determine that we're in the background scheduling group
	public static boolean isBackground() {
		final String cgroup = getSchedulingGroup();
		return cgroup == null ? false : cgroup.equals(BACKGROUND_GROUP);
	}

	public static boolean isShortDisplay() {
		getDisplayMetrics();
		return (float)height / (float)width <= MAX_SHORT_DISPLAY_RATIO;
	}
}
