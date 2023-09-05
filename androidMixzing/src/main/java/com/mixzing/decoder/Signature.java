package com.mixzing.decoder;

import android.os.Parcel;
import android.os.Parcelable;


public class Signature implements Parcelable {
	public int length;
	public int rate;
	public int chans;
	public String sig;


	public Signature(int length, int rate, int chans, String sig) {
		this.length = length;
		this.rate = rate;
		this.chans = chans;
		this.sig = sig;
	}

	public Signature(Parcel parcel) {
		length = parcel.readInt();
		rate = parcel.readInt();
		chans = parcel.readInt();
		sig = parcel.readString();
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(length);
		parcel.writeInt(rate);
		parcel.writeInt(chans);
		parcel.writeString(sig);
	}

	public static final Parcelable.Creator<Signature> CREATOR = new Parcelable.Creator<Signature>() {
		public Signature createFromParcel(Parcel source) {
			return new Signature(source);
		}

		public Signature[] newArray(int size) {
			return new Signature[size];
		}
	};


	public int describeContents() {
		return 0;
	}


	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getChans() {
		return chans;
	}

	public void setChans(int chans) {
		this.chans = chans;
	}

	public String getSig() {
		return sig;
	}

	public void setSig(String sig) {
		this.sig = sig;
	}

	@Override
	public String toString() {
		return super.toString() + ": " + sig.substring(0, 120);
	}
}