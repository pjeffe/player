package com.mixzing.servicelayer.impl;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mixzing.SupportedOS;
import com.mixzing.SystemInfo;
import com.mixzing.decoder.MixZingFingerPrinter;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.musicobject.EnumSignatureProcessingStatus;
import com.mixzing.musicobject.SignatureRequest;
import com.mixzing.musicobject.Track;
import com.mixzing.musicobject.TrackSignatureValue;
import com.mixzing.musicobject.dao.SignatureRequestDAO;
import com.mixzing.musicobject.dao.TrackSignatureValueDAO;
import com.mixzing.musicobject.impl.SignatureRequestImpl;
import com.mixzing.servicelayer.MessagingService;
import com.mixzing.servicelayer.SignatureService;
import com.mixzing.servicelayer.TrackService;
import com.mixzing.signature.common.MixzingAudioInfo;
import com.mixzing.signature.common.MixzingSignatureData;
import com.mixzing.signature.common.MixzingSignatureRequest;
import com.mixzing.signature.common.MixzingSignatureResponse;
import com.mixzing.signature.common.MixzingSignatureWindow;
import com.mixzing.signature.common.impl.MixzingAudioInfoImpl;
import com.mixzing.signature.common.impl.MixzingSignatureDataImpl;
import com.mixzing.signature.common.impl.MixzingSignatureResponseImpl;
import com.mixzing.signature.common.impl.MixzingSignatureWindowImpl;

public class SignatureServiceImpl extends BaseServiceImpl implements SignatureService, Runnable, UncaughtExceptionHandler {

	protected static final int SIG_CODE_VER=20;
	protected class FingerSigResp implements MixzingSignatureResponse {

		public MixzingAudioInfo getAudioInfo() {
			return null;
		}

		public MixzingSignatureData getSignatureForWindow(int skip, int dur,int superWin) {
			return null;
		}

		public MixzingSignatureData getSignatureForWindow(MixzingSignatureWindow win) {
			return null;
		}

		public List<MixzingSignatureData> getSignatures() {
			return null;
		}

	}

	protected SignatureRequestDAO sigDAO;
	private TrackSignatureValueDAO sigValDAO;
	private MessagingService mesg;
	private TrackService tSvc;

	private boolean isShuttingDown;
	private Thread thr = null;

	private boolean SIGNATURE_PROCESS_DISABLED = false;
	private int MAX_SLEEP_TIME = 1 * 60 * 60 * 1000;

	/* 
	 * Until we fix the server processing of this message in offline mode, we need to 
	 * limit signature batch size to 30 or so.
	 * 
	 */
	private final int SIGNATURE_MAX_BATCH_SIZE = 30;


	public SignatureServiceImpl(SignatureRequestDAO sigDAO, TrackSignatureValueDAO valDOA, MessagingService ms, TrackService ts) {
		this.sigDAO = sigDAO;
		this.sigValDAO = valDOA;
		this.mesg = ms;
		this.tSvc = ts;
	}


	public synchronized void start() {
		thr = new Thread(this, "SignatureThread");
		thr.setUncaughtExceptionHandler(this);
		thr.start();		
	}

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

	public void addSignatureRequest(long lsid, int skip, int duration, int superWinMs, boolean isPri, boolean isLong) {
		if(duration == 0) {
			lgr.error("Got a sig request message with duration 0");
			duration = 30000;
		}

		if(sigDAO.findUnprocessedRequest(lsid, skip, duration, superWinMs).size() == 0) {

			SignatureRequest req = new SignatureRequestImpl();
			req.setLsid(lsid);
			req.setPriority(isPri);
			req.setSkip(skip);
			req.setDuration(duration);
			req.setSuperWindowMs(superWinMs);
			req.setProcessingStatus(EnumSignatureProcessingStatus.REQUESTED);

			sigDAO.insert(req);
		}

	}

	private void sendBatchOfSignatures(List<TrackSignatureValue> ls) {
		if(ls.size() > 0) {
			beginTransaction();
			boolean commit = false;
			try {
				mesg.trackSignature(ls);
				for(TrackSignatureValue ts : ls) {
					sigValDAO.markAsSent(ts.getId());
				}
				commit = true;
			} finally {
				commitOrRollback(commit);
				/*
				 * Enable ServerThread to wakeup
				 */
				if(commit) {
					mesg.wakeup();
				}
			}
		}

	}

