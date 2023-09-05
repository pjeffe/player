package com.mixzing.servicelayer;

import com.mixzing.message.messages.impl.ServerMessageEnvelope;
import com.mixzing.musicobject.OutboundMsgQ;

public interface ServerInteractionService  {

	public void processErroredMessage(OutboundMsgQ request);
	public boolean processServerMessage(ServerMessageEnvelope response, OutboundMsgQ request);
	public long getPingDelay();

}
