package com.mixzing.musicobject.dao.impl;

import java.sql.ResultSet;

import com.mixzing.derby.DatabaseManager;
import com.mixzing.musicobject.EnumLibraryStatus;
import com.mixzing.musicobject.Library;
import com.mixzing.musicobject.dao.LibraryDAO;
import com.mixzing.musicobject.impl.LibraryImpl;

public class LibraryDAOImpl extends BaseDAO<Library> implements LibraryDAO  {

	
	@Override
	protected Library createInstance(ResultSet rs) {
		return new LibraryImpl(rs);
	}

	@Override
	protected String tableName() {
		return "library";
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.LibraryDAO#readLibrary()
	 */
	public Library readLibrary() {

			String sql = "SELECT * FROM " + tableName() + " WHERE id = 1 ";
			return this.readOne(sql);
		
	}
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.LibraryDAO#updateLibraryId(java.lang.String)
	 */
	public void updateLibraryId(String server_id) {
		String sql = "UPDATE " + tableName() + " SET server_id = '" + server_id + "' WHERE id = 1";
		try {
			DatabaseManager.executeUpdate(sql);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.LibraryDAO#updateLibraryId(java.lang.String)
	 */
	public void updateResolvedCount(int count) {
		String sql = "UPDATE " + tableName() + " SET  resolved_song_count = ? WHERE id = 1";
		try {
			DatabaseManager.executeUpdateLongParams(sql,(long)count);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void updateLibraryStatus(EnumLibraryStatus status) {
		String sql = "UPDATE " + tableName() + " SET  library_status = '" + 
				status.toString() + "' WHERE id = 1";
		try {
			DatabaseManager.executeUpdate(sql);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
