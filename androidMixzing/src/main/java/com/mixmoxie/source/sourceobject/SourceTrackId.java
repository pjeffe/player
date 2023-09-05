package com.mixmoxie.source.sourceobject;

public class SourceTrackId {
    private long id;
    private String compositeId;
    
    public SourceTrackId(long id, String compId) {
        this.id = id;
        this.compositeId = compId; 
    }
    
    public long getInternalId() {
    	return id;
    }
    
    
    public String getCompositeId() {
    	return compositeId;
    }
    
    public String toString() {
       return compositeId;
    }
    
}
