package com.mixzing.servicelayer.impl;

import java.util.HashMap;
import java.util.List;

import com.mixzing.android.AndroidUtil;
import com.mixzing.android.PackageHandler;
import com.mixzing.android.Preferences;
import com.mixzing.android.PackageHandler.InstalledPackages;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.AndroidPackage;
import com.mixzing.musicobject.dao.AndroidPackageDAO;
import com.mixzing.musicobject.impl.AndroidPackageImpl;
import com.mixzing.servicelayer.AndroidPackageService;
import com.mixzing.servicelayer.MessagingService;

public class AndroidPackageServiceImpl implements AndroidPackageService {

	protected static final Logger lgr = Logger.getRootLogger();
	
	protected PackageHandler srcSvc;
	protected MessagingService msgSvc;
	protected AndroidPackageDAO pkgDAO;

	public AndroidPackageServiceImpl(PackageHandler srcHandler, MessagingService mess, AndroidPackageDAO pkd) {
		srcSvc  = srcHandler;
		msgSvc = mess;
		pkgDAO = pkd;
	}

	protected long srcRetrieveTime;

	public boolean resolve() {

		boolean changed = false;
		String key = Preferences.Keys.PKG_RESOLVE_TIME;

		if(isOkToResolve(key)) {
			List<InstalledPackages> tracks = srcSvc.retrievePackageInformation();		
			srcRetrieveTime = System.currentTimeMillis();

			List<AndroidPackage> mZtracks = pkgDAO.findAllPackages();


			if(tracks.size() == 0 && mZtracks.size() > 0) {
				return changed;
			}

			HashMap<String, InstalledPackages> map = new HashMap<String, InstalledPackages>();
			HashMap<String, AndroidPackage> mzMap = new HashMap<String, AndroidPackage>();

			for(InstalledPackages t : tracks) {
				map.put(t.getName() + ":" + t.getVersion(), t);
			}

			for(AndroidPackage t : mZtracks) {
				InstalledPackages tr = null;
				if((tr = map.get(t.getName() + ":" + t.getVersion())) == null) {
					mzMap.put(t.getName() + ":" + t.getVersion(), t);
				} else {
					map.remove(t.getName() + ":" + t.getVersion());
				}
			}

			// mzMap contains tracks to delete
			// map contains tracks to add

			for(InstalledPackages t : map.values()) {
				changed = true;
				addSourcePackage(t);
			}

			for(AndroidPackage t : mzMap.values()) {
				changed = true;
				deletePackage(t);
			}

			AndroidUtil.setLongPref(null, key, srcRetrieveTime);
			
		}
		return false;
	}


	private boolean isOkToResolve(String key) {
		long lastTime = AndroidUtil.getLongPref(null,key, 0);
		long minDelayTime = AndroidUtil.getMinDelayBetweenPkgResolves();
		if(Logger.IS_DEBUG_ENABLED) {
			lgr.debug("Resolve delay pkg = " + minDelayTime + " lastTimeWeResolved = " + lastTime + " now=" + System.currentTimeMillis());
		}
		if(lastTime + minDelayTime > System.currentTimeMillis()) {
			return false;
		}
		return true;
	}
	
	private void deletePackage(AndroidPackage t) {
		pkgDAO.delete(t);
		msgSvc.packageDeleted(t);
	}

	private void addSourcePackage(InstalledPackages t) {
		AndroidPackage pkg = new AndroidPackageImpl();
		pkg.setName(t.getName());
		pkg.setVersion(t.getVersion());
		pkgDAO.insert(pkg);
		msgSvc.packageAdded(pkg,t);		
	}

}
