package com.mixzing.musicobject.dto.impl;

import com.mixzing.musicobject.dto.PlaylistStatusDTO;

public class PlaylistStatusDTOImpl implements PlaylistStatusDTO {

	protected long plid;
	
	protected long lastRecoTimeStamp;

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistStatusDB#getLastRecoTimeStamp()
	 */
	public long getLastRecoTimeStamp() {
		return lastRecoTimeStamp;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistStatusDB#setLastRecoTimeStamp(long)
	 */
	public void setLastRecoTimeStamp(long lastRecoTimeStamp) {
		this.lastRecoTimeStamp = lastRecoTimeStamp;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistStatusDB#getPlid()
	 */
	public long getPlid() {
		return plid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.PlaylistStatusDB#setPlid(long)
	 */
	public void setPlid(long plid) {
		this.plid = plid;
	}
	
}
