package com.mixzing.android;

import com.mixzing.android.IMixZingClient;
import com.mixzing.external.android.Result;
import com.mixzing.external.android.IdResult;
import com.mixzing.external.android.LibraryStatusResult;
import com.mixzing.external.android.RecResult;
import com.mixzing.external.android.SongSpecResult;


interface IMixZingManager {

	Result rateSong(long plid, int value, long recoId, long recoAltId);

	RecResult getRecommendations(long lastTime, long plid);

	SongSpecResult getGlobalSong(int sourceId);
	
	LibraryStatusResult getLibraryStatus();

	IdResult getGlobalSongIds(in int[] sourceIds);

	Result registerCallback(IMixZingClient client);

	Result unregisterCallback(IMixZingClient client);

	Result getManagerStatus();
}