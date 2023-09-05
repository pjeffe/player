package com.mixzing.musicobject;

import com.mixzing.musicobject.dto.LibraryDTO;

public interface Library extends LibraryDTO {
	public int getTotalSongCount();
	
	public void setTotalSongCount(int count);
	
	public int getUserPlaylistCount();
	
	public void setUserPlaylistCount(int count);
	
	public int getPlaylistWithMoreThanThreeSongCount();
	
	public void setPlaylistWithMoreThanThreeSongCount(int count);
	
	public int getGsidReceivedCount();
	
	public void setGsidReceivedCount(int count);
	
	public int getResolvedSongCount();
	
	public void setResolvedSongCount(int count);
	
	public EnumLibraryStatus getLibraryStatus();
	
	public void setLibraryStatus(EnumLibraryStatus status);
    
/*    public Map<String,String> getServerEnvelopeParameters();
    
    public String getServerParameter(String key);
    
    public void setServerParameters(Map<String, String> params);*/
    
}
