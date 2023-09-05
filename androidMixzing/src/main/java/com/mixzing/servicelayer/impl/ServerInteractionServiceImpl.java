package com.mixzing.servicelayer.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mixzing.MixzingConstants;
import com.mixzing.android.AndroidUtil;
import com.mixzing.android.Preferences;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.message.messageobject.impl.GlobalSongSpec;
import com.mixzing.message.messageobject.impl.TrackEquivalence;
import com.mixzing.message.messageobject.impl.TrackMapping;
import com.mixzing.message.messageobject.impl.TrackRecommendation;
import com.mixzing.message.messageobject.impl.TrackSignatureRequest;
import com.mixzing.message.messages.ServerMessage;
import com.mixzing.message.messages.ServerMessageEnum;
import com.mixzing.message.messages.impl.ServerFileResponse;
import com.mixzing.message.messages.impl.ServerGenreBasisVectors;
import com.mixzing.message.messages.impl.ServerMessageEnvelope;
import com.mixzing.message.messages.impl.ServerNewLibraryResponse;
import com.mixzing.message.messages.impl.ServerPingMe;
import com.mixzing.message.messages.impl.ServerRecommendations;
import com.mixzing.message.messages.impl.ServerRequestSignature;
import com.mixzing.message.messages.impl.ServerResponseDelayed;
import com.mixzing.message.messages.impl.ServerTagResponse;
import com.mixzing.message.messages.impl.ServerTrackEquivalence;
import com.mixzing.message.messages.impl.ServerTrackMapping;
import com.mixzing.musicobject.EnumPlaylistType;
import com.mixzing.musicobject.OutboundMsgQ;
import com.mixzing.musicobject.Playlist;
import com.mixzing.musicobject.PlaylistTrack;
import com.mixzing.musicobject.dto.OutboundMsgQDTO.TargetServer;
import com.mixzing.servicelayer.LibraryService;
import com.mixzing.servicelayer.MessagingService;
import com.mixzing.servicelayer.PlaylistService;
import com.mixzing.servicelayer.ServerInteractionService;
import com.mixzing.servicelayer.SignatureCodeUpgradeService;
import com.mixzing.servicelayer.SignatureService;
import com.mixzing.servicelayer.TrackService;

public class ServerInteractionServiceImpl extends BaseServiceImpl implements ServerInteractionService {

	private PlaylistService playService;
	private MessagingService msgService;
	private TrackService trkService;
	private SignatureService sigService;

	private long DEFAULT_PING_DELAY = 30000;

	private long pingDelay = DEFAULT_PING_DELAY;
	private boolean newSignatureRequest;
	private boolean trackMappingReceived;
	private LibraryService libraryService;
	private boolean requestForRecosSentInThiSession;

	private SignatureCodeUpgradeService sigUpgrade;

