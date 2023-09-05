package com.mixzing.ads;

import android.app.Activity;
import android.view.View;

public interface MixZingAdInterface {

	public String getNextAd();
	
	public View getView(Activity activity);
	
	public AdapterType getType();
	
	public enum AdapterType {
		QUATTRO,
		ADMOB,
		MOBCLIX,
		MILLENNIAL,
		SMAATO,
		INHOUSEREMOTE,
		PONTIFLEX,
		RHYTHM,
		TRIBALFUSION
	}
	
	public enum Gender {
		MALE,
		FEMALE,
		UNKNOWN
	}
	
}
