package com.mixzing.message.transport;

import com.mixzing.message.messages.impl.ServerFileResponse;
import com.mixzing.message.messages.impl.ServerMessageEnvelope;

public interface SimulateServerMessage {

	public ServerMessageEnvelope generateMessageEnvelope();

	public ServerFileResponse generateFileMessage(byte[] b,	boolean isUuenc);

	public byte[] marshall(ServerMessageEnvelope msg);

	public Object unmarshall(byte[] b);

}