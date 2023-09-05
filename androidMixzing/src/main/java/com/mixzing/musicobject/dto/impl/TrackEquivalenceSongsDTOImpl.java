package com.mixzing.musicobject.dto.impl;

import com.mixzing.musicobject.dto.TrackEquivalenceSongsDTO;

public class TrackEquivalenceSongsDTOImpl implements TrackEquivalenceSongsDTO {

	
	protected long id;
	
	protected long equivId;
	
	protected long lsid;
	
	protected long globalSongId;

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceSongsDTO#getEquivId()
	 */
	public long getEquivId() {
		return equivId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceSongsDTO#setEquivId(long)
	 */
	public void setEquivId(long equivId) {
		this.equivId = equivId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceSongsDTO#getGlobalSongId()
	 */
	public long getGlobalSongId() {
		return globalSongId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceSongsDTO#setGlobalSongId(long)
	 */
	public void setGlobalSongId(long globalSongId) {
		this.globalSongId = globalSongId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceSongsDTO#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceSongsDTO#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceSongsDTO#getLsid()
	 */
	public long getLsid() {
		return lsid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceSongsDTO#setLsid(long)
	 */
	public void setLsid(long lsid) {
		this.lsid = lsid;
	}
	

}
