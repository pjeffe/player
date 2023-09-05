package com.mixzing.servicelayer;

import java.io.File;

public interface SignatureService extends BaseService {

    public String getInstalledCodeVersion();
    
    public void updateSignatureCode(File sigJar);
    
	public void addSignatureRequest(long lsid, int skip, int duration, int superWinMs, boolean isPri, boolean isLong);
	
	public void wakeup();
    
}