package com.mixzing.message.messageobject.impl;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.MixzingConstants;
import com.mixzing.musicobject.RatingSong;

/**
 * <p>Title: MixMoxie Java Client</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: MixMoxie</p>
 * @author G.Miller S Mathur.
 * @version 1.0
 */
public class TrackRating  implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long gsid;

	private long timestamp;

	private long plid;

	private String rating;

	private String source;

	public TrackRating () {
	}

	//@XmlAttribute
	public long getPlid() {
		return plid;
	}

	public void setPlid(long plid) {
		this.plid = plid;
	}


	public TrackRating(RatingSong rate) {
		gsid = rate.getGlobalSong().getGsid();
		assert(gsid != 0 && gsid != Long.MIN_VALUE);
		timestamp = rate.getTimeRated();
		rating = rate.getRatingValue().name();
		source = rate.getRatingSource().name();
		plid = rate.getPlid();
		assert(plid == MixzingConstants.PLAYLIST_ID_FOR_SCROBBLES || plid >= 0);
	}    

	// TODO ADD A Constructor that takes in a Server Recommendation and generates
	// a negative rating for the gsid in the recommendation. Used when server recommends
	// a hated artist.

	//@XmlAttribute
	public long getGsid() {
		return gsid;
	}

	public void setGsid(long gsid) {
		this.gsid = gsid;
	}
	//@XmlAttribute
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	//@XmlAttribute
	public String getRating() {
		return rating;
	}
	//@XmlAttribute
	public String getSource() {
		return source;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void toJson(JSONStringer stringer) throws JSONException  {
		stringer.object();

		stringer.key("plid");
		stringer.value(plid);
		stringer.key("gsid");
		stringer.value(gsid);

		stringer.key("timestamp");
		stringer.value(timestamp);
		stringer.key("rating");
		stringer.value(rating);
		stringer.key("source");
		stringer.value(source);

		stringer.endObject();
	}

	public TrackRating(JSONObject json) throws JSONException {

		plid = json.getLong("plid");
		gsid = json.getLong("gsid");
		timestamp = json.getLong("timestamp");
		source = json.getString("source");
		rating = json.getString("rating");

	}
}
