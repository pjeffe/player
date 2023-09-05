package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.EnumRatingSource;
import com.mixzing.musicobject.EnumRatingValue;
import com.mixzing.musicobject.dto.RatingSongDTO;

public class RatingSongDTOImpl implements RatingSongDTO {


	protected long id;
	
	protected long globalSongId;
	
	protected long plid;
	
	protected EnumRatingSource ratingSource;
	
	protected EnumRatingValue ratingValue;
	
	protected long timeRated;
	
	protected boolean isDeleted;

	public RatingSongDTOImpl() {
		plid = Long.MIN_VALUE;
		
	}

	public String toString() {
		return "RatingSong: " + 
				id + " : " +
				globalSongId + " : " +
				plid + " : " +
				ratingSource + " : " +
				ratingValue + " : " +
				timeRated + " : " + isDeleted;
	}
	
	
	public RatingSongDTOImpl(ResultSet rs) {
		try {
			this.setId(rs.getLong("id"));
			this.setGlobalSongId(rs.getLong("globalsong_id"));
			this.setPlid(rs.getLong("plid"));
			this.setRatingSource(EnumRatingSource.fromIntValue(rs.getInt("rating_source")));
			this.setRatingValue(EnumRatingValue.fromIntValue(rs.getInt("rating_value")));
			this.setTimeRated(rs.getTimestamp("timerated").getTime());
			this.setDeleted(rs.getInt("is_deleted") == 1 ? true : false);
		} catch ( SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}
	
	public static RatingSongDTOImpl createAutomaticNegativeHate(long plid, long gsongid) {
		RatingSongDTOImpl rate = new RatingSongDTOImpl();
		rate.setGlobalSongId(gsongid);
		rate.setPlid(plid);
		rate.setRatingValue(EnumRatingValue.DISLIKE);
		rate.setRatingSource(EnumRatingSource.FROM_HATE_ARTIST);
		return rate;
	}

	public static RatingSongDTOImpl createAutomaticNegativeIgnore(long plid, long gsongid) {
		RatingSongDTOImpl rate = new RatingSongDTOImpl();
		rate.setGlobalSongId(gsongid);
		rate.setPlid(plid);
		rate.setRatingValue(EnumRatingValue.DISLIKE);
		rate.setRatingSource(EnumRatingSource.FROM_IGNORE_ARTIST);
		return rate;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#getGlobalSongId()
	 */
	public long getGlobalSongId() {
		return globalSongId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#setGlobalSongId(long)
	 */
	public void setGlobalSongId(long globalSongId) {
		this.globalSongId = globalSongId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#isDeleted()
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#setDeleted(boolean)
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#getPlid()
	 */
	public long getPlid() {
		return plid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#setPlid(long)
	 */
	public void setPlid(long plid) {
		this.plid = plid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#getRatingSource()
	 */
	public EnumRatingSource getRatingSource() {
		return ratingSource;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#setRatingSource(com.mixzing.musicobject.db.impl.RatingDBImpl.RatingSource)
	 */
	public void setRatingSource(EnumRatingSource ratingSource) {
		this.ratingSource = ratingSource;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#getRatingValue()
	 */
	public EnumRatingValue getRatingValue() {
		return ratingValue;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#setRatingValue(com.mixzing.musicobject.db.impl.RatingDBImpl.RatingValue)
	 */
	public void setRatingValue(EnumRatingValue ratingValue) {
		this.ratingValue = ratingValue;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#getTimeRated()
	 */
	public long getTimeRated() {
		return timeRated;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RatingDB#setTimeRated(long)
	 */
	public void setTimeRated(long timeRated) {
		this.timeRated = timeRated;
	}
}
