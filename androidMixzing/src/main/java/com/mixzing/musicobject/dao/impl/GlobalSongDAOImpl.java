package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.DatabaseVersions;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.dao.GlobalSongDAO;
import com.mixzing.musicobject.dao.GlobalSongSourceDAO;
import com.mixzing.musicobject.impl.GlobalSongImpl;
import com.mixzing.musicobject.impl.GlobalSongWeakRefWrapper;

public class GlobalSongDAOImpl extends BaseDAO<GlobalSong> implements GlobalSongDAO{

	private GlobalSongSourceDAO gssDAO;

	public GlobalSongDAOImpl(GlobalSongSourceDAO gssDAO) {
		this.gssDAO = gssDAO;
		GlobalSongWeakRefWrapper.setDAOs(this, gssDAO);
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.MusicObjectDAO#findById(long)
	 */
	public GlobalSong findById(long id) {
		return findUnwrappedById(id);
	}
	
	public GlobalSong findUnwrappedById(long gsid) {

		String sql;

		if(DatabaseVersions.CURRENT_DATABASE_VERSION_ID == DatabaseVersions.DB_VERSION_ONE) {
			sql = "SELECT * FROM " + tableName() + " WHERE id = ? ";
		} else {
			sql = "SELECT gs.id as id, " +
			"gs.gsid AS gsid, " + 
			"ar.artist AS artist, " +
			"gs.title AS title, " +
			"al.album AS album, " +
			"gs.genre AS genre, " +
			"gs.releaseyear AS releaseyear, " +
			"gs.duration AS duration, " +
			"gs.trackNumber AS trackNumber, " +
			"gs.time_updated AS time_updated " + 
			"FROM global_song gs, album al, artist ar " + 
			"WHERE gs.artist_id = ar.id AND gs.album_id = al.id AND gs.id = ?";
		}


		try {
			ResultSet rs = DatabaseManager.executeQueryLongParams(DatabaseManager.getConnection(), 
					sql,
					gsid);
			while(rs.next()) {
				GlobalSong play = new GlobalSongImpl(rs);
				rs.close();
				return play;
			}   
			rs.close();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}

		return null;
	}

	public GlobalSong createInstance(GlobalSongSpec gss) {
		return new GlobalSongWeakRefWrapper(gss);
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.GlobalSongDAO#insert(com.mixzing.musicobject.GlobalSong)
	 */
	public long insert(GlobalSong gss) {
		String sql;

		if(DatabaseVersions.CURRENT_DATABASE_VERSION_ID == DatabaseVersions.DB_VERSION_ONE) {
			sql = "INSERT INTO global_song " +
			"(gsid, artist, title, album, " +
			"genre, releaseyear, duration, " +
			"trackNumber, time_updated) " +
			"VALUES (?,?,?,?,?,?,?,?,?)";

		} else {
			sql = "INSERT INTO global_song " +
			"(gsid, artist_id, title, album_id, " +
			"genre, releaseyear, duration, " +
			"trackNumber, time_updated) " +
			"VALUES (?,?,?,?,?,?,?,?,?)";	
		}


		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			long artist_id = 0;
			long album_id = 0;

			if(DatabaseVersions.CURRENT_DATABASE_VERSION_ID > DatabaseVersions.DB_VERSION_ONE) {
				artist_id = getOrCreateArtist(gss.getArtist());
				album_id = getOrCreateAlbum(gss.getAlbum(),artist_id);				
			}

			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setLong(1, gss.getGsid());

			if(DatabaseVersions.CURRENT_DATABASE_VERSION_ID == DatabaseVersions.DB_VERSION_ONE)
				ps.setString(2, gss.getArtist());
			else
				ps.setLong(2, artist_id);

			ps.setString(3, gss.getTitle());

			if(DatabaseVersions.CURRENT_DATABASE_VERSION_ID == DatabaseVersions.DB_VERSION_ONE) {
				ps.setString(4, gss.getAlbum());
			} else {
				ps.setLong(4, album_id);
			}
			ps.setString(5, gss.getGenre());
			ps.setInt(6, gss.getReleaseYear()); // XXX: TODO Change data type to String
			ps.setFloat(7, gss.getDuration());
			ps.setString(8, gss.getTrackNumber());
			ps.setTimestamp(9, new Timestamp(gss.getTimeUpdated()));


			AndroidPreparedStatement aps = (AndroidPreparedStatement) ps;
			idVal = aps.executeInsert();


		} catch (SQLException e) {
			String err = sql + "," + gss.getGsid() + "," + gss.getArtist() + "," + gss.getTitle() + ",";
			err += gss.getTitle() + "," + gss.getGenre() + "," + gss.getReleaseYear() + ",";
			err += gss.getDuration() + "," + gss.getTrackNumber() + "," + gss.getTimeUpdated();
			throw new UncheckedSQLException(e,err);		
		} catch (Exception e1) {
			String err = sql + "," + gss.getGsid() + "," + gss.getArtist() + "," + gss.getTitle() + ",";
			err += gss.getTitle() + "," + gss.getGenre() + "," + gss.getReleaseYear() + ",";
			err += gss.getDuration() + "," + gss.getTrackNumber() + "," + gss.getTimeUpdated();
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
		gss.setId(idVal);
		return idVal;
	}


	protected long getOrCreateArtist(String artist) {
		String read = "SELECT id FROM artist WHERE artist = ?";
		String insert = "INSERT INTO artist (artist) VALUES (?)";
		return getOrCreateArtist(read,insert,artist);
	}

	protected long getOrCreateAlbum(String album, long artist_id) {
		String read = "SELECT id FROM album WHERE album = ? AND artist_id = ?";
		String insert = "INSERT INTO album (album,artist_id) VALUES (?,?)";
		return getOrCreateAlbum(read,insert,album,artist_id);
	}

	protected long getExistingArtistFromDB(String sql, String value) {
		long id = Long.MIN_VALUE;
		try {
			ResultSet rs = DatabaseManager.executeQueryStringParams(DatabaseManager.getConnection(), 
					sql,
					value);
			while(rs.next()) {
				id = rs.getLong("id");
				rs.close();
				return id;
			}   
			rs.close();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
		return id;
	}

	protected long insertNewArtistInDB(String sql, String value) {
		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setString(1, value);			
			AndroidPreparedStatement aps = (AndroidPreparedStatement) ps;
			idVal = aps.executeInsert();

		} catch (SQLException e) {
			String err = sql + "," + value;
			throw new UncheckedSQLException(e,err);		
		} catch (Exception e1) {
			String err = sql + "," + value;
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
		return idVal;
	}


	/*
	 * XXX: Fix the query to take addl param
	 */
	protected long getExistingAlbumFromDB(String sql, String value, long artist_id) {
		long id = Long.MIN_VALUE;
		try {
			ResultSet rs = DatabaseManager.executeQueryStringLongParam(DatabaseManager.getConnection(), 
					sql,
					value,
					artist_id);
			while(rs.next()) {
				id = rs.getLong("id");
				rs.close();
				return id;
			}   
			rs.close();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
		return id;
	}

	protected long insertNewAlbumInDB(String sql, String value, long artist_id) {
		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setString(1, value);
			ps.setLong(2, artist_id);
			AndroidPreparedStatement aps = (AndroidPreparedStatement) ps;
			idVal = aps.executeInsert();

		} catch (SQLException e) {
			String err = sql + "," + value;
			throw new UncheckedSQLException(e,err);		
		} catch (Exception e1) {
			String err = sql + "," + value;
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
		return idVal;
	}

	
	protected long getOrCreateAlbum(String selSql, String insSql, String val, long artist_id) {
		long id = getExistingAlbumFromDB(selSql, val,artist_id);
		if(id == Long.MIN_VALUE) {
			id = insertNewAlbumInDB(insSql, val,artist_id);
		}
		return id;
	}

	protected long getOrCreateArtist(String selSql, String insSql, String val) {
		long id = getExistingArtistFromDB(selSql, val);
		if(id == Long.MIN_VALUE) {
			id = insertNewArtistInDB(insSql, val);
		}
		return id;
	}

	private String tableName = "global_song";

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.GlobalSongDAO#readAll()
	 */
	public ArrayList<GlobalSong> readAll() {
		ArrayList<GlobalSong> list = new ArrayList<GlobalSong>();
		String sql = "SELECT * from " + tableName;
		if(DatabaseVersions.CURRENT_DATABASE_VERSION_ID == DatabaseVersions.DB_VERSION_ONE) {
			sql = "SELECT * FROM " + tableName() ;
		} else {
			sql = "SELECT gs.id as id, " +
			"gs.gsid AS gsid, " + 
			"ar.artist AS artist, " +
			"gs.title AS title, " +
			"al.album AS album, " +
			"gs.genre AS genre, " +
			"gs.releaseyear AS releaseyear, " +
			"gs.duration AS duration, " +
			"gs.trackNumber AS trackNumber, " +
			"gs.time_updated AS time_updated " + 
			"FROM global_song gs, album al, artist ar " + 
			"WHERE gs.artist_id = ar.id AND gs.album_id = al.id";
		}
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				GlobalSong play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}

		return list;
	}



	@Override
	protected GlobalSong createInstance(ResultSet rs) {
		return new GlobalSongWeakRefWrapper(rs);
	}

	@Override
	protected String tableName() {
		return tableName;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.GlobalSongDAO#findByServerGsid(long)
	 */
	public GlobalSong findByServerGsid(long gsid) {
		
		String sql;

		if(DatabaseVersions.CURRENT_DATABASE_VERSION_ID == DatabaseVersions.DB_VERSION_ONE) {
			sql = "SELECT * FROM " + tableName() + " WHERE gsid = ?";
		} else {
			sql = "SELECT gs.id as id, " +
			"gs.gsid AS gsid, " + 
			"ar.artist AS artist, " +
			"gs.title AS title, " +
			"al.album AS album, " +
			"gs.genre AS genre, " +
			"gs.releaseyear AS releaseyear, " +
			"gs.duration AS duration, " +
			"gs.trackNumber AS trackNumber, " +
			"gs.time_updated AS time_updated " + 
			"FROM global_song gs, album al, artist ar " + 
			"WHERE gs.artist_id = ar.id AND gs.album_id = al.id AND gs.gsid = ?";
		}
		
		return readOne(sql, gsid);
	}

	private void updateGsidUNUSED(long id, long gsid) {
		String sql = "UPDATE " + tableName() + " SET gsid = ? WHERE id = ?";
		try {
			DatabaseManager.executeUpdateLongParams(sql, gsid,id);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public void updateGsidByLsid(long oldGsid, long newGsid) {
		String sql = "UPDATE " + tableName() + " SET gsid = ? WHERE gsid = ?";
		try {
			DatabaseManager.executeUpdateLongParams(sql, newGsid,oldGsid);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}	
	}	

	public int tracksWithGsid() {
		int cnt = 0;
		String sql = "SELECT COUNT(*) AS cnt FROM global_song, track WHERE " +  
		"track.globalsong_id = global_song.id and global_song.gsid > 0 and track.is_deleted = 0";
		ResultSet rs = null;
		try {
			rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			if(rs != null && rs.next()) {
				cnt = rs.getInt(1);
			}
			if(rs != null) rs.close();
		} catch (SQLException e) {

		}		
		return cnt;
	}
}
