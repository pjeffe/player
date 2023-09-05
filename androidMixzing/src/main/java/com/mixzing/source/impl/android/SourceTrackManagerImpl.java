package com.mixzing.source.impl.android;



import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;
import android.provider.MediaStore.Audio.Media;

import com.mixmoxie.source.dao.SourceTrackManager;
import com.mixmoxie.source.sourceobject.SourceTrack;
import com.mixmoxie.source.sourceobject.SourceTrackId;
import com.mixmoxie.source.sourceobject.SourceTrackTag;
import com.mixzing.android.AndroidUtil;
import com.mixzing.android.SdCardHandler;
import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.DatabaseVersions;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.Track;
import com.mixzing.servicelayer.SourceService;
import com.mixzing.servicelayer.TrackService;
import com.mixzing.source.android.AndroidSourceManager;
import com.mixzing.source.impl.android.StoreUtilImpl.MyEmptyCursor;


public class SourceTrackManagerImpl implements SourceTrackManager {

	private static Logger lgr = Logger.getRootLogger();
	private Context context;
	private AndroidSourceManager srcMgr;

	private static String[] MINIMAL_COLUMN_LIST = {
		Media._ID,
	};
	private static String[] FULL_COLUMN_LIST = {
		Media._ID,
		Media.TITLE,
		Media.ARTIST,
		Media.ALBUM,
		Media.DATE_ADDED,
		Media.DURATION,
		Media.DATA,
		Media.SIZE,
		Media.YEAR,
		Media.TRACK,
	};
	private static String[] DUMP_COLUMN_LIST = {
		Media._ID,
		Media.DATA,
		Media.IS_MUSIC,
		Media.MIME_TYPE,
	};

	public static final int ID = 0;
	public static final int TITLE = 1;
	public static final int ARTIST = 2;
	public static final int ALBUM = 3;
	public static final int DATE_ADDED = 4;
	public static final int DURATION = 5;
	public static final int DATA = 6;
	public static final int SIZE = 7;
	public static final int YEAR = 8;
	public static final int TRACK = 9;


	protected ArrayList<SourceTrack> tracks;

	protected HashMap<String, SourceTrack> tracksByCompositeId;
	protected HashMap<Long, SourceTrack> tracksByInternalId;


	protected boolean inited = false;

	protected Object trackLock = new Object();
	private static final Logger log = Logger.getRootLogger();
	private StoreUtils utils;
	private long initUptime;
	private int trackCount = 0;

	protected long lastValidLoadTime = 0;
	protected SourceManagerImpl mzSrcMgr;
	protected TrackService trkService;
	protected Checksum checksum = new CRC32();
	protected TrackService trackService;

