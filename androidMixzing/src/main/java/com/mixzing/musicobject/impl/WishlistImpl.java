package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.Wishlist;
import com.mixzing.musicobject.dto.impl.WishlistDTOImpl;

public class WishlistImpl extends WishlistDTOImpl implements Wishlist {

	private GlobalSong globalSong;
	
	public WishlistImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WishlistImpl(ResultSet rs) {
		super(rs);
		// TODO Auto-generated constructor stub
	}

	public GlobalSong getGlobalSong() {
		// TODO Auto-generated method stub
		return globalSong;
	}

	public void setGlobalSong(GlobalSong song) {
		globalSong = song;
	}

}
