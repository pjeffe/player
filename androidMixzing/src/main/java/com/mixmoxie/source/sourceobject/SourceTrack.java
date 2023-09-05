package com.mixmoxie.source.sourceobject;



public interface SourceTrack {

    public String location();

    public String name();

    public String artist();

    public String album();

    public float duration();

    public int size();
    
    public long creationDate();

    public SourceTrackId id();

    public long getQuickKey();


    /**
     * Valid values for name are:
     * kind dateAdded playedcount rating trackDbId location
     * album artist duration genre year name title tracknumber
     */
    public String getTag(SourceTrackTag tag);

    public void setTag(SourceTrackTag tag, String val);

    public boolean isInPlaylist();

    public void setLocation (String location);
    
    public boolean isCleared();


}
