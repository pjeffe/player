package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.Track;
import com.mixzing.musicobject.dao.TrackDAO;
import com.mixzing.musicobject.impl.TrackImpl;

public class TrackDAOImpl extends BaseDAO<Track> implements TrackDAO{

	private String tableName = "track";
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.TrackDAO#insert(com.mixzing.musicobject.Track)
	 */
	public long insert(Track track) {
		String sql = "INSERT INTO track " +
		"(location, globalsong_id, is_deleted,source_id)" + 
		" VALUES (?,?,?,?)";
		String identityQuery = "VALUES IDENTITY_VAL_LOCAL()";
		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setString(1, track.getLocation());
			ps.setLong(2, track.getGlobalSongId());
			ps.setInt(3, track.isDeleted() ? 1 : 0);
			ps.setString(4, track.getSourceId());
			
			AndroidPreparedStatement aps = (AndroidPreparedStatement) ps;
			idVal = aps.executeInsert();
			
		} catch (SQLException e) {
			throw new UncheckedSQLException(e,sql);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {

				}
			}
		}
		track.setId(idVal);
		return idVal;	
	}
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.TrackDAO#readAll()
	 */
	public ArrayList<Track> readAll() {
		ArrayList<Track> list = new ArrayList<Track>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				Track play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}
		
		return list;
	}
	

	@Override
	protected Track createInstance(ResultSet rs) {
		// TODO Auto-generated method stub
		return new TrackImpl(rs);
	}
	
	@Override
	protected String tableName() {
		return tableName;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.TrackDAO#findAllTracks()
	 */
	public ArrayList<Track> findAllTracks() {
		String sql = "SELECT * FROM " + tableName() + " WHERE is_deleted = 0";
		return getCollection(sql);
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.TrackDAO#findTracksInRecommendation()
	 */
	public ArrayList<Track> findTracksInRecommendation() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.TrackDAO#findTracksInAPlaylist()
	 */
	public ArrayList<Track> findTracksInAPlaylist(long plid) {
		String sql = "SELECT track.* FROM track, playlist_track WHERE playlist_track.lsid = track.id AND playlist_track.plid = ?"; 
		return getCollection(sql, plid);
	}

	public void delete(Track t) {
		String sql = "UPDATE " + tableName() + " SET is_deleted = 1 WHERE id = ? ";	
		try {
			DatabaseManager.executeUpdateLongParams( sql, t.getId());
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}		

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#updateRecoAsRated(long)
	 */
	public void updateSourceIdAndLocation(Track t) {
		String sql = "UPDATE " + tableName() + " SET source_id = ?, location = ? WHERE id = ?";
		try {
			DatabaseManager.executeUpdateByIdWithStringParams(sql, t.getId(), t.getSourceId(), t.getLocation());
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#updateRecoAsRated(long)
	 */
	public void updateSourceId(Track t) {
		String sql = "UPDATE " + tableName() + " SET source_id = ? WHERE id = ?";
		try {
			DatabaseManager.executeUpdateByIdWithStringParams(sql, t.getId(), t.getSourceId());
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
}
