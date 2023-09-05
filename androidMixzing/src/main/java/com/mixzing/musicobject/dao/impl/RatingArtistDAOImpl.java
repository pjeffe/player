package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.RatingArtist;
import com.mixzing.musicobject.dao.RatingArtistDAO;
import com.mixzing.musicobject.impl.RatingArtistImpl;

public class RatingArtistDAOImpl extends BaseDAO<RatingArtist> implements RatingArtistDAO{
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RatingArtistDAO#insert(com.mixzing.musicobject.RatingArtist)
	 */
	public long insert(RatingArtist rat) {
		String sql_plid = "INSERT INTO hate_ignore_artist " +
		"(artist_name, plid)" + 
		"VALUES (?,?)";

		String sql_noplid = "INSERT INTO hate_ignore_artist " +
		"(artist_name)" + 
		"VALUES (?)";

		String sql;
		if(rat.getPlid() > 0) {
			sql = sql_plid;
		} else {
			sql = sql_noplid;
		}
		
		String identityQuery = "VALUES IDENTITY_VAL_LOCAL()";
		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setString(1, rat.getArtistName());
			if(rat.getPlid() > 0) {
				ps.setLong(2, rat.getPlid());
			} 
			
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
		rat.setId(idVal);
		return idVal;	
	}
	
	
	private String tableName = "hate_ignore_artist";
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RatingArtistDAO#readAll()
	 */
	public ArrayList<RatingArtist> readAll() {
		ArrayList<RatingArtist> list = new ArrayList<RatingArtist>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				RatingArtist play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}
		
		return list;
	}
	


	@Override
	protected RatingArtist createInstance(ResultSet rs) {
		// TODO Auto-generated method stub
		return new RatingArtistImpl(rs);
	}
	
	@Override
	protected String tableName() {
		return tableName;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RatingArtistDAO#findCurrentRatings(long)
	 */
	public ArrayList<RatingArtist> findCurrentRatings(long plid) {
		String sql = "SELECT * FROM " + tableName() + " WHERE plid = ?";
		return getCollection(sql, plid);
	}	
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RatingArtistDAO#findCurrentHatedRatings()
	 */
	public ArrayList<RatingArtist> findCurrentHatedRatings() {
		String sql = "SELECT * FROM " + tableName() + " WHERE plid IS NULL";
		return getCollection(sql);
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RatingArtistDAO#deleteRatingsForPlaylist(long)
	 */
	public void deleteRatingsForPlaylist(long plid) {
		String sql = "DELETE FROM " + tableName() + " WHERE plid = ? ";
		try {
			DatabaseManager.executeUpdateLongParams(sql, plid);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}	
	

}
