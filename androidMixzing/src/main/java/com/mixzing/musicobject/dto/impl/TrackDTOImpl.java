package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.dto.TrackDTO;

public class TrackDTOImpl implements TrackDTO {

	protected long id;
		
	protected String location;
	
	protected boolean isDeleted;
	
	protected long globalsong_id;

	private String sourceId;

	public String toString() {
		return "Track: " + id + " : " + globalsong_id + " : " + location + " : " + isDeleted;
	}
	
	public TrackDTOImpl() {
		id = Long.MIN_VALUE;	
	}

	public TrackDTOImpl(ResultSet rs) {
		try {
			this.setId(rs.getLong("id"));
			this.setGlobalSongId(rs.getLong("globalsong_id"));
			this.setLocation(rs.getString("location"));
			this.setSourceId(rs.getString("source_id"));
			this.setDeleted(rs.getInt("is_deleted") == 1 ? true : false);
		} catch ( SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}
		
	

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.TrackDB#isDeleted()
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.TrackDB#setDeleted(boolean)
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.TrackDB#getLocation()
	 */
	public String getLocation() {
		return location;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.TrackDB#setLocation(java.lang.String)
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.TrackDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.TrackDB#setId(long)
	 */
	public void setId(long lsid) {
		this.id = lsid;
	}

	public long getGlobalSongId() {
		// TODO Auto-generated method stub
		return globalsong_id;
	}

	public void setGlobalSongId(long gsid) {
		this.globalsong_id = gsid;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

}
