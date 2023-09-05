package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.TrackSignatureValue;
import com.mixzing.musicobject.dto.impl.TrackSignatureValueDTOImpl;

public class TrackSignatureValueImpl extends TrackSignatureValueDTOImpl implements TrackSignatureValue {

    public TrackSignatureValueImpl() {
        super();
    }

    public TrackSignatureValueImpl(ResultSet rs) {
        super(rs);
    }

}
