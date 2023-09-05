package com.mixzing.external.android;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.GlobalSongSource;
import com.mixzing.util.Web;

public class SongSpec implements Parcelable {
	private long id;
	private long gsid;
	private String album;
	private String artist;
	private String title;
	private int year;
	private String genre;
	private long duration;
	private String auditionurl;
	private String purchaseurl;
	private String imageurl;

	public SongSpec() {
	}

	public SongSpec(GlobalSong gs, boolean isLocal) {
		id = gs.getId();
		gsid = gs.getGsid();

		if (!isLocal) {
			album = gs.getAlbum();
			artist = gs.getArtist();
			duration = Math.round(gs.getDuration());
			genre = gs.getGenre();
			title = gs.getTitle();
			year = gs.getReleaseYear();

			List<GlobalSongSource> gssList = gs.getGlobalSongSources();
			if (gssList != null && gssList.size() != 0) {
				GlobalSongSource gss = gssList.get(0);
				auditionurl = gss.getAuditionUrl();
				// if this is an external song then add the purchase url
				if (auditionurl != null && auditionurl.length() > 0 && auditionurl.startsWith("http:")) {
					purchaseurl = gss.getPurchaseUrl();
				}
			}
		}
	}

	public SongSpec(long gsid, String auditionurl, String purchaseurl, String imageurl, MPXInfo mpx) {
		this.gsid = gsid;
		this.auditionurl = Web.uriDecode(auditionurl);
		this.purchaseurl = Web.uriDecode(purchaseurl);
		this.imageurl = Web.uriDecode(imageurl);
		if (mpx != null) {
			this.artist = mpx.artist;
			this.album = mpx.album;
			this.title = mpx.title;
			this.year = mpx.year;
			this.genre = mpx.genre;
			this.duration = mpx.duration;
		}
	}

	public SongSpec(Parcel parcel) {
		id = parcel.readLong();
		gsid = parcel.readLong();
		album = parcel.readString();
		artist = parcel.readString();
		auditionurl = parcel.readString();
		duration = parcel.readLong();
		genre = parcel.readString();
		purchaseurl = parcel.readString();
		title = parcel.readString();
		year = parcel.readInt();
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeLong(gsid);
		parcel.writeString(album);
		parcel.writeString(artist);
		parcel.writeString(auditionurl);
		parcel.writeLong(duration);
		parcel.writeString(genre);
		parcel.writeString(purchaseurl);
		parcel.writeString(title);
		parcel.writeInt(year);
	}

	public static final Parcelable.Creator<SongSpec> CREATOR = new Parcelable.Creator<SongSpec>() {
		public SongSpec createFromParcel(Parcel source) {
			return new SongSpec(source);
		}

		public SongSpec[] newArray(int size) {
			return new SongSpec[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public long getId() {
		return id;
	}

	public long getGsid() {
		return gsid;
	}

	public String getAlbum() {
		return album;
	}

	public String getArtist() {
		return artist;
	}

	public String getTitle() {
		return title;
	}

	public int getYear() {
		return year;
	}

	public String getGenre() {
		return genre;
	}

	public long getDuration() {
		return duration;
	}

	public String getAuditionurl() {
		return auditionurl;
	}

	public String getPurchaseurl() {
		return purchaseurl;
	}

	public String getImageurl() {
		return imageurl;
	}

	@Override
	public String toString() {
		return String.format("id %d, gsid %s, %s | %s | %s | %s, year %d, dur %d, aud %s, purch %s",
			id, gsid, artist, title, album, genre, year, duration, auditionurl, purchaseurl);
	}
}
