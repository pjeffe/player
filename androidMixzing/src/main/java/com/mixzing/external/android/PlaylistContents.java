package com.mixzing.external.android;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public class PlaylistContents implements Parcelable {
	private long id;
	private List<String> contents;


	public PlaylistContents(long id, List<String> contents) {
		this.id = id;
		this.contents = contents;
	}

	public PlaylistContents(Parcel parcel) {
		id = parcel.readLong();
		contents = parcel.createStringArrayList();
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeStringList(contents);
	}

	public static final Parcelable.Creator<PlaylistContents> CREATOR = new Parcelable.Creator<PlaylistContents>() {
		public PlaylistContents createFromParcel(Parcel parcel) {
			return new PlaylistContents(parcel);
		}

		public PlaylistContents[] newArray(int size) {
			return new PlaylistContents[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public long getPlid() {
		return id;
	}

	public List<String> getContents() {
		return contents;
	}
}
