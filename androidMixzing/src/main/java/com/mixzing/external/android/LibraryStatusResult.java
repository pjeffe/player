package com.mixzing.external.android;

import android.os.Parcel;
import android.os.Parcelable;

public class LibraryStatusResult implements Parcelable {

	protected LibraryStatus status;
	protected Result result;
	
	
	public LibraryStatusResult(Result resr, LibraryStatus libraryStatus) {
		// TODO Auto-generated constructor stub
		this.result = resr;
		this.status = libraryStatus;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<LibraryStatusResult> CREATOR = new Parcelable.Creator<LibraryStatusResult>() {
		public LibraryStatusResult createFromParcel(Parcel parcel) {
			return new LibraryStatusResult(parcel);
		}

		public LibraryStatusResult[] newArray(int size) {
			return new LibraryStatusResult[size];
		}
	};
	
	public LibraryStatusResult(Parcel parcel) {
		result = Result.CREATOR.createFromParcel(parcel);
		status = new LibraryStatus(parcel);
	}

	public void writeToParcel(Parcel parcel, int flags) {
		result.writeToParcel(parcel, 0);
		status.writeToParcel(parcel, 0);
	}

	public LibraryStatus getStatus() {
		return status;
	}

	public void setStatus(LibraryStatus status) {
		this.status = status;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
