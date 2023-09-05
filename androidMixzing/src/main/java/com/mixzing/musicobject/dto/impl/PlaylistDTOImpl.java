package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.EnumPlaylistType;
import com.mixzing.musicobject.dto.PlaylistDTO;

public class PlaylistDTOImpl implements PlaylistDTO {
	
	protected long id;
	
	protected long sourceId;
	
	protected String name;
	
	protected String sourceSpecificId;
	
	protected EnumPlaylistType playlistType;
	
	protected boolean isDeleted;


	public String toString() {
		return id + " : " + name + " : " + " : " + sourceSpecificId + " : " + playlistType;
	}
	
	public PlaylistDTOImpl() {
		id = Long.MIN_VALUE;
		
	}

	public PlaylistDTOImpl(ResultSet rs) {
		try {
			this.setId(rs.getLong("id"));
			this.setName(rs.getString("name"));
			this.setSourceSpecificId(rs.getString("source_specific_id"));
			this.setPlaylistType(EnumPlaylistType.valueOf(rs.getString("pl_type")));
			this.setDeleted(rs.getInt("is_deleted") == 1 ? true : false);
		} catch ( SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#isDeleted()
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#setDeleted(boolean)
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#getPl_type()
	 */
	public EnumPlaylistType getPlaylistType() {
		return playlistType;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#setPl_type(java.lang.String)
	 */
	public void setPlaylistType(EnumPlaylistType pl_type) {
		this.playlistType = pl_type;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#setId(long)
	 */
	public void setId(long plid) {
		this.id = plid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#getSourceId()
	 */
	public long getSourceId() {
		return sourceId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#setSourceId(long)
	 */
	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#getSourceSpecificId()
	 */
	public String getSourceSpecificId() {
		return sourceSpecificId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistDB#setSourceSpecificId(java.lang.String)
	 */
	public void setSourceSpecificId(String sourceSpecificId) {
		this.sourceSpecificId = sourceSpecificId;
	}
}
