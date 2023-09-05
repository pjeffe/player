package com.mixzing.musicobject.dto;

import com.mixzing.musicobject.EnumPlaylistType;

public interface PlaylistDTO {

	public  boolean isDeleted();

	public  void setDeleted(boolean isDeleted);

	public  String getName();

	public  void setName(String name);

	public  EnumPlaylistType getPlaylistType();

	public  void setPlaylistType(EnumPlaylistType pl_type);

	public  long getId();

	public  void setId(long plid);

	public  String getSourceSpecificId();

	public  void setSourceSpecificId(String sourceSpecificId);

}