package com.mixzing.external.android;


import android.os.Parcel;
import android.os.Parcelable;

import com.mixzing.log.Logger;
import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.RecoAlternate;
import com.mixzing.musicobject.Track;
import com.mixzing.servicelayer.TrackService;

public class RecommendationAlternate implements Parcelable {
	private long id;
	private long recid;
	private float rank;
	private boolean isLocal;
	private SongSpec song;
	private int sourceId;
	private static final Logger log = Logger.getRootLogger();

	public RecommendationAlternate(RecoAlternate alt, long recid, TrackService trkSvc) {
		id = alt.getId();
		this.recid = recid;
		isLocal = alt.isLocal();
		rank = alt.getRank();
		GlobalSong gs = alt.getGlobalSong();
		song = new SongSpec(gs, isLocal);
		setSourceId(gs.getId(), trkSvc);
	}

	public RecommendationAlternate(Parcel parcel) {
		id = parcel.readLong();
		recid = parcel.readLong();
		isLocal = parcel.readInt() == 1;
		rank = parcel.readFloat();
		song = SongSpec.CREATOR.createFromParcel(parcel);
		sourceId = parcel.readInt();
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeLong(recid);
		parcel.writeInt(isLocal ? 1 : 0);
		parcel.writeFloat(rank);
		song.writeToParcel(parcel, 0);
		parcel.writeInt(sourceId);
	}

	private void setSourceId(long globalSongId, TrackService trkSvc) {
		if (isLocal) {
			Track track = trkSvc.findByGlobalSongId(globalSongId);
			if (track != null) {
				String sid = track.getSourceId();
				sourceId = (int)track.getAndroidId();
			}
			else if (Logger.IS_DEBUG_ENABLED) {
				log.error("Track.setSourceId: unable to get source track for globalsong_id " + globalSongId);
			}
		}
	}

	public static final Parcelable.Creator<RecommendationAlternate> CREATOR = new Parcelable.Creator<RecommendationAlternate>() {
		public RecommendationAlternate createFromParcel(Parcel source) {
			return new RecommendationAlternate(source);
		}

		public RecommendationAlternate[] newArray(int size) {
			return new RecommendationAlternate[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public long getId() {
		return id;
	}

	public long getRecId() {
		return recid;
	}

	public float getRank() {
		return rank;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public SongSpec getSong() {
		return song;
	}

	public int getSourceId() {
		return sourceId;
	}

	@Override
	public String toString() {
		return String.format("recid = %d, altid = %d, sourceid = %d, local = %s: %s", recid, id, sourceId, isLocal, song);
	}
}
