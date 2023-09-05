package com.mixzing.external.android;

import android.os.Parcel;
import android.os.Parcelable;

public class KeyValuePair implements Parcelable {

	protected String key;
	protected String value;

	public KeyValuePair(String k, String v) {
		this.key = k;
		this.value = v;
	}

	public KeyValuePair(Parcel p) {
		this.key = p.readString();
		this.value = p.readString();
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<KeyValuePair> CREATOR = new Parcelable.Creator<KeyValuePair>() {
		public KeyValuePair createFromParcel(Parcel parcel) {
			return new KeyValuePair(parcel);
		}

		public KeyValuePair[] newArray(int size) {
			return new KeyValuePair[size];
		}
	};
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(key);
		dest.writeString(value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return key + " = " + (value == null ? "<null>" : "'" + value + "'");
	}
}
