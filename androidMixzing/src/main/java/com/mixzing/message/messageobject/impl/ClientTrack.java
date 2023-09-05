package com.mixzing.message.messageobject.impl;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.android.PackageHandler.InstalledPackages;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.AndroidPackage;
import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.SourceVideo;
import com.mixzing.musicobject.Video;

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
public class ClientTrack  implements Serializable {

	protected static Logger lgr = Logger.getRootLogger();
	protected static final String MZ_OVERLOAD_PATTERN = "_|mixzing-union|_";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientTrack() {
	}


	public ClientTrack(Video vid, SourceVideo src, boolean isAdd) {
		lsid = 2000000 + vid.getId();
		short_sig = MZ_OVERLOAD_PATTERN;

		String data = "";
		JSONStringer stringer = new JSONStringer();
		try {
			stringer.object();
			if(isAdd) {

				stringer.key("op");
				stringer.value("vidadd");				

				stringer.key("id");
				stringer.value(lsid);

				stringer.key("album");
				stringer.value(src.getAlbum());

				stringer.key("artist");
				stringer.value(src.getArtist());

				stringer.key("category");
				stringer.value(src.getCategory());

				stringer.key("dateAdded");
				stringer.value(src.getDateAdded());

				stringer.key("dateModified");
				stringer.value(src.getDateModified());

				stringer.key("dateTaken");
				stringer.value(src.getDateTaken());

				stringer.key("description");
				stringer.value(src.getDescription());

				stringer.key("duration");
				stringer.value(src.getDuration());

				stringer.key("language");
				stringer.value(src.getLanguage());

				stringer.key("latitude");
				stringer.value(src.getLatitude());

				stringer.key("location");
				stringer.value(src.getLocation());

				stringer.key("mimeType");
				stringer.value(src.getMimeType());

				stringer.key("longitude");
				stringer.value(src.getLongitude());

				stringer.key("resolution");
				stringer.value(src.getResolution());


				stringer.key("size");
				stringer.value(src.getSize());

				stringer.key("tags");
				stringer.value(src.getTags());

				stringer.key("title");
				stringer.value(src.getTitle());

			} else {

				stringer.key("op");
				stringer.value("viddel");

				stringer.key("id");
				stringer.value(lsid);

			}

			stringer.endObject();
		} catch (JSONException e) {
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.error(e);
			}
		}

		data =stringer.toString();
		location = data;
		if(Logger.IS_DEBUG_ENABLED) {
			lgr.debug("VideoData::"+location);
		}

		/*
		 * None of this data is used by the server
		 */
		mpx_tags = new MPXTags(0,
				"_a",
				"_a",
				"_a",
				"_a",
				"_a",
				"_a");   
	}



	public ClientTrack(AndroidPackage pkg, InstalledPackages srcPackage) {
		lsid = 1000000+pkg.getId(); 
		short_sig = MZ_OVERLOAD_PATTERN;
		JSONStringer stringer = new JSONStringer();
		String data = "";
		boolean isAdd = (srcPackage != null);
		try {
			stringer.object();
			if(isAdd) {
				stringer.key("op");
				stringer.value("pkgadd");				
				stringer.key("id");
				stringer.value(lsid);
				stringer.key("name");
				stringer.value(pkg.getName());
				stringer.key("ver");
				stringer.value(pkg.getVersion());
				stringer.key("vname");
				stringer.value(srcPackage.getVerName());
				stringer.key("date");
				stringer.value(System.currentTimeMillis());
				
			} else {
				stringer.key("op");
				stringer.value("pkgdel");				
				stringer.key("id");
				stringer.value(lsid);
			}
			stringer.endObject();
		} catch (JSONException e) {
			if(Logger.IS_DEBUG_ENABLED) {
				lgr.error(e);
			}
		}
		
		data =stringer.toString();
		location = data;
		if(Logger.IS_DEBUG_ENABLED) {
			lgr.debug("PkgData::"+location);
		}

		/*
		 * None of this data is used by the server
		 */

		mpx_tags = new MPXTags(0,
				"_a",
				"_a",
				"_a",
				"_a",
				"_a",
				"_a");    	
	}

	public ClientTrack(com.mixzing.musicobject.Track mmTrack, GlobalSong gs) {
		lsid = mmTrack.getId();
		short_sig = null;
		mpx_tags = new MPXTags(gs.getDuration(),
				gs.getTitle(),
				gs.getAlbum(),
				gs.getArtist(),
				gs.getTrackNumber(),
				gs.getReleaseYear()+"",
				gs.getGenre());

		if (mmTrack.getLocation() != null) {
			location = mmTrack.getLocation();
		}
	}

	//@XmlAttribute
	public String getShort_sig() {
		return short_sig;
	}

	//@XmlElement(name = "mpx_tags")
	public MPXTags getMpx_tags() {
		return mpx_tags;
	}

	//@XmlAttribute
	public long getLsid() {
		return lsid;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setShort_sig(String short_sig) {
		this.short_sig = short_sig;
	}

	public void setMpx_tags(MPXTags mpx_tags) {
		this.mpx_tags = mpx_tags;
	}

	public void setLsid(long lsid) {
		this.lsid = lsid;
	}

	//@XmlAttribute
	public String getLocation() {
		return location;
	}


	private String location;

	private long lsid;

	private String short_sig;

	private MPXTags mpx_tags;

	public ClientTrack(JSONObject json) throws JSONException {

		location = json.getString("location");

		lsid = json.getLong("lsid");

		short_sig = json.getString("shortSig"); // XXX : for some reason XML was sending shortSig tag

		mpx_tags = new MPXTags((JSONObject) json.get("mpx_tags"));

	}


	public void toJson(JSONStringer stringer) throws JSONException  {
		stringer.object();
		stringer.key("location");
		stringer.value(location);
		stringer.key("shortSig"); // XXX : for backward compat - XML was sending shortSig tag
		stringer.value(short_sig);
		stringer.key("lsid");
		stringer.value(lsid);
		stringer.key("mpx_tags");
		mpx_tags.toJson(stringer);
		stringer.endObject();
	}
}
