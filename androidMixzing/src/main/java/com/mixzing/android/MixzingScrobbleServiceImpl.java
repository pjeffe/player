package com.mixzing.android;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.mixzing.MixzingConstants;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.EnumRatingSource;
import com.mixzing.musicobject.EnumRatingValue;
import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.RatingSong;
import com.mixzing.musicobject.Track;
import com.mixzing.musicobject.impl.RatingSongImpl;
import com.mixzing.servicelayer.GlobalSongService;
import com.mixzing.servicelayer.MessagingService;
import com.mixzing.servicelayer.TrackService;

public class MixzingScrobbleServiceImpl implements MixzingScrobblerService, Runnable {

	protected MessagingService msgSvc;
	protected TrackService trkSvc;
	protected GlobalSongService gss;

	protected static Logger log = Logger.getRootLogger();

	public MixzingScrobbleServiceImpl(MessagingService mess, TrackService trk, GlobalSongService gs) {
		msgSvc = mess;
		trkSvc = trk;
		gss = gs;
	}


	/* (non-Javadoc)
	 * @see com.mixzing.android.MixzingScrobblerService#processPlayedData()
	 */
	public void processPlayedData() {
		try {
			String playedSongs = AndroidUtil.getAndZeroPlayedSongList();
			
			ArrayList<RatingSong> ratingSongs = new ArrayList<RatingSong>();
			
			if (Logger.IS_DEBUG_ENABLED) {
				log.debug("MixzingScrobbleServiceImpl.Got playedsongs = " + playedSongs);
			}
			StringTokenizer tok = new StringTokenizer(playedSongs, AndroidUtil.PLAYED_SONG_DELIM);
			final String sep = AndroidUtil.PLAYED_SONG_SEP;
			while (tok.hasMoreTokens()) {

				String token = tok.nextToken();
				if (token == null || token.equals("")) {
					continue;
				}

				if (Logger.IS_DEBUG_ENABLED) {
					log.debug("Got token : " + token);
				}
				StringTokenizer tok2 = new StringTokenizer(token, sep);

				String time = null;
				if (tok2.hasMoreTokens()) {
					time = tok2.nextToken();
				}
				String id = null;
				if (tok2.hasMoreTokens()) {
					id = tok2.nextToken();
				}

				if (Logger.IS_DEBUG_ENABLED) {
					log.debug("Got id : " + id + " time : " + time);
				}

				if (id != null && time != null) {

					long timePlayed = System.currentTimeMillis();

					try {
						timePlayed = Long.valueOf(time);
					} catch (Exception e) {

					}
					GlobalSong song = lookupGsid(id);
					if(song != null && song.getGsid() > 0) {
						if(Logger.IS_DEBUG_ENABLED) {
							log.debug("Got song to scrobble:  " + song.getArtist() + ":" + song.getTitle()  + ":" + song.getGsid());
						}
						RatingSong ratSong = new RatingSongImpl();
						ratSong.setGlobalSong(song);
						ratSong.setPlid(MixzingConstants.PLAYLIST_ID_FOR_SCROBBLES);
						ratSong.setRatingSource(EnumRatingSource.INFERRED_LISTEN);
						ratSong.setRatingValue(EnumRatingValue.LIKE);
						ratSong.setTimeRated(timePlayed);
						ratingSongs.add(ratSong);
					} else {
						if(Logger.IS_DEBUG_ENABLED) {
							log.debug("Got null global song or the song is not yet known to server. "  + ((song == null) ? 0 : song.getGsid()));
						}
					}
				}
			}
			if(ratingSongs.size() > 0) {
				boolean commit = false;
				boolean txStarted = false;
				boolean urgentTx = false;
				synchronized(DatabaseManager.class) {
					msgSvc.batchStart(urgentTx);
					try {
						DatabaseManager.beginTransaction();
						txStarted = true;
						for(RatingSong song : ratingSongs) {
							msgSvc.ratingAdded(song);
						}
						commit = true;
					} finally {
						msgSvc.batchFinish();
						if(txStarted) {
							if(commit) {
								DatabaseManager.commitTransaction();
								AndroidUtil.setLongPref(null, Preferences.Keys.LAST_SCROBBLE_TIME, System.currentTimeMillis());
							} else {
								DatabaseManager.rollbackTransaction();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if(Logger.IS_DEBUG_ENABLED) {
				log.error("Got exception processing played songs ",e);
				log.debug("Exception processing played somgs : " + e);
			}
		}
	}


	private GlobalSong lookupGsid(String id) {
		Track t = trkSvc.locateByAndroidSourceId(Integer.valueOf(id));
		if(t != null) {
			long globalSongId = t.getGlobalSongId();

			// XXX : check which method to use
			return gss.getSong(globalSongId);
		} else {
			// This is possible when playing external recos - possibly in other cases as well
			if(Logger.IS_DEBUG_ENABLED) {
				log.debug("Could not find track for source id " + id);
			}
			return null;
		}

	}


	public void run() {
		processPlayedData();
	}
}
