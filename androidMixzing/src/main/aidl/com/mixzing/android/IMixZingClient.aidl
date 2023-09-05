package com.mixzing.android;

oneway interface IMixZingClient {

	void onNewRecs(in List sourceIds);

	void onPlaylistDeleted(in List sourceIds);

	void onNetworkChange(in int status);
}