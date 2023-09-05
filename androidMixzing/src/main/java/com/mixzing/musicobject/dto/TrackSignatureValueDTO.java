package com.mixzing.musicobject.dto;

import java.util.List;

public interface TrackSignatureValueDTO {

    public long getId();

    public void setId(long id);

    public long getLsid();

    public void setLsid(long lsid);

    public List<Long> getSig();

    public void setSig(List<Long> sig);
    
    public int getSkip();

	public void setSkip(int skip);

	public int getDuration();

	public void setDuration(int dur);

	public int getSuperWindowMs();

	public void setSuperWindowMs(int swin);
	
	public int getFrequency();

	public void setFrequency(int freq);	

	public int getBitRate();

	public void setBitRate(int freq);
	
	public int getChannels();

	public void setChannels(int freq);
	
	public float getMsPerFrame();

	public void setMsPerFrame(float msFrame);	
	
	public String getCodeVersion();
	
	public void setCodeVersion(String ver);
	
	public boolean isSentToServer();
	
	public void setSetToServer(boolean sent);
}