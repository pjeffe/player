package com.mixzing.android;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.mixzing.log.Logger;

public class PackageHandler {

	protected static Logger lgr = Logger.getRootLogger();
	protected PackageManager pm;

	public PackageHandler() {
		pm = AndroidUtil.getAppContext().getPackageManager();		
	}

	public class InstalledPackages {
		protected String name;
		protected long version;
		protected String verName;

		public InstalledPackages(String name, int ver, String vName) {
			this.name = name;
			this.version = ver;
			this.verName = vName;
		}

		public long getVersion() {
			return version;
		}

		public String getName() {
			return name;
		}	

		public String getVerName() {
			return verName;
		}	
		
		public String toString() {
			return name + ":" + version;
		}
	}


	public List<InstalledPackages> retrievePackageInformation() {
		ArrayList<InstalledPackages> installed  = new ArrayList<InstalledPackages>();

		List<ApplicationInfo> apps = pm.getInstalledApplications(0);
		for(ApplicationInfo app : apps) {

			try {
				String pkgname = app.packageName;
				int ver = 0;
				PackageInfo pkg = pm.getPackageInfo(pkgname, 0);
				ver = pkg.versionCode;
				String verName = pkg.versionName;
				InstalledPackages ip = new InstalledPackages(pkgname,ver, verName); 
				installed.add(ip);
				if(Logger.IS_TRACE_ENABLED) {
					lgr.debug("retrievePackageInformation: " + ip);
				}
			} catch (NameNotFoundException ex) {				
			}
		}
		return installed;
	}

	public  String getUsageStats() {
		String data = null;
		Process p  = null;
		try {
			StringBuffer buffer = new StringBuffer();
			String line;
			boolean found = false;
			if(Logger.IS_TRACE_ENABLED) {
				lgr.debug("getUsageStats: about to exec ");
			}
			p = Runtime.getRuntime().exec("/system/bin/dumpsys usagestats -c");
			if(p != null) {
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = input.readLine()) != null) {
					buffer.append(line);
					buffer.append("\n");
					if(!found && line != null && line.startsWith("A:")) {
						found = true;
					}
				}
				input.close();
				if(found) {
					data = buffer.toString();
				}
			} else {
				if(Logger.IS_TRACE_ENABLED) {
					lgr.debug("getUsageStats: could not exec process ");
				}
			}
		}  catch (Exception e) {
			if(Logger.IS_TRACE_ENABLED) {
				lgr.debug("getUsageStats: exception " + e);
			}
		} finally {
			try {
				if(p != null) {
					p.destroy();
				}
			} catch (Exception e) {
			}
		}
		return data;
	}
}
