package com.mixzing.servicelayer.impl;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.location.Location;

import com.mixmoxie.util.StackTrace;
import com.mixzing.MixzingConstants;
import com.mixzing.android.AndroidUtil;
import com.mixzing.android.MixzingLocationManager;
import com.mixzing.android.Preferences;
import com.mixzing.android.PackageHandler.InstalledPackages;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.message.messageobject.impl.ClientPlaylist;
import com.mixzing.message.messageobject.impl.ClientTrack;
import com.mixzing.message.messageobject.impl.TrackRating;
import com.mixzing.message.messageobject.impl.TrackSignature;
import com.mixzing.message.messages.ClientMessage;
import com.mixzing.message.messages.ClientMessageFactory;
import com.mixzing.message.messages.impl.ClientDeleteRatings;
import com.mixzing.message.messages.impl.ClientLibraryChanges;
import com.mixzing.message.messages.impl.ClientMessageEnvelope;
import com.mixzing.message.messages.impl.ClientNewLibrary;
import com.mixzing.message.messages.impl.ClientPing;
import com.mixzing.message.messages.impl.ClientPlaylistChanges;
import com.mixzing.message.messages.impl.ClientRatings;
import com.mixzing.message.messages.impl.ClientRequestDefaultRecommendations;
import com.mixzing.message.messages.impl.ClientRequestFile;
import com.mixzing.message.messages.impl.ClientRequestRecommendations;
import com.mixzing.message.messages.impl.ClientTrackSignatures;
import com.mixzing.musicobject.AndroidPackage;
import com.mixzing.musicobject.GlobalSong;
import com.mixzing.musicobject.OutboundMsgQ;
import com.mixzing.musicobject.Playlist;
import com.mixzing.musicobject.RatingSong;
import com.mixzing.musicobject.SourceVideo;
import com.mixzing.musicobject.Track;
import com.mixzing.musicobject.TrackSignatureValue;
import com.mixzing.musicobject.Video;
import com.mixzing.musicobject.dao.OutboundMsgQDAO;
import com.mixzing.musicobject.dto.OutboundMsgQDTO.TargetServer;
import com.mixzing.musicobject.impl.OutboundMsgQImpl;
import com.mixzing.servicelayer.LibraryService;
import com.mixzing.servicelayer.MessagingService;
import com.mixzing.servicelayer.MixzingMarshaller;
import com.mixzing.servicelayer.ServerCommunicationThread;

public class MessagingServiceImpl extends BaseServiceImpl implements MessagingService {
	private static final int MAX_MESSAGES_PER_SEND = 128;
	protected boolean isInBatchMode = false;
	private ServerCommunicationThread commo;
	//private GlobalSongService gsService;
	private OutboundMsgQDAO qDAO;
	private ClientMessageFactory cmf;
	private MixzingMarshaller marshaller;
	private MixzingMarshaller serializing;
	private MixzingLocationManager locationManager;
	private ClientLibraryChanges libChanges;
	private ClientPlaylistChanges playlistChanges;
	private ClientRatings clientRatings;
	private ClientRequestRecommendations reqReco; 
	private ClientRequestDefaultRecommendations reqDefReco; 
	private long lastLocationSentTime = 0l;
	private long lastfixtime = 0l;
	private boolean isNewData = false;
	private LibraryService libSvc;
	private int msgCount = 0; 
	private String msgType;
	private ReentrantLock batchLock;
	private boolean urgentBatch;
	private final boolean isAmazonDevice = AndroidUtil.isAmazonDevice();
	
	
	public MessagingServiceImpl(OutboundMsgQDAO qDAO, ClientMessageFactory cf, LibraryService libService, MixzingMarshaller marsh, MixzingLocationManager lMgr, MixzingMarshaller serial ) {
		super();
		//this.gsService = gs;
		this.qDAO = qDAO;
		this.cmf = cf;
		this.libSvc = libService;
		this.batchLock = new ReentrantLock();
		this.marshaller = marsh;
		this.serializing = serial;
		
		if(MixzingConstants.USE_SERIALIZING_MARSHALLER) {
			this.marshaller = serializing;
		}
		
		this.locationManager = lMgr;
		
		/*
		 * If we have seen this library once before load location prefs.
		 * Leaving it unloaded should send out a location message first time we created it.
		 * 
		 */
		if(libService.getLibrary().getId() != -1) {
			loadLocationFromPrefs();
		}
		
		if(Logger.IS_TRACE_ENABLED) {
			lgr.trace("MessagingServiceImpl.ctor: Started up with fixtime = " + lastfixtime + " Sendtime = " + lastLocationSentTime);
		}
	}


