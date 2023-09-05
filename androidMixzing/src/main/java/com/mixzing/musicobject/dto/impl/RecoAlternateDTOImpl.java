package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.musicobject.dto.RecoAlternateDTO;

public class RecoAlternateDTOImpl implements RecoAlternateDTO {

	protected static Logger lgr = Logger.getRootLogger();
	
	protected long id;
	
	protected long recoId;
	
	protected long globalSongId;
	
	protected boolean isLocal;
	
	protected float rank;

	public String toString() {
		return "RecoAlt: " + id + " : " + recoId + " : " + globalSongId + " : " + isLocal + " : " + rank;
	}
	public RecoAlternateDTOImpl() {
		id = Long.MIN_VALUE;
	}
	
	public RecoAlternateDTOImpl(ResultSet rs) {
		try {
			id = rs.getLong("id");
			recoId = rs.getLong("reco_id");
			globalSongId = rs.getLong("globalsong_id");
			isLocal = rs.getInt("is_local") == 1 ? true : false;
			rank = rs.getFloat("rank");
		} catch (SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}

	public RecoAlternateDTOImpl(GlobalSongSpec gss, long recoId, long globalSongId, int rank) {
			this.id = Long.MIN_VALUE;
			this.recoId = recoId;
			this.globalSongId = globalSongId;
			if(gss.getLsid() != -1 && gss.getLsid() != 0) {
				this.isLocal = true;
			}			
			this.rank = rank;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecoAlternateDB#getGlobalSongId()
	 */
	public long getGlobalSongId() {
		return globalSongId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecoAlternateDB#setGlobalSongId(long)
	 */
	public void setGlobalSongId(long globalSongId) {
		this.globalSongId = globalSongId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecoAlternateDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecoAlternateDB#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecoAlternateDB#isLocal()
	 */
	public boolean isLocal() {
		return isLocal;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecoAlternateDB#setLocal(boolean)
	 */
	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecoAlternateDB#getRank()
	 */
	public float getRank() {
		return rank;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecoAlternateDB#setRank(float)
	 */
	public void setRank(float rank) {
		this.rank = rank;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecoAlternateDB#getRecoId()
	 */
	public long getRecoId() {
		return recoId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecoAlternateDB#setRecoId(long)
	 */
	public void setRecoId(long recoId) {
		this.recoId = recoId;
	}
	
}
