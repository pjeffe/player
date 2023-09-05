package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.musicobject.dto.GlobalSongSourceDTO;

public class GlobalSongSourceDTOImpl implements GlobalSongSourceDTO {

	protected static Logger lgr = Logger.getRootLogger();
	
	protected long id;
	
	protected long globalSongID;
	
	protected String purchaseLibrary;
	
	protected String purchaseUrl;
	
	protected String auditionUrl;

	
	public GlobalSongSourceDTOImpl(long globalSongId, GlobalSongSpec gss, String purchLib) {
		this.id = Long.MIN_VALUE;
		this.globalSongID = globalSongId;
		this.purchaseLibrary = purchLib;
		this.purchaseUrl = gss.getPurchase_url();
		this.auditionUrl = gss.getAudition_url();
	}

	public GlobalSongSourceDTOImpl() {
		id = Long.MIN_VALUE;
	}
	
	public GlobalSongSourceDTOImpl(ResultSet rs) {
		try {
			this.id = rs.getLong("id");
			this.globalSongID = rs.getLong("globalsong_id");
			this.purchaseLibrary = rs.getString("purchase_library");
			this.purchaseUrl = rs.getString("purchase_url");
			this.auditionUrl = rs.getString("audition_url");
		} catch (SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}

	public String toString() {
		return "GSS: " + id + " : " + globalSongID + " : " + purchaseLibrary + " : " + purchaseUrl + " : " + auditionUrl;
	}
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongSourceDB#getAuditionUrl()
	 */
	public String getAuditionUrl() {
		return auditionUrl;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongSourceDB#setAuditionUrl(java.lang.String)
	 */
	public void setAuditionUrl(String auditionUrl) {
		this.auditionUrl = auditionUrl;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongSourceDB#getGlobalSongID()
	 */
	public long getGlobalSongID() {
		return globalSongID;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongSourceDB#setGlobalSongID(long)
	 */
	public void setGlobalSongID(long globalSongID) {
		this.globalSongID = globalSongID;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongSourceDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongSourceDB#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongSourceDB#getPurchaseLibrary()
	 */
	public String getPurchaseLibrary() {
		return purchaseLibrary;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongSourceDB#setPurchaseLibrary(java.lang.String)
	 */
	public void setPurchaseLibrary(String purchaseLibrary) {
		this.purchaseLibrary = purchaseLibrary;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongSourceDB#getPurchaseUrl()
	 */
	public String getPurchaseUrl() {
		return purchaseUrl;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongSourceDB#setPurchaseUrl(java.lang.String)
	 */
	public void setPurchaseUrl(String purchaseUrl) {
		this.purchaseUrl = purchaseUrl;
	}
	
	
}
