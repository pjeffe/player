package com.mixzing.servicelayer.impl;

import java.util.HashMap;
import java.util.List;

import com.mixzing.android.AndroidUtil;
import com.mixzing.android.Preferences;
import com.mixzing.android.SourceVideoHandler;
import com.mixzing.android.SourceVideoImpl;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.SourceVideo;
import com.mixzing.musicobject.Video;
import com.mixzing.musicobject.dao.VideoDAO;
import com.mixzing.musicobject.impl.VideoImpl;
import com.mixzing.servicelayer.MessagingService;
import com.mixzing.servicelayer.VideoService;

public class VideoServiceImpl implements VideoService {

	protected static final Logger lgr = Logger.getRootLogger();
	
	protected SourceVideoHandler srcSvc;
	protected MessagingService msgSvc;
	protected VideoDAO vidDAO;

	public VideoServiceImpl(SourceVideoHandler srcHandler, MessagingService mess, VideoDAO pkd) {
		srcSvc  = srcHandler;
		msgSvc = mess;
		vidDAO = pkd;
	}

	protected long srcRetrieveTime;



	public boolean resolve() {

		boolean changed = false;
		
		String key = AndroidUtil.getCardSpecificPrefKey(Preferences.Keys.VIDEO_RESOLVE_TIME);
		
		if(isOkToResolve(key)) {
			List<SourceVideoImpl> tracks = srcSvc.getVideos();		
			srcRetrieveTime = System.currentTimeMillis();

			List<Video> mZtracks = vidDAO.findAllVideos();

			if(tracks.size() == 0 && mZtracks.size() > 0) {
				return changed;
			}

			HashMap<String, SourceVideoImpl> map = new HashMap<String, SourceVideoImpl>();
			HashMap<String, Video> mzMap = new HashMap<String, Video>();

			for(SourceVideoImpl t : tracks) {
				map.put(AndroidUtil.getMd5Hash(t.getLocation()), t);
			}

			for(Video t : mZtracks) {
				SourceVideo tr = null;
				if((tr = map.get(t.getSource_id())) == null) {
					mzMap.put(t.getSource_id(), t);
				} else {
					map.remove(t.getSource_id());
				}
			}

			// mzMap contains tracks to delete
			// map contains tracks to add

			for(SourceVideo t : map.values()) {
				changed = true;
				addSourceVideo(t);
			}

			for(Video t : mzMap.values()) {
				changed = true;
				deleteSourceVideo(t);
			}
			
			AndroidUtil.setLongPref(null, key, System.currentTimeMillis());
			
		}
		
		return changed;
	}

	/*
	 * XXX: Check if sufficient time has elapsed, since we do not want to slow down the
	 * time for music track resolve.
	 * 
	 * TODO Put a check in for last resolve time by cardspecific id and rerun resolve only if minimum 
	 * time has elapsed
	 * 
	 */
	private boolean isOkToResolve(String key) {
		long lastTime = AndroidUtil.getLongPref(null,key, 0);
		long minDelayTime = AndroidUtil.getMinDelayBetweenVideoResolves();
		if(Logger.IS_DEBUG_ENABLED) {
			lgr.debug("Resolve delay vid = " + minDelayTime + " lastTimeWeResolved = " + lastTime + " now=" + System.currentTimeMillis());
		}
		if(lastTime + minDelayTime > System.currentTimeMillis()) {
			return false;
		}
		return true;
	}

	private void deleteSourceVideo(Video t) {
		vidDAO.delete(t);
		msgSvc.videoDeleted(t);
	}

	private void addSourceVideo(SourceVideo t) {
		Video vid = new VideoImpl();
		vid.setLocation(t.getLocation());
		vid.setSource_id(AndroidUtil.getMd5Hash(t.getLocation()));
		vidDAO.insert(vid);
		msgSvc.videoAdded(vid, t);		
	}

}
