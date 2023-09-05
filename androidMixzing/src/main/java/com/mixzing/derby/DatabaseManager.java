package com.mixzing.derby;
/*

Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.StatFs;

import com.mixzing.android.SdCardHandler;
import com.mixzing.log.Logger;

/**
 * A container for the singleton data source, so we don't have to
 * create a separate one for each class that wants to do JDBC
 */
public class DatabaseManager {

	/*
	 * ERRORS TO FIX ON ANDROID
	 *  
	 * Failed to init database: android.database.sqlite.SQLiteException: unable to open
 database file
Failed to init database: android.database.sqlite.SQLiteFullException: database o
r disk is full
Uncaught exception: android.database.sqlite.SQLiteFullException: database or dis
k is full
android.database.sqlite.SQLiteDiskIOException: disk I/O error
android.database.sqlite.SQLiteDiskIOException: disk I/O error: COMMIT;

android.database.sqlite.SQLiteException: unable to open database file: BEGIN EXC
LUSIVE;
android.database.sqlite.SQLiteFullException: database or disk is full:
android.database.sqlite.SQLiteFullException: database or disk is full: BEGIN EXC
LUSIVE;
java.sql.SQLException: error code 10

android.database.sqlite.SQLiteException: cannot commit - no transaction is activ
e: COMMIT;
android.database.sqlite.SQLiteException: cannot rollback - no transaction is act
ive: ROLLBACK;
	 */
	private static boolean USE_SHARED_CONNECTION = true;

	private static final boolean DEBUG_QUERY = false;
	private static DatabaseHelper helper;

	private static Set<AndroidConnection> connectionSet = new HashSet<AndroidConnection>();
	private static AndroidConnection openConnection = null;

	public static boolean isTestMode = false;
	private static Logger lgr = Logger.getRootLogger();

	private static SQLiteDatabase ads1;
	private static Context dbcontext;
	private static String dbname;
	private static ThreadLocal<AndroidConnection> tranConnection = new ThreadLocal<AndroidConnection>();

	private static StringBuilder formatsb = new StringBuilder();
	private static Formatter formatter = new Formatter(formatsb, Locale.US);
	
	private static MixzingAppSql appSql;

	private static long getFreeSpaceInBytes() {
		long space = 0;
		try {
			String dev = Environment.getDataDirectory().getAbsolutePath();
			android.os.StatFs fs = new StatFs(dev);
			space = 1L * fs.getBlockSize() * fs.getFreeBlocks();
			lgr.debug("Free space on device " + dev + "=" + space);
		} catch (Exception e) {
			lgr.debug("Got exception computing freespace" + e);
		}
		return space;
	}
	
	private static SQLiteDatabase getAds() {
		return getConnection().getSqlite();
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context, String dbName, int dbversion) {
			super(context, dbName, null, dbversion);
			
			// set app sql
            setMixzingAppSql(context);
		}

