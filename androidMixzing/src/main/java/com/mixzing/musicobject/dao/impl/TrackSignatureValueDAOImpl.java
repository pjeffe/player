package com.mixzing.musicobject.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.mixzing.derby.AndroidPreparedStatement;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.TrackSignatureValue;
import com.mixzing.musicobject.dao.TrackSignatureValueDAO;
import com.mixzing.musicobject.impl.TrackSignatureValueImpl;
import com.mixzing.signature.common.MixzingAudioInfo;
import com.mixzing.signature.common.MixzingPeak;
import com.mixzing.signature.common.MixzingSignatureData;

public class TrackSignatureValueDAOImpl extends BaseDAO<TrackSignatureValue>
        implements TrackSignatureValueDAO {

    public TrackSignatureValueDAOImpl() {
        super();
    }

    public void addSignature(long lsid, MixzingAudioInfo info, MixzingSignatureData sigData) {
        TrackSignatureValueImpl impl = new TrackSignatureValueImpl();
        impl.setBitRate(info.getBitRate());
        impl.setChannels(info.getChannels());
        impl.setCodeVersion(sigData.getCodeVersion()+"");
        impl.setDuration(sigData.getWindow().getDuration());
        impl.setSuperWindowMs(sigData.getWindow().getSuperWindow());
        impl.setFrequency(info.getFrequency());
        impl.setLsid(lsid);
        impl.setMsPerFrame(info.getMsPerframe());
        impl.setSetToServer(false);
        impl.setSig(convertToList(convertToString(sigData.getSignature()))); // XXX TODO FIX THIS !!
        impl.setSkip(sigData.getWindow().getSkip());
        
        insert(impl);
    }

    @Override
    protected TrackSignatureValue createInstance(ResultSet rs) {
        TrackSignatureValue ts =  new TrackSignatureValueImpl();
        try {
            ts.setId(rs.getLong("id"));
            ts.setLsid(rs.getLong("lsid"));
			ts.setSkip(rs.getInt("skip"));
			ts.setDuration(rs.getInt("duration"));
			ts.setSuperWindowMs(rs.getInt("super_window_ms"));
			ts.setSig(convertToList(rs.getString("signature")));
            ts.setChannels(rs.getInt("channels"));
            ts.setBitRate(rs.getInt("bitrate"));
            ts.setFrequency(rs.getInt("frequency"));
            ts.setMsPerFrame(rs.getFloat("ms_per_frame"));
            ts.setSetToServer(rs.getInt("sent_to_server") == 1 ? true : false);
            ts.setCodeVersion(rs.getString("code_version"));
            
        } catch ( SQLException e) {
            throw new UncheckedSQLException(e);
        }
        return ts;
    }

    @Override
    protected String tableName() {
        // TODO Auto-generated method stub
        return "track_signature_value";
    }
    
    public long insert(TrackSignatureValue gss) {
        String sql = "INSERT INTO track_signature_value " +
        "(lsid, skip, duration, super_window_ms, signature, energy, channels, bitrate, frequency, ms_per_frame, code_version) " +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        String identityQuery = "VALUES IDENTITY_VAL_LOCAL()";
        long idVal = Long.MIN_VALUE;
        PreparedStatement ps = null;
        try {
            ps = DatabaseManager.getConnection().prepareStatement(sql);
            ps.setLong(1, gss.getLsid());
            ps.setInt(2, gss.getSkip());
            ps.setInt(3, gss.getDuration());
            ps.setInt(4, gss.getSuperWindowMs());
            ps.setString(5, convertToString(gss.getSig()));
            ps.setString(6, "");
            ps.setInt(7,gss.getChannels());
            ps.setInt(8, gss.getBitRate());
            ps.setInt(9,gss.getFrequency());
            ps.setFloat(10, gss.getMsPerFrame());
            ps.setString(11, gss.getCodeVersion());

			
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

    public TrackSignatureValue findSignature(long lsid, int skip, int dur, int superwin, String codeVersion) {
        /*
         * XXX: Not using parameterized query since I did not have access to a READONE method that 
         * takes a String also
         *  
         */
    	String sql = "SELECT * FROM " + tableName() + " WHERE lsid = " + lsid + 
        								" AND skip = " + skip + 
        								" AND duration =  " + dur +
        								" AND super_window_ms =  " +  superwin +
        								" AND code_version = '" + codeVersion + "'";
        
        return readOne(sql);
    }

    
    private String convertToString(List<Long> peaks) {
        String ret = null;
        if(!peaks.isEmpty()) {
         ret = "";
         for(Long peak : peaks) {
             ret += peak + "|";
         }
        }
        return ret;
    }

    private List<Long> convertToList(String sigs) {
        ArrayList<Long> sig = new ArrayList<Long>();
        if(sigs != null) {
            StringTokenizer tok = new StringTokenizer(sigs,"|");
            while(tok.hasMoreElements()) {
                String s = (String) tok.nextElement();
                Long d = Long.valueOf(s);
                sig.add(d);
            }
        }
        return sig;
    }

	public List<TrackSignatureValue> findSignature(long lsid) {
        String sql = "SELECT * FROM " + tableName() + " WHERE lsid = ?";
        return getCollection(sql, lsid);
	}

	public void markAsSent(Long... ids) {
		for(long id : ids) {
			String sql = "DELETE FROM " + tableName() + "  WHERE id = ?";
			try {
				DatabaseManager.executeUpdateLongParams(sql, id);
			} catch (SQLException e) {
				throw new UncheckedSQLException(e);
			}
		}
	}

	public List<TrackSignatureValue> findUnsentSignatures() {
	       String sql = "SELECT * FROM " + tableName() + " WHERE sent_to_server = 0";
	       return getCollection(sql);
	}
}
