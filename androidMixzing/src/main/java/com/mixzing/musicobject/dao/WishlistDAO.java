package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.musicobject.Wishlist;

public interface WishlistDAO extends MusicObjectDAO<Wishlist>{

	public long insert(Wishlist alt);

	public ArrayList<Wishlist> readAll();

	public ArrayList<Wishlist> findWishlistForPlaylist(long plid);

	public void deleteWishlistForPlaylist(long plid);

	public void delete(long id);

	public void updateIsInShoppingCart(Wishlist wish);

}