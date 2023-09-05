package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.dto.AndroidPackageDTO;

public class AndroidPackageDTOImpl implements AndroidPackageDTO {

	protected long id;
		

	protected String name;
	
	protected long version;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String toString() {
		return "Package: " + id + " : " + name + " : " + version ;
	}
	
	public AndroidPackageDTOImpl() {
		id = Long.MIN_VALUE;	
	}

	public AndroidPackageDTOImpl(ResultSet rs) {
		try {
			this.setId(rs.getLong("id"));
			this.setName(rs.getString("name"));
			this.setVersion(rs.getLong("version"));
		} catch ( SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}
		

	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.TrackDB#setId(long)
	 */
	public void setId(long lsid) {
		this.id = lsid;
	}

}
