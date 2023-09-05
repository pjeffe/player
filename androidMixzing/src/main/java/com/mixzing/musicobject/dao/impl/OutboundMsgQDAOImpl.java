package com.mixzing.musicobject.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.mixzing.MixzingConstants;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.OutboundMsgQ;
import com.mixzing.musicobject.dao.OutboundMsgQDAO;
import com.mixzing.musicobject.impl.OutboundMsgQImpl;


public class OutboundMsgQDAOImpl extends BaseDAO<OutboundMsgQ> implements OutboundMsgQDAO{

	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.OutboundMsgQDAO#insert(com.mixzing.musicobject.OutboundMsgQ)
	 */
	public long insert(OutboundMsgQ out) {
		String sql = "INSERT INTO outbound_msg_q " +
						"(id, lib_id, is_priority, time_added, msg, gsid, msgcount, msgtype, target_server) " +
							"VALUES (?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setLong(1, out.getId());
			ps.setString(2, out.getLibId());
			ps.setBoolean(3, out.isPriority());
			ps.setTimestamp(4, new Timestamp(out.getTimeAdded()));
			ps.setString(5, new String(out.getMsg()));
			ps.setLong(6, out.getGsid());
			ps.setInt(7, out.getMsgCount());
			ps.setString(8, out.getMsgType());
            ps.setString(9,out.getMsgTargetServer().toString());
			ps.execute();
			ps.close();
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
		return 0;
	}
	
	private String tableName = "outbound_msg_q";
	
	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.OutboundMsgQDAO#readAll()
	 */
	public ArrayList<OutboundMsgQ> readAll() {
		ArrayList<OutboundMsgQ> list = new ArrayList<OutboundMsgQ>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				OutboundMsgQ play = new OutboundMsgQImpl(rs);
				list.add(play);
			}
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}
		
		return list;
	}

	@Override
	protected OutboundMsgQ createInstance(ResultSet rs) {
		return new OutboundMsgQImpl(rs);
	}		
	@Override
	protected String tableName() {
		return tableName;
	}

	public int getQCount() {
		PreparedStatement ps = null;
		try {
			String sql = "SELECT COUNT(*) AS rowcount FROM " + tableName();
			Connection con = DatabaseManager.getConnection();
			ps = con.prepareStatement(sql, DatabaseManager.RAW_QUERY_ONLY);
			//ps.setMaxRows(1);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {

				}
			}
		}
		return 0;
	}

	public OutboundMsgQ readQHead() {
		PreparedStatement ps = null;
		OutboundMsgQ inst = null;
		try {
			String sql = null;
			if(MixzingConstants.ALLOW_RATINGS_WITH_LSID) { 
				sql = "SELECT * FROM " + tableName() + " WHERE lib_id <> '-2' ORDER BY is_priority DESC, id ASC LIMIT 1";
			} else {
				sql = "SELECT * FROM " + tableName() + " WHERE gsid >= 0 AND lib_id <> '-2' ORDER BY is_priority DESC, id ASC LIMIT 1";
			}
			Connection con = DatabaseManager.getConnection();
			ps = con.prepareStatement(sql,DatabaseManager.RAW_QUERY_ONLY);
			//ps.setMaxRows(1);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				inst = createInstance(rs);
			}
			rs.close();
			return inst;
			
		} catch (SQLException e) {
			lgr.error(e,e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {

				}
			}
		}
		return inst;
	}

	public void delete(long id) {
		String sql = "DELETE FROM " + tableName() + " WHERE id = ?";
		try {
			DatabaseManager.executeUpdateLongParams(sql, id);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public ArrayList<OutboundMsgQ> getQueuedMessageByGsid(long oldVal) {
		String sql = "SELECT * FROM " + tableName() + " WHERE gsid = ?";
		return getCollection(sql, oldVal);
	}

	public ArrayList<OutboundMsgQ> getQueuedMessages() {
		String sql = "SELECT * FROM " + tableName() + " WHERE lib_id = '-2'";
		return getCollection(sql);
	}

	public void updateGsidAndMessage(OutboundMsgQ msg) {
		String sql = "UPDATE " + tableName() + " SET gsid = ?, msg = ? WHERE id = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseManager.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setLong(1, msg.getGsid());
			ps.setBytes(2, msg.getMsg());
			ps.setLong(3, msg.getId());		
			ps.executeUpdate();
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
	}

	public void updateLibraryIdAndMessage(OutboundMsgQ msg) {
		String sql = "UPDATE " + tableName() + " SET lib_id = ?, msg = ?, target_server = ? WHERE id = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseManager.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, msg.getLibId());
			ps.setBytes(2, msg.getMsg());
            ps.setString(3,msg.getMsgTargetServer().toString());			
			ps.setLong(4, msg.getId());		
			ps.executeUpdate();
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
	}

	public void updateLibraryId(String serverId) {
		String sql = "UPDATE " + tableName() + " SET lib_id = ?";
		Connection conn = null;
		try {
			conn = DatabaseManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, serverId);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e,sql);
		}
		
	}		
	

	
}
