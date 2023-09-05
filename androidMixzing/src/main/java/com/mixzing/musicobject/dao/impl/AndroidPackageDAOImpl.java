package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.AndroidPackage;
import com.mixzing.musicobject.dao.AndroidPackageDAO;
import com.mixzing.musicobject.impl.AndroidPackageImpl;

public class AndroidPackageDAOImpl extends BaseDAO<AndroidPackage> implements AndroidPackageDAO{

	private String tableName = "android_package";
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.TrackDAO#insert(com.mixzing.musicobject.Track)
	 */
	public long insert(AndroidPackage pkg) {
		String sql = "INSERT INTO " + tableName +
		" (name, version)" + 
		" VALUES (?,?)";
		String identityQuery = "VALUES IDENTITY_VAL_LOCAL()";
		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setString(1, pkg.getName());
			ps.setLong(2, pkg.getVersion());
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
		pkg.setId(idVal);
		return idVal;	
	}
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.TrackDAO#readAll()
	 */
	public List<AndroidPackage> readAll() {
		ArrayList<AndroidPackage> list = new ArrayList<AndroidPackage>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				AndroidPackage play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}
		
		return list;
	}
	

	@Override
	protected AndroidPackage createInstance(ResultSet rs) {
		// TODO Auto-generated method stub
		return new AndroidPackageImpl(rs);
	}
	
	@Override
	protected String tableName() {
		return tableName;
	}

	public void delete(AndroidPackage t) {
		String sql = "DELETE FROM " + tableName() + " WHERE id = ? ";	
		try {
			DatabaseManager.executeUpdateLongParams( sql, t.getId());
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}		


	public List<AndroidPackage> findAllPackages() {
		return readAll();
	}

	
	public void deleteAll() {
		String sql = "DELETE FROM " + tableName();	
		try {
			DatabaseManager.executeUpdate( sql);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}	
	}
}