	protected static final String LOC_TIME_SEP = ":";

	protected void loadLocationFromPrefs() {
		String locs = AndroidUtil.getStringPref(null, Preferences.Keys.LAST_LOC_TIMES, null);
		if(locs != null) {
			StringTokenizer tok2 = new StringTokenizer(locs, LOC_TIME_SEP);

			String sendtime = null;
			if (tok2.hasMoreTokens()) {
				sendtime = tok2.nextToken();
			}
			String fixtime = null;
			if (tok2.hasMoreTokens()) {
				fixtime = tok2.nextToken();
			}
			if(fixtime != null && sendtime != null) {
				long ft = -1, st = -1;
				try {
					ft = Long.valueOf(fixtime);
					st = Long.valueOf(sendtime);
				} catch (Exception e) {					
				}
				if(ft != -1 && st != -1) {
					this.lastLocationSentTime = st;
					this.lastfixtime = ft;
				}
			}
		}
	}
	
	public void updateLibraryId(String serverId) {
		qDAO.updateLibraryId(serverId);
	}	
	
	/**
	 * Only allow one thread to be in batch mode at a time
	 */
	public  void batchStart() {
		batchStart(false);
	}

	public  void batchFinish() {
		if(isInBatchMode && batchLock.isHeldByCurrentThread()) {
			try {
				doCommit();
				isInBatchMode = false;
				urgentBatch = false;
			} finally {
				if(Logger.IS_TRACE_ENABLED)
					lgr.trace("Unlocking batch lock");
				batchLock.unlock();
				if(Logger.IS_TRACE_ENABLED)
					lgr.trace("Unlocked batch lock");
			}
		} else {
			lgr.error("Batch finish called without holding the lock"+ StackTrace.getStackTrace());
		}
	}

