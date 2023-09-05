package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.dto.WishlistDTO;

public class WishlistDTOImpl implements WishlistDTO {

	protected long id;
	
	protected long globalSongId;
	
	protected long plid;
	
	protected boolean isInShoppingCart;
	
	protected boolean isPurchasePending;
	
	protected long timeModified;
	
	protected long timeAddedToCart;


	public String toString() {
		return "Wishlist: " + id + " : " + globalSongId + " : " + plid + " : " + isInShoppingCart + " : " + isPurchasePending + " : " + timeAddedToCart + " : " + timeModified;
	}
	
	public WishlistDTOImpl() {
		id = Long.MIN_VALUE;
	}
	
	public WishlistDTOImpl(ResultSet rs) {
		try {
			id = rs.getLong("id");
			globalSongId = rs.getLong("globalsong_id");
			plid = rs.getLong("plid");
			isInShoppingCart = rs.getInt("is_in_shopping_cart") == 1 ? true : false;
			isPurchasePending = rs.getInt("is_purchase_pending") == 1 ? true : false;
			timeModified = rs.getTimestamp("time_modified").getTime();
			timeAddedToCart = rs.getTimestamp("time_added_to_cart").getTime();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e,"Create Object");
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#getGlobalSongId()
	 */
	public long getGlobalSongId() {
		return globalSongId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#setGlobalSongId(long)
	 */
	public void setGlobalSongId(long globalSongId) {
		this.globalSongId = globalSongId;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#isInShoppingCart()
	 */
	public boolean isInShoppingCart() {
		return isInShoppingCart;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#setInShoppingCart(boolean)
	 */
	public void setInShoppingCart(boolean isInShoppingCart) {
		this.isInShoppingCart = isInShoppingCart;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#isPurchasePending()
	 */
	public boolean isPurchasePending() {
		return isPurchasePending;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#setPurchasePending(boolean)
	 */
	public void setPurchasePending(boolean isPurchasePending) {
		this.isPurchasePending = isPurchasePending;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#getPlid()
	 */
	public long getPlid() {
		return plid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#setPlid(long)
	 */
	public void setPlid(long plid) {
		this.plid = plid;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#getTimeAddedToCart()
	 */
	public long getTimeAddedToCart() {
		return timeAddedToCart;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#setTimeAddedToCart(long)
	 */
	public void setTimeAddedToCart(long timeAddedToCart) {
		this.timeAddedToCart = timeAddedToCart;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#getTimeModified()
	 */
	public long getTimeModified() {
		return timeModified;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.WishlistDB#setTimeModified(long)
	 */
	public void setTimeModified(long timeModified) {
		this.timeModified = timeModified;
	}

}
