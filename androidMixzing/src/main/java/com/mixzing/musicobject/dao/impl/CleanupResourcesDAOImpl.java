package com.mixzing.musicobject.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;

import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.dao.CleanupResourceDAO;

/*
 * 
 * BEGIN_TX
 * 
 * Playlist related data
 * 1. Remove playlist_track for deleted playlists
 * 2. Remove rating for deleted playlists
 * 3. Remove wishlist for deleted playlists
 * 4. Remove hate_ignore_artist for deleted playlists
 * 5. Remove reco_alternates for deleted playlists
 * 6. Remove recommendation for deleted playlists
 * 7. Remove deleted playlists
 * 
 * Track Related data
 * 8. Remove playlist_track for deleted track
 * 9. Remove signatures for deleted tracks
 * 10. Remove signature requests for deleted tracks
 * 11. Remove deleted tracks
 * 
 * Recommendation related data
 * 12. Remove reco_alternates for deleted recos
 * 13. Remove deleted recos
 *
 * COMMIT_TX
 * 
 * Not sure about tx isolation on Sqlite, so doing this in two different txs
 * 
 * BEGIN TX
 *   
 *   Delete all global_songs other than 
 *         
 *   1. Songs referenced from tracks
 *   2. Songs referenced from recommendations 
 *   3. Songs referenced from ratings
 *   4. Songs referenced from wishlist
 *   
 * COMMIT TX
 * 
 */

public class CleanupResourcesDAOImpl implements CleanupResourceDAO {

	protected static Logger lgr = Logger.getRootLogger();
	
	protected static final String[] nonGsResources = {
		"DELETE FROM playlist_track WHERE plid IN (SELECT id FROM playlist p WHERE p.is_deleted = 1 )",
		"DELETE FROM rating WHERE plid IN (SELECT id FROM playlist p WHERE p.is_deleted = 1 )",
		"DELETE FROM wishlist WHERE plid IN (SELECT id FROM playlist p WHERE p.is_deleted = 1 )",
		"DELETE FROM hate_ignore_artist WHERE plid IN (SELECT id FROM playlist p WHERE p.is_deleted = 1 )",
		"DELETE FROM reco_alternates WHERE id IN  (SELECT ra.id FROM reco_alternates ra, playlist p, recommendation r " +
		"WHERE p.is_deleted = 1 AND p.id = r.plid AND ra.reco_id  = r.id )",
		"DELETE FROM recommendation WHERE id IN " +
		" ( SELECT r.id FROM playlist p, recommendation r WHERE p.is_deleted = 1 AND p.id = r.plid )",
		"DELETE FROM playlist WHERE is_deleted = 1",
		"DELETE FROM playlist_track WHERE lsid IN (SELECT id FROM track t WHERE t.is_deleted = 1 )",
		"DELETE FROM signature_request WHERE lsid IN (SELECT id FROM track t WHERE t.is_deleted = 1 )",
		"DELETE FROM track_signature_value WHERE lsid IN (SELECT id FROM track t WHERE t.is_deleted = 1 )",
		"DELETE FROM track WHERE is_deleted = 1",
		"DELETE FROM reco_alternates WHERE rank > 1.0",
		"DELETE FROM reco_alternates WHERE reco_id IN (SELECT id from recommendation WHERE is_deleted = 1)",
		"DELETE FROM recommendation WHERE is_deleted = 1",
		"UPDATE global_song_source SET purchase_url = SUBSTR(purchase_url,LENGTH(purchase_url)-9,10), purchase_library = 'I' " + 
		                          " WHERE purchase_url LIKE 'http://www.amazon%'",
		 "UPDATE global_song_source SET purchase_url = 'l', purchase_library = 'I' WHERE purchase_url LIKE 'local itunes%'"		                          
	};

	protected static final String[] gsStatements = {
		"SELECT globalsong_id FROM track",
		"SELECT globalsong_id FROM reco_alternates",
		"SELECT globalsong_id FROM rating",
		"SELECT globalsong_id FROM wishlist"
	};

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.CleanupResourceO#deleteUnusedNonGlobalSongObjects()
	 */
	public void deleteUnusedNonGlobalSongObjects() {
		try {
			for(String statement : nonGsResources) {
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("CleanupResources: " + statement);
				}
				DatabaseManager.executeUpdate(statement);
			}
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}		
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.CleanupResourceO#deleteUnreferencedGlobalSongs()
	 */
	public void deleteUnreferencedGlobalSongs() {
		// First get all the gsids to avoid deleting any gsids that get added while we are selecting
		HashSet<Long> allGsids = getAllGsids();

		// From this list eliminate globalsongs which are referenced from the track, rec_alt,, rating and wishlist tables
		try {
			for(String sql: gsStatements) {
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("CleanupResources: reading: " + sql);
				}
				ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
				while(rs.next()) {
					long id = rs.getLong("globalsong_id");
					allGsids.remove(id);
				}
				rs.close();
			}
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.debug("CleanupResources: number of gsids to delete " + allGsids.size());
			}
			Iterator<Long> it = allGsids.iterator();
			while(it.hasNext()) {
				deleteGlobalSongObjects(it.next());
			}

		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	protected HashSet<Long> getAllGsids() {
		HashSet<Long> allGsids = new HashSet<Long>();
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), "SELECT id FROM global_song");
			while(rs.next()) {
				long id = rs.getLong("id");
				allGsids.add(id);
			}
			rs.close();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
		return allGsids;
	}
	
	protected void deleteGlobalSongObjects(long gsid) {
		try {
			DatabaseManager.executeUpdateLongParams("DELETE FROM global_song_source WHERE globalsong_id = ?", gsid);
			DatabaseManager.executeUpdateLongParams("DELETE FROM global_song WHERE id = ?", gsid);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}		
	}
}
