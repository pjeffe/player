package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.RecoAlternate;
import com.mixzing.musicobject.dao.RecoAlternateDAO;
import com.mixzing.musicobject.impl.RecoAlternateImpl;

public class RecoAlternateDAOImpl extends BaseDAO<RecoAlternate> implements RecoAlternateDAO{
	
	/*
	 * READ
	 */
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecoAlternateDAO#findAllAlternatesForReco(long)
	 */
	public ArrayList<RecoAlternate> findAllAlternatesForReco(long recoid)  {
		String sql = "SELECT * FROM reco_alternates WHERE reco_id = ?";
		return getCollection(sql,recoid);
	}
	
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecoAlternateDAO#insert(com.mixzing.musicobject.RecoAlternate)
	 */
	public long insert(RecoAlternate alt) {
		String sql = "INSERT INTO reco_alternates " +
		"(reco_id, globalsong_id, is_local, rank)" + 
		"VALUES (?,?,?,?)";
		String identityQuery = "VALUES IDENTITY_VAL_LOCAL()";
		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setLong(1, alt.getRecoId());
			ps.setLong(2, alt.getGlobalSongId());
			ps.setInt(3, alt.isLocal() ? 1 : 0);
			ps.setFloat(4, alt.getRank());			
			
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
		alt.setId(idVal);
		return idVal;
	}
	
	private String tableName = "reco_alternates";
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.RecoAlternateDAO#readAll()
	 */
	public ArrayList<RecoAlternate> readAll() {
		ArrayList<RecoAlternate> list = new ArrayList<RecoAlternate>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				RecoAlternate play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}
		
		return list;
	}

	@Override
	protected RecoAlternate createInstance(ResultSet rs) {
		return new RecoAlternateImpl(rs);
	}		
	
	@Override
	protected String tableName() {
		return tableName;
	}		

	
	public static void main(String[] args) throws Exception{
		System.setProperty("derby.system.home", 
		"C:/Documents and Settings/sandeep/My Documents/My Music/MixMoxie");

		DatabaseManager.initDatabase("MixDB", "mixzing", "mixzing", false);
		
		RecoAlternateDAO dao = new RecoAlternateDAOImpl();
		
		DatabaseManager.beginTransaction();
		
		RecoAlternate reco = new RecoAlternateImpl();

		long time = System.currentTimeMillis();
		reco.setGlobalSongId(3);
		reco.setLocal(true);
		reco.setRank(time);
		reco.setRecoId(2);
				
		dao.insert(reco);
		
		DatabaseManager.commitTransaction();
		
		ArrayList<RecoAlternate> list = dao.readAll();
		for(RecoAlternate pl : list) {
			System.out.println(pl);
		}
		
	}


	
}
