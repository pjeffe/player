package com.mixzing.external.android;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.mixzing.log.Logger;
import com.mixzing.musicobject.RecoAlternate;
import com.mixzing.servicelayer.TrackService;


public class Recommendation implements Parcelable {
	private static final Logger log = Logger.getRootLogger();
	private long plid;
	private long recid;
	private long dateAdded;
	private float score;
	private List<RecommendationAlternate> alternates;
	//private static Logger log = Logger.getRootLogger();

	public Recommendation(com.mixzing.musicobject.Recommendation rec, TrackService trkSvc) {
		plid = rec.getPlid();
		recid = rec.getId();
		dateAdded = rec.getTimeReceived();
		score = rec.getScore();
		List<RecoAlternate> alts = rec.getAlternates();
		if (alts != null) {
			int num = alts.size();
			if (num != 0) {
				alternates = new ArrayList<RecommendationAlternate>(num);
				for (RecoAlternate alt : alts) {
					alternates.add(new RecommendationAlternate(alt, recid, trkSvc));
				}
			}
		}
	}

	public Recommendation(Parcel parcel) {
		plid = parcel.readLong();
		recid = parcel.readLong();
		dateAdded = parcel.readLong();
		score = parcel.readFloat();
		alternates = parcel.createTypedArrayList(RecommendationAlternate.CREATOR);
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(plid);
		parcel.writeLong(recid);
		parcel.writeLong(dateAdded);
		parcel.writeFloat(score);
		parcel.writeTypedList(alternates);
	}

	public static final Parcelable.Creator<Recommendation> CREATOR = new Parcelable.Creator<Recommendation>() {
		public Recommendation createFromParcel(Parcel source) {
			return new Recommendation(source);
		}

		public Recommendation[] newArray(int size) {
			return new Recommendation[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public long getRecId() {
		return recid;
	}

	public long getDateAdded() {
		return dateAdded;
	}

	public float getScore() {
		return score;
	}

	public List<RecommendationAlternate> getAlternates() {
		return alternates;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(String.format("plid %d, recid %d, score %.3f, date %d",
			plid, recid, score, dateAdded));
		for (RecommendationAlternate alt : alternates) {
			sb.append("\n    " + alt);
		}
		return sb.toString();
	}
}
