package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.EnumRatingSource;
import com.mixzing.musicobject.EnumRatingValue;
import com.mixzing.musicobject.RatingSong;
import com.mixzing.musicobject.dao.RatingSongDAO;
import com.mixzing.musicobject.impl.RatingSongImpl;

public class RatingSongDAOImpl extends BaseDAO<RatingSong> implements RatingSongDAO{

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RatingSongDAO#insert(com.mixzing.musicobject.RatingSong)
	 */
	public long insert(RatingSong rat) {
		String sql = "INSERT INTO rating " +
		"(globalsong_id, plid, rating_value, rating_source, timerated, is_deleted)" + 
		"VALUES (?,?,?,?,?,?)";
		String identityQuery = "VALUES IDENTITY_VAL_LOCAL()";
		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setLong(1, rat.getGlobalSongId());
			ps.setLong(2, rat.getPlid());
			ps.setInt(3, rat.getRatingValue().getIntValue());
			ps.setInt(4, rat.getRatingSource().getIntValue());
			ps.setTimestamp(5, new Timestamp(rat.getTimeRated()));
			ps.setInt(6, rat.isDeleted() ? 1 : 0);
						
			
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
	
	
	private String tableName = "rating";
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RatingSongDAO#readAll()
	 */
	public ArrayList<RatingSong> readAll() {
		ArrayList<RatingSong> list = new ArrayList<RatingSong>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				RatingSong play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}
		
		return list;
	}
	
	public static void main(String[] args) throws Exception{
		System.setProperty("derby.system.home", 
		"C:/Documents and Settings/sandeep/My Documents/My Music/MixMoxie");

		DatabaseManager.initDatabase("MixDB", "mixzing", "mixzing", false);
		
		RatingSongDAO dao = new RatingSongDAOImpl();
		
		DatabaseManager.beginTransaction();
		
		RatingSong reco = new RatingSongImpl();

		long time = System.currentTimeMillis();

		reco.setGlobalSongId(2);
		reco.setPlid(3);
		reco.setRatingSource(EnumRatingSource.INFERRED_ADD);
		reco.setRatingValue(EnumRatingValue.HATE);
		reco.setTimeRated(time);
		
		dao.insert(reco);
		
		DatabaseManager.commitTransaction();
		
		ArrayList<RatingSong> list = dao.readAll();
		for(RatingSong pl : list) {
			System.out.println(pl);
		}
		
	}

	@Override
	protected RatingSong createInstance(ResultSet rs) {
		// TODO Auto-generated method stub
		return new RatingSongImpl(rs);
	}		
	@Override
	protected String tableName() {
		return tableName;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RatingSongDAO#findCurrentRatings(long)
	 */
	public ArrayList<RatingSong> findCurrentRatings(long plid) {
		String sql = "SELECT * from " + tableName + " WHERE is_deleted = 0 AND plid = ?";
		return getCollection(sql, plid);
	}		

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RatingSongDAO#is_already_rated(long, long)
	 */
	public RatingSong is_already_rated(long plid, long globalsong_id) {
		String sql = "SELECT * FROM " + tableName() +  " WHERE plid = ? AND globalsong_id = ? AND is_deleted = 0";
		ArrayList<RatingSong> list = getCollection(sql,plid, globalsong_id);
		if(list.isEmpty()) {
			return null;
		}
		if(list.size() == 1) {
			return list.get(0);
		}
		
		RatingSong lastRated = null;
		long timeLastrated = Long.MIN_VALUE;
		for(RatingSong rs : list) {
			long time = rs.getTimeRated();
			if(time >= timeLastrated) {
				timeLastrated = time;
				lastRated = rs;
			}
		}
		return lastRated;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RatingSongDAO#deleteRatingsForPlaylist(long)
	 */
	public void deleteRatingsForPlaylist(long plid) {
		String sql = "DELETE FROM " + tableName() + " WHERE plid = ? ";
		try {
			DatabaseManager.executeUpdateLongParams(sql, plid);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public void delete(RatingSong rat) {
		String sql = "DELETE FROM " + tableName() + " WHERE id = ? " ;
		try {
			DatabaseManager.executeUpdateLongParams(sql, rat.getId());
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
		
	}
	
}