	protected void beginTransaction() {
		try {
			DatabaseManager.beginTransaction();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	private void commitOrRollback(boolean commit) {
		try {
			if(commit) {
				DatabaseManager.commitTransaction();
			} else {
				DatabaseManager.rollbackTransaction();
			}
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}	
	}

	public ServerInteractionServiceImpl(PlaylistService pl, TrackService tr, MessagingService ms, SignatureService ss, LibraryService libSvc, SignatureCodeUpgradeService sigUp) {
		super();
		playService = pl;
		msgService = ms;
		trkService = tr;
		sigService = ss;
		libraryService = libSvc;
		this.sigUpgrade = sigUp;
	}

	/*
	 * 
	 * Called when there is a 500 error response from the server
	 * 
	 */
	public void processErroredMessage(OutboundMsgQ request) {
		boolean commit = false;
		if(request.getLibId().equals("-1") || request.getLibId().equals("-2")) {
			if(Logger.IS_TRACE_ENABLED)
				lgr.trace("Catastrophic error at boot strap. Cannot discard this message.");
			return;
		}
		synchronized(DatabaseManager.class) {
			beginTransaction();
			try {
				// XXX: TODO LOG THE MESSAGE AS AN ERROR
				msgService.deleteQueuedMessage(request);
				commit = true;
			} finally {
				commitOrRollback(commit);
			}		
		}
	}


	
	
	public boolean processServerMessage(ServerMessageEnvelope sEnv, OutboundMsgQ request) {
		List<ServerMessage> l = sEnv.getMessages();
		String libId = libraryService.getLibrary().getServerId();
		boolean delayed = false;

		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Process server message: " + sEnv.getLib_id());
		// reset message loop wait period, a PingMe message will change this though.
		pingDelay = DEFAULT_PING_DELAY;
		newSignatureRequest = false;
		trackMappingReceived = false;

		ArrayList<ServerMessage> delayResponse = new ArrayList<ServerMessage>();

		for (ServerMessage o : l) {
			if(ServerMessageEnum.valueOf(o.getType()).equals(ServerMessageEnum.RESPONSE_DELAYED)) {
				delayResponse.add(o);
				delayed = true;
			}
		}



		/*
		 * 
		 * It is possible that the server can give us cached recommendations while it is waiting to 
		 * process the request that we sent. In that scenario we need to just hang on to the old message 
		 * we sent to the server and retry with it after the delay period.
		 * 
		 * 
		 */
		for(ServerMessage o : delayResponse) {
			l.remove(o);
		}


		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Messages: delayed=" + delayResponse.size() + " other=" + l.size());

		
		if(l.size() > 0) {
			boolean commit = false;
			TargetServer svr = request.getMsgTargetServer();
			Map<String,String> attrMap = null;
			// Only the app server will return server params, not webserver
			if(TargetServer.APPSERVER_JSON.equals(svr) || TargetServer.APPSERVER.equals(svr)) {
				attrMap = sEnv.getServer_params_android().getOtherAttributes();
				String reinit = attrMap.get(MixzingConstants.SERVER_PARAM_REINIT_LIBRARY);
				if(reinit != null && reinit.equals(MixzingConstants.REINIT_LIB_TRUE)) {
					if(Logger.IS_DEBUG_ENABLED)
						lgr.debug("Messages: Attempt reinit");
					reinitializeAndExit();
				}
			}

			synchronized(DatabaseManager.class) {
				beginTransaction();
				try {

					updateLibraryStatus(sEnv,attrMap);
					for (ServerMessage o : l) {
						if (sEnv.getLib_id().equals(libId) ||
								o.getType() == ServerMessageEnum.NEW_LIBRARY.toString() ||
								o.getType() == ServerMessageEnum.PING_ME.toString()) {
							processMessage(o);
						}
					}
					if(!delayed) {
						msgService.deleteQueuedMessage(request);
					}
					commit = true;
				} catch (Exception e) {
					lgr.error("Error processing incoming message, discarding it: " + e,e);
					/*
					 * If we get an exception processing the response we should log and discard the incoming message
					 * or else we may get into an infinite loop with the server ?
					 * 
					 */
					if(!delayed) {
						/*
						 * Rollback the original tx
						 */
						commitOrRollback(false);

						/*
						 * Start a new tx to delete the queued message, we still have some unresolved conditions
						 * like failures here, but we will let them slide fro now XXX TODO
						 */
						beginTransaction();
						msgService.deleteQueuedMessage(request);
						commit = true;
					}
				} finally {
					commitOrRollback(commit);
				}
			}

		}

		for(ServerMessage o : delayResponse) {
			this.processResponseDelayed((ServerResponseDelayed) o);
		}

		if(newSignatureRequest) {
			sigService.wakeup();
		}

		if(trackMappingReceived) {
			int cnt = trkService.tracksWithGsid();
			libraryService.setGsidReceivedCount(cnt);
		}
		return !delayed;
	}

	private void reinitializeAndExit() {
		try {
			AndroidUtil.reinitWithFileDelete();
		} catch (Exception e) {
			if(Logger.IS_DEBUG_ENABLED) {
			   lgr.debug("reinit  " +  e.getMessage());
			   lgr.error("reinit",e);
			}
		}
	}

	private void updateLibraryStatus(ServerMessageEnvelope env, Map<String,String> attrMap) {
		if(Logger.IS_TRACE_ENABLED)
				lgr.trace("In process libstatus");
		if(attrMap != null) {
			libraryService.setServerParameters(attrMap);
			libraryService.parametersUpdated();
		}
	}

	private void processMessage(ServerMessage o) {

		try {
			switch (ServerMessageEnum.valueOf(o.getType())) {

			case PING_ME:
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("MSG: PINGME");
				processPingMe((ServerPingMe) o);
				break;    		
			case NEW_LIBRARY:
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("MSG: NEWLIB");
				processNewLibrary((ServerNewLibraryResponse) o);
				break;
			case TRACK_MAPPING:
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("MSG: TRACKMAP");
				processTrackMapping((ServerTrackMapping) o);
				break;
			case REQUEST_SIGNATURE:
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("MSG: REQSIG");
				processRequestSignature((ServerRequestSignature) o);
				break;
			case RECOMMENDATIONS:
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("MSG: RECOMMENDATIONS");
				//fixYAMLbug((ServerRecommendations) o);
				processRecommendations((ServerRecommendations) o);
				break;
			case GENRE_BASIS_VECTORS:
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("MSG: GENREBASISVECTORS");
				processDefaultRecommendations((ServerGenreBasisVectors) o);
				break;
			case TAG_RESPONSE:
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("MSG: TAGRESP");
				processTagResponse((ServerTagResponse) o);
				break;
			case TRACK_EQUIVALENCE:
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("MSG: TRACKEQUIV");
				processTrackEquivalence((ServerTrackEquivalence) o);
				break;
			case FILERESPONSE:
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("MSG: FILERESPONSE");
				processFileResponse((ServerFileResponse) o);
				break;

			default:
				lgr.warn("Got unhandled server messaage: " + o.getClass().getSimpleName());

			}
		} catch (Exception e) {
			if(Logger.IS_TRACE_ENABLED)
				lgr.trace("Exception processing information from the server: ", e);
			lgr.error("Error processing server message", e);
		}
	}

	protected void processFileResponse(ServerFileResponse file) {
		sigUpgrade.handleDownloadComplete(file);
	}


	private void processPingMe (ServerPingMe message){
		if(Logger.IS_TRACE_ENABLED)
			lgr.debug("Process ping me every " + Integer.toString(message.getDelay_time()) + " seconds");
		pingDelay = message.getDelay_time() * 1000;
	}

	/*
	 * Can't do this in a transaction so starting a transaction needs to be done
	 * after making this check
	 * 
	 */
	private void processResponseDelayed(ServerResponseDelayed message) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.debug("Process delayed response " + Long.toString(message.getRetry_time()) + " seconds");
		synchronized (message) {
			try {
				// simply wait out the retry time
				message.wait(message.getRetry_time() * 1000);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}



	protected void processNewLibrary(ServerNewLibraryResponse message) {	
		// store the user id in the prefs
//		final String userId = message.getUser_id();
//		try {
//			final int id = Integer.parseInt(userId);
//			AndroidUtil.setIntPref(null, Preferences.Keys.SERVER_USER_ID, id);
//		}
//		catch (Exception e) {
//			lgr.error("ServerInteractionServiceImpl.processNewLibrary: bad user id:", e);
//		}

		String serverId = message.getLibrary_id();
		libraryService.updateLibraryId(serverId);
		msgService.updateLibraryId(serverId);
	}

	protected void processRecommendations(ServerRecommendations recos){
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Got recos: " + recos.getServer_recommendations().size());
		playService.processRecommendations(recos);

	}

	protected void processTrackMapping(ServerTrackMapping message) {
		List<TrackMapping> map = message.getMapped();
		try {
			msgService.batchStart(true);
			for(TrackMapping m : map) {
				long gsid = m.getGsid();
				long lsid = m.getLsid();
				ArrayList<PlaylistTrack> playlistTracks = trkService.mapTrackLsidGsid(lsid, gsid);
				for(PlaylistTrack pt : playlistTracks) {
					playService.addPositiveRating(pt.getPlid(), pt.getLsid());
				}
				// not needed with new logic, msgService.updateGsid(lsid, gsid);
			}
		} finally {
			msgService.batchFinish();
		}
		if(map.size() > 0 && playService.areAllGsidsReceived()) {
			List<Long> plids = new ArrayList<Long>();
			if(!this.requestForRecosSentInThiSession) {
				if(Logger.IS_TRACE_ENABLED)
					lgr.trace("All gsids are received,  server please start resolution");
				plids.add(-2l);
				msgService.batchStart(true);
				msgService.requestRecommendations(plids);
				this.requestForRecosSentInThiSession = true;
				msgService.batchFinish();
			}
		}
		trackMappingReceived = true;
	}

	protected void processRequestSignature(ServerRequestSignature message) {

		try {
			for(TrackSignatureRequest req: message.getSignature_requests()) {
				long lsid = req.getLsid();
				boolean isLong = req.getIs_long() == 0 ? false : true;
				boolean isHighPri = req.getIs_high_priority() == 0 ? false : true;
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("Signature request for : " + lsid + ":" + isLong + ":" + isHighPri);
				sigService.addSignatureRequest(lsid, req.getSkip(), req.getDuration(), req.getSuper_win(), isHighPri, isLong);
			}
		} catch (Exception e) {
			lgr.error(e,e);
		}

		// Wake up the signature processing thread, but the tx needs to be committed
		newSignatureRequest = true;

	}

	protected void processTagResponse(ServerTagResponse message) {

	}

	protected void processTrackEquivalence(ServerTrackEquivalence message) {

		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Got track equivalence message from server. Discarding for Now. TBD");
		for (TrackEquivalence m : message.getEquiv()) {
			if(Logger.IS_TRACE_ENABLED)
				lgr.trace("For gsid: " + m.getWishlist_gsid() + " match_level: " + m.getMatch_level());
			for(Object o : m.getLocal_matches()) {
				if(Logger.IS_TRACE_ENABLED)
					lgr.trace("       match:" + o);
			}
		}	
	}

	protected void processDefaultRecommendations(
			ServerGenreBasisVectors message) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("ServerGenreBasisVectors: Got default recos");
		/*
		 * Construct a name for the default genre playlist
		 * 	- If a playlist by that name exists
		 */
		ServerRecommendations recos = new ServerRecommendations();

		for(String genre : message.getBasis_vectors().keySet()) {
			Playlist play = playService.addMagicPlaylist("MAGIC_GENRE_" + genre, EnumPlaylistType.MAGIC_SERVER);
			long plid = play.getId();
			if(Logger.IS_TRACE_ENABLED)
				lgr.trace("Got genre: " + genre);
			List<TrackRecommendation> srecos = message.getBasis_vectors().get(genre);
			for (TrackRecommendation rec : srecos) {
				if(Logger.IS_TRACE_ENABLED)
					lgr.trace("      Reco is: " + rec.getAs_artist() + ":" + rec.getAs_title());
				rec.setPlid(play.getId());
			}
			recos.addServer_recommendation(plid, srecos);
		}

		playService.processRecommendations(recos);

	}

	public long getPingDelay() {
		return pingDelay;
	}	


}
