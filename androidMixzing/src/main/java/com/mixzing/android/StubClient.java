package com.mixzing.android;

import java.util.List;

import android.os.RemoteException;

import com.mixzing.android.IMixZingClient.Stub;

public class StubClient extends Stub {

	public void onNetworkChange(int status) throws RemoteException {
		// TODO Auto-generated method stub

	}

	public void onNewRecs(List sourceIds) throws RemoteException {
		// TODO Auto-generated method stub

	}

	public void onPlaylistDeleted(List sourceIds) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
