package com.mixzing.upgrade;

oneway interface IFacebookListener {
	void onFriendEvent(in int friendCode, in String wallData, in String friendProfile);
	void onServiceError(String message);
	void onRemoteException();
	void onLicenseRequested();
}