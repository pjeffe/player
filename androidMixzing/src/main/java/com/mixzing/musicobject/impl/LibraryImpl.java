package com.mixzing.musicobject.impl;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.mixzing.musicobject.Library;
import com.mixzing.musicobject.dto.impl.LibraryDTOImpl;

public class LibraryImpl extends LibraryDTOImpl implements Library {
	
	protected int gsidReceivedCount;	
	protected int playlistWithMoreThanThreeSongCount;
	protected int totalSongCount;
	protected int userPlaylistCount;
    protected Map<String,String> map = new HashMap<String,String>();
	
	public LibraryImpl() {
		super();
	}
	
	public LibraryImpl(ResultSet rs) {
		super(rs);
	}

	public int getGsidReceivedCount() {
		return gsidReceivedCount;
	}



	public int getPlaylistWithMoreThanThreeSongCount() {
		return playlistWithMoreThanThreeSongCount;
	}

	public int getTotalSongCount() {
		return totalSongCount;
	}


	public int getUserPlaylistCount() {
		return userPlaylistCount;
	}

	public void setGsidReceivedCount(int count) {
		gsidReceivedCount = count;
	}



	public void setPlaylistWithMoreThanThreeSongCount(int count) {
		playlistWithMoreThanThreeSongCount = count;
	}

	public void setTotalSongCount(int count) {
		totalSongCount = count;
	}

	public void setUserPlaylistCount(int count) {
		userPlaylistCount = count;	
	}

    public Map<String, String> getServerEnvelopeParameters() {
        return map;
    }

    public String getServerParameter(String key) {
        return map.get(key);
    }

    public void setServerParameter(String key, String value) {
        map.put(key,value);
    }
    
    public void setServerParameters(Map<String, String> params) {
    	if(params == null) {
    		// a default map is created on construction
    		map.clear();
    	} else {
    		map = params;
    	}
    }
}
