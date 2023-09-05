package com.mixzing.external.android;

import android.os.Parcel;
import android.os.Parcelable;


public class IdResult implements Parcelable {
	private Result result;
	private long[] ids;

	public IdResult(Result r, long[] ids) {
	    this.result = r;
	    this.ids = ids;
	}

	public IdResult(Parcel parcel) {
		result = Result.CREATOR.createFromParcel(parcel);
		ids = parcel.createLongArray();
	}

	public void writeToParcel(Parcel parcel, int flags) {
		result.writeToParcel(parcel, 0);
		parcel.writeLongArray(ids);
	}

	public static final Parcelable.Creator<IdResult> CREATOR = new Parcelable.Creator<IdResult>() {
		public IdResult createFromParcel(Parcel parcel) {
			return new IdResult(parcel);
		}

		public IdResult[] newArray(int size) {
			return new IdResult[size];
		}
	};

	public Result getResult() {
		return result;
	}

	public long[] getIds() {
		return ids;
	}

	public int describeContents() {
		return 0;
	}
}
