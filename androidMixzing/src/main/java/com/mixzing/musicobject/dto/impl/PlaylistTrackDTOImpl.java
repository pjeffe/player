package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.dto.PlaylistTrackDTO;

public class PlaylistTrackDTOImpl implements PlaylistTrackDTO {
	
	protected long plid;
	
	protected long lsid;

	public String toString() {
		return "PlaylistTrack: " +  + lsid + " : " + plid;				
	}
	
	public PlaylistTrackDTOImpl() {
			
	}
	
	public PlaylistTrackDTOImpl(ResultSet rs) {
		try {
			this.setPlid(rs.getLong("plid"));
			this.setLsid(rs.getLong("lsid"));
		} catch ( SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistTrackDB#getLsid()
	 */
	public long getLsid() {
		return lsid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistTrackDB#setLsid(long)
	 */
	public void setLsid(long lsid) {
		this.lsid = lsid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistTrackDB#getPlid()
	 */
	public long getPlid() {
		return plid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistTrackDB#setPlid(long)
	 */
	public void setPlid(long plid) {
		this.plid = plid;
	}
	
	
}
