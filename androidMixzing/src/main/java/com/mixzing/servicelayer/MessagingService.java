package com.mixzing.servicelayer;

import java.util.List;

import com.mixzing.android.PackageHandler.InstalledPackages;
import com.mixzing.message.messages.impl.ClientMessageEnvelope;
import com.mixzing.musicobject.AndroidPackage;
import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.OutboundMsgQ;
import com.mixzing.musicobject.Playlist;
import com.mixzing.musicobject.RatingSong;
import com.mixzing.musicobject.SourceVideo;
import com.mixzing.musicobject.Track;
import com.mixzing.musicobject.TrackSignatureValue;
import com.mixzing.musicobject.Video;

public interface MessagingService {
		
	public void setServerCommunicationThread(ServerCommunicationThread coms);
	
	public void queueNewLibraryRequest();
	
	public void updateLibraryId(String serverId);
	
	public void updateGsid(long lsid, long gsid) ;
	
	public void playlistAdded(Playlist play);
	
	public void playlistDeleted(Playlist play);
	
	public void trackAdded(Track track, GlobalSong globalSong);
	
	public void trackDeleted(Track track);
	
	public void ratingAdded(RatingSong rating);
	
	public void ratingDeleted(RatingSong rating);
	
	public void batchFinish();
	
	public void wakeup();
	
	public OutboundMsgQ getNextQueuedMessage();
	
	public void deleteQueuedMessage(OutboundMsgQ msg);
	
	public void requestRecommendations(List<Long> plids);
	
	public void requestDefaultRecommendations();

	public void trackSignature(List<TrackSignatureValue> signatures);

	public void batchStart(boolean urgent);
    
    public ClientMessageEnvelope unpack(OutboundMsgQ msg);
    
	public void requestFile(String filePart);

	public void packageDeleted(AndroidPackage t);

	public void packageAdded(AndroidPackage pkg, InstalledPackages src);
	
	public void videoDeleted(Video t);

	public void videoAdded(Video video, SourceVideo vid);
	
	
}
