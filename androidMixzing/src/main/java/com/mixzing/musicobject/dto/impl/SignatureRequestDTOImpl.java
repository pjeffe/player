package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.EnumSignatureProcessingStatus;
import com.mixzing.musicobject.dto.SignatureRequestDTO;

public class SignatureRequestDTOImpl implements SignatureRequestDTO {

	protected long id;
	
	protected long lsid;
	
	protected EnumSignatureProcessingStatus processingStatus;
		
	protected boolean isPriority;
	
	protected int skip, duration, superWindowMs;

	public String toString() {
		return id + " : " + lsid + " : "  + processingStatus +  " : " + isPriority;
	}
	
	public SignatureRequestDTOImpl() {
		id = Long.MIN_VALUE;
		
	}

	
	public SignatureRequestDTOImpl(ResultSet rs) {
		try {
			this.setId(rs.getLong("id"));
			this.setLsid(rs.getLong("lsid"));
			this.setSkip(rs.getInt("skip"));
			this.setDuration(rs.getInt("duration"));
			this.setSuperWindowMs(rs.getInt("super_window_ms"));
			this.setProcessingStatus(EnumSignatureProcessingStatus.fromIntValue(rs.getInt("processing_status")));
			this.setPriority(rs.getInt("is_priority") == 1 ? true : false);
		} catch ( SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}
		
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.SignatureRequestDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.SignatureRequestDB#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.SignatureRequestDB#isPriority()
	 */
	public boolean isPriority() {
		return isPriority;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.SignatureRequestDB#setPriority(boolean)
	 */
	public void setPriority(boolean isPriority) {
		this.isPriority = isPriority;
	}


	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.SignatureRequestDB#getProcessingStatus()
	 */
	public EnumSignatureProcessingStatus getProcessingStatus() {
		return processingStatus;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.SignatureRequestDB#setProcessingStatus(com.mixzing.musicobject.db.impl.SignatureRequestDBImpl.ProcessingStatus)
	 */
	public void setProcessingStatus(EnumSignatureProcessingStatus processingStatus) {
		this.processingStatus = processingStatus;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.SignatureRequestDB#getTrackId()
	 */
	public long getLsid() {
		return lsid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.SignatureRequestDB#setTrackId(long)
	 */
	public void setLsid(long lsid) {
		this.lsid = lsid;
	}

	public int getDuration() {
		// TODO Auto-generated method stub
		return this.duration;
	}

	public int getSkip() {
		// TODO Auto-generated method stub
		return this.skip;
	}

	public void setDuration(int dur) {
		this.duration = dur;
		
	}

	public void setSkip(int skip) {
		this.skip = skip;
		
	}

	public int getSuperWindowMs() {
		return superWindowMs;
	}

	public void setSuperWindowMs(int swinMs) {
		superWindowMs = swinMs;
	}
	
}
