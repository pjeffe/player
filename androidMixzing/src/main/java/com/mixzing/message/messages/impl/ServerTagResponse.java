package com.mixzing.message.messages.impl;

import java.util.HashMap;

import com.mixzing.message.messageobject.impl.MPXTags;
import com.mixzing.message.messages.ServerMessage;
import com.mixzing.message.messages.ServerMessageEnum;
/**
 * <p>Title: MixMoxie Java Client</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: MixMoxie</p>
 *
 * @author G.Miller S Mathur.
 * @version 1.0
 */
//@XmlRootElement
public class ServerTagResponse implements ServerMessage {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<Long,MPXTags> tags;

    public ServerTagResponse() {
    }

    public void setTags(HashMap<Long,MPXTags> tags) {
        this.tags = tags;
    }

    //@XmlElement
    public HashMap<Long,MPXTags> getTags() {
        return tags;
    }

    public void addTags (long gsid, MPXTags mpxTags) {
        this.tags.put(Long.valueOf(gsid), mpxTags);
    }

    public String getType() {
        return ServerMessageEnum.TAG_RESPONSE.toString();
    }

	public void setType(String s) {
		
	}
}
