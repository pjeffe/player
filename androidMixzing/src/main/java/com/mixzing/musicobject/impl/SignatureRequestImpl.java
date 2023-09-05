package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.SignatureRequest;
import com.mixzing.musicobject.dto.impl.SignatureRequestDTOImpl;

public class SignatureRequestImpl extends SignatureRequestDTOImpl implements
		SignatureRequest {

	public SignatureRequestImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SignatureRequestImpl(ResultSet rs) {
		super(rs);
		// TODO Auto-generated constructor stub
	}

}
