package com.mixzing.external.android;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class LibraryStatus implements Parcelable {

	protected String libId;
	protected int userPlaylistCount;
    protected int totalSongCount;
    protected int gsidReceivedCount;
    protected int resolvedSongsCount;
    protected int playlistsWithMoreThanThreeSongsCount;
    protected String libraryStatus;
    protected List<KeyValuePair> parameters;

	public static final String STATE_ACTIVE = "ACTIVE";


    public LibraryStatus() {
    }

	public LibraryStatus(Parcel parcel) {
		parameters = new ArrayList<KeyValuePair>();
		libId = parcel.readString();
		userPlaylistCount = parcel.readInt();
		totalSongCount = parcel.readInt();
		gsidReceivedCount = parcel.readInt();
		resolvedSongsCount = parcel.readInt();
		playlistsWithMoreThanThreeSongsCount = parcel.readInt();
		libraryStatus = parcel.readString();
		parcel.readTypedList(parameters, KeyValuePair.CREATOR);
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(libId);
		parcel.writeInt(userPlaylistCount);
		parcel.writeInt(totalSongCount);
		parcel.writeInt(gsidReceivedCount);
		parcel.writeInt(resolvedSongsCount);
		parcel.writeInt(playlistsWithMoreThanThreeSongsCount);
		parcel.writeString(libraryStatus);
		parcel.writeTypedList(parameters);
	}

    public int getUserPlaylistCount() {
		return userPlaylistCount;
	}


	public void setUserPlaylistCount(int userPlaylistCount) {
		this.userPlaylistCount = userPlaylistCount;
	}


	public int getTotalSongCount() {
		return totalSongCount;
	}


	public void setTotalSongCount(int totalSongCount) {
		this.totalSongCount = totalSongCount;
	}


	public int getGsidReceivedCount() {
		return gsidReceivedCount;
	}


	public void setGsidReceivedCount(int gsidReceivedCount) {
		this.gsidReceivedCount = gsidReceivedCount;
	}


	public int getResolvedSongsCount() {
		return resolvedSongsCount;
	}


	public void setResolvedSongsCount(int resolvedSongsCount) {
		this.resolvedSongsCount = resolvedSongsCount;
	}


	public int getPlaylistsWithMoreThanThreeSongsCount() {
		return playlistsWithMoreThanThreeSongsCount;
	}


	public void setPlaylistsWithMoreThanThreeSongsCount(
			int playlistsWithMoreThanThreeSongsCount) {
		this.playlistsWithMoreThanThreeSongsCount = playlistsWithMoreThanThreeSongsCount;
	}


	public String getLibraryStatus() {
		return libraryStatus;
	}


	public void setLibraryStatus(String libraryStatus) {
		this.libraryStatus = libraryStatus;
	}


	public List<KeyValuePair> getParameters() {
		return parameters;
	}


	public void setParameters(List<KeyValuePair> parameters) {
		this.parameters = parameters;
	}


	public String getLibId() {
		return libId;
	}


	public void setLibId(String libId) {
		this.libId = libId;
	}


	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected String getKvp()  {
		String s = "";
		for(KeyValuePair kvp: parameters) {
			s += kvp.getKey() + "=";
			s += kvp.getValue() + ";";
		}
		return s;
	}

	public String toString() {
		return String.format(
			"libId = %s, status = %s, totalSongs = %d, gsidReceived = %d, resolved = %d, playlists = %d, params = %s",
			libId, libraryStatus, totalSongCount, gsidReceivedCount, resolvedSongsCount, userPlaylistCount, getKvp());
	}
	
	public static void main(String[] args) {
		
		LibraryStatus st = new LibraryStatus();
		int i=1;
		st.setGsidReceivedCount(i++);
		st.setLibId("" + i++);
		st.setLibraryStatus(""+i++);
		ArrayList<KeyValuePair> kvp = new ArrayList<KeyValuePair>();
		for(int j=0;j<5;j++) {
			kvp.add(new KeyValuePair("key" + j, "value" +j));
		}
		st.setParameters(kvp);
		st.setResolvedSongsCount(i++);
		st.setTotalSongCount(i++);
		st.setUserPlaylistCount(i++);
		
		System.out.println("Input object: " + st);
		Parcel p = Parcel.obtain();
		st.writeToParcel(p, 0);
		LibraryStatus out = new LibraryStatus(p);
		System.out.println("Output object: " + out);
		
	}
}