        protected void setMixzingAppSql(Context context)
        {
            if (context != null) {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = null;
                Properties properties = null;

                // Read from the /assets directory
                try {
                    inputStream = assetManager.open("app.properties");
                    properties = new Properties();
                    properties.load(inputStream);
                    if (Logger.IS_DEBUG_ENABLED) {
                        lgr.debug("DatabaseManager.setMixzingAppSql: loaded properties");
                    }
                }
                catch (IOException e) {
                    if (Logger.IS_DEBUG_ENABLED) {
                        lgr.debug("DatabaseManager.setMixzingAppSql: failed to load properties: " + e);
                    }
                }
                
                if (properties == null) {
                    appSql = new DatabaseSQL();
                }
                else {
                    try {
                        appSql = (MixzingAppSql) Class.forName(properties.getProperty("dbSql")).newInstance();
                    }
                    catch (Exception e) {
                        if (Logger.IS_DEBUG_ENABLED) {
                            lgr.error(e.getMessage(), e);
                        }
                        appSql = new DatabaseSQL();
                    }
                }
            }
        }

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
			    createTables(appSql.createTableStatements(), db);
			} catch (Exception e) {
				lgr.error("Unable to create database:", e);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(Logger.IS_DEBUG_ENABLED)
				lgr.debug("Datbase upgarde called  old: " + oldVersion + " new: " + newVersion);
			if(oldVersion == 1 && newVersion == 2) {
				try {
					updateDatabaseToVersion2(db);
				} catch (Exception e) {
					lgr.error("Unable to update database:", e);
				}
			}
		}


	}



	public synchronized static void shutdown() throws Exception {
		if(USE_SHARED_CONNECTION) {
			openConnection.close();
			openConnection = null;
			helper.close();
		} else {
			for(AndroidConnection con : connectionSet) {
				con.close();
			}
			connectionSet.clear();
			helper.close();
		}
	}

	public static void logSql() throws Exception {
		throw new RuntimeException("Not supported");
	}

	public static void beginTransaction() throws SQLException {
		//lgr.trace("In begin tx");
		SQLiteDatabase x = getAds();
		//lgr.trace("Calling sqlite begin tx: " + x);
		
		try {
			x.beginTransaction();
		} catch (SQLiteFullException e) {
			
			throw e;
		} catch (SQLiteDiskIOException e1) {
			throw e1;
		} 
		//lgr.trace("Called sqlite begin tx");
	}

	public static void commitTransaction() throws SQLException {
		//lgr.trace("CommmitTx...getting ads");
		SQLiteDatabase ad = getAds();
		//lgr.trace("Marking tx success: " + ad);
		ad.setTransactionSuccessful();
		//lgr.trace("commiting tx");
		ad.endTransaction();
		//lgr.trace("Commited tx");
	}

	public static void rollbackTransaction() throws SQLException {
		//lgr.trace("In roll back tx");
		SQLiteDatabase x = getAds();
		//lgr.trace("Calling sqlite roll back tx: " + x);
		x.endTransaction();
		//lgr.trace("rolled back tx");
	}

	/*
	 * XXX: TODO fix this
	 */
	public static AndroidConnection getConnection()  {
		if(USE_SHARED_CONNECTION) {
			//lgr.trace("Getconnection returning: " + openConnection);
			return openConnection;
		} else {
			if ( tranConnection.get() != null ) {
				//lgr.trace("Tran connection");
				return tranConnection.get();
			} else {
				SQLiteDatabase db = dbcontext.openOrCreateDatabase(dbname, 0, null); 
				AndroidConnection con = new AndroidConnection(db);
				tranConnection.set(con);
				connectionSet.add(con);
				return con;
			}
		}
	}

	protected static void releaseConnectionInternal(Connection conn) throws SQLException {
		if(USE_SHARED_CONNECTION) {
			openConnection = null;
			conn.close();
		} else {
			tranConnection.remove();
			try {
				connectionSet.remove(conn);
			} catch (Exception e) {
			}
			conn.close();
		}
	}

	public static void releaseConnection(Connection conn) throws SQLException {
		if(!USE_SHARED_CONNECTION) {
			releaseConnectionInternal(conn);
		}
	}


	public static void initDatabase(String name, Context context, int version) {
		dbcontext = context;
		dbname = name;
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("init database: db name = " + name);
		DatabaseHelper helper = new DatabaseHelper(context,name,version);
		ads1 = helper.getWritableDatabase();

		//ads1 = copyFromSdCard(ads1, helper);

		manageDatabaseFiles(context, ads1);
		if(Logger.IS_DEBUG_ENABLED)
			lgr.debug("Got database handle: " + ads1);
		AndroidConnection con = new AndroidConnection(ads1);
		if(USE_SHARED_CONNECTION) {
			openConnection = con;
		} else {
			tranConnection.set(con);
			connectionSet.add(con);
		}
	}	

	public static boolean tableExists(String tablename) throws Exception {
		Connection conn = getConnection();
		ResultSet rs;
		boolean exists;

		try {
			DatabaseMetaData md = conn.getMetaData();

			rs = md.getTables(null, null, tablename.toUpperCase(), null);
			exists = rs.next();
		} finally {
			releaseConnection(conn);
		}
		//lgr.trace("exists:" + tablename + ":" + exists);
		return exists;
	} 

	private static void createTables(SQLiteDatabase db) throws Exception {
		//lgr.trace("Creating tables");
	    // check from context which class to use for db management
	    
		ArrayList<String> stm = DatabaseSQL.createStatements();

		// Little inefficient since we end up getting and release connection for every iteration
		for(String str: stm) {
			/*
			 * Use ads1 instead of connection since this is called before
			 * the threadlocal variable is initialized with the connection object
			 */
			if(lgr.IS_DEBUG_ENABLED) {
				lgr.debug("Creating: " + str);
			}
			db.execSQL(str);
		}

	}
	
	private static void createTables(List<String> stm, SQLiteDatabase db) throws Exception {

        // Little inefficient since we end up getting and release connection for every iteration
        for(String str: stm) {
            /*
             * Use ads1 instead of connection since this is called before
             * the threadlocal variable is initialized with the connection object
             */
            if(lgr.IS_DEBUG_ENABLED) {
                lgr.debug("Creating: " + str);
            }
            db.execSQL(str);
        }

    }

	private static void updateDatabaseToVersion2(SQLiteDatabase db) throws Exception {
		//lgr.trace("Creating tables");
		ArrayList<String> stm = DatabaseSQL.updateFromVersion_1_to_2();
		// Little inefficient since we end up getting and release connection for every iteration
		for(String str: stm) {
			/*
			 * Use ads1 instead of connection since this is called before
			 * the threadlocal variable is initialized with the connection object
			 */
			if(lgr.IS_DEBUG_ENABLED) {
				lgr.debug("Executing: " + str);
			}
			db.execSQL(str);
		}

	}
	
	/**
	 * Drop the tables.  Used mostly for unit testing, to get back
	 * to a clean state
	 */
	public static void dropTables() throws Exception {
		ArrayList<String> stm = DatabaseSQL.dropStatements();
		/*
		 * Use ads1 instead of connection since this is called before
		 * the threadlocal variable is initialized with the connection object
		 */
		SQLiteDatabase ad = getAds();
		//Little inefficient since we end up getting and release connection for every iteration
		for(String str : stm) {
			ad.execSQL(str);
		}     
	}


	/**
	 * Clean out the tables
	 */
	public static void clearTables() throws Exception {

		ArrayList<String> stm = DatabaseSQL.clearStatements();
		SQLiteDatabase ad = getAds();
		for(String str : stm) {
			ad.execSQL(str);
		} 

	}

	/**
	 * Helper wrapper around boilerplate JDBC code.  Execute a statement
	 * that doesn't return results using a PreparedStatment, and returns 
	 * the number of rows affected
	 */
	public static void executeUpdateDateLongParams(String statement, long time, Long...longs) 
	throws SQLException {
		SQLiteDatabase ad = getAds();
		Object[] data = new Object[1+longs.length];
		data[0]= Long.valueOf(time);
		int i = 1;
		for(Long l : longs) {
			data[i++]= Long.valueOf(l);;
		}
		try {
			ad.execSQL(statement, data);
		} catch (android.database.SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * Helper wrapper around boilerplate JDBC code.  Execute a statement
	 * that doesn't return results using a PreparedStatment, and returns 
	 * the number of rows affected
	 */
	public static void executeUpdateLongParams(String statement, Long...longs) 
	throws SQLException {
		SQLiteDatabase ad = getAds();
		Object[] data = new Object[longs.length];
		int i = 0;
		for(Long l : longs) {
			data[i++]= Long.valueOf(l);;
		}
		try {
			ad.execSQL(statement, data);
		} catch (android.database.SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * Helper wrapper around boilerplate JDBC code.  Execute a statement
	 * that doesn't return results using a PreparedStatment, and returns 
	 * the number of rows affected
	 * 
	 */
	public static void executeUpdateStringParams(String statement, String...strings) 
	throws SQLException {
		SQLiteDatabase ad = getAds();
		Object[] data = new Object[strings.length];
		int i = 0;
		for(String s : strings) {
			data[i++]= s;
		}
		try {
			ad.execSQL(statement, data);
		} catch (android.database.SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}
	
	/**
	 * Helper wrapper around boilerplate JDBC code.  Execute a statement
	 * that doesn't return results using a PreparedStatment, and returns 
	 * the number of rows affected
	 * 
	 * The id param is alwys the last param in the statement, but first in the
	 * argument list.
	 * 
	 */
	public static void executeUpdateByIdWithStringParams(String statement, long id, String...strings) 
	throws SQLException {
		SQLiteDatabase ad = getAds();
		Object[] data = new Object[1+strings.length];
		int i = 0;
		for(String s : strings) {
			data[i++]= s;
		}
		data[i]= Long.valueOf(id);
		try {
			ad.execSQL(statement, data);
		} catch (android.database.SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}
	/**
	 * Helper wrapper around boilerplate JDBC code.  Execute a statement
	 * that doesn't return results using a PreparedStatment, and returns 
	 * the number of rows affected
	 */
	public static void executeUpdate(String statement) throws SQLException {
		try {
			getAds().execSQL(statement);
		} catch (android.database.SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}

	public static int RAW_QUERY_ONLY = 100;
	/**
	 * Helper wrapper around boilerplat JDBC code.  Execute a statement
	 * that returns results using a PreparedStatement that takes no 
	 * parameters (you're on your own if you're binding parameters).
	 *
	 * @return the results from the query
	 */
	public static ResultSet executeQueryLongParams(Connection conn, 
			String statement, Long... params) throws SQLException {

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(statement, RAW_QUERY_ONLY);
			int i = 1;
			for(Long l : params) {
				ps.setLong(i, l);
				i++;
			}
			long start = 0;
			if(DEBUG_QUERY) {
					start = System.nanoTime();
			}
			rs = ps.executeQuery();
			if(DEBUG_QUERY) {
						long elapsed = System.nanoTime() - start;
						logQuery("executeQueryLongParams", statement, elapsed);
			}
		} catch (SQLException e) {
			String sql = statement + ",";
			for(Long l : params) {
				sql += l + ",";
			}			
			lgr.error(sql,e);
			throw(e);			
		} finally {
			if(ps != null) {
				ps.close();
			}
		}
		return rs;
	}

	/**
	 * Helper wrapper around boilerplat JDBC code.  Execute a statement
	 * that returns results using a PreparedStatement that takes no 
	 * parameters (you're on your own if you're binding parameters).
	 *
	 * @return the results from the query
	 */
	public static ResultSet executeQueryStringParams(Connection conn, 
			String statement, String... params) throws SQLException {

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(statement, RAW_QUERY_ONLY);
			int i = 1;
			for(String l : params) {
				ps.setString(i, l);
				i++;
			}
			long start = 0;
			if(DEBUG_QUERY) {
					start = System.nanoTime();
			}
			rs = ps.executeQuery();
			if(DEBUG_QUERY) {
				long elapsed = System.nanoTime() - start;
				logQuery("executeQueryStringParams", statement, elapsed);
			}
		} catch (SQLException e) {
			String sql = statement + ",";
			for(String l : params) {
				sql += l + ",";
			}			
			lgr.error(sql,e);
			throw(e);				
		} finally {
			if(ps != null) {
				ps.close();
			}
		}
		return rs;
	}

	/**
	 * Helper wrapper around boilerplat JDBC code.  Execute a statement
	 * that returns results using a PreparedStatement that takes no 
	 * parameters (you're on your own if you're binding parameters).
	 *
	 * @return the results from the query
	 */
	public static ResultSet executeQueryStringLongParam(Connection conn, 
			String statement, String str, Long lng) throws SQLException {

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(statement, RAW_QUERY_ONLY);
			int i = 1;

			ps.setString(1, str);
			ps.setLong(2, lng);
			
			long start = 0;
			if(DEBUG_QUERY) {
					start = System.nanoTime();
			}
			rs = ps.executeQuery();
			if(DEBUG_QUERY) {
				long elapsed = System.nanoTime() - start;
				logQuery("executeQueryStringParams", statement, elapsed);
			}
		} catch (SQLException e) {
			String sql = statement + ",";
			sql += str + "," + lng;
			lgr.error(sql,e);
			throw(e);				
		} finally {
			if(ps != null) {
				ps.close();
			}
		}
		return rs;
	}

	/**
	 * Helper wrapper around boilerplat JDBC code.  Execute a statement
	 * that returns results using a PreparedStatement that takes no 
	 * parameters (you're on your own if you're binding parameters).
	 *
	 * @return the results from the query
	 */
	public static ResultSet executeQueryNoParams(Connection conn, 
			String statement) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(statement, RAW_QUERY_ONLY);
			long start = 0;
			if(DEBUG_QUERY) {
					start = System.nanoTime();
			}
			rs = ps.executeQuery();
			if(DEBUG_QUERY) {
						long elapsed = System.nanoTime() - start;
						logQuery("executeQueryNoParams", statement, elapsed);
			}
		} catch (SQLException e) {
			lgr.error(statement,e);
			throw(e);
		} finally {
			if(ps != null) {
				ps.close();
			}
		}
		return rs;
	}

	public static void initDatabase(String string, String string2,
			String string3, boolean b) {

		throw new RuntimeException("Deprecated API for Android");

	}

	// maximum number of cached external databases to keep
	private static final int MAX_EXTERNAL_DATABASES = 3;

	// Delete databases that have not been used in two months
	// 60 days in milliseconds (1000 * 60 * 60 * 24 * 60)
	private static final long OBSOLETE_DATABASE_DB = 5184000000L;


	protected static void copyToSdCard(File dbFile) {
		String sdName = SdCardHandler.getRootDir() + "/" + dbFile.getName() + "." + System.currentTimeMillis();
		String dbName = dbFile.getPath();
		copyFile(dbName, sdName);
	}

	protected static SQLiteDatabase copyFromSdCard(SQLiteDatabase db, DatabaseHelper helper) {
		String dbName = db.getPath();
		File dbFile = new File(dbName);
		String sdName = SdCardHandler.getRootDir() + "/" + dbFile.getName();
		File sdFile = new File(sdName);
		if (sdFile.exists()) {
			lgr.info("DatabaseManager.copyFromSdCard: copying " + sdName + " to " + dbName);
			db.close();
			copyToSdCard(dbFile);  // save it
			copyFile(sdName, dbName);  // overwrite it
			db = helper.getWritableDatabase();  // open restored one
		}
		else {
			lgr.warn("DatabaseManager.copyFromSdCard: non-existent source file " + sdName);
		}
		return db;
	}

	protected static void copyFile(String src, String dest) {
		try {
			FileOutputStream out = new FileOutputStream(dest);
			FileInputStream in = new FileInputStream(src);
			byte[] b = new byte[4096];
			int rc = 0;
			while((rc = in.read(b)) > 0) {
				out.write(b, 0, rc);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			lgr.warn("DatabaseManager.copyFile: error copying " + src + " to " + dest + ":", e);
		}
	}


	protected static void manageDatabaseFiles(Context context, SQLiteDatabase db) {
		if ("mixzing.db".equals(db.getPath())) return;  // The internal database is kept separately.

		// touch the database file to show it is most recently used
		File file = new File(db.getPath());
		long now = System.currentTimeMillis();
		file.setLastModified(now);

		if(Logger.IS_DEBUG_ENABLED) {
			//copyToSdCard(file);
		}

		// delete least recently used databases if we are over the limit
		String[] databases = context.databaseList();
		
		// Filter for only mixzing dbs, not webview or any other
		ArrayList<String> mzdbs = new ArrayList<String>();
		for(String dbn : databases) {
			if(dbn.startsWith("mixzing")) {
				if(Logger.IS_DEBUG_ENABLED)
					lgr.debug("DatabaseManager.manageDatabaseFiles: found a mz db on disk. " + dbn );
				mzdbs.add(dbn);
			}
		}
		
		databases = new String[mzdbs.size()];
		
		for(int i=0;i<mzdbs.size();i++) {
			databases[i] = mzdbs.get(i);
		}
		
		if(Logger.IS_DEBUG_ENABLED)
			lgr.debug("DatabaseManager.manageDatabaseFiles: Number of mzdbs on disk. " + databases.length );

		
		int count = databases.length;
		int limit = MAX_EXTERNAL_DATABASES;


		// delete external databases that have not been used in the past two months
		long twoMonthsAgo = now - OBSOLETE_DATABASE_DB;
		for (int i = 0; i < databases.length; i++) {
			File other = context.getDatabasePath(databases[i]);
			if (file.equals(other)) {
				databases[i] = null;
				count--;
				if (file.equals(other)) {
					// reduce limit to account for the existence of the database we
					// are about to open, which we removed from the list.
					limit--;
				}
			} else {
				long time = other.lastModified();
				if (time < twoMonthsAgo) {
					if (Logger.IS_DEBUG_ENABLED) {
						lgr.debug("DatabaseManager.manageDatabaseFiles: deleting old database " + databases[i]);
						lgr.debug("DatabaseManager.manageDatabaseFiles: Last mod time = " + time + " two months ago = " + twoMonthsAgo);
					}
					if(isSendDeleteNotifies()) {
						lgr.error("DatabaseManager.manageDatabaseFiles: deleting old database " + databases[i] + ". Last mod time = " + time + " two months ago = " + twoMonthsAgo);
					}
					context.deleteDatabase(databases[i]);
					databases[i] = null;
					count--;
				} else {
					if(Logger.IS_DEBUG_ENABLED)
						lgr.debug("DatabaseManager.manageDatabaseFiles: Time left to delete this db. " + databases[i] + " " + (time - twoMonthsAgo) );
				}
			}
		}

		// delete least recently used databases until
		// we are no longer over the limit
		while (count > limit) {
			int lruIndex = -1;
			long lruTime = 0;

			for (int i = 0; i < databases.length; i++) {
				if (databases[i] != null) {
					long time = context.getDatabasePath(databases[i]).lastModified();
					if (lruTime == 0 || time < lruTime) {
						lruIndex = i;
						lruTime = time;
					}
				}
			}

			// delete least recently used database
			if (lruIndex != -1) {
				if (Logger.IS_DEBUG_ENABLED)
					lgr.debug("DatabaseManager.manageDatabaseFiles: deleting old database lru: " + " " + databases[lruIndex]);
				context.deleteDatabase(databases[lruIndex]);
				databases[lruIndex] = null;
				count--;
			}
		}
	}

	private static boolean isSendDeleteNotifies() {
		// TODO: Replace unconditional true with a check for server parameter?
		return false;
	}

	private static void logQuery(String tag, String stmt, long time) {
		formatsb.setLength(0);
		formatter.format("DatabaseManager.%s: %.3f ms executing %s", tag, (float)time / 1000000f, stmt);
		lgr.debug(formatsb.toString());
	}

	public static void performUpdates() {
		DatabaseSQL.checkAndPerformUpdates();
	}
}