	private void sendGeneratedSignatures() {


		if(isShuttingDown) {
			return;
		}

		List<TrackSignatureValue> sigs = sigValDAO.findUnsentSignatures();
		ArrayList<TrackSignatureValue> toSend = new ArrayList<TrackSignatureValue>();


		if(sigs != null) {
			if(Logger.IS_TRACE_ENABLED) {
				lgr.trace("Attempting to send sigs - " + sigs.size() );
			}
		} else {
			if(Logger.IS_TRACE_ENABLED) {
				lgr.trace("Attempting to send sigs - no sigs to send");
			}
		}

		int cnt =0;
		if(sigs != null && sigs.size() > 0) {
			for(TrackSignatureValue ts : sigs) {
				toSend.add(ts);
				if(cnt++ > SIGNATURE_MAX_BATCH_SIZE) {
					sendBatchOfSignatures(toSend);
					cnt = 0;
					toSend.clear();
				}
			}
			if(toSend.size() > 0) {
				sendBatchOfSignatures(toSend);
			}
		}
	}


	private void processPriorityList(List<SignatureRequest> reqlist) {
		if(isShuttingDown) {
			return;
		}

		int cnt = 0;
		for( SignatureRequest req : reqlist) {
			processSignatureRequest(req);
			cnt += reqlist.size();
			if(cnt >= SIGNATURE_MAX_BATCH_SIZE){
				sendGeneratedSignatures();
				cnt = 0;
			}
			if(isShuttingDown) {
				break;
			}
		}
	}

	public void run() {

		boolean firstPass = true;
		while(!isShuttingDown) {
			try {

				if(!SIGNATURE_PROCESS_DISABLED) {

					List<SignatureRequest> sigReqs = sigDAO.getRequested();	

					boolean hasSigs = !sigReqs.isEmpty();
					int lsidsToProcess = sigReqs.size();
					if (Logger.IS_TRACE_ENABLED) {
						lgr.trace("Signature service : processing : " + lsidsToProcess);
					}
					if(!hasSigs) {
						if(firstPass) {
							firstPass = false;
							sendGeneratedSignatures();
						}
						try {
							synchronized(this) {
								// Wakeup just in case we have timing issues
								this.wait(MAX_SLEEP_TIME);
							}
						} catch (InterruptedException e) {
						} 
						continue;
					} else {
						ArrayList<SignatureRequest> high = new ArrayList<SignatureRequest>();
						ArrayList<SignatureRequest> low = new ArrayList<SignatureRequest>();
						for( SignatureRequest req : sigReqs) {
							if(req.isPriority()) {
								high.add(req);
							} else {
								low.add(req);
							}

						}
						processPriorityList(high);	
						sendGeneratedSignatures();
						firstPass = false;
						processPriorityList(low);	
						sendGeneratedSignatures();

					}
				} else {
					if(Logger.IS_TRACE_ENABLED) {
						lgr.trace("Signature service is disabled" );
					}
					try {
						synchronized (this) {
							this.wait(MAX_SLEEP_TIME);							
						}
					} catch (InterruptedException e) {
					}
				}
			} catch (Exception e) {
				lgr.error(e,e);
			}

		}
	}


	private static String useableFilename(String file) {
		// strip any prefixes
		String prefixes[] = { "file://localhost/", "file://" };
		if(SystemInfo.OS() == SupportedOS.OSX) {
			prefixes[0] = "file://localhost";
		}
		for (String prefix : prefixes) {
			if (file.startsWith(prefix)) {
				if (Logger.IS_TRACE_ENABLED) {
					lgr.trace("prefixed filename: " + file);
				}
				file = file.substring(prefix.length());
			}
		}

		if(SystemInfo.OS() == SupportedOS.Windows) {
			file = file.replace('/', '\\');
		}
		return file;
	}


