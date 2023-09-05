package com.mixzing.servicelayer;

import java.util.ArrayList;

import com.mixzing.musicobject.Wishlist;

public interface ShoppingCartService {

	public ArrayList<Wishlist> getShoppingCart(long plid);

	public void deleteWishlistForPlaylist(long plid);

	public void addToCart(Wishlist w);

	public void updateShoppingCartState(Wishlist wish);

	public void deleteWishlist(Wishlist wish);

}