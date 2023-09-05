package com.mixzing.musicobject.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.dao.MusicObjectDAO;

public abstract class BaseDAO<T> implements MusicObjectDAO<T> {

	protected static Logger lgr = Logger.getRootLogger();

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.MusicObjectDAO#readOne(java.lang.String, java.lang.Long)
	 */
	public T readOne(String sql, Long ...longs ) {

		ArrayList<T> list = getCollection(sql,longs);
		if(list.isEmpty())
			return null;
		if(list.size() > 1) {
			assert(false);
			return null;
		}
		return list.get(0);

	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.MusicObjectDAO#readOne(java.lang.String)
	 */
	public T readOne(String sql) {
		ArrayList<T> list = getCollection(sql);
		if(list.isEmpty())
			return null;
		if(list.size() > 1) {
			assert(false);
			return null;
		}
		return list.get(0);
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.MusicObjectDAO#findById(long)
	 */
	public T findById(long id) {
		String sql = "SELECT * FROM " + tableName() + " WHERE id = ? ";
		return readOne(sql, id);
	}

	protected  ArrayList<T> getCollection(String sql, Long... params) {
		ArrayList<T> list = new ArrayList<T>();

		try {
			ResultSet rs = DatabaseManager.executeQueryLongParams(DatabaseManager.getConnection(), 
					sql,
					params);
			while(rs.next()) {
				T play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
		return list;		
	}

	protected  ArrayList<T> getCollection(String sql)  {
		ArrayList<T> list = new ArrayList<T>();
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				T play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
		return list;		
	}

	protected  ArrayList<T> getCollectionStringParams(String sql,String ... str)  {
		ArrayList<T> list = new ArrayList<T>();
		try {
			ResultSet rs = DatabaseManager.executeQueryStringParams(DatabaseManager.getConnection(), sql,str);
			while(rs.next()) {
				if(Logger.IS_TRACE_ENABLED)
					lgr.trace("getCollectionStringParams: Found match");
				T play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
		return list;		
	}

	protected abstract T createInstance(ResultSet rs);

	protected abstract String tableName();

}
