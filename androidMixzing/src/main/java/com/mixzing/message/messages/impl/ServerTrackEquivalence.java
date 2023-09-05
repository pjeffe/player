package com.mixzing.message.messages.impl;

import java.util.ArrayList;
import java.util.List;

import com.mixzing.message.messageobject.impl.TrackEquivalence;
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
public class ServerTrackEquivalence implements ServerMessage {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerTrackEquivalence() {
        this.equiv = new ArrayList<TrackEquivalence>();
    }

    private List<TrackEquivalence> equiv;

    public String getType() {
        return ServerMessageEnum.TRACK_EQUIVALENCE.toString();
    }

    public void setType(String s) {

    }

    //@XmlElement
    public List<TrackEquivalence> getEquiv() {
        return equiv;
    }

    public void setEquiv(List<TrackEquivalence> equiv) {
        this.equiv = equiv;
    }

    public void addTrackEquivalence(TrackEquivalence te) {
        equiv.add(te);
    }
}
