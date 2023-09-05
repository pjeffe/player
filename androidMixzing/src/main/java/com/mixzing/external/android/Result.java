package com.mixzing.external.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Result implements Parcelable {

	public enum Status {
		SUCCESS,
		ERROR,
		WAIT,
		ENOSPC
	}
	
	private int status;
	
	public Result(Status status) {
		this.status = status.ordinal();
	}

	public Result(Parcel parcel) {
		status = parcel.readInt();
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(status);
	}

	public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
		public Result createFromParcel(Parcel parcel) {
			return new Result(parcel);
		}

		public Result[] newArray(int size) {
			return new Result[size];
		}
	};

	public Status getStatus() {
		return Status.values()[status];
	}

	public int describeContents() {
		return 0;
	}
}
