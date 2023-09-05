package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.OutboundMsgQ;
import com.mixzing.musicobject.dto.impl.OutboundMsgQDTOImpl;

public class OutboundMsgQImpl extends OutboundMsgQDTOImpl implements
		OutboundMsgQ {

	private boolean isPingMessage;
	public OutboundMsgQImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OutboundMsgQImpl(ResultSet rs) {
		super(rs);
	}

	public OutboundMsgQImpl(boolean isPing) {
		super();
		this.isPingMessage = isPing;
	}

	public boolean isPingMessage() {
		return this.isPingMessage;
	}
}
