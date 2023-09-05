package com.mixzing.derby;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.mixmoxie.util.StackTrace;
import com.mixzing.log.Logger;

public class AndroidPreparedStatement implements PreparedStatement {
	protected String[] args = new String[40];
	protected SQLiteStatement compiled;
	protected SQLiteDatabase adb;
	protected boolean isInsert;
	protected boolean isQuery;
	protected int maxArg = 0;
	protected String sql;
	protected boolean rawQueryOnly;
	protected static HashMap<String, SQLiteStatement> prepStatementCache = new HashMap<String, SQLiteStatement>();
	protected static HashMap<String, Long> prepStatementCountCache = new HashMap<String, Long>();

	protected static int cacheHits = 0;

	protected static final boolean REUSE_STATEMENTS = false;

	protected String allocatedStack;
	boolean isClosed;
	private static Logger lgr = Logger.getRootLogger();

	protected void setMax(int argnum) {
		if(argnum > maxArg) {
			maxArg = argnum;
		}
	}

	protected void bindDouble(int i, double d) {
		if(!rawQueryOnly)
			compiled.bindDouble(i, d);
		args[i-1] = Double.toString(d);
		setMax(i);
	}

	private void bindString(int i, String x) {
		if(!rawQueryOnly)
			compiled.bindString(i, x);
		args[i-1] = x;
		setMax(i);		
	}

	private void bindLong(int i, long x) {
		if(!rawQueryOnly)
			compiled.bindLong(i, x);
		args[i-1] = Long.toString(x);
		setMax(i);		
	}

	public AndroidPreparedStatement(String sql, SQLiteDatabase db) throws SQLException{
		this(sql,db,false);
	}

	public AndroidPreparedStatement(String sql, SQLiteDatabase db, boolean queryOnly) throws SQLException{
		try {
			rawQueryOnly = queryOnly;
			String trimmed = sql.toLowerCase(Locale.US).trim();
			if(trimmed.startsWith("insert")) {
				isInsert = true;
			} else 	if(trimmed.startsWith("select")) {
				isQuery = true;;
			}
			adb = db;
			if(!rawQueryOnly) {
				if(REUSE_STATEMENTS) {
					if(prepStatementCache.get(sql) == null) {
						compiled = adb.compileStatement(sql);
						synchronized(AndroidPreparedStatement.class) {
							prepStatementCache.put(sql,compiled);		
						}
						if(Logger.IS_DEBUG_ENABLED) {
							lgr.debug("Number of cached statements: " + prepStatementCache.size());
						}
					} else {
						if(++cacheHits % 500 == 0) {
							if(Logger.IS_DEBUG_ENABLED) {
								lgr.debug("Number of cache hits: " + cacheHits);
								if(prepStatementCountCache.size() > 2) {
									for(String key : prepStatementCountCache.keySet()) {
										lgr.debug(prepStatementCountCache.get(key) + " : " + key);
									}
								}
							}
						}					
					}

					compiled = prepStatementCache.get(sql);
				} else {
					compiled = adb.compileStatement(sql);
				}
			}

			if(REUSE_STATEMENTS) {
				Long l = prepStatementCountCache.get(sql);
				if(l == null) {
					l = new Long(0);
				}
				l = l + 1;

				prepStatementCountCache.put(sql, l);
			}

			this.sql = sql;
		} catch (android.database.SQLException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
		allocatedStack = StackTrace.getStackTrace(false);
	}

	public void addBatch() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void clearParameters() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public boolean execute() throws SQLException {
		try {
			if(isInsert) {
				compiled.executeInsert();
			} else {
				compiled.execute();
			}
			return true;
		} catch (android.database.SQLException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public ResultSet executeQuery() throws SQLException {
		try {
			String[] selectionArgs = maxArg == 0 ? null : new String[maxArg];
			for(int i=0;i<maxArg;i++) {
				selectionArgs[i] = args[i];
			}
			Cursor c = adb.rawQuery(sql, selectionArgs);
			return new AndroidResultSet(c);
		} catch (android.database.SQLException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public long executeInsert() throws SQLException {
		try {
			if (!isInsert && Logger.shouldSelectivelyLog()) {
				lgr.error("AndroidPreparedStatement.executeInsert: sql = <" + sql + ">, allocated by:\n" +
					allocatedStack + "called by:\n" + StackTrace.getStackTrace(false));
			}
			return compiled.executeInsert();
		} catch (android.database.SQLException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int executeUpdate() throws SQLException {
		try {
			if(isInsert) {
				long i = compiled.executeInsert();
				/*
				 * XXX: TODO create a new method to return long
				 */
				return (int) i;

			} else {
				compiled.execute();
				return 0;
			}
		} catch (android.database.SQLException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setArray(int i, Array x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setBlob(int i, Blob x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		try {
			if(x) {
				this.bindLong(parameterIndex, 1);
			} else {
				this.bindLong(parameterIndex, 0);
			}
		} catch (android.database.SQLException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		setString(parameterIndex, new String(x));
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setClob(int i, Clob x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		bindDouble(parameterIndex, x);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		bindDouble(parameterIndex, x);
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		bindLong(parameterIndex, x);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		bindLong(parameterIndex, x);
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setRef(int i, Ref x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		bindLong(parameterIndex, x);
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		bindString(parameterIndex, x);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		bindLong(parameterIndex, x.getTime());
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void addBatch(String sql) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void cancel() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void clearBatch() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void clearWarnings() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void close() throws SQLException {
		try {
			isClosed = true;
			if(!rawQueryOnly) {
				if(!REUSE_STATEMENTS) {
					compiled.close();
				}
			}
		} catch (android.database.SQLException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean execute(String sql) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public boolean execute(String sql, String[] columnNames) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int[] executeBatch() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int executeUpdate(String sql) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public Connection getConnection() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int getFetchDirection() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int getFetchSize() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int getMaxFieldSize() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int getMaxRows() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public boolean getMoreResults() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public boolean getMoreResults(int current) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int getQueryTimeout() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public ResultSet getResultSet() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int getResultSetConcurrency() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int getResultSetHoldability() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int getResultSetType() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public int getUpdateCount() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public SQLWarning getWarnings() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setCursorName(String name) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setEscapeProcessing(boolean enable) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setFetchDirection(int direction) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setFetchSize(int rows) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setMaxFieldSize(int max) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setMaxRows(int max) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setQueryTimeout(int seconds) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	protected void finalize() throws Throwable {
		try {
			if(!isClosed) {
				lgr.error("AndroidPreparedStatement.finalize: not closed, allocated by:\n" + allocatedStack);
				close();        // close open files
			}

		} finally {
			super.finalize();
		}
	}

	@Override
	public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAsciiStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBinaryStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlob(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlob(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNString(int parameterIndex, String theString) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRowId(int parameterIndex, RowId theRowId) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPoolable(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
