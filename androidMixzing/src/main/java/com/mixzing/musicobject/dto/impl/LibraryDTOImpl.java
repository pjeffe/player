package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.EnumLibraryStatus;
import com.mixzing.musicobject.dto.LibraryDTO;

public class LibraryDTOImpl implements LibraryDTO {

	protected long id;
	
	protected String serverId;
	
	protected long timeCreated;
	
	protected EnumLibraryStatus libraryStatus;
	
	protected long resolvedSongCount;

	
	public String toString() {
		return "Library: " + id + " : " + serverId;
	}
	
	public LibraryDTOImpl() {
		id = Long.MIN_VALUE;
		libraryStatus = EnumLibraryStatus.CREATED;
		resolvedSongCount = Integer.MAX_VALUE;
	}

	public LibraryDTOImpl(ResultSet rs) {
		try {
			this.setId(rs.getLong("id"));
			this.setServerId(rs.getString("server_id"));
			this.setTimeCreated(rs.getTimestamp("time_created").getTime());
			this.setResolvedSongCount((int)rs.getLong("resolved_song_count"));
			try {
				EnumLibraryStatus s = EnumLibraryStatus.valueOf(rs.getString("library_status"));
				this.setLibraryStatus(s);
			} catch (Exception e) {
				this.setLibraryStatus(EnumLibraryStatus.CREATED);
			}
		} catch ( SQLException e) {
			throw new UncheckedSQLException(e,"Creating library object");
		}
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.LIbraryDTO#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.LIbraryDTO#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.LIbraryDTO#getServerId()
	 */
	public String getServerId() {
		return serverId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.LIbraryDTO#setServerId(java.lang.String)
	 */
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.LIbraryDTO#getTimeCreated()
	 */
	public long getTimeCreated() {
		return timeCreated;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.LIbraryDTO#setTimeCreated(long)
	 */
	public void setTimeCreated(long timeCreated) {
		this.timeCreated = timeCreated;
	}	
	
	public EnumLibraryStatus getLibraryStatus() {
		return libraryStatus;
	}
	
	public void setLibraryStatus(EnumLibraryStatus status) {
		libraryStatus = status;
	}
	
	public int getResolvedSongCount() {
		return (int) resolvedSongCount;
	}
	
	public void setResolvedSongCount(int count) {
		resolvedSongCount = (long) count;
	}
}
