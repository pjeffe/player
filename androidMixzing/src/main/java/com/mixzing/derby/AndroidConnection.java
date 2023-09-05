package com.mixzing.derby;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import android.database.sqlite.SQLiteDatabase;

import com.mixmoxie.util.StackTrace;
import com.mixzing.log.Logger;

public class AndroidConnection implements Connection {

	protected SQLiteDatabase adb;
	protected boolean isOpen;
	protected String stackTrack = "not filled";
	protected static Logger lgr = Logger.getRootLogger();

	public SQLiteDatabase getSqlite() {
		return adb;
	}
	public AndroidConnection(SQLiteDatabase db) {
		isOpen = true;
		adb = db;
		//this.stackTrack = StackTrace.getStackTrace();
	}
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public synchronized void close() throws SQLException {
		if(this.isOpen) {
			if(this.adb.isOpen()) {
				this.adb.close();
			}
			this.isOpen = false;
		}
	}

	public void commit() throws SQLException {
		// TODO Auto-generated method stub

	}

	public Statement createStatement() throws SQLException {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace(StackTrace.getStackTrace());
		throw new RuntimeException("Not supported");
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
	throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
	throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public boolean getAutoCommit() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public String getCatalog() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public int getTransactionIsolation() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public boolean isReadOnly() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public String nativeSQL(String sql) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
	throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {		
		return new AndroidPreparedStatement(sql,adb);
	}

	/*
	 * 
	 * XXX: Hijacked this API for Android to not create a "Sqlite Compiled Statement" 
	 * 
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
	throws SQLException {
		return new AndroidPreparedStatement(sql,adb,true);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
	throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames)
	throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
	throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public void rollback() throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public void setCatalog(String catalog) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public void setHoldability(int holdability) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTransactionIsolation(int level) throws SQLException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not supported");
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		throw new RuntimeException("Not supported");

	}

	protected void finalize() throws Throwable {
		try {
			if(isOpen) {
				if(Logger.IS_TRACE_ENABLED) {
					lgr.trace("Not closed connection");
					lgr.trace(this.stackTrack);
				}
				close(); 
			}

		} finally {
			super.finalize();
		}
	}
	@Override
	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isValid(int timeout) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
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
