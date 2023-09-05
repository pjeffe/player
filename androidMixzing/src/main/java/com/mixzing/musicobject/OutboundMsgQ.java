package com.mixzing.musicobject;

import com.mixzing.musicobject.dto.OutboundMsgQDTO;

public interface OutboundMsgQ extends OutboundMsgQDTO{

	public boolean isPingMessage();
	
}