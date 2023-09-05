package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.Recommendation;
import com.mixzing.musicobject.dao.RecommendationDAO;
import com.mixzing.musicobject.dto.RecommendationDTO.RecoSource;
import com.mixzing.musicobject.impl.RecommendationImpl;

public class RecommendationDAOImpl extends BaseDAO<Recommendation> implements RecommendationDAO{
	
	private String tableName = "recommendation";
	
	/*
	 * CREATE
	 */
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#insert(com.mixzing.musicobject.Recommendation)
	 */
	public long insert(Recommendation reco)  {
		String sql = "INSERT INTO " + tableName + " " +
		"(plid, score, artsongid, artsongartist, " +
		"artsongtitle, timereceived, reco_source, is_rated ) " +
		"VALUES (?,?,?,?,?,?,?,?)";
		String identityQuery = "values IDENTITY_VAL_LOCAL()";
		long idVal = Long.MIN_VALUE;

		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setLong(1, reco.getPlid());
			ps.setFloat(2, reco.getScore());
			ps.setLong(3, reco.getArtsongId());
			ps.setString(4, reco.getArtsongArtist());
			ps.setString(5, reco.getArtsongTitle());
			ps.setTimestamp(6, new Timestamp(reco.getTimeReceived()));
			int src;
			if(reco.getRecoSource() == RecoSource.INFERRED ) {
				src = 1;
			} else {
				src = 0;
			}
			ps.setInt(7, src);
			if(reco.isRated()) {
				ps.setInt(8, 1);
			} else {
				ps.setInt(8, 0);
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


		reco.setId(idVal);
		return idVal;
	}

	
	/*
	 * READ
	 */
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#findAllRecommendationForPlaylist(long)
	 */
	public ArrayList<Recommendation> findAllRecommendationForPlaylist(long plid) {
		String sql = "SELECT * FROM recommendation WHERE plid = ?";
		return getCollection(sql,plid);
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#findCurrentRecommendations(long)
	 */
	public ArrayList<Recommendation> findCurrentRecommendations(long plid)  {
		String sql = "SELECT * FROM recommendation WHERE plid = ? AND is_rated = 0 AND is_deleted = 0";
		return getCollection(sql,plid);
	}
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#findCurrentRecommendations()
	 */
	public ArrayList<Recommendation> findCurrentRecommendations()  {
		String sql = "SELECT * FROM recommendation WHERE is_rated = 0 and is_deleted = 0";
		return getCollection(sql);
	}
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#findRatedRecoByPlidAsid(long, long)
	 */
	public ArrayList<Recommendation> findRatedRecoByPlidAsid(long plid, long artsongId)  {
		String sql = "SELECT * FROM recommendation WHERE is_rated=1 AND plid = ? AND artsongid =?";
		return getCollection(sql, plid, artsongId);
	}
	
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#readAll()
	 */
	public ArrayList<Recommendation> readAll() {
		String sql = "SELECT * from " + tableName;
		return getCollection(sql);
	}
	
	
	/*
	 * UPDATE
	 */

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#invalidatePreviousRecos(long, long)
	 */
	public int invalidatePreviousRecos(long plid, long time) {
		String sql = "UPDATE recommendation SET is_deleted = 1 WHERE timereceived <> ? AND plid = ?";
		try {
			DatabaseManager.executeUpdateDateLongParams(sql, time, plid);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}	
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#invalidatePreviousRecos(com.mixzing.musicobject.Recommendation)
	 */
	public int invalidatePreviousRecos(Recommendation reco)  {
		invalidatePreviousRecos(reco.getTimeReceived(),reco.getPlid());
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#updateRecoAsRated(long)
	 */
	public int updateRecoAsRated(long recoId) {
		String sql = "UPDATE recommendation SET is_rated = 1 WHERE id = ?";
		try {
			DatabaseManager.executeUpdateLongParams(sql, recoId);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#softDelete(com.mixzing.musicobject.Recommendation)
	 */
	public int softDelete(Recommendation reco) {
		return softDelete( reco.getId());			
	}
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#softDelete(long)
	 */
	public int softDelete(long recoId) {
		String sql = "UPDATE recommendation SET is_deleted = 1 WHERE id = ?";
		try {
			DatabaseManager.executeUpdateLongParams(sql, recoId);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}	
		return 0;
	}
	
	/*
	 * DELETE
	 */
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#hardDelete(com.mixzing.musicobject.Recommendation)
	 */
	public int hardDelete(Recommendation reco) {
		return hardDelete( reco.getId());			
	}
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecommendationDAO#hardDelete(long)
	 */
	public int hardDelete(long recoId) {
		String sql = "DELETE FROM recommendation WHERE id = ?";
		try {
			DatabaseManager.executeUpdateLongParams(sql, recoId);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}	
		return 0;
	}



	@Override
	protected Recommendation createInstance(ResultSet rs) {
		return new RecommendationImpl(rs);
	}		
	
	@Override
	protected String tableName() {
		return tableName;
	}		

	
	public static void main(String[] args) throws Exception{
		System.setProperty("derby.system.home", 
		"C:/Documents and Settings/sandeep/My Documents/My Music/MixMoxie");

		DatabaseManager.initDatabase("MixDB", "mixzing", "mixzing", false);
		
		RecommendationDAO dao = new RecommendationDAOImpl();
		
		DatabaseManager.beginTransaction();
		
		Recommendation reco = new RecommendationImpl();

		long time = System.currentTimeMillis();
		reco.setArtsongArtist("Artist" + time);
		reco.setArtsongId(time);
		reco.setArtsongTitle("Title" + time);
		reco.setPlid(2);
		reco.setRated(true);
		reco.setDeleted(false);
		reco.setRecoSource(RecoSource.SERVER);
		reco.setScore(time);
		reco.setTimeReceived(time);
		
		dao.insert(reco);
		
		DatabaseManager.commitTransaction();
		
		ArrayList<Recommendation> list = dao.readAll();
		for(Recommendation pl : list) {
			System.out.println(pl);
		}
		
	}


	
}
