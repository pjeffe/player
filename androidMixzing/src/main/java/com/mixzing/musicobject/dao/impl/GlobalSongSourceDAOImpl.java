package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.GlobalSongSource;
import com.mixzing.musicobject.dao.GlobalSongSourceDAO;
import com.mixzing.musicobject.impl.GlobalSongSourceImpl;

public class GlobalSongSourceDAOImpl extends BaseDAO<GlobalSongSource> implements GlobalSongSourceDAO{

	protected static final String AMZAUD="http://www.amazon.com/gp/dmusic/get_sample_url.html/ref=1?ie=UTF8&ASIN=";
	protected static final String AMZAUD_REPLACE="${1}";
	
	protected HashMap<String, String> translateMap = new HashMap<String, String>();
	protected HashMap<String, String> reverseTranslateMap = new HashMap<String, String>();
	
	public GlobalSongSourceDAOImpl() {
		translateMap.put(AMZAUD, AMZAUD_REPLACE);
		reverseTranslateMap.put(AMZAUD_REPLACE,AMZAUD);
	}
	
	protected String getTranslate(String aud) {
		if(aud != null) {
			for(String key : translateMap.keySet()) {
				if(aud.startsWith(key)) {
					return translateMap.get(key) + aud.substring(key.length());
				}
			}
		}
		return aud;
	}
	

	protected String getReverseTranslate(String aud) {
		if(aud != null) {
			for(String key : reverseTranslateMap.keySet()) {
				if(aud.startsWith(key)) {
					return reverseTranslateMap.get(key) + aud.substring(key.length());
				}
			}
		}
		return aud;
	}


	public String getTranslatedPurchaseUrl(String purch) {
		if(purch != null && purch.startsWith("http://www.amazon.com")) {
			return purch.substring(purch.length() - 10); 
		}
		return purch;
	}
	
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.GlobalSongSourceDAO#insert(com.mixzing.musicobject.GlobalSongSource)
	 */
	public long insert(GlobalSongSource gss) {
			String sql = "INSERT INTO global_song_source " +
			"(globalsong_id, purchase_library, purchase_url, audition_url ) " + 
			"VALUES (?,?,?,?)";
			String identityQuery = "values IDENTITY_VAL_LOCAL()";
			long idVal = Long.MIN_VALUE;
			PreparedStatement ps = null;
			try {
				ps = DatabaseManager.getConnection().prepareStatement(sql);
				ps.setLong(1, gss.getGlobalSongID());
				ps.setString(2, gss.getPurchaseLibrary());
				ps.setString(3, getTranslatedPurchaseUrl(gss.getPurchaseUrl()));
				ps.setString(4, getTranslate(gss.getAuditionUrl()));
				AndroidPreparedStatement aps = (AndroidPreparedStatement) ps;
				idVal = aps.executeInsert();

			} catch (SQLException e) {
				String err = sql + "," + gss.getGlobalSongID() + "," + gss.getPurchaseLibrary() + ",";
				err += gss.getPurchaseUrl() + "," + gss.getAuditionUrl();
				throw new UncheckedSQLException(e,err);
			} finally {
				if(ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {

					}
				}
			}
			gss.setId(idVal);
			return idVal;
		}		
	
	private String tableName = "global_song_source";
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.GlobalSongSourceDAO#readAll()
	 */
	public ArrayList<GlobalSongSource> readAll() {
		ArrayList<GlobalSongSource> list = new ArrayList<GlobalSongSource>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				GlobalSongSource play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}
		
		return list;
	}
	

	@Override
	protected GlobalSongSource createInstance(ResultSet rs) {
		GlobalSongSourceImpl gs =  new GlobalSongSourceImpl(rs);
		gs.setAuditionUrl(getReverseTranslate(gs.getAuditionUrl()));
		return gs;
	}	
	
	@Override
	protected String tableName() {
		return tableName;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.GlobalSongSourceDAO#findByGlobalSong(long)
	 */
	public ArrayList<GlobalSongSource> findByGlobalSong(long id) {
		String sql = "SELECT * FROM " + tableName() + " WHERE globalsong_id = ?";
		return getCollection(sql, id);
	}
		

}
