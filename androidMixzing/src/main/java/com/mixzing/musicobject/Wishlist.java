package com.mixzing.musicobject;

import com.mixzing.musicobject.dto.WishlistDTO;

public interface Wishlist  extends WishlistDTO{

	public void setGlobalSong(GlobalSong song);

	public GlobalSong getGlobalSong();

}