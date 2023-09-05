package com.mixzing.musicobject;

import com.mixzing.musicobject.dto.TrackDTO;

public interface Track  extends  TrackDTO {

    public long getAndroidId();
    
    public void setAndroidId(long anId);

}