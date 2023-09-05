package com.mixzing.servicelayer.impl;

import java.util.ArrayList;

import com.mixzing.message.messages.ServerMessage;
import com.mixzing.message.messages.impl.ServerFileResponse;
import com.mixzing.message.messages.impl.ServerMessageEnvelope;
import com.mixzing.message.transport.SimulateServerMessage;
import com.mixzing.servicelayer.LibraryService;
import com.mixzing.servicelayer.MixzingMarshaller;

public class SimulateServerMessageImpl implements SimulateServerMessage {

    
    protected LibraryService libSvc;
    protected MixzingMarshaller marshaller;
    
    public SimulateServerMessageImpl(LibraryService libSvc, MixzingMarshaller marshaller) {
        this.libSvc = libSvc;
        this.marshaller = marshaller;
    }
    
    /* (non-Javadoc)
	 * @see com.mixzing.servicelayer.impl.SimulateServerMessage#generateMessageEnvelope()
	 */
    public ServerMessageEnvelope generateMessageEnvelope() {
        ServerMessageEnvelope msg = new ServerMessageEnvelope();
        msg.setLib_id(libSvc.getLibrary().getServerId()); 
        ArrayList<ServerMessage> msgs = new ArrayList<ServerMessage>();
        msg.setMessages(msgs);
        return msg;
    }
    
    /* (non-Javadoc)
	 * @see com.mixzing.servicelayer.impl.SimulateServerMessage#generateFileMessage(byte[], boolean)
	 */
    public ServerFileResponse generateFileMessage(byte[] b, boolean isUuenc) {
       
        ServerFileResponse resp = new ServerFileResponse();
        resp.setFileData(b);
        resp.setUuencoded(isUuenc);        
        
        return resp;       
    }

    /* (non-Javadoc)
	 * @see com.mixzing.servicelayer.impl.SimulateServerMessage#marshall(com.mixzing.message.messages.impl.ServerMessageEnvelope)
	 */
    public byte[] marshall(ServerMessageEnvelope msg) {
        return marshaller.marshall(msg);
    }

    /* (non-Javadoc)
	 * @see com.mixzing.servicelayer.impl.SimulateServerMessage#unmarshall(byte[])
	 */
    public Object unmarshall(byte[] b) {
        return marshaller.unmarshall(b);
    }
}
