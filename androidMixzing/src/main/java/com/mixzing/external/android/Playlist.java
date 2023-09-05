package com.mixzing.external.android;


import android.os.Parcel;
import android.os.Parcelable;

import com.mixzing.log.Logger;
import com.mixzing.musicobject.EnumPlaylistType;


public class Playlist implements Parcelable {
	private static final Logger log = Logger.getRootLogger();
	private long id;
	private long sourceId;
	private String name;
	private int type;
	//private static Logger log = Logger.getRootLogger();

	public Playlist(com.mixzing.musicobject.Playlist playlist) {
		id = playlist.getId();
		name = playlist.getName();
		type = playlist.getPlaylistType().ordinal();

		String sid = playlist.getSourceSpecificId();
		try {
			sourceId = Integer.parseInt(sid);
		}
		catch (NumberFormatException e) {
			log.error("Playlist: error converting source id " + sid + " to integer");
		}
	}

	public Playlist(Parcel parcel) {
		id = parcel.readLong();
		sourceId = parcel.readLong();
		name = parcel.readString();
		type = parcel.readInt();
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeLong(sourceId);
		parcel.writeString(name);
		parcel.writeInt(type);
	}

	public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
		public Playlist createFromParcel(Parcel parcel) {
			return new Playlist(parcel);
		}

		public Playlist[] newArray(int size) {
			return new Playlist[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public long getPlid() {
		return id;
	}

	public long getSourceId() {
		return sourceId;
	}

	public String getName() {
		return name;
	}

	public EnumPlaylistType getType() {
		return EnumPlaylistType.values()[type];
	}

	@Override
	public String toString() {
		return String.format("id %d, sourceId %d, name '%s', type %s", id, sourceId, name, getType().name());
	}
}
