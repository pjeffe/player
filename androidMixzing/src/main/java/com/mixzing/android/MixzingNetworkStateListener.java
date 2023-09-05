package com.mixzing.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.mixzing.message.transport.ServerTransport;


public class MixzingNetworkStateListener {

	public static final int TEMPORARILY_UNAVAILABLE = 1;
	public static final int AVAILABLE = 2;
	ServerTransport poster;
	ServerTransport getter;

	Context context;
	NetworkStateBroadcastReceiver networkReceiver;
	public MixzingNetworkStateListener(Context context, ServerTransport poster, ServerTransport getter) {
		this.poster = poster;
		this.getter = getter;
		networkReceiver = new NetworkStateBroadcastReceiver();
		IntentFilter networkIntentFilter = new IntentFilter();
		networkIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		context.registerReceiver(networkReceiver, networkIntentFilter);
		this.context = context;
	}

	private class NetworkStateBroadcastReceiver extends BroadcastReceiver {
		@Override public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				int networkState = TEMPORARILY_UNAVAILABLE;

				boolean noConnectivity =
					intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
				if (!noConnectivity) {
					networkState = AVAILABLE;
				}
				informTransports(networkState);
			}
		}
	}

	private void informTransports(int state) {
		boolean isAvailable = false;
		if(state == AVAILABLE) {
			isAvailable = true;
		}
		if(poster != null) {
			poster.networkStateChanged(isAvailable);
		}
		if(getter != null) {
			getter.networkStateChanged(isAvailable);
		}
	}


	public void shutdown() {
		context.unregisterReceiver(networkReceiver);
	}
}
