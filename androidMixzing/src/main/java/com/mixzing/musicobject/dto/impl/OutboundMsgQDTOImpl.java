package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.dto.OutboundMsgQDTO;

public class OutboundMsgQDTOImpl implements OutboundMsgQDTO {

	protected static Logger lgr = Logger.getRootLogger();
	
	protected long id;
	
	protected String libId;
	
	protected long gsid;
	
	protected boolean isPriority;
	
	protected long timeAdded;
	
	protected int msgCount;
	
	protected String msgType;
	
	protected byte[] msg;
    
    protected TargetServer targetServer;
		
	public String toString() {
		String s = "MSGQ: " + id + " : " + libId + ":" + isPriority + " : " + timeAdded + ":" + targetServer;
		s = s + " bytes: ";
		for(int i=0;i<msg.length;i++) {
			String s1 = Byte.toString(msg[i]);
			s = s + ":" + s1;
		}
		return s;
	}

	public OutboundMsgQDTOImpl() {
		this.gsid = 0;
        this.targetServer = TargetServer.APPSERVER_JSON;
	}
    
	public OutboundMsgQDTOImpl(ResultSet rs) {
		try {
			this.setId(rs.getLong("id"));
			this.setMsgCount(rs.getInt("msgcount"));
			this.setMsgType(rs.getString("msgtype"));
			this.setGsid(rs.getLong("gsid"));
			this.setLibId(rs.getString("lib_id"));
			this.setTimeAdded(rs.getTimestamp("time_added").getTime());
			this.setPriority(rs.getInt("is_priority") == 1 ? true : false);
			this.setMsg(rs.getString("msg").getBytes());
			
			TargetServer svr = TargetServer.APPSERVER_JSON;
			String target_server = rs.getString("target_server");
            try {
            	svr = TargetServer.valueOf(target_server);
            } catch (Exception e) {
				lgr.error("OutboundMsgQDTOImpl: Unexpected error target_server value was :" +  target_server + ": for " + this.getMsgType() + ":" + this.getTimeAdded(),e);
            }
            this.setMsgTargetServer(svr);
            
		} catch ( SQLException e) {
			throw new UncheckedSQLException(e,"Create Outboung_msg_q");
		} 
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.OutboundMsgQDB#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.OutboundMsgQDB#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.OutboundMsgQDB#isPriority()
	 */
	public boolean isPriority() {
		return isPriority;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.OutboundMsgQDB#setPriority(boolean)
	 */
	public void setPriority(boolean isPriority) {
		this.isPriority = isPriority;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.OutboundMsgQDB#getMsg()
	 */
	public byte[] getMsg() {
		return msg;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.OutboundMsgQDB#setMsg(byte[])
	 */
	public void setMsg(byte[] msg) {
		this.msg = msg;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.OutboundMsgQDB#getTimeAdded()
	 */
	public long getTimeAdded() {
		return timeAdded;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.musicobject.db.impl.OutboundMsgQDB#setTimeAdded(long)
	 */
	public void setTimeAdded(long timeAdded) {
		this.timeAdded = timeAdded;
	}

	public String getLibId() {
		// TODO Auto-generated method stub
		return libId;
	}

	public void setLibId(String libId) {
		this.libId = libId;
	}

	public long getGsid() {
		return gsid;
	}

	public void setGsid(long gsid) {
		this.gsid = gsid;
	}

	public int getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}


    public TargetServer getMsgTargetServer() {
        // TODO Auto-generated method stub
        return targetServer;
    }

    public void setMsgTargetServer(TargetServer targetServer) {
        this.targetServer = targetServer;
        
    }
	
}
