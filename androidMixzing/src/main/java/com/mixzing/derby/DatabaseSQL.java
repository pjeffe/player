package com.mixzing.derby;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mixzing.android.AndroidUtil;
import com.mixzing.android.Preferences;
import com.mixzing.android.SdCardHandler;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.Track;
import com.mixzing.musicobject.dao.impl.TrackDAOImpl;



public class DatabaseSQL implements MixzingAppSql {

	public static final Logger log = Logger.getRootLogger();
	private static final int LATEST_CODE_VERSION = 4;

	private static int existing_code_version = 3;


	public static ArrayList<String> dropStatements() {
		ArrayList<String>  statements = new ArrayList<String>();
		statements.add("DROP TABLE playlist_status");
		statements.add("DROP TABLE version_tracker");
		statements.add("DROP TABLE hate_ignore_artist");
		statements.add("DROP TABLE wishlist");
		statements.add("DROP TABLE rating");
		statements.add("DROP TABLE reco_alternates");
		statements.add("DROP TABLE recommendation");
		statements.add("DROP TABLE signature_request");
		statements.add("DROP TABLE track_signature_value");
		statements.add("DROP TABLE playlist_track");
		statements.add("DROP TABLE track");
		statements.add("DROP TABLE global_song_source");
		statements.add("DROP TABLE global_song");
		statements.add("DROP TABLE playlist");
		statements.add("DROP TABLE newsrctrack");
		statements.add("DROP TABLE library");
		//statements.add("DROP TABLE source");
		statements.add("DROP TABLE outbound_msg_q");
		statements.add("DROP TABLE error_msg_log");
		return statements;
	}

	private static String createVersionTracker() {
		return "CREATE TABLE version_tracker (" +              
		"db_schema_version INTEGER NOT NULL DEFAULT 1 " +
		" )";
	}

	private static String[] deleteAll = { 
		"DELETE FROM wishlist",
		"DELETE FROM rating",
		"DELETE FROM reco_alternates",
		"DELETE FROM recommendation",
		"DELETE FROM signature_request",
		"DELETE FROM track_signature_value",
		"DELETE FROM playlist_track",
		"DELETE FROM track",
		"DELETE FROM global_song_source",
		"DELETE FROM global_song",
		"DELETE FROM playlist",
		"DELETE FROM newsrctrack",
		"DELETE FROM outbound_msg_q",
		"DELETE FROM error_msg_log",
		"DELETE FROM android_package",
		"DELETE FROM  android_metadata",
		"DELETE FROM video",
		"DELETE FROM track_equivalence",
		"DELETE FROM track_equivalence_songs",
		"DELETE FROM hate_ignore_artist",
		"DELETE FROM playlist_status",
		"UPDATE library SET resolved_song_count = 0,library_status = 'CREATED'",
	};

	public static void deleteEveryThing() throws Exception {
		for(String cmd : deleteAll) {
			if(Logger.IS_DEBUG_ENABLED) {
				log.debug("reinit Execing : " + cmd);
			}
			execute(cmd);
		}
	}

	private static String createArtistTable() {
		return "CREATE TABLE  IF NOT EXISTS artist (" +              
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"artist VARCHAR(330) NOT NULL" +    			
		" )";
	}

	private static String createAlbumTable() {
		return "CREATE TABLE IF NOT EXISTS album (" +              
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"album VARCHAR(255) NOT NULL, " +   
		"artist_id INTEGER, " +
		"album_art VARCHAR(255), " +
		"CONSTRAINT album_uq UNIQUE(album,artist_id)" +
		" )";
	}

	private static String createGenreTable() {
		return "CREATE TABLE  IF NOT EXISTS  genre (" +              
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"album VARCHAR(255)" +    			
		" )";
	}

	private static String createPackageTable() {
		return "CREATE TABLE IF NOT EXISTS android_package (" +              
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"name TEXT NOT NULL, " +   
		"version INTEGER, " +
		"CONSTRAINT pkg_uq UNIQUE(name,version)" +
		" )";
	}

	/*
	 *                        
	 */
	private static String createVideoTable() {
		return "CREATE TABLE IF NOT EXISTS video (" +              
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"location TEXT," +      
		"source_id VARCHAR(32) NOT NULL " +       			
		" )";
	}

