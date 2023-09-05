package com.mixzing.musicobject;

import android.os.Parcel;
import android.os.Parcelable;

public enum EnumRatingValue implements Parcelable {
    UNKNOWN(16),
    HATEARTIST(15),
    IGNOREARTIST(14),
    LOVE(2),
    LIKE(1),
    NOT_HERE(0),
    DISLIKE(-1),
    HATE(-2);

    private final int intValue;
    private boolean implied;

    EnumRatingValue(int intValue) {
        this.intValue = intValue;
    }

    public boolean isPositive() {
    	return this == LIKE || this == LOVE;
    }

    public boolean isNegative() {
    	return this == DISLIKE || this == HATE;
    }

    public int describeContents() {
        return ordinal();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ordinal());
    }

    public static final Parcelable.Creator<EnumRatingValue> CREATOR
            = new Parcelable.Creator<EnumRatingValue>() {
       
   	 	public EnumRatingValue createFromParcel(Parcel in) {
   	 		final int ordinal = in.readInt();
   	 		return values()[ordinal];
        }

		public EnumRatingValue[] newArray(int size) {
			return new EnumRatingValue[size];
		}
    };

    public static EnumRatingValue fromIntValue(int intValue) {
        for (EnumRatingValue rter: values()) {
            if (rter.getIntValue() == intValue) {
                return rter;
            }
        }
        return UNKNOWN;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setImplied(boolean implied) {
    	this.implied = implied;
    }

    public boolean isImplied() {
    	return implied;
    }
}
