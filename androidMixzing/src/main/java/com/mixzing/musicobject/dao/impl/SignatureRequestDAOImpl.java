package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.EnumSignatureProcessingStatus;
import com.mixzing.musicobject.SignatureRequest;
import com.mixzing.musicobject.dao.SignatureRequestDAO;
import com.mixzing.musicobject.impl.SignatureRequestImpl;

public class SignatureRequestDAOImpl extends BaseDAO<SignatureRequest> implements SignatureRequestDAO{

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.SignatureRequestDAO#insert(com.mixzing.musicobject.SignatureRequest)
	 */
	public long insert(SignatureRequest gss) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Inserting signature request for " + gss.getLsid());
		String sql = "INSERT INTO signature_request " +
		"(lsid, processing_status, is_priority, skip, duration, super_window_ms) " +
		"VALUES (?,?,?,?,?,?)";
		String identityQuery = "VALUES IDENTITY_VAL_LOCAL()";
		long idVal = Long.MIN_VALUE;
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.getConnection().prepareStatement(sql);
			ps.setLong(1, gss.getLsid());
			ps.setInt(2, gss.getProcessingStatus().getIntValue());
			ps.setInt(3, gss.isPriority() ? 1 : 0);			
			ps.setInt(4, gss.getSkip());			
			ps.setInt(5, gss.getDuration());
			ps.setInt(6, gss.getSuperWindowMs());

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
		gss.setId(idVal);
		return idVal;
	}

	private String tableName = "signature_request";

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.dao.impl.SignatureRequestDAO#readAll()
	 */
	public ArrayList<SignatureRequest> readAll() {
		ArrayList<SignatureRequest> list = new ArrayList<SignatureRequest>();
		String sql = "SELECT * from " + tableName;
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				SignatureRequest play = createInstance(rs);
				list.add(play);
			}
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}

		return list;
	}



	@Override
	protected SignatureRequest createInstance(ResultSet rs) {
		// TODO Auto-generated method stub
		return new SignatureRequestImpl(rs);
	}			

	@Override
	protected String tableName() {
		return tableName;
	}		

	public List<SignatureRequest> getRequested() {

		List<SignatureRequest> list = new ArrayList<SignatureRequest>();


		String sql = "SELECT * from " + tableName() + " WHERE processing_status = " 
		+ EnumSignatureProcessingStatus.REQUESTED.getIntValue() +
		" ORDER BY is_priority DESC";
		try {
			ResultSet rs = DatabaseManager.executeQueryNoParams(DatabaseManager.getConnection(), sql);
			while(rs.next()) {
				SignatureRequest sig = createInstance(rs);
				list.add(sig);
			}	
			rs.close();
		} catch (SQLException e) {
			lgr.error(e,e);
		}

		return list;		
	}


	public List<SignatureRequest> findUnprocessedRequest(long lsid, int skip, int duration, int superWinMs) {
		String sql = "SELECT * from " + tableName() + 
		" WHERE lsid = ? AND skip = ? AND duration = ? AND super_window_ms = ? AND processing_status = ?";
		return getCollection(sql, lsid, (long) skip, (long) duration, (long) superWinMs,  (long) EnumSignatureProcessingStatus.REQUESTED.getIntValue() );

	}

	public void signatureProcessed(long id,boolean isError) {
		String sql = "UPDATE " + tableName() + " SET processing_status = ? WHERE id = ?";
		long status = EnumSignatureProcessingStatus.DONE.getIntValue();
		if(isError) {
			status = EnumSignatureProcessingStatus.ERRORED.getIntValue();
		} 
		try {
			DatabaseManager.executeUpdateLongParams(sql, status, id);
			if(Logger.IS_TRACE_ENABLED)
				lgr.trace(sql + ": " + status + ", " + id);
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
}