	private static String insert_version_row() {
		existing_code_version  = LATEST_CODE_VERSION;
		return "INSERT INTO version_tracker (db_schema_version) VALUES (" + existing_code_version + ")" ;
	}

	private static String createTrackSignatureValue() {
		return "CREATE TABLE  IF NOT EXISTS track_signature_value (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"lsid INTEGER NOT NULL, " +
		"skip INTEGER NOT NULL," +
		"duration INTEGER NOT NULL," +
		"super_window_ms INTEGER NOT NULL DEFAULT 0," +
		"channels INTEGER," +
		"frequency INTEGER," +
		"bitrate INTEGER," +
		"ms_per_frame FLOAT," +                
		"signature VARCHAR (31000)," +
		"energy VARCHAR (31000)," +
		"code_version VARCHAR (10) NOT NULL," +
		// 0 - Created, 1 - Sent to server
		"sent_to_server INTEGER DEFAULT 0 CONSTRAINT track_signature_value_sent_to_server_ck CHECK (sent_to_server IN (0,1)), " +
		"CONSTRAINT track_signature_value_uq UNIQUE(lsid,skip,duration,super_window_ms,code_version)" + 
		" )";
	}

	private static String createTrackEquivalence() {
		return "CREATE TABLE  IF NOT EXISTS track_equivalence (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"match_level INTEGER, " +
		// 0 - Created, 1 - Processed, 2 - 
		"status INTEGER DEFAULT 0 CONSTRAINT track_equivalence_status_ck CHECK (status IN (0,1,2)) " +
		" )";
	}

	private static String createTrackEquivalenceSongs() {
		return "CREATE TABLE  IF NOT EXISTS track_equivalence_songs (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"equiv_id INTEGER NOT NULL REFERENCES track_equivalence (id)," +
		"lsid INTEGER NOT NULL, " +
		"globalsong_id INTEGER NOT NULL " + 
		" )";
	}

	private static String createTrackDeletedIndex() {        
		return "CREATE INDEX TrackDeletedIndex ON track(is_deleted)";
	}

	private static String createPlaylistDeletedIndex() {        
		return "CREATE INDEX PlaylistDeletedIndex ON playlist(is_deleted)";
	}

	private static String createGlobalSongSourceIndex() {        
		return "CREATE INDEX GlobalSongSourceGsid ON global_song_source(globalsong_id)";
	}    

	private static String createRecoAlternatesRecoIdIndex() {        
		return "CREATE INDEX RecoAlternatesRecoId ON reco_alternates(reco_id)";
	}    