	protected void insert(SourceTrack st) {
		String sql;


		sql = "INSERT OR IGNORE INTO newsrctrack " +
			"(artist, title, album, " +
			"genre, duration, " +
			"trackNumber, year, location, location_hash) " +
			"VALUES (?,?,?,?,?,?,?,?,?)";

		
		PreparedStatement ps = null;
		try {

			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setString(1, st.artist());
			ps.setString(2, st.name());
			ps.setString(3, st.album());
			ps.setString(4, st.getTag(SourceTrackTag.GENRE));
			ps.setFloat(5,st.duration());
			ps.setString(6, st.getTag(SourceTrackTag.TRACKNUMBER));
			ps.setString(7, st.getTag(SourceTrackTag.YEAR));
			String loc = st.location();
			ps.setString(8, loc);

			/*
			byte bytes[] = loc.getBytes();
			checksum.update(bytes,0,bytes.length);
			long lngChecksum = checksum.getValue();
			*/
			
			ps.setString(9, st.id().getCompositeId());
			

			AndroidPreparedStatement aps = (AndroidPreparedStatement) ps;
			aps.executeInsert();

			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("Added to newsrctrack : " + loc);
			}

		} catch (SQLException e) {
			String err = sql + "," + st.artist() + "," + st.name() + "," + st.album() + ",";
			err += st.getTag(SourceTrackTag.GENRE) + "," + st.duration() + "," + st.getTag(SourceTrackTag.TRACKNUMBER) + ",";
			err += st.location();
			throw new UncheckedSQLException(e,err);		
		} catch (Exception e1) {
			String err = sql + "," + st.artist() + "," + st.name() + "," + st.album() + ",";
			err += st.getTag(SourceTrackTag.GENRE) + "," + st.duration() + "," + st.getTag(SourceTrackTag.TRACKNUMBER) + ",";
			err += st.location();	
			lgr.error(err);
			throw new RuntimeException(e1);			
		}
		finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {

				}
			}
		}
	}

	
	public SourceTrackManagerImpl(Context con, StoreUtils utils, SourceManagerImpl srcMgr2, TrackService tk) {
		tracks = new ArrayList<SourceTrack>();
		tracksByInternalId = new HashMap<Long, SourceTrack>();
		tracksByCompositeId = new HashMap<String, SourceTrack>();
		this.context = con;
		this.utils = utils;
		this.mzSrcMgr = srcMgr2;
		this.initUptime = SystemClock.uptimeMillis();
		this.trackService = tk;
		try {
			reloadTracks();
		} catch (EmptyQueryException e) {
			log.warn("No tracks found for the user");
		}
	}

	protected static final String WHERE = Media.IS_MUSIC + " = 1";
	protected static final int MAX_ZERO_RETRIES = 5;

	public long getLastValidLoadTime() {
		return lastValidLoadTime;
	}

	public boolean isInited() {
		return inited;
	}

	protected int getTrackCountFromDB()  {
		boolean isChanged = false;

		Cursor cur = null;
		try {
			cur = utils.query(Media.getContentUri(SdCardHandler.getVolume()), MINIMAL_COLUMN_LIST, WHERE, null, null);
			if(cur == null) {
				return -1;
			}
			int cnt = cur.getCount();
			cur.close();
			return cnt;
		}
		catch (Exception e) {
			return -1;
		}
	}

	protected int getTrackCountFromMemory()  {

		synchronized (trackLock) {
			return trackCount; 
		}
	}

	protected boolean beginTransaction() {
		boolean isOk = false;
		try {
			DatabaseManager.beginTransaction();
			isOk = true;
		} catch (SQLException e) {
			throw new UncheckedSQLException(e,"begintransaction");
		}
		return isOk;
	}


	private boolean commitOrRollback(boolean commit) {
		boolean isOk = false;

		try {
			if(commit) {
				DatabaseManager.commitTransaction();

			} else {
				DatabaseManager.rollbackTransaction();
			}
			isOk = true;
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		} catch (IllegalStateException e1) {
			lgr.error("commitOrRollback",e1);
		}
		return isOk;
	}

	/*
	 * XXX: Made it synchronized so that multiple reloads cannot kick in at same time, specially now 
	 * that we can sleep hCere.
	 * 
	 */
	protected synchronized boolean reloadTracks() throws EmptyQueryException {
		boolean isChanged = false;

		if(!mzSrcMgr.isResolvingEnabled()) {
			return isChanged;
		}

		Cursor cur = null;
		try {
			cur = utils.query(Media.getContentUri(SdCardHandler.getVolume()), FULL_COLUMN_LIST, WHERE, null, null);
			if(cur == null) {
				log.error("SourceTrackManagerImpl.reloadTracks: got null cursor for " +
						Media.getContentUri(SdCardHandler.getVolume()) + " inited=" + inited + " uptime=" +
						SystemClock.uptimeMillis() + " initUptime = " + initUptime);
			}
		}
		catch (Exception e) {
			log.error("SourceTrackManagerImpl.reloadTracks: query exception: inited = " + inited + " uptime=" +
					SystemClock.uptimeMillis() + " initUptime = " + initUptime, e);
			// force a retry
			cur = null;
		}

		if(cur == null || (cur.getCount() == 0)) {
			if(cur != null) {
				cur.close();
			}
			if(cur == null || !(cur instanceof MyEmptyCursor)) {
				if(Logger.IS_DEBUG_ENABLED) {
					log.debug("Track query Cur either null or count 0, cur = " + cur);
				}
			}
			// XXX: requeue to test again laterEmptyQueryExceptionn
			if(Logger.IS_DEBUG_ENABLED) {
				log.debug("SourceTrackManagerImpl: throwing empty query exception.");
			}

			throw new EmptyQueryException();
		}

		lastValidLoadTime = SystemClock.uptimeMillis();

		synchronized(SourceService.class) { // Synchronize with resolver
			synchronized (trackLock) {
				HashSet<String> sourceIds = new HashSet<String>();
				cur.moveToFirst();
				trackCount = cur.getCount();
				beginTransaction();
				try {
					if(Logger.IS_DEBUG_ENABLED) {
						lgr.debug("SourceTrackManagerImpl: Number of source tracks = " + cur.getCount());
					}
					for (int i = cur.getCount(); i > 0; --i) {
						String compSourceId = this.getCompositeSourceId(cur);
						sourceIds.add(compSourceId);
						if(tracksByCompositeId.get(compSourceId) == null) {
							addTrack(cur,compSourceId);
							isChanged = true;
						}
						cur.moveToNext();
					}
				} finally {
					if(isChanged) {
						commitOrRollback(true);
					} else {
						commitOrRollback(false);
					}
				}
				List<SourceTrack> tracksToRemove = new ArrayList<SourceTrack>();
				for(SourceTrack t : this.tracks) {
					if(!sourceIds.contains(t.id().getCompositeId())) {
						tracksToRemove.add(t);
					}
				}
				for(SourceTrack t : tracksToRemove) {
					removePreviousTrack(t);
					isChanged = true;
				}

				rebuildColumnIdIndex();
			}
		}
		inited = true;

		cur.close();


		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("SourceTrackManagerImpl: returning ischanged=" + isChanged + " total tracks = " + tracksByInternalId.size());
		}

		return isChanged;
	}

	protected void removePreviousTrack(SourceTrack t) {
		if(Logger.IS_DEBUG_ENABLED)
			log.debug("Track Deleted: " + t);
		this.tracks.remove(t);
		this.tracksByCompositeId.remove(t.id().getCompositeId());

	}

	protected void rebuildColumnIdIndex() {
		tracksByInternalId.clear();
		for(SourceTrack t : tracks) {
			tracksByInternalId.put(t.id().getInternalId(),t);
			//			if(Logger.IS_DEBUG_ENABLED) {
			//				log.debug("Storing source track for internal id = " + t.id().getInternalId());
			//			}
		}
		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("Done Storing source tracks for internal ids" );
		}
	}
	// NOTE: assumes caller is holding the lock
	private void add(String id, String compositeId, String title,
			String artist, String album, String genre, String tracknum,
			String year, long createDate, int duration, String location,
			int size) {

		SourceTrack st = new SourceTrackImpl(id, compositeId, title, artist, album,
				genre, tracknum, year, createDate, duration, location, size);
		
		
		// Start a db tx ???
		if(trackService.locateBySourceId(compositeId) == null) {
			this.insert(st);
		}
		// End a db tx  ???
		
		
		tracks.add(st);
		tracksByCompositeId.put(compositeId, st);
		
		SourceTrackImpl t1 = (SourceTrackImpl) st;
		t1.clearNonRetainedResources();

	}


	public SourceTrack findByTrackDbId(String id) {
		synchronized (trackLock) {
			return tracksByCompositeId.get(id);
		}
	}

	protected SourceTrack findByColumnId(Long lid) {
		synchronized (trackLock) {
			return tracksByInternalId.get(lid);
		}		
	}

	public int getTrackCount() {
		synchronized (trackLock) {
			return tracks.size();
		}
	}

	public List<SourceTrack> getTracks() {
		synchronized (trackLock) {
			return tracks;
		}
	}


	private String getCompositeSourceId(Cursor cur) {
		String compid = AndroidUtil.getMd5Hash(getLocation(cur));
		return compid;
	}

	private String getSourceId(Cursor cur) {
		return cur.getString(ID);
	}

	private void addTrack(Cursor cur, String compositeId) {
		String s = cur.getString(DATE_ADDED);
		long createDate;
		try { createDate = Long.valueOf(s); } catch (Exception e) { createDate = 0; }

		s = cur.getString(DURATION);
		int duration;
		try { duration = Integer.valueOf(s) / 1000; } catch (Exception e) { duration = 0; }

		s = cur.getString(SIZE);
		int size;
		try { size = Integer.valueOf(s); } catch (Exception e) { size = 0; }

		String genre = "";  // XXX handle

		// strip possible disc number from track number
		String tracknum = cur.getString(TRACK);
		if (tracknum != null) {
			int len = tracknum.length();
			int i = len - Math.min(len, 3);
			tracknum = tracknum.substring(i);
		} else {
			tracknum = "0";
		}

		/*
		Logger log = Logger.getRootLogger();
		s = cur.getString(ALBUM);
		String s2 = "";
		for (int i = 0; i < s.length(); ++i) {
			s2 += String.format("%02x ", (int)s.charAt(i));
		}
		log.debug(s + ": " + s2);
		 */

		String artist = cur.getString(ARTIST);
		String album = cur.getString(ALBUM);
		String title = cur.getString(TITLE);

		if(artist == null) {
			artist = "<unknown>";
		}

		if(album == null) {
			album = "<unknown>";
		}

		if(title == null) {
			title = "<unknown>";
		}

		if(genre == null) {
			genre = "";
		}

		if(tracknum == null) {
			tracknum = "0";
		}

		String location = getLocation(cur);

		String year = cur.getString(YEAR);
		if (year == null) {
			year = "0";
		}

		String id = getSourceId(cur);
		add(id, compositeId, title, artist, album,
				genre, tracknum, year, createDate, duration, location, size);
	}

	private String getLocation(Cursor cur) {
		String location = cur.getString(DATA);
		if(location == null) {
			location = "/sdcard/<unknown>-" + getSourceId(cur)+ ":" + cur.getString(DATE_ADDED) + ":" + cur.getString(DURATION);
		}
		return location;
	}



	// unused stubs
	public List<SourceTrack> createTracksForPlaylist(List playlistTracks) {
		assert(false);
		return null;
	}

	public List<SourceTrack> getTracksInPlaylists() {
		assert(false);
		return null;
	}

	public void changeTrackTag(SourceTrackId id, String tag, String value) {
		assert(false);
	}

	public void removeTrack(SourceTrack track) {
		assert(false);
	}

	public void clearNonRetainedResources() {
		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("Clearing non retained resources");
		}
		synchronized (trackLock) {
			for(SourceTrack t : this.tracks) {
				SourceTrackImpl t1 = (SourceTrackImpl) t;
				t1.clearNonRetainedResources();
			}
		}
	}

	public static final String NOT_MUSIC = "NOT_MUSIC";
	public String dumpTrack(Long tid) {
		Cursor cur = null;
		try {
			cur = utils.query(Media.getContentUri(SdCardHandler.getVolume()), DUMP_COLUMN_LIST, Media._ID + " = " + tid, null, null);
			if(cur == null || (cur.getCount() == 0)) {
				return null;
			}
			cur.moveToFirst();
			String is_music = cur.getString(2);
			if(!"1".equals(is_music)) {
				if(Logger.IS_DEBUG_ENABLED) {
					log.debug("Non music track dump : " + cur.getString(0) + ":" + cur.getString(1) + ": is_music= " + is_music + ": mime_type = " + cur.getString(3));
				}
				return NOT_MUSIC;
			}
			String ret = "" + cur.getString(0) + ":" + cur.getString(1) + ": is_music= " + is_music + ": mime_type = " + cur.getString(3);
			cur.close();
			return ret;
		}
		catch (Exception e) {
			return null;
		}

	}
}