	/*
	 * 
	 * Needs to be synchronized with updateSignatureCode()
	 * 
	 */
	private synchronized void processSignatureRequest(SignatureRequest req) {

		boolean toGenerate = true;

		boolean isError = false;
		Track t = null;
		long lsid = -1;
		int cnt = 0;

		lsid = req.getLsid();
		t = locateTrack(lsid); 
		if(t == null){
			/*
			 * Mark all the windows for this track as erorred
			 */
			toGenerate = false;
			isError = true;
		}				


		boolean existed = sigValDAO.findSignature(lsid, req.getSkip(), req.getDuration(), req.getSuperWindowMs(), "fft20") != null;
		if(!existed) {
			toGenerate = true;
		} 

		if(Logger.IS_TRACE_ENABLED) {
			lgr.trace("Examining: " + lsid + " (" + (t == null ? "<deleted>" : t.getLocation()) +
				") existed: "  + existed + " skip: " + req.getSkip() + " duration: " + req.getDuration() + " super_win: " + req.getSuperWindowMs());				
		}


		MixzingSignatureResponse sig = null;

		if (toGenerate) {
			try {        
				File f = tSvc.getLocation(lsid);
				if(f != null) {
					String  file = f.getAbsolutePath();
					isError = true;
					if(f.exists()) {
						List<MixzingSignatureWindow> windows = new ArrayList<MixzingSignatureWindow>();
						MixzingSignatureRequest sr = new MixzingSignatureRequestImpl(file,windows);
						if(Logger.IS_TRACE_ENABLED) {
							lgr.trace("Generating sig for : " + file);
						}
						windows.add(new MixzingSignatureWindowImpl(req.getSkip(), req.getDuration(), req.getSuperWindowMs()));
						sig = genSig(sr);
						if(sig != null) {
							isError = false;
						}
						if(Logger.IS_TRACE_ENABLED) {
							lgr.trace("Generated sig. " + (sig != null));
						}
					} else {
						if(Logger.IS_TRACE_ENABLED) {
							lgr.info("Could not locate file for fingerprinting: " + file);
						}
					}
				} else {
					if(Logger.IS_TRACE_ENABLED) {
						lgr.trace("File is null for lsid");
					}
				}
			} catch (Exception e) {
				lgr.error(e,e);
				isError = true;
			} 
		}

		boolean commit = false;

		try {
			if(Logger.IS_TRACE_ENABLED) {
				lgr.trace("Updating database for : " + lsid);
			}
			beginTransaction();

			if(toGenerate) {
				sigDAO.signatureProcessed(req.getId(),isError);
			} else {
				sigDAO.signatureProcessed(req.getId(),false);
			}

			if(sig != null) {
				storeSignatures(lsid, sig);
			}

			commit = true;

		}  finally {
			commitOrRollback(commit);
		}

	}


	private  MixzingSignatureResponse genSig(MixzingSignatureRequest sr) {
		int[] data = new int[4];
		String inputfile = sr.getFileName();
		MixzingAudioInfoImpl inf = null;
		List<MixzingSignatureData> sigs = new ArrayList<MixzingSignatureData>();

		for(MixzingSignatureWindow win : sr.getWindows()) {
			long[] finger = MixZingFingerPrinter.generateFingerValues(inputfile, win.getSkip(), win.getDuration(), data);
			if(finger != null && finger.length > 0) {
				List<Long> sigp = new ArrayList<Long>();
				for(long l : finger) {
					sigp.add(l);
				}
				if(inf == null) {
					inf = new MixzingAudioInfoImpl();
					inf.setChannels(data[2]);
					inf.setFrequency(data[1]);
					inf.setMsPerframe(0);
					inf.setBitRate(0);
				}
				MixzingSignatureData sigdata = new MixzingSignatureDataImpl(sigp,win,SIG_CODE_VER);
				sigs.add(sigdata);
			}
		}

		MixzingSignatureResponse resp= null; 

		if(sigs.size() > 0) {
			resp = new MixzingSignatureResponseImpl(inf,sigs);
		}

		return resp;
	}

	private void storeSignatures(long lsid, MixzingSignatureResponse sig) {

		MixzingAudioInfo info = sig.getAudioInfo();

		for(MixzingSignatureData sd : sig.getSignatures()) {
			sigValDAO.addSignature(lsid,info,sd);
		}
	}



	private Track locateTrack(long lsid) {
		return tSvc.findByLsid(lsid);
	}

	public void shutDown() {
		if(thr != null) {
			synchronized (this) {
				try {
					thr.setPriority(Thread.NORM_PRIORITY);
				}
				catch (Exception e) {
					lgr.debug("exception setting priority to norm on shutdown: " + e);
				}
				this.isShuttingDown = true;
				this.notifyAll();
			}
			try {
				thr.join();
			} catch (InterruptedException e) {
			}		
		}
	}

	public synchronized void wakeup() {
		this.notifyAll();
	}

	public void uncaughtException(Thread t, Throwable e) {
		lgr.fatal("uncaught exception in " + t.toString(), e);
	}

	public class MixzingSignatureRequestImpl implements MixzingSignatureRequest {

		private String fileName;
		private List<MixzingSignatureWindow> windows;

		MixzingSignatureRequestImpl(String f, List<MixzingSignatureWindow> w) {
			this.fileName = f;
			this.windows  = w;
		}
		public String getFileName() {
			// TODO Auto-generated method stub
			return fileName;
		}

		public List<MixzingSignatureWindow> getWindows() {
			// TODO Auto-generated method stub
			return windows;
		}

	}

	public String getInstalledCodeVersion() {
		return SIG_CODE_VER + "";
	}


	public void updateSignatureCode(File sigJar) {
		// TODO Auto-generated method stub
		
	}
	
}
