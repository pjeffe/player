package com.mixzing.upgrade;

import com.mixzing.upgrade.IFacebookListener;

interface IMarketLicenseService
{
	void performMarketLicenseCheck (in int friendId, in String packageName, in IFacebookListener listener);
}