	public  void batchStart(boolean urgent) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Acquiring batch lock");
		batchLock.lock();
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Acquired batch lock");
		isInBatchMode = true;
		this.urgentBatch = urgent;
	}


	protected void clear() {
		this.libChanges = null;
		this.playlistChanges = null;
		this.clientRatings = null;
		this.reqReco = null;
		this.reqDefReco = null;
		this.isNewData = false;
		this.msgCount = 0;
		this.msgType = null;
	}

	protected void doBatchCommit() {
		if(isInBatchMode && this.msgCount < MAX_MESSAGES_PER_SEND)
			return;
		doCommit();
	}

	protected void doCommit() {
		/*
		 * Write out in the following order
		 *   - New Library
		 *   - Library changes 
		 *   - Playlist changes
		 *   - ClientRatings
		 *   - deleteRatings 
		 */
		boolean isPriority = false;

		boolean isLocationNeeded = false;
		

		if(!isNewData) {
			return;
		}

		boolean urgent = false;
		if(isInBatchMode && urgentBatch) {
			urgent = true;
		}
		ClientMessageEnvelope env = cmf.createNewEnvelope(urgent);


		if(this.libChanges != null) {
			env.addMessage(this.libChanges);
			isPriority = true;
		}
		if(this.playlistChanges != null) {
			env.addMessage(this.playlistChanges);
			isPriority = true;
		}
		if(this.clientRatings != null) {
			env.addMessage(this.clientRatings);
			isPriority = true;
		}

		if(this.reqReco != null) {
			env.addMessage(this.reqReco);
			isPriority = true;
			isLocationNeeded = true;
		}
		if(this.reqDefReco != null) {
			env.addMessage(this.reqDefReco);
			isPriority = true;
			isLocationNeeded = true;
		}	

		if(System.currentTimeMillis() > (lastLocationSentTime + libSvc.getServerParameterLong(MixzingConstants.SERVER_PARAM_LOCDEL))) {
			isLocationNeeded = true;
		}
		
		if(isLocationNeeded) {
			addLocation(env);
		}

		persistMessage(env, isPriority, this.msgType, this.msgCount);
		clear();


	}


	public void playlistAdded(Playlist play) {
		isNewData = true;
		this.msgCount++;
		this.msgType="PLAYLISTADDED_" + play.getId();
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("PLAYLIST ADDED: " + play.getName() + ":" + play.getId());
		ClientPlaylistChanges plc = getPlaylistChanges();
		ClientPlaylist pl = new ClientPlaylist(play);
		plc.addPlaylist(pl);
		doBatchCommit();
	}

	public void playlistDeleted(Playlist play) {
		isNewData = true;
		this.msgCount++;
		this.msgType="PLAYLISTDELETED_" + play.getId();
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("PLAYLIST DELETED: " + play.getName() + ":" + play.getId());
		ClientPlaylistChanges plc = getPlaylistChanges();
		ClientPlaylist pl = new ClientPlaylist(play);
		plc.deletePlaylist(pl);
		doBatchCommit();
	}

	protected long totalSignatureTime = 0;
	protected static final long MAX_TIME_IN_SIGS = 5000; 
	protected static final boolean IS_CKSUM_SIG_ENABLED = true;
	
	private static final boolean isMp3File(String name) {
		int len;
		if(name == null || (len = name.length()) < 5) {
			return false;
		}
		if(name.substring(len - 4, len).equalsIgnoreCase(".mp3")) {
			return true;
		}
		return false;
	}
	
	public void trackAdded(Track track, GlobalSong gs) {
		isNewData = true;
		this.msgCount++;
		this.msgType="TRACKADDED_" + track.getId();
		ClientLibraryChanges lc = getLibChanges();
		ClientTrack trk = new ClientTrack(track, gs);
	
		if(IS_CKSUM_SIG_ENABLED) {
			if(totalSignatureTime < MAX_TIME_IN_SIGS) {
				String sig = null;
				long start = System.currentTimeMillis();
				String location = track.getLocation();
				if(isMp3File(location)) {
					sig = AndroidUtil.getMp3DataChecksum(new File(location));
					if(sig != null) {
						trk.setShort_sig(sig);
					}
				}
				long tm = (System.currentTimeMillis() - start);
				totalSignatureTime += tm;
				if(Logger.IS_DEBUG_ENABLED) {
					lgr.debug("trackAdded-sig: " + location + " : " + tm + " : " + totalSignatureTime + " : " + sig);
				}
			}
		}
		//lgr.trace("TRK ADDED: " + trk.getMpx_tags().getTitle());
		lc.addTrack(trk);
		doBatchCommit();
	}

	public void trackDeleted(Track track) {
		isNewData = true;
		this.msgCount++;
		this.msgType="TRACKDELETED_" + track.getId();
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("TRK DELETED: " + track.getLocation());
		ClientLibraryChanges lc = getLibChanges();
		lc.deleteTrack(track);
		doBatchCommit();
	}

	public void ratingAdded(RatingSong rating) {

		boolean batch = false;

		if(isInBatchMode) {
			if(batchLock.isHeldByCurrentThread()) {
				batch = true;
			}
		}

		String mType = "RATINGADDED_" + rating.getRatingSource() + "_" + rating.getRatingValue() + "_" + rating.getPlid() + "_" + rating.getGlobalSongId();
		if(!batch || (rating.getGlobalSong().getGsid() < 0 && 
				!MixzingConstants.ALLOW_RATINGS_WITH_LSID)) {
			addSingleRating(rating, mType);
		} else {
			ClientRatings cr = getClientRatings();
			TrackRating tr = new TrackRating(rating);
			cr.addRating(tr);
			isNewData = true;
			this.msgCount++;
			this.msgType= mType;
			doBatchCommit();
		}
	}

	private void addSingleRating(RatingSong rating, String mType) {
		/*
		 * First create a new client message
		 */
		ClientMessageEnvelope env = cmf.createNewEnvelope();
		//addLocation(env);
		ClientRatings cr = cmf.createClientRatings();
		TrackRating tr = new TrackRating(rating);
		cr.addRating(tr);
		env.addMessage(cr);		
		long gsid = tr.getGsid();
		if(gsid < 0) {
			mType = "RATING_WITH_LSID_" + tr.getGsid();	
		} 
		persistMessage(env, true, mType, 1);

	}

	public void requestFile(String filePart) {
		String mType = "FILEREQUEST: " + filePart;
		/*
		 * First create a new client message
		 */
		ClientMessageEnvelope env = cmf.createNewEnvelope();
		ClientRequestFile cr = cmf.createFileRequest();
		cr.setFileName(filePart);
		env.addMessage(cr);     

		persistMessage(env, true, mType, 1);

	}

	public void ratingDeleted(RatingSong rating) {
		String mType = "RATINGDELETED_" + rating.getRatingValue() + "_" + rating.getPlid() + "_" + rating.getGlobalSongId(); 
		/*
		 * First create a new client message
		 */
		ClientMessageEnvelope env = cmf.createNewEnvelope();
		ClientDeleteRatings cr = cmf.createDeleteRatings();
		TrackRating tr = new TrackRating(rating);
		cr.addRating(tr);
		env.addMessage(cr);
		persistMessage(env, true, mType, 1);
	}

	protected void addLocation(ClientMessageEnvelope env) {
		// TODO generalize this to exclude devices on which getting location is intrusive
		if (!isAmazonDevice) {
			Location loc = locationManager.getLocation();
			if (loc != null) {
				long curfixtime = loc.getTime();
				if(curfixtime > lastfixtime) {
				StringBuilder sb = new StringBuilder("location=");
				lastLocationSentTime = AndroidUtil.fmtLocation(sb, loc, false);
				sb.append(";");
				env.appendEnvironMent(sb.toString());
				lastfixtime = curfixtime;
				String store = lastLocationSentTime + LOC_TIME_SEP + lastfixtime;
				AndroidUtil.setStringPref(null, Preferences.Keys.LAST_LOC_TIMES, store);
				} else {
					if(Logger.IS_TRACE_ENABLED) {
						lgr.trace("addLocation: Fix was the same as last time, not sending again - cur:last = " + curfixtime + " : " + lastfixtime );
					}
				}
			} else {
				if(Logger.IS_TRACE_ENABLED) {
					lgr.trace("addLocation: got null fix from LM");
				}			
			}
		}
	} 
		
	public void queueNewLibraryRequest() {
		ClientMessageEnvelope env = cmf.createNewEnvelope();
		env.addEnvironment(true);
		addLocation(env);
		ClientNewLibrary newLibrary = cmf.createNewLibraryRequest();
		env.addMessage(newLibrary);
		String mType = "NEWLIBRARY";
		persistMessage(env, true, mType, 1);
	}

	private OutboundMsgQ persistMessage(ClientMessageEnvelope env, boolean isPriority, String msgType, int cnt) {
		boolean isPing = false;
		boolean isFile = false;
		boolean isRecoPossible = false;
		long gsid = 0;
		List<ClientMessage> cms = (List<ClientMessage>) env.getMessages();
		for(ClientMessage cm : cms) {
			if(cm instanceof ClientPing) {
				isPing = true;
			}
			if(cm instanceof ClientRequestFile) {
				isFile = true;
			}
			if(cm instanceof ClientRatings) {
				List<TrackRating> ratings = ((ClientRatings) cm).getRating_vector();
				if(ratings != null) {
					for(TrackRating rating : ratings) {
						// we should expect a reco, unless we are scrobbling
						if(rating.getPlid() != MixzingConstants.PLAYLIST_ID_FOR_SCROBBLES) {
							isRecoPossible = true;
							break;
						}
					}
				}
				ClientRatings cr = (ClientRatings) cm;
				gsid = cr.getRating_vector().get(0).getGsid();
				break;
			}
			if(cm instanceof ClientDeleteRatings || 
					cm instanceof ClientLibraryChanges ||
					cm instanceof ClientPlaylistChanges ||
					cm instanceof ClientTrackSignatures
					
			) {
				isRecoPossible = true;
			}
		}
		
		OutboundMsgQ out = new OutboundMsgQImpl(isPing);
		out.setId(env.getSeqno());
		out.setLibId(env.getLib_id());
		out.setTimeAdded(System.currentTimeMillis());
		out.setPriority(isPriority);
		out.setGsid(gsid);
		out.setMsgCount(cnt);
		if(isRecoPossible) {
			// MSGTYPE should start with Recopossible since by convention the commo thread looks for it 
			// to determine whether to ping more
			msgType = MixzingConstants.RECO_POSSIBLE + msgType;
		}
		out.setMsgType(msgType);
		if(isFile)
			out.setMsgTargetServer(TargetServer.WEBSERVER);
		else {
			out.setMsgTargetServer(MixzingConstants.USE_SERIALIZING_MARSHALLER ? 
										TargetServer.APPSERVER : 
										TargetServer.APPSERVER_JSON);
		}
		byte[] b = pack(env);
		Runtime r = Runtime.getRuntime();
		long heap = r.totalMemory() - r.freeMemory();
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Message size = " + b.length + " " + msgType + " isPing=" + isPing + " heap: " + heap);
		out.setMsg(b);
		qDAO.insert(out);

		/*
		 * On all messages other than ping we need to wakeup the commo service.
		 * Ping should never wake it up since it would mess up the counts in 
		 * the commo service.
		 * Strictly this call should be called wherever we commit transactions, and not here
		 * since the new message will not be seen until committed.
		 * 
		 */
		if(!isPing) {
			wakeupCommunicationService();
		}

		return out;
	}

	 
	public void requestRecommendations(List<Long> plids) {
		isNewData = true;
		this.msgCount++;
		this.msgType = "REQUESTRECOMMENDATIONS:";
		for(long l : plids) {
			this.msgType = this.msgType + ":" + l;
		}
		ClientRequestRecommendations crr = getRequestRecommendations();
		crr.setPlids(plids);
		this.reqReco = crr;
		doBatchCommit();
		// XXX: TODO hack call
		//requestDefaultRecommendations();
	}

	public void requestDefaultRecommendations() {
		isNewData = true;
		this.msgCount++;
		this.msgType = "REQUEST_GENRE_BASISVECTORS:";
		ClientRequestDefaultRecommendations crdr = getRequestDefaultRecommendations();
		this.reqDefReco = crdr;
		doBatchCommit();		
	}



	private ClientRatings getClientRatings() {
		if(this.clientRatings == null)
			this.clientRatings = cmf.createClientRatings();
		return this.clientRatings;
	}

	private ClientPlaylistChanges getPlaylistChanges() {
		if(this.playlistChanges == null)
			this.playlistChanges = cmf.createPlaylistChanges();
		return this.playlistChanges;
	}



	private ClientLibraryChanges getLibChanges() {
		if(this.libChanges == null)
			this.libChanges = cmf.createLibraryChanges();
		return this.libChanges;
	}


	private ClientRequestRecommendations getRequestRecommendations() {
		if(this.reqReco == null)
			this.reqReco = cmf.createClientRequestRecommendations();
		return this.reqReco;
	}	

	private ClientRequestDefaultRecommendations getRequestDefaultRecommendations() {
		if(this.reqDefReco == null)
			this.reqDefReco = cmf.createClientRequestDefaultRecommendations();
		return this.reqDefReco;
	}	
	
	
	public OutboundMsgQ getNextQueuedMessage() {
		OutboundMsgQ out = qDAO.readQHead();

		if(out == null) {
			ClientMessageEnvelope env = cmf.createNewEnvelope();
			ClientPing p = cmf.createClientPing();
			// always send location at least once every X hours
			if(System.currentTimeMillis() > (lastLocationSentTime + libSvc.getServerParameterLong(MixzingConstants.SERVER_PARAM_LOCDEL))) {
				addLocation(env);
			}
			env.addMessage(p);
			synchronized(DatabaseManager.class) {
				try {
					beginTransactionPingMsg();
					out = persistMessage(env, true, "PING", 1);
				} finally {
					commitOrRollbackPingMsg(true);
				}
			}
		} else {
			if(!MixzingConstants.USE_SERIALIZING_MARSHALLER) {
				if(out.getMsgTargetServer().equals(TargetServer.APPSERVER)) {
					remarshallToJson(out);
				}
			}
		}

		return out;
	}

	private void remarshallToJson(OutboundMsgQ out) {
		if(Logger.IS_DEBUG_ENABLED) {
			lgr.debug("Remarshalling message to JSON " + out.getMsgType() + " " + out.getId() + " " + out.getMsgTargetServer());
		}
		Object data = serializing.unmarshall(out.getMsg());
		out.setMsg(marshaller.marshall(data));
		out.setMsgTargetServer(TargetServer.APPSERVER_JSON);
	}

	public void deleteQueuedMessage(OutboundMsgQ msg) {
		qDAO.delete(msg.getId()); 
	}

	public void updateGsid(long lsid, long gsid) {
		/*
		if(!MessagingService.ALLOW_RATINGS_WITH_LSID)
			unpackAndfixGsid(-lsid,gsid);
		 */
	}

	private void unpackAndFixQueuedMessages(String serverId) {
		ArrayList<OutboundMsgQ> msgs = qDAO.getQueuedMessages();
		for(OutboundMsgQ msg : msgs) {
			msg.setLibId(serverId);
			ClientMessageEnvelope cenv = unpack(msg);
			if(!MixzingConstants.USE_SERIALIZING_MARSHALLER) {
				msg.setMsgTargetServer(TargetServer.APPSERVER_JSON);
				if(Logger.IS_TRACE_ENABLED) {
					lgr.trace("unpackAndFixQueuedMessages: Setting - target server to json." + msg.getMsgType() + " " + msg.getId());
				}
			}
			cenv.setLib_id(serverId);
			msg.setMsg(pack(cenv));
			qDAO.updateLibraryIdAndMessage(msg);
		}
	}

	private byte[] pack(ClientMessageEnvelope cenv) {
		return this.marshaller.marshall(cenv);
	}

	public ClientMessageEnvelope unpack(OutboundMsgQ msg) {
		MixzingMarshaller m = marshaller;
		if(msg.getMsgTargetServer().equals(TargetServer.APPSERVER)) {
			if(Logger.IS_TRACE_ENABLED) {
				lgr.trace("Found old message - using serializing marshaller to unmarshall ." + msg.getMsgType() + " " + msg.getId());
			}
			m = serializing;
		}		
		return (ClientMessageEnvelope) m.unmarshall(msg.getMsg());
	}

	/*
	 * (non-Javadoc)
	 * @see com.mixzing.servicelayer.MessagingService#trackSignature(int, com.mixzing.musicobject.SignatureRequest, com.mixzing.signature.MixzingSignature, int)
	 * 
	 * This is called from the signature processing thread so commits are done under 
	 * a different transaction.
	 * 
	 */
	public void trackSignature(List<TrackSignatureValue> signatures) {


		/*
		 * First create a new client message
		 */
		ClientMessageEnvelope env = cmf.createNewEnvelope();
		ClientTrackSignatures sigs = cmf.createClientTrackSignatures();
		env.addMessage(sigs);
		String mType = "TRACKSIGNATURE: " + signatures.size() + " ";
		boolean isFirst = true;
		for(TrackSignatureValue sig : signatures) {
			TrackSignature ts = new TrackSignature();
			ts.setLsid(sig.getLsid());
			ts.setDuration(sig.getDuration());
			ts.setSkip(sig.getSkip());
			ts.setVersion(Integer.valueOf(sig.getCodeVersion()));
			ts.setAudioFileData(sig.getBitRate() + "|" + sig.getChannels() + "|" + sig.getFrequency() + "|" + sig.getMsPerFrame());
			ts.setSignature(convertToString(sig.getSig()));
			if(isFirst) {
				mType += sig.getLsid() + ":" + sig.getSkip() + ":" + sig.getDuration();
				isFirst = false;
			}

			sigs.addTrackSignature(ts);
		}


		/*
		 * Then wrapper it in a outboundmsg_q database row
		 */
		if(signatures.size() > 0)
			persistMessage(env, false, mType, 1 );

	}

	private String convertToString(List<Long> sig) {

		String ret = null;
		if(!sig.isEmpty()) {
			ret = "";
			for(long d : sig) {
				ret += d + "|";
			}
		}
		return ret;   
	}

	protected void beginTransactionPingMsg() {
		try {
			DatabaseManager.beginTransaction();
		} catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	private void commitOrRollbackPingMsg(boolean commit) {
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

	private void wakeupCommunicationService() {
		if(this.commo != null) {
			commo.wakeup();
		} else {
			if(Logger.IS_TRACE_ENABLED)
				lgr.trace("Why is commo handle not set in Messaging Service");
		}
	} 

	public void setServerCommunicationThread(ServerCommunicationThread coms) {
		this.commo = coms;
	}

	public void wakeup() {
		wakeupCommunicationService();
	}

	public static void main(String[] args) {
		Lock l = new ReentrantLock();
		l.lock();
		System.out.println(l.tryLock() + "");
	}

	public void packageAdded(AndroidPackage pkg, InstalledPackages src) {
		processPackage(pkg,src);
	}

	public void packageDeleted(AndroidPackage pkg) {
		processPackage(pkg,null);
	}

	public void videoAdded(Video video, SourceVideo src) {
		addVideo(video, src);
	}

	public void videoDeleted(Video vid) {
		deleteVideo(vid);
	}

	
	protected void processPackage(AndroidPackage pkg, InstalledPackages src) {
		isNewData = true;
		this.msgCount++;
		if(this.msgType == null)
		this.msgType="PKGADDED_" + pkg.getId();
		
		ClientLibraryChanges lc = getLibChanges();
		ClientTrack trk = new ClientTrack(pkg, src);
		//lgr.trace("TRK ADDED: " + trk.getMpx_tags().getTitle());
		lc.addTrack(trk);
		doBatchCommit();
	}

	protected void addVideo(Video vid, SourceVideo src) {
		isNewData = true;
		this.msgCount++;
		if(this.msgType == null)
			this.msgType="VIDADDED_" + vid.getId();
		
		ClientLibraryChanges lc = getLibChanges();
		ClientTrack trk = new ClientTrack(vid, src, true);
		//lgr.trace("TRK ADDED: " + trk.getMpx_tags().getTitle());
		lc.addTrack(trk);
		doBatchCommit();
	}

	protected void deleteVideo(Video vid) {
		isNewData = true;
		this.msgCount++;
		if(this.msgType == null)
			this.msgType="VIDDELETED_" + vid.getId();
		
		ClientLibraryChanges lc = getLibChanges();
		ClientTrack trk = new ClientTrack(vid, null, false);
		//lgr.trace("TRK ADDED: " + trk.getMpx_tags().getTitle());
		lc.addTrack(trk);
		doBatchCommit();
	}

}
