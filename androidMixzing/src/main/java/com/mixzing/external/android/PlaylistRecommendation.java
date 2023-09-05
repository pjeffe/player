package com.mixzing.external.android;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public class PlaylistRecommendation implements Parcelable {
	private Playlist playlist;
	private ArrayList<Recommendation> recommendations;
	private Long timeUpdated;
	//private static Logger log = Logger.getRootLogger();

	public PlaylistRecommendation(com.mixzing.musicobject.Playlist pl, List<Recommendation>recs) {
		playlist = new Playlist(pl);
		int num = recs.size();
		if (num != 0) {
			recommendations = new ArrayList<Recommendation>(num);
			for (Recommendation rec : recs) {
				recommendations.add(rec);
				long time = rec.getDateAdded();
				if (timeUpdated == null || time > timeUpdated.longValue()) {
					timeUpdated = new Long(time);
				}
			}
		}
	}

	public PlaylistRecommendation(Parcel parcel) {
		playlist = Playlist.CREATOR.createFromParcel(parcel);
		recommendations = parcel.createTypedArrayList(Recommendation.CREATOR);
		timeUpdated = new Long(parcel.readLong());
	}

	public void writeToParcel(Parcel parcel, int flags) {
		playlist.writeToParcel(parcel, 0);
		parcel.writeTypedList(recommendations);
		parcel.writeLong(timeUpdated.longValue());
	}

	public static final Parcelable.Creator<PlaylistRecommendation> CREATOR = new Parcelable.Creator<PlaylistRecommendation>() {
		public PlaylistRecommendation createFromParcel(Parcel source) {
			return new PlaylistRecommendation(source);
		}

		public PlaylistRecommendation[] newArray(int size) {
			return new PlaylistRecommendation[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public Playlist getPlaylist() {
		return playlist;
	}

	public ArrayList<Recommendation> getRecommendations() {
		return recommendations;
	}

	public Long getTimeUpdated() {
		return timeUpdated;
	}

	public void setRecommendations(ArrayList<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}
}