	private static String createNewSrcTrackTable() {
		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("Creating newsrc table");
		}
		return "CREATE TABLE  IF NOT EXISTS newsrctrack (" +
				"artist VARCHAR(512)," +
				"title  VARCHAR(512)," +
				"album  VARCHAR(512)," +
				"genre VARCHAR(64)," +
				"duration FLOAT," +
				"trackNumber VARCHAR(64)," +
				"location VARCHAR(1024)," +
				"location_hash VARCHAR(64)," +
				"year VARCHAR(32)," +
				"CONSTRAINT location_hash_uq UNIQUE (location_hash))";
	
	}
	
	public static ArrayList<String> createStatements() {
		ArrayList<String>  statements = new ArrayList<String>();

		// Created on first startup
		// Updated on ServerNewLibraryResponse Message
		// Deleted Never
		statements.add("CREATE TABLE library (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"server_id VARCHAR(30) NOT NULL," +
				"library_status VARCHAR(30) NOT NULL," +
				"resolved_song_count INTEGER NOT NULL," +
				"time_created INTEGER )"
		);

		String st = "CREATED";
		String VALUES = "('-1'," + 0 + ",'" + st + "') ";
		statements.add("INSERT INTO library (server_id,resolved_song_count,library_status) VALUES " + VALUES) ;

		if(DatabaseVersions.CURRENT_DATABASE_VERSION_ID > DatabaseVersions.DB_VERSION_ONE) {
			statements.add(createArtistTable());
			statements.add(createAlbumTable());
			statements.add(createGenreTable());
		}

		// This table contains a reference to a "song" whether it is local or 
		// remote (global). For performance we will duplicate the mpx tags for 
		// local songs here, and periodically sync up. If the server detects a
		// change to the tags for a gsid, it can send down new tags and we will 
		// update the entry in this table. 

		// Created from source resolution
		// Created from recommendation alternates of ServerRecommendations message
		// Created from Magicplaylist server message
		// Updated from ServerTrackMapping message

		if(DatabaseVersions.CURRENT_DATABASE_VERSION_ID == DatabaseVersions.DB_VERSION_ONE) {
			statements.add("CREATE TABLE global_song (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					// Will be = - lsid, when creating one for a local track
					// Will be updated when a track mapping is received
					"gsid INTEGER NOT NULL," +
					"artist VARCHAR(255)," +
					"title  VARCHAR(255)," +
					"album  VARCHAR(255)," +
					"genre VARCHAR(255)," +
					"releaseyear INTEGER," +
					"duration FLOAT," +
					"trackNumber VARCHAR(255)," +
					"time_updated INTEGER," +
					"CONSTRAINT global_song_uq UNIQUE (gsid))"
			);			
		} else {
			statements.add("CREATE TABLE global_song (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					// Will be = - lsid, when creating one for a local track
					// Will be updated when a track mapping is received
					"gsid INTEGER NOT NULL," +
					"artist_id INTEGER," +
					"title  VARCHAR(255)," +
					"album_id  INTEGER," +
					"genre VARCHAR(255)," +
					"releaseyear INTEGER," +
					"duration FLOAT," +
					"trackNumber VARCHAR(255)," +
					"time_updated INTEGER," +
					"CONSTRAINT global_song_uq UNIQUE (gsid))"
			);
		}

		// Created from source resolution code
		// Created from Magicplaylist server message
		// Updated never
		// Deleted from source resolution
		statements.add("CREATE TABLE playlist (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				//"source INTEGER REFERENCES source(id)," +
				"name VARCHAR(512) NOT NULL," +
				"source_specific_id VARCHAR(512) NOT NULL," +
				"pl_type VARCHAR(32) NOT NULL DEFAULT 'SOURCE' " + 
				"CONSTRAINT playlist_pl_type_ck CHECK (pl_type IN ('SOURCE_USER', 'SOURCE_GENIUS', 'MAGIC_CLIENT', 'MAGIC_SERVER'))," +
				"is_deleted INTEGER DEFAULT 0 CONSTRAINT playlist_is_deleted_ck CHECK (is_deleted IN (1,0))" +
				")"
		);

		// Created from source resolution code
		// Updated ServerTrackMapping message
		// Deleted from  source resolution code
		statements.add("CREATE TABLE track (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				//"source INTEGER REFERENCES source(id)," +
				"globalsong_id INTEGER NOT NULL REFERENCES global_song(id)," +
				"source_id VARCHAR(512)," +
				"is_deleted INTEGER DEFAULT 0 CONSTRAINT track_is_deleted_ck CHECK (is_deleted IN (1,0))," +
				"location VARCHAR(2048)" +
				")"
		);

		// Created from source resolution
		// Updated from source resolution on discovering offline user updates
		// Updated from client gui on positive local recommendation rating
		// Deleted from source resolution
		statements.add("CREATE TABLE playlist_track (" +
				"plid INTEGER NOT NULL REFERENCES playlist(id)," +
				"lsid INTEGER NOT NULL REFERENCES track (id), " + 
				"CONSTRAINT playlist_track_pk PRIMARY KEY (plid,lsid))"
		);



		// This table provides access to on or more sources for purchasing or
		// previewing the "song"

		// Created from recommendation alternates of ServerRecommendations message
		// Created from Magicplaylist server message
		// Updated never
		// Deleted never
		statements.add("CREATE TABLE global_song_source (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"globalsong_id INTEGER NOT NULL REFERENCES global_song (id)," +
				"purchase_library VARCHAR(2048)," +
				"purchase_url VARCHAR(2048)," +
				"audition_url VARCHAR(2048)" +
				")"
		);



		statements.add(createGlobalSongSourceIndex());

		statements.add(createTrackSignatureValue());

		statements.add(createTrackEquivalence());

		statements.add(createTrackEquivalenceSongs());

		// Created from ServerRecommendations message
		// Updated never
		// Created from Magicplaylist server message
		// Deleted on new ServerRecommendations message for the same plid
		statements.add("CREATE TABLE recommendation (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"plid INTEGER REFERENCES playlist(id)," +
				"score FLOAT," +
				// should we move the following 3 fields out to a separate table?
				"artsongid INTEGER," +
				// used for hate artist
				"timereceived INTEGER ," +
				"is_deleted INTEGER DEFAULT 0  CONSTRAINT recommendation_is_deleted_ck CHECK (is_deleted IN (1,0))," +
				"is_rated INTEGER DEFAULT 0  CONSTRAINT recommendation_is_rated_ck  CHECK (is_rated IN (1,0))," +
				// reco source of 1 implies recommendation generated when user 
				// added a track to the playlist, 0 means recommendation generated
				// when server recommends a song
				"reco_source INTEGER DEFAULT 0 CONSTRAINT recommendation_reco_source_ck CHECK (reco_source IN (1,0))," +
				//INDEX (plid,asid),
				"artsongartist VARCHAR(330)," +
				"artsongtitle  VARCHAR(255))" 

		);

		// Created from ServerRecommendations message
		// Created from Magicplaylist server message
		// Updated never
		// Deleted on new ServerRecommendations message for the same plid
		statements.add("CREATE TABLE reco_alternates (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"reco_id INTEGER NOT NULL REFERENCES recommendation (id)," +
				"globalsong_id INTEGER NOT NULL REFERENCES global_song (id)," +
				"is_local INTEGER DEFAULT 0 CONSTRAINT reco_alternates_is_local_ck CHECK (is_local IN (1,0))," +
				"rank FLOAT," +
				"CONSTRAINT reco_alternates_uq UNIQUE(reco_id,globalsong_id))" 
		);


		statements.add(createRecoAlternatesRecoIdIndex());

		// Created from Source resolution
		// Created from GUI user action
		// Updated never
		// Deleted from GUI user action
		statements.add("CREATE TABLE rating (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				// make this point to global song so that we can clean up recommendations
				"globalsong_id INTEGER REFERENCES global_song (id) ," +
				"plid INTEGER REFERENCES playlist (id)," +
				/*
					UNKNOWN(16),
				    LOVE(2),
				    LIKE(1),
				    NOT_HERE(0),
				    DISLIKE(-1),
				    HATE(-2);
				    XXX: TODO ADD A CHECK CLAUSE
				 */
				"rating_value INTEGER NOT NULL," +
				/*
				    UNKNOWN(0),
				    INFERRED_ADD(1),
				    INFERRED_REMOVE(2),
				    USER_SELECTION(3),
				    INFERRED_RATING_TAG(4),
				    INFERRRED_PLAY_COUNT(5),
				    FROM_HATE_ARTIST(6),
				    FROM_IGNORE_ARTIST(7),
				    INFERRED_LISTEN(8);
				    XXX: TODO ADD A CHECK CLAUSE
				 */
				"rating_source INTEGER NOT NULL," +
				"timerated INTEGER NOT NULL ," +
				// maybe on delete we should delete the rating here, instead of a soft delete
				"is_deleted INTEGER DEFAULT 0 CONSTRAINT rating_is_deleted_ck CHECK (is_deleted IN (1,0))" +
				")"
		);

		statements.add("CREATE TABLE wishlist (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				// make this point to global song so that we can clean up recommendations
				"globalsong_id INTEGER REFERENCES global_song (id) ," +
				"plid INTEGER REFERENCES playlist (id)," +
				"is_in_shopping_cart INTEGER DEFAULT 0 CONSTRAINT wishlist_is_in_shopping_cart_ck CHECK (is_in_shopping_cart IN (1,0))," +
				"is_purchase_pending INTEGER DEFAULT 0 CONSTRAINT wishlist_is_purchase_pending_ck  CHECK (is_purchase_pending IN (1,0))," +
				"time_modified INTEGER ," +
				"time_added_to_cart INTEGER )" 
		);

		statements.add("CREATE TABLE signature_request (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"lsid INTEGER REFERENCES track (id)," +
				// processing status 0=>to be generated, 1=>being processed, 2=>signature queued
				"processing_status INTEGER DEFAULT 0 CONSTRAINT signature_request_processing_status_ck CHECK (processing_status IN (0,1,2))," +
				"skip INTEGER," +
				"duration INTEGER," +
				"super_window_ms INTEGER DEFAULT 0," +
				"is_priority INTEGER DEFAULT 0 CONSTRAINT signature_request_is_priority_ck  CHECK (is_priority IN (1,0)))" 
		);

		statements.add("CREATE TABLE outbound_msg_q (" +
				"id INTEGER, " +
				"lib_id VARCHAR(30), " +
				"gsid INTEGER, " +
				"msgcount INTEGER, " +
				"msgtype VARCHAR(256), " +
				"target_server VARCHAR(24), " +
				"is_priority INTEGER DEFAULT 0 CONSTRAINT outbound_msg_q_is_priority_ck CHECK (is_priority IN (1,0)), " +
				"time_added INTEGER NOT NULL," +
				"msg VARCHAR (1000000))" 
		);

		statements.add("CREATE TABLE error_msg_log (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"error_str VARCHAR(2048)," +
				"time_added INTEGER ," +
				"orig_id INTEGER," +
				"orig_is_priority INTEGER DEFAULT 0 CONSTRAINT error_msg_log_orig_is_priority_ck CHECK (orig_is_priority IN (1,0))," +
				"orig_timeAdded INTEGER,"+
				"orig_msg VARCHAR(31000)" +
				")"
		);

		statements.add("CREATE TABLE hate_ignore_artist (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				// if plid is NULL then it implies that we hate the artist
				// otherwise the artist is to be ignored for the playlist : plid
				"plid INTEGER REFERENCES playlist (id)," +
				"artist_name VARCHAR(330) NOT NULL" +
				")"
		);

		statements.add(createPackageTable());
		statements.add(createVideoTable());

		statements.add(createVersionTracker());
		statements.add(insert_version_row());

		statements.add("CREATE TABLE playlist_status (" +
				"plid INTEGER NOT NULL REFERENCES playlist (id)," +
				"last_reco_time INTEGER NOT NULL)"
		);

		if(LATEST_CODE_VERSION >=4) {
			statements.add(createNewSrcTrackTable());
		}
		
		//statements.add(createTrackDeletedIndex());

		//statements.add(createPlaylistDeletedIndex());

		return statements;
	}

	public static String lastTable() {
		return "PLAYLIST_STATUS";
	}

	public static ArrayList<String> clearStatements() {
		// TODO Auto-generated method stub
		return null;
	}

	static HashMap<String,String> sqlStatements = new HashMap<String,String>(); 
	public static void init() {
		sqlStatements.put("", "");
	}

	public static String getStatement(String key) {
		return sqlStatements.get(key);
	}

	public static void updateSchema() throws Exception{
		if(!DatabaseManager.tableExists("version_tracker")) {
			updateFromVersion_0_to_1();
		} else {
			// we will read the version info and operate on it in future releases
		}
	}

	public static void execute(String sql) throws Exception {
		DatabaseManager.executeUpdate(sql);
	}

	protected static void updateFromVersion_0_to_1() throws Exception {
		execute("ALTER TABLE track_signature_value ADD COLUMN super_window_ms INTEGER NOT NULL DEFAULT 0");
		execute("ALTER TABLE track_signature_value ALTER COLUMN signature SET DATA TYPE VARCHAR(31000)");
		execute("ALTER TABLE track_signature_value ALTER COLUMN energy SET DATA TYPE VARCHAR(31000)");
		execute("ALTER TABLE signature_request     ADD COLUMN super_window_ms INTEGER DEFAULT 0");
		execute("ALTER TABLE global_song ALTER COLUMN genre SET DATA TYPE VARCHAR(255)");
		execute("ALTER TABLE global_song ALTER COLUMN trackNumber SET DATA TYPE VARCHAR(255)");
		execute("ALTER TABLE playlist ALTER COLUMN source_specific_id SET DATA TYPE VARCHAR(512)");
		execute("ALTER TABLE track ALTER COLUMN source_id SET DATA TYPE VARCHAR(512)");		
		execute("ALTER TABLE track ALTER COLUMN location SET DATA TYPE VARCHAR(2048)");	

		execute("ALTER TABLE global_song_source ALTER COLUMN purchase_library SET DATA TYPE VARCHAR(2048)");	
		execute("ALTER TABLE global_song_source ALTER COLUMN purchase_url SET DATA TYPE VARCHAR(2048)");
		execute("ALTER TABLE global_song_source ALTER COLUMN audition_url SET DATA TYPE VARCHAR(2048)");

		execute("ALTER TABLE error_msg_log ALTER COLUMN error_str SET DATA TYPE VARCHAR(2048)");
		execute("ALTER TABLE error_msg_log ALTER COLUMN orig_msg SET DATA TYPE VARCHAR(31000)");

		execute("ALTER TABLE error_msg_log ALTER COLUMN orig_msg SET DATA TYPE VARCHAR(31000)");


		execute(createVersionTracker());
		execute(insert_version_row());
	}


	protected static int getExistingVersion() throws Exception {
		ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), "SELECT db_schema_version FROM version_tracker");
		int ver = 0;
		while(rs.next()) {
			ver = rs.getInt("db_schema_version");
		}
		rs.close();
		return ver;
	}

	protected static void updateVersion(int ver) throws Exception {
		DatabaseManager.executeUpdate("UPDATE version_tracker SET db_schema_version = " + ver);
	}

	protected static boolean isPathFixCheckNeeded() {
		boolean isNeeded =false;
		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("DatabaseSQL: isPathFixCheckNeeded: at build.version.release=" + android.os.Build.VERSION.RELEASE);
		}
		String prevRel = AndroidUtil.getStringPref(null, 
							AndroidUtil.getCardSpecificPrefKey(Preferences.Keys.DB_BUILD_VERSION_PROCESSED), 
								null);
		if(prevRel == null || !prevRel.equals(android.os.Build.VERSION.RELEASE)) {
			if(Logger.IS_DEBUG_ENABLED) {
				log.debug("DatabaseSQL - Need to check path fix since releasese differ, prev = " + prevRel + " cur = " + android.os.Build.VERSION.RELEASE);
			}
			isNeeded = true;
		}
		return isNeeded;
	}


	protected static void fixUpPaths() {
		int numFilesFixed = 0;
		String origName = null;
		String newName = null;

		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("DatabaseSQL: In fixup paths - reading all non deleted tracks");
		}
		TrackDAOImpl tdaO = new TrackDAOImpl();
		ArrayList<Track> tracks = tdaO.findAllTracks();
		if(tracks != null && tracks.size() > 0) {
			if(Logger.IS_DEBUG_ENABLED) {
				log.debug("DatabaseSQL: In fixup paths examining track count : " + tracks.size());
			}
			synchronized(DatabaseManager.class) {
				boolean txStarted = false;
				try {
					DatabaseManager.beginTransaction();
					txStarted = true;
					for(Track track : tracks) {
						String location = track.getLocation();

						String abs = null;
						String can = null;
						try {
							File f = new File(location);
							abs = f.getAbsolutePath();
							can = f.getCanonicalPath();
						} catch (Exception e) {
							if(Logger.IS_DEBUG_ENABLED) {
								log.debug("DatabaseSQL: Got exception " + e + " while examining " + location);
							}
						}

						if(abs != null && can != null) {
							if(!abs.equals(can)) {
								/*
								 * Our path changed - so lets adjust our DB.
								 */
								if(Logger.IS_DEBUG_ENABLED) {
									log.debug("DatabaseSQL: Found fixable track : " + abs + " fixing to " + can);
								}
								track.setSourceId(AndroidUtil.getMd5Hash(can));
								track.setLocation(can);
								tdaO.updateSourceIdAndLocation(track);
								numFilesFixed++;
								if(numFilesFixed == 1) {
									origName = abs;
									newName = can;
								}
							} else {
								/*
								 * Our paths have not changed - lets find the first file that exists and bail.
								 * 
								 */
								File f = new File(abs);
								if(f.exists()) {
									if(Logger.IS_DEBUG_ENABLED) {
										log.debug("DatabaseSQL: Stat found the file, we must not have changed os " + abs);
									}
									break;
								} else {
									if(Logger.IS_DEBUG_ENABLED) {
										log.debug("DatabaseSQL: File not found using stat, looking at next " + abs);
									}
								}
							}
						}

					}
				} catch (Exception e) {
					log.error(e);
					return; // Don't update the os version ?
				} finally {
					if(txStarted) {
						try {
							DatabaseManager.commitTransaction();
						} catch (SQLException e) {
							log.error(e);
							return; // Don't update the os version ?
						}
					}
				}
			}
		} else {
			if(Logger.IS_DEBUG_ENABLED) {
				log.debug("DatabaseSQL: In fixup paths - no tracks found to process");
			}
		}

		AndroidUtil.setStringPref(null, 
				AndroidUtil.getCardSpecificPrefKey(Preferences.Keys.DB_BUILD_VERSION_PROCESSED),
				android.os.Build.VERSION.RELEASE);

		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("DatabaseSQL: Done fixup paths");
			if(numFilesFixed > 0) {
				log.error("DatabaseSQL: Changed " + numFilesFixed + " sample " + origName + " -> " + newName + " Root = "  + SdCardHandler.getRootDir());
			}
		}

	}
	
	protected static void upgrade_from_2_to_3() throws Exception {
		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("DatabaseSQL.createPackageTable: will attempt to create a package table");
		}

		DatabaseManager.executeUpdate(createPackageTable());
		DatabaseManager.executeUpdate(createVideoTable());

		TrackDAOImpl tdaO = new TrackDAOImpl();

		synchronized(DatabaseManager.class) {
			DatabaseManager.beginTransaction();
			ArrayList<Track> tracks = tdaO.findAllTracks();
			if(tracks != null) {
				for(Track track : tracks) {
					String location = track.getLocation();
					if(location != null) {
						track.setSourceId(AndroidUtil.getMd5Hash(location));
						tdaO.updateSourceId(track);
					}
				}
			}
			updateVersion(3);
			DatabaseManager.commitTransaction();
		}
	}

	protected static void upgrade_from_3_to_4() throws Exception {
		if(Logger.IS_DEBUG_ENABLED) {
			log.debug("DatabaseSQL.createPackageTable: will attempt to create newsrc");
		}
		DatabaseManager.executeUpdate(createNewSrcTrackTable());
		synchronized(DatabaseManager.class) {
			DatabaseManager.beginTransaction();
			updateVersion(4);
			DatabaseManager.commitTransaction();
		}
	}
	protected static void checkAndPerformUpdates() {

		int db_code_version = AndroidUtil.getIntPref(null, "db_code_version", -1);

		if(existing_code_version != LATEST_CODE_VERSION) {
			if(db_code_version < LATEST_CODE_VERSION) {
				try {
					int ver = getExistingVersion();
					if(ver == 2 && LATEST_CODE_VERSION > 2) {
						upgrade_from_2_to_3();
						AndroidUtil.setIntPref(null, "db_code_version",3);
						ver = 3;
					}
					if(ver == 3 && LATEST_CODE_VERSION > 3) {
						upgrade_from_3_to_4();
						AndroidUtil.setIntPref(null, "db_code_version",4);
						ver = 4;
					}
				} catch (Exception e) {
					log.error(e,e);
				}
			} else {
				AndroidUtil.setIntPref(null, "db_code_version",LATEST_CODE_VERSION);
			}
		}

		// Fix the problem stemming from upgarde to Froyo (2.2) where the paths changed from
		// /sdcard to /mnt/sdcard etc
		
		if(isPathFixCheckNeeded()) {
			fixUpPaths();
		}

	}


	public static ArrayList<String> updateFromVersion_1_to_2() throws Exception {
		ArrayList<String> stmts = new ArrayList<String>();


		stmts.add("ALTER TABLE global_song ADD COLUMN artist_id INTEGER");
		stmts.add("ALTER TABLE global_song ADD COLUMN album_id INTEGER");	
		stmts.add(createArtistTable());
		stmts.add(createAlbumTable());
		stmts.add(createGenreTable());

		// XXX: we need to run this under tx
		stmts.add("INSERT OR IGNORE INTO artist (artist) SELECT DISTINCT artist FROM global_song WHERE artist IS NOT NULL");
		stmts.add("UPDATE global_song SET artist_id = (SELECT id FROM artist WHERE artist.artist = global_song.artist)");		
		stmts.add("INSERT OR IGNORE INTO album  (album,artist_id ) SELECT album,artist_id  FROM global_song WHERE album  IS NOT NULL");
		stmts.add("UPDATE global_song SET album_id  = (SELECT id FROM album  WHERE album.album   = global_song.album AND album.artist_id = global_song.artist_id)" );
		stmts.add("UPDATE global_song SET album = null, artist = null");
		// XXX: we need to run this under tx

		return stmts;
	}

    @Override
    public List<String> dropTableStatements()
    {
        return dropStatements();
    }

    @Override
    public List<String> createTableStatements()
    {
        return createStatements();
    }

    @Override
    public List<String> clearTableStatements()
    {
        return clearStatements();
    }
} 
