package com.mixzing.servicelayer;

import java.util.Map;

import com.mixzing.musicobject.EnumLibraryStatus;

public interface LibraryService {
	public com.mixzing.musicobject.Library getLibrary() ;
	
	public void updateLibraryId(String serverId) ;
	
	public void updateResolvedSongCount(int count);
	
	public void updateLibraryStatus(EnumLibraryStatus status) ;
	
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
	
    public Map<String,String> getServerParameters();
    
    public String getServerParameterString(String key);
    
    public long getServerParameterLong(String key);
    
    public void setServerParameters(Map<String, String> params);
    
    public void registerUpdateListener(ServerParameterUpdateListener listener);
    
    public void removeUpdateListener(ServerParameterUpdateListener listener);

    public void parametersUpdated();
    
    public long getLocationDelay();
    
}
