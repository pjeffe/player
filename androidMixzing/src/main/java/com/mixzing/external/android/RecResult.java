package com.mixzing.external.android;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public class RecResult implements Parcelable {
	private Result result;
	private List<PlaylistRecommendation> recs;
	
	public RecResult(Result r, List<PlaylistRecommendation> recs) {
		this.result = r;
		this.recs = recs;
	}

	public RecResult(Parcel parcel) {
		result = Result.CREATOR.createFromParcel(parcel);
		recs = parcel.createTypedArrayList(PlaylistRecommendation.CREATOR);
	}

	public void writeToParcel(Parcel parcel, int flags) {
		result.writeToParcel(parcel, 0);
		parcel.writeTypedList(recs);
	}

	public static final Parcelable.Creator<RecResult> CREATOR = new Parcelable.Creator<RecResult>() {
		public RecResult createFromParcel(Parcel parcel) {
			return new RecResult(parcel);
		}

		public RecResult[] newArray(int size) {
			return new RecResult[size];
		}
	};

	public Result getResult() {
		return result;
	}

	public List<PlaylistRecommendation> getRecs() {
		return recs;
	}

	public int describeContents() {
		return 0;
	}
}
