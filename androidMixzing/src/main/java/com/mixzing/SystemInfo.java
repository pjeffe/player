package com.mixzing;

import java.util.Locale;


public class SystemInfo {
	private static SupportedOS myOS = null;

	public static SupportedOS OS() {
		if (myOS != null) {
			return myOS;
		}

		final String osName = System.getProperty("os.name").toLowerCase(Locale.US);

		if (osName.startsWith("windows")) {
			myOS = SupportedOS.Windows;
		}
		else if (osName.startsWith("mac os")) {
			myOS = SupportedOS.OSX;
		}
		else if (osName.startsWith("linux")) {
			String runtime = System.getProperty("java.runtime.name");
			if (runtime != null && runtime.toLowerCase(Locale.US).contains("android")) {
				myOS = SupportedOS.Android;
			}
			else {
				myOS = SupportedOS.Linux;
			}
		}
		else {
			throw new RuntimeException("running on an unsupported Operating System: " + osName);
		}
		
		return myOS;
	}
}
