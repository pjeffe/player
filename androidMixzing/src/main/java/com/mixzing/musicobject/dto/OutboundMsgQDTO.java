package com.mixzing.musicobject.dto;


public interface OutboundMsgQDTO {

    public enum TargetServer {
        APPSERVER,
        APPSERVER_JSON,
        WEBSERVER
    }
    
	public long getId();

	public void setId(long id);
	
	public String getLibId();
	
	public void setLibId(String libId);

	public boolean isPriority();

	public void setPriority(boolean isPriority);

	public byte[] getMsg();

	public void setMsg(byte[] msg);

	public long getTimeAdded();

	public void setTimeAdded(long timeAdded);
	
	public long getGsid();

	public void setGsid(long gsid);
	
	public int getMsgCount() ;
	
	public void setMsgCount(int msgCount);

	public String getMsgType();
	
	public void setMsgType(String msgType);

    public TargetServer getMsgTargetServer();
    
    public void setMsgTargetServer(TargetServer msgType);
}