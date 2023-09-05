package com.mixzing.musicobject.dao;

import java.util.ArrayList;

import com.mixzing.musicobject.OutboundMsgQ;

public interface OutboundMsgQDAO extends MusicObjectDAO<OutboundMsgQ>{

	public long insert(OutboundMsgQ out);

	public ArrayList<OutboundMsgQ> readAll();
	
	public OutboundMsgQ readQHead();
	
	public int getQCount();

	public void delete(long id);
	
	public void updateLibraryIdAndMessage(OutboundMsgQ msg);

	public ArrayList<OutboundMsgQ> getQueuedMessages();

	public ArrayList<OutboundMsgQ> getQueuedMessageByGsid(long oldVal);

	public void updateGsidAndMessage(OutboundMsgQ msg);

	public void updateLibraryId(String serverId);

}