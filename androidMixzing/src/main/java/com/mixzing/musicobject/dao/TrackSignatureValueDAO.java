package com.mixzing.musicobject.dao;

import java.util.List;

import com.mixzing.musicobject.TrackSignatureValue;
import com.mixzing.signature.common.MixzingAudioInfo;
import com.mixzing.signature.common.MixzingSignatureData;

public interface TrackSignatureValueDAO extends MusicObjectDAO<TrackSignatureValue> {

    public void addSignature(long lsid, MixzingAudioInfo info, MixzingSignatureData sigData);
    
    public TrackSignatureValue findSignature(long lsid, int skip, int dur, int superWin, String codeVersion);

    public List<TrackSignatureValue> findSignature(long lsid);
    
    public List<TrackSignatureValue> findUnsentSignatures();
    
    public void markAsSent(Long... ids);

}
