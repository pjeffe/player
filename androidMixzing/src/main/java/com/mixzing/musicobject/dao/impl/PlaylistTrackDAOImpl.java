package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.PlaylistTrack;
import com.mixzing.musicobject.dao.PlaylistTrackDAO;
import com.mixzing.musicobject.impl.PlaylistTrackImpl;

public class PlaylistTrackDAOImpl extends BaseDAO<PlaylistTrack> implements PlaylistTrackDAO{
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.PlaylistTrackDAO#insert(com.mixzing.musicobject.PlaylistTrack)
	 */
	public long insert(PlaylistTrack gss) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("INPLTRK: " + gss.getPlid() + " " + gss.getLsid());
		String sql = "INSERT OR IGNORE INTO playlist_track " + 
		"(lsid, plid) " +
		"VALUES (?,?)";
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setLong(1, gss.getLsid());
			ps.setLong(2, gss.getPlid());
			ps.execute();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e,sql + ":" + gss.getLsid() + ":" + gss.getPlid());			
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {

				}
			}
		}
		return 0;
	}

	private String tableName = "playlist_track";

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.PlaylistTrackDAO#readAll()
	 */
	public ArrayList<PlaylistTrack> readAll() {
		ArrayList<PlaylistTrack> list = new ArrayList<PlaylistTrack>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				PlaylistTrack play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}

		return list;
	}


	@Override
	protected PlaylistTrack createInstance(ResultSet rs) {
		// TODO Auto-generated method stub
		return new PlaylistTrackImpl(rs);
	}		
	@Override
	protected String tableName() {
		return tableName;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.PlaylistTrackDAO#findbyPlid(long)
	 */
	public ArrayList<PlaylistTrack> findbyPlid(long plid) {
		String sql = "SELECT * FROM " + tableName() + " WHERE plid = ? ";
		return getCollection(sql, plid);
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.PlaylistTrackDAO#findbyPlid(long)
	 */
	public ArrayList<PlaylistTrack> findbyLsid(long lsid) {
		String sql = "SELECT * FROM " + tableName() + " WHERE lsid = ? ";
		return getCollection(sql, lsid);
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.PlaylistTrackDAO#delete(com.mixzing.musicobject.PlaylistTrack)
	 */
	public void delete(PlaylistTrack pltrk) {
		String sql = " DELETE FROM " + tableName() + " WHERE plid = ? AND lsid = ? ";
		try {
			DatabaseManager.executeUpdateLongParams(sql, pltrk.getPlid(), pltrk.getLsid());
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}

	}


	public void deleteAllPlaylistTracks(long plid) {
		try {
			String sql = " DELETE FROM " + tableName() + " WHERE plid = ?";
			DatabaseManager.executeUpdateLongParams(sql, plid);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);	
		}
	}		

}
