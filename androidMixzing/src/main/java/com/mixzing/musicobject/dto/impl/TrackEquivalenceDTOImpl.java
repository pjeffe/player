package com.mixzing.musicobject.dto.impl;

import com.mixzing.musicobject.dto.TrackEquivalenceDTO;

public class TrackEquivalenceDTOImpl implements TrackEquivalenceDTO {

	
	protected long id;
	
	protected int matchLevel;
	
	protected int status;

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceDTO#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceDTO#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceDTO#getMatchLevel()
	 */
	public int getMatchLevel() {
		return matchLevel;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceDTO#setMatchLevel(int)
	 */
	public void setMatchLevel(int matchLevel) {
		this.matchLevel = matchLevel;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceDTO#getStatus()
	 */
	public int getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dto.impl.TrackEquivalenceDTO#setStatus(int)
	 */
	public void setStatus(int status) {
		this.status = status;
	}
		
	
}
