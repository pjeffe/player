package com.mixzing.musicobject.dto;

public interface WishlistDTO {

	public long getGlobalSongId();

	public void setGlobalSongId(long globalSongId);

	public long getId();

	public void setId(long id);

	public boolean isInShoppingCart();

	public void setInShoppingCart(boolean isInShoppingCart);

	public boolean isPurchasePending();

	public void setPurchasePending(boolean isPurchasePending);

	public long getPlid();

	public void setPlid(long plid);

	public long getTimeAddedToCart();

	public void setTimeAddedToCart(long timeAddedToCart);

	public long getTimeModified();

	public void setTimeModified(long timeModified);

}