package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.dto.VideoDTO;

public class VideoDTOImpl implements VideoDTO {

	protected long id;
	protected String source_id;
	protected String location;   

	
	
	public String toString() {
		return "Video: " + id + " : " + location ;
	}
	
	public VideoDTOImpl() {
		id = Long.MIN_VALUE;	
	}

	public VideoDTOImpl(ResultSet rs) {
		try {
			this.setId(rs.getLong("id"));
			this.setSource_id(rs.getString("source_id"));
			this.setLocation(rs.getString("location"));
		} catch ( SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.VideoDTO#getId()
	 */
	public long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.VideoDTO#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.VideoDTO#getSource_id()
	 */
	public String getSource_id() {
		return source_id;
	}
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.VideoDTO#setSource_id(java.lang.String)
	 */
	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}


	public String getLocation() {
		return location;
	}
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.VideoDTO#setLocation(java.lang.String)
	 */
	public void setLocation(String location) {
		this.location = location;
	}

}
