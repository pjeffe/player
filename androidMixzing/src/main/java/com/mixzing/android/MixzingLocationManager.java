package com.mixzing.android;

import android.location.Location;
import android.os.Handler;

public interface MixzingLocationManager {

	public Location getLocation();
	
	public void registerForLocationFromDevice(Handler handler);
	
	public void unRegisterForLocationFromDevice();

}