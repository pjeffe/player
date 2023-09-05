package com.mixzing.external.android;

import android.os.Parcel;
import android.os.Parcelable;


public class SongSpecResult implements Parcelable {
	private Result result;
	private SongSpec song;

	public SongSpecResult(Result r, SongSpec song) {
	    this.result = r;
	    this.song = song;
	}


	public SongSpecResult(Parcel parcel) {
		result = Result.CREATOR.createFromParcel(parcel);
		song = SongSpec.CREATOR.createFromParcel(parcel);
	}

	public void writeToParcel(Parcel parcel, int flags) {
		result.writeToParcel(parcel, 0);
		song.writeToParcel(parcel, 0);
	}

	public static final Parcelable.Creator<SongSpecResult> CREATOR = new Parcelable.Creator<SongSpecResult>() {
		public SongSpecResult createFromParcel(Parcel parcel) {
			return new SongSpecResult(parcel);
		}

		public SongSpecResult[] newArray(int size) {
			return new SongSpecResult[size];
		}
	};

	public Result getResult() {
		return result;
	}

	public SongSpec getSong() {
		return song;
	}

	public int describeContents() {
		return 0;
	}
}
