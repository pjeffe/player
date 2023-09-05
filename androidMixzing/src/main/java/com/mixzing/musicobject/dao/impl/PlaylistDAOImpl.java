package com.mixzing.musicobject.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.Playlist;
import com.mixzing.musicobject.dao.PlaylistDAO;
import com.mixzing.musicobject.impl.PlaylistImpl;

public class PlaylistDAOImpl extends BaseDAO<Playlist> implements PlaylistDAO{

	private String tableName = "playlist";

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.PlaylistDAO#insert(com.mixzing.musicobject.Playlist)
	 */
	public long insert(Playlist play) {
		String sql = "INSERT INTO playlist " +
		"(name, source_specific_id, pl_type, is_deleted)" + 
		" VALUES (?,?,?,?)";
		String identityQuery = "VALUES IDENTITY_VAL_LOCAL()";
		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setString(1, play.getName());
			ps.setString(2, play.getSourceSpecificId());
			ps.setString(3, play.getPlaylistType().toString());
			ps.setInt(4, play.isDeleted() ? 1 : 0);
			
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
		play.setId(idVal);
		return idVal;	
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.PlaylistDAO#readAll()
	 */
	public ArrayList<Playlist> readAll() {
		ArrayList<Playlist> list = new ArrayList<Playlist>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				Playlist play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}

		return list;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.PlaylistDAO#findCurrentPlaylists()
	 */
	public ArrayList<Playlist> findCurrentPlaylists() {
		String sql = "SELECT * FROM " + tableName() + " WHERE is_deleted = 0";
		return getCollection(sql);
	}


	@Override
	protected Playlist createInstance(ResultSet rs) {
		// TODO Auto-generated method stub
		return new PlaylistImpl(rs);
	}
	@Override
	protected String tableName() {
		return tableName;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.PlaylistDAO#delete(com.mixzing.musicobject.Playlist)
	 */
	public void delete(Playlist play) {
		String sql = "UPDATE " + tableName() + " SET is_deleted = 1 WHERE id = ? ";	
		try {
			DatabaseManager.executeUpdateLongParams(sql, play.getId());
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public void updateName(Playlist play) {
		String sql = "UPDATE " + tableName() + " SET name = ? WHERE id = ? ";	
		PreparedStatement ps = null;
		try {
			Connection conn = DatabaseManager.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, play.getName());
			ps.setLong(2, play.getId());
			ps.executeUpdate();
			DatabaseManager.releaseConnection(conn);
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
	}

	public void updateType(Playlist play) {
		String sql = "UPDATE " + tableName() + " SET pl_type = ? WHERE id = ? ";
		PreparedStatement ps = null;
		try {
			Connection conn = DatabaseManager.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, play.getPlaylistType().toString());
			ps.setLong(2, play.getId());
			ps.executeUpdate();
			DatabaseManager.releaseConnection(conn);
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
	}
	
	public void updateFromMagicToSource(Playlist play) {
		String sql = "UPDATE " + tableName() + " SET name = ?, source_specific_id =?,  pl_type =? WHERE id = ? ";	
		PreparedStatement ps = null;
		try {
			Connection conn = DatabaseManager.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, play.getName());
			ps.setString(2, play.getSourceSpecificId());
			ps.setString(3, play.getPlaylistType().toString());
			ps.setLong(4, play.getId());
			ps.executeUpdate();
			DatabaseManager.releaseConnection(conn);
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
		
	}		

}
