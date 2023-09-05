package com.mixmoxie.source.sourceobject;

import java.util.List;

public interface SourcePlaylist {

	public interface Signature {
		public int getTrackCount();
		public float getDuration();
		public int getSize();
	}
	
	public boolean isGenius();
	
    public String getName();

    public String getParent();

    public List<SourceTrack> getTracks();

    public void addTrack(SourceTrack track);

	public String getDbId();
	
	public Signature getSignature();

}
