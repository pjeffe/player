package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.message.messageobject.impl.TrackRecommendation;
import com.mixzing.musicobject.dto.RecommendationDTO;

public class RecommendationDTOImpl implements RecommendationDTO {

	protected long id;
	
	protected long plid;
	
	protected float score;
	
	protected long artsongId;
	
	protected String artsongArtist;
	
	protected String artsongTitle;
	
	protected long timeReceived;
	
	protected boolean isDeleted;
	
	protected boolean isRated;
	
	
	protected RecoSource recoSource;

	public String toString() {
		return "Reco: " + id + " : " + plid + " : " + " : " + score + " : " + artsongId + " : " + artsongArtist + " : " + artsongTitle + " : " + timeReceived + " : " + isDeleted + " : " + isRated + " : " + recoSource;  
	}
	public RecommendationDTOImpl() {
		
	}
	
	public RecommendationDTOImpl(ResultSet rs) {
		try {
			id = rs.getLong("id");
			plid = rs.getLong("plid");
			score = rs.getFloat("score");
			artsongId = rs.getLong("artsongid");
			artsongArtist = rs.getString("artsongartist");
			artsongTitle = rs.getString("artsongtitle");
			timeReceived = rs.getTimestamp("timereceived").getTime();
			isRated = rs.getInt("is_rated") == 1 ? true : false;
			isDeleted = rs.getInt("is_deleted") == 1 ? true : false;
			recoSource = rs.getInt("reco_source") == 1 ? RecoSource.INFERRED : RecoSource.SERVER;
		} catch (SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}

	public RecommendationDTOImpl(long plid,TrackRecommendation t, long time) {
		this.id = Long.MIN_VALUE;
		this.plid = plid;
		this.timeReceived = time;
		this.score = t.getScore();
		this.artsongId = t.getAsid();
		this.artsongArtist = t.getAs_artist();
		this.artsongTitle = t.getAs_title();
		this.timeReceived = time;
		this.recoSource = RecoSource.SERVER;	
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#getArtsongArtist()
	 */
	public String getArtsongArtist() {
		return artsongArtist;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#setArtsongArtist(java.lang.String)
	 */
	public void setArtsongArtist(String artsongArtist) {
		this.artsongArtist = artsongArtist;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#getArtsongId()
	 */
	public long getArtsongId() {
		return artsongId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#setArtsongId(long)
	 */
	public void setArtsongId(long artsongId) {
		this.artsongId = artsongId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#getArtsongTitle()
	 */
	public String getArtsongTitle() {
		return artsongTitle;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#setArtsongTitle(java.lang.String)
	 */
	public void setArtsongTitle(String artsongTitle) {
		this.artsongTitle = artsongTitle;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#isDeleted()
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#setDeleted(boolean)
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#isRated()
	 */
	public boolean isRated() {
		return isRated;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#setRated(boolean)
	 */
	public void setRated(boolean isRated) {
		this.isRated = isRated;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#getPlid()
	 */
	public long getPlid() {
		return plid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#setPlid(long)
	 */
	public void setPlid(long plid) {
		this.plid = plid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#getRecoSource()
	 */
	public RecoSource getRecoSource() {
		return recoSource;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#setRecoSource(com.mixzing.musicobject.db.impl.RecommendationDBImpl.RecoSource)
	 */
	public void setRecoSource(RecoSource recoSource) {
		this.recoSource = recoSource;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#getScore()
	 */
	public float getScore() {
		return score;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#setScore(float)
	 */
	public void setScore(float score) {
		this.score = score;
	}


	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#getTimeReceived()
	 */
	public long getTimeReceived() {
		return timeReceived;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.RecommendationDB#setTimeReceived(long)
	 */
	public void setTimeReceived(long timeReceived) {
		this.timeReceived = timeReceived;
	}
}
