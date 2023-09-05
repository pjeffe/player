package com.mixzing.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mixzing.external.android.Images;
import com.mixzing.log.Logger;


public class SongIdData extends CoreMetaData {
	private static final Logger log = Logger.getRootLogger();
	protected double albumScore;


	public SongIdData(JSONObject json) {
		try {
			artist = json.getString("artist");
			album = json.getString("album");
			title = json.getString("title");
			genre = json.getString("genre");
			duration = json.getLong("duration");
			year = json.getInt("year");
			trackNum = json.getInt("tracknum");
			posIndex = json.getInt("albumindex");
			albumScore = json.getDouble("albumscore");
			images = Images.parseImages(json.getJSONArray("albumarts"));
		}
		catch (JSONException e) {
			log.error("IdData.ctor: malformed json: " + json + ":", e);
		}
		catch (Exception e) {
			log.error("IdData.ctor: " + json + ":", e);
		}
	}

	public SongIdData(Parcel parcel) {
		super(parcel);
		albumScore = parcel.readDouble();
	}

	public void writeToParcel(Parcel parcel, int flags) {
		super.writeToParcel(parcel, flags);
		parcel.writeDouble(albumScore);
	}

	public static final Parcelable.Creator<SongIdData> CREATOR = new Parcelable.Creator<SongIdData>() {
		public SongIdData createFromParcel(Parcel source) {
			return new SongIdData(source);
		}

		public SongIdData[] newArray(int size) {
			return new SongIdData[size];
		}
	};

	public double getAlbumScore() {
		return albumScore;
	}

	public void setAlbumScore(double albumScore) {
		this.albumScore = albumScore;
	}

	public Images getImages() {
		return images;
	}

	public void setImages(Images images) {
		this.images = images;
	}

	public String getImageURLSmall() {
		return super.getImageURLSmall();
	}

	public String getImageURLLarge() {
		return super.getImageURLLarge();
	}
}
