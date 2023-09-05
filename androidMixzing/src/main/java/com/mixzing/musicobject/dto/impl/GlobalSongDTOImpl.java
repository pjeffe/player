package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.musicobject.dto.GlobalSongDTO;

public class GlobalSongDTOImpl implements GlobalSongDTO {

	protected static Logger lgr = Logger.getRootLogger();

	protected long id;

	protected long gsid;

	protected String artist;

	protected String title;

	protected String album;

	protected String genre;

	protected int releaseYear;

	protected float duration;

	protected String trackNumber;

	protected long timeUpdated;

	public String toString() {
		return "GlobalSong: " + id + " : " + gsid + " : " + title + " : " + album + " : " + artist + " : " + genre + " : " + releaseYear + " : " + duration + " : " + trackNumber + " : " + timeUpdated ;  
	}

	public GlobalSongDTOImpl() {
		id = Long.MIN_VALUE;
	}

	public GlobalSongDTOImpl(ResultSet rs) {
		try {
			this.id = rs.getLong("id");
			this.gsid = rs.getLong("gsid");
			this.artist = rs.getString("artist");
			this.title = rs.getString("title");
			this.album = rs.getString("album");
			this.genre = rs.getString("genre");
			this.releaseYear = rs.getInt("releaseyear");
			this.duration = rs.getFloat("duration");
			this.trackNumber = rs.getString("trackNumber");
			this.timeUpdated = rs.getTimestamp("time_updated").getTime();

		} catch (SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");		
		}		
	}


	public GlobalSongDTOImpl(GlobalSongSpec gss) {
		this.id = Long.MIN_VALUE;
		this.gsid = gss.getGsid();
		this.artist = gss.getMpx_info().getArtist();
		this.title =  gss.getMpx_info().getTitle();	
		this.album = gss.getMpx_info().getAlbum();
		this.genre = gss.getMpx_info().getGenre();
		try {
			this.releaseYear = Integer.valueOf(gss.getMpx_info().getYear()); // XXX: TODO Change data type to String
		} catch (NumberFormatException e) {
			this.releaseYear = 0;
			if(Logger.IS_TRACE_ENABLED)
				lgr.trace("Got exception in release year " + gss.getMpx_info().getYear() );
		}
		this.duration = gss.getMpx_info().getDuration();
		this.trackNumber = gss.getMpx_info().getTrack_number();
	}



	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#getAlbum()
	 */
	public String getAlbum() {
		return album;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#setAlbum(java.lang.String)
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#getArtist()
	 */
	public String getArtist() {
		return artist;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#setArtist(java.lang.String)
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#getDuration()
	 */
	public float getDuration() {
		return duration;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#setDuration(float)
	 */
	public void setDuration(float duration) {
		this.duration = duration;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#getGenre()
	 */
	public String getGenre() {
		return genre;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#setGenre(java.lang.String)
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#getGsid()
	 */
	public long getGsid() {
		return gsid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#setGsid(long)
	 */
	public void setGsid(long gsid) {
		this.gsid = gsid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#getReleaseYear()
	 */
	public int getReleaseYear() {
		return releaseYear;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#setReleaseYear(int)
	 */
	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#getTimeUpdated()
	 */
	public long getTimeUpdated() {
		return timeUpdated;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#setTimeUpdated(long)
	 */
	public void setTimeUpdated(long timeUpdated) {
		this.timeUpdated = timeUpdated;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#getTrackNumber()
	 */
	public String getTrackNumber() {
		return trackNumber;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.GlobalSongDB#setTrackNumber(java.lang.String)
	 */
	public void setTrackNumber(String trackNumber) {
		this.trackNumber = trackNumber;
	}

}
