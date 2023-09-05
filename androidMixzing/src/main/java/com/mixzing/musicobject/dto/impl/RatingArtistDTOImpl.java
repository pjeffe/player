package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.dto.RatingArtistDTO;

public class RatingArtistDTOImpl implements RatingArtistDTO {


	protected long id;
	
	protected String artistName;
	
	/*
	 * Long.MINVALUE implies for all playlists
	 */
	protected long plid;

	public String toString() {
		return "RatingArtist: " + 
				id + " : " +
				plid + " : " +
				artistName	;
	}
	
	public RatingArtistDTOImpl() {
		id = Long.MIN_VALUE;
	}
	
	public RatingArtistDTOImpl(ResultSet rs) {
		try {
			this.setId(rs.getLong("id"));
			long pl = rs.getLong("plid");
			this.setPlid(pl);
			this.setArtistName(rs.getString("artist_name"));
		} catch ( SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.ArtistRatingDB#getArtistName()
	 */
	public String getArtistName() {
		return artistName;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.ArtistRatingDB#setArtistName(java.lang.String)
	 */
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.ArtistRatingDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.ArtistRatingDB#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.ArtistRatingDB#getPlid()
	 */
	public long getPlid() {
		return plid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.ArtistRatingDB#setPlid(long)
	 */
	public void setPlid(long plid) {
		this.plid = plid;
	}
	
}
