package com.mixzing.musicobject.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.Wishlist;
import com.mixzing.musicobject.dao.WishlistDAO;
import com.mixzing.musicobject.impl.WishlistImpl;

public class WishlistDAOImpl extends BaseDAO<Wishlist> implements WishlistDAO{

	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.WishlistDAO#insert(com.mixzing.musicobject.Wishlist)
	 */
	public long insert(Wishlist alt) {
		String sql = "INSERT INTO wishlist " +
		"(globalsong_id, plid, is_in_shopping_cart, is_purchase_pending, time_modified, time_added_to_cart)" + 
		"VALUES (?,?,?,?,?,?)";
		String identityQuery = "VALUES IDENTITY_VAL_LOCAL()";
		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setLong(1, alt.getGlobalSongId());
			ps.setLong(2, alt.getPlid());
			ps.setBoolean(3, alt.isInShoppingCart());
			ps.setBoolean(4, alt.isPurchasePending());
			ps.setTimestamp(5, new Timestamp(alt.getTimeModified()));
			ps.setTimestamp(6, new Timestamp(alt.getTimeAddedToCart()));			
			
			AndroidPreparedStatement aps = (AndroidPreparedStatement) ps;
			idVal = aps.executeInsert();
			
		} catch (SQLException e) {
			throw new UncheckedSQLException(e,sql);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {

				}
			}
		}
		alt.setId(idVal);
		return idVal;
	}
	
	private String tableName = "wishlist";
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.WishlistDAO#readAll()
	 */
	public ArrayList<Wishlist> readAll() {
		ArrayList<Wishlist> list = new ArrayList<Wishlist>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				Wishlist play = createInstance(rs);
				list.add(play);
			}	
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}
		
		return list;
	}
	

	@Override
	protected Wishlist createInstance(ResultSet rs) {
		// TODO Auto-generated method stub
		return new WishlistImpl(rs);
	}		
	
	@Override
	protected String tableName() {
		return tableName;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.WishlistDAO#findWishlistForPlaylist(long)
	 */
	public ArrayList<Wishlist> findWishlistForPlaylist(long plid) {
		String sql = "SELECT * FROM " + tableName() + " WHERE plid = ? ";
		return getCollection(sql, plid);
	}

	public void deleteWishlistForPlaylist(long plid) {
		String sql = "DELETE FROM " + tableName() + " WHERE plid = ? ";
		try {
			DatabaseManager.executeUpdateLongParams(sql, plid);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
		
	}

	public void delete(long id) {
		String sql = "DELETE FROM " + tableName() + " WHERE id = ? ";
		try {
			DatabaseManager.executeUpdateLongParams(sql, id);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}		
	}

	public void updateIsInShoppingCart(Wishlist wish) {
		String sql = "UPDATE " + tableName() + " SET is_in_shopping_cart = ?, time_modified = ?, time_added_to_cart =? WHERE id = ?";
		long timeAdded = System.currentTimeMillis();
		long timeModified = timeAdded;
		int add = 1;
		if(!wish.isInShoppingCart()) {
			timeAdded = wish.getTimeAddedToCart();
			add = 0;
		}
		PreparedStatement ps = null;
		try {	
			Connection conn = DatabaseManager.getConnection();
			try {
				ps = conn.prepareStatement(sql);
				ps.setInt(1, add);
				ps.setTimestamp(2, new Timestamp(timeModified));
				ps.setTimestamp(3, new Timestamp(timeAdded));
				ps.setLong(4, wish.getId());
				ps.executeUpdate();
			} finally {
				DatabaseManager.releaseConnection(conn);
				if(ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {

					}
				}
			}
		} catch (SQLException e) {
			String err = sql + "," + add + "," + timeModified + "," + timeAdded + "," + wish.getId();
			throw new UncheckedSQLException(e,err);
		}

	}		

}
