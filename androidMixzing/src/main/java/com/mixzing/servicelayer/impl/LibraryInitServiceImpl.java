package com.mixzing.servicelayer.impl;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mixzing.MixzingConstants;
import com.mixzing.android.AndroidUtil;
import com.mixzing.android.MixzingNetworkStateListener;
import com.mixzing.android.Preferences.Keys;
import com.mixzing.derby.DatabaseManager;
import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.log.Logger;
import com.mixzing.message.messages.ServerMessage;
import com.mixzing.message.messages.ServerMessageEnum;
import com.mixzing.message.messages.impl.ServerMessageEnvelope;
import com.mixzing.message.messages.impl.ServerNewLibraryResponse;
import com.mixzing.message.messages.impl.ServerResponseDelayed;
import com.mixzing.message.transport.ServerTransport;
import com.mixzing.musicobject.Library;
import com.mixzing.musicobject.OutboundMsgQ;
import com.mixzing.servicelayer.LibraryService;
import com.mixzing.servicelayer.MessagingService;
import com.mixzing.servicelayer.MixzingMarshaller;

public class LibraryInitServiceImpl extends Thread {

	protected static final Logger lgr = Logger.getRootLogger();

	protected LibraryService libSvc;
	protected MessagingService messService;
	protected MixzingMarshaller marshaller;
	protected ServerTransport messageHandler;
	private boolean isShuttingDown;
	protected long WAIT_NOTIFY_TIMEOUT = 30000;
	private static final int FAIL_THRESHOLD = 5;
	private long DEFAULT_PING_DELAY = 30000;
	private long pingDelay = DEFAULT_PING_DELAY;
	boolean processed = false;

	public LibraryInitServiceImpl(LibraryService ls, MessagingService ms, MixzingMarshaller mm, ServerTransport st) {
		libSvc=ls;
		messService = ms;
		marshaller = mm;
		messageHandler= st;
		this.setName("Library Id");
	}

	public void ensureLibrary() {


		Library lib = libSvc.getLibrary();
		String libraryId = lib.getServerId();

		if(Logger.IS_TRACE_ENABLED) {
			lgr.trace("Ensure library called id was : " + libraryId);
		}

		boolean commit= false;
		if(libraryId.equals("-1")) {
			beginTransaction();
			try {
				messService.queueNewLibraryRequest();
				libraryId = "-2";
				libSvc.updateLibraryId("-2");
				commit = true;
			} finally {
				commitOrRollback(commit);
			}
		}

		if("-2".equals(libraryId)) {
			MixzingNetworkStateListener listener = new MixzingNetworkStateListener(AndroidUtil.getAppContext(), messageHandler, null);
			fetchNewLibId();
			listener.shutdown();
		}

		libraryId = lib.getServerId();

		AndroidUtil.setStringPref(null, AndroidUtil.getCardSpecificPrefKey(Keys.LIB_ID), libraryId);

		if(Logger.IS_TRACE_ENABLED) {
			lgr.trace("Ensure library returning - id is : " +  libraryId);
		}
	}

	private void fetchNewLibId() {
		this.start();
		boolean interrupted = true;
		while(interrupted) {
			try {
				this.join(); 
				interrupted = false;
			} catch (InterruptedException e) {
				interrupted = true;
				if(Logger.IS_TRACE_ENABLED) {
					lgr.trace("MIXZING","Attempting server communicator shutdown ... interrupted");
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.mixzing.servicelayer.impl.BaseService#shutDown()
	 */
	public void shutDown() {
		synchronized(this) {
			isShuttingDown = true;	 
			if(Logger.IS_TRACE_ENABLED)
				lgr.trace("Attempting server communicator shutdown");
			messageHandler.shutDown();

			this.notifyAll();
			try {
				this.interrupt();
				if(Logger.IS_TRACE_ENABLED)
					lgr.trace("Should have interrupted Server Thread: " + this.isInterrupted());
			} catch (Exception e) {
				lgr.trace("Attempting to interrrupt gave an exception: " + e.getMessage());
			}
		}

		try {
			this.join(10*1000); // Wait for only 10 seconds since destroy gives us 20 secs before showing ANR to user
		} catch (InterruptedException e) {
			if(Logger.IS_TRACE_ENABLED) {
				lgr.trace("MIXZING","Attempting server communicator shutdown ... interrupted");
			}
		}

	}

	private boolean isUnitTest = false;

	protected boolean beginTransaction() {
		boolean isOk = false;
		if(!isUnitTest) {
			try {
				DatabaseManager.beginTransaction();
				isOk = true;
			} catch (SQLException e) {
				throw new UncheckedSQLException(e,"begintransaction");
			}
		}
		return isOk;
	}


	private boolean commitOrRollback(boolean commit) {
		boolean isOk = false;
		if(!isUnitTest) {
			try {
				if(commit) {
					DatabaseManager.commitTransaction();

				} else {
					DatabaseManager.rollbackTransaction();
				}
				isOk = true;
			} catch (SQLException e) {
				throw new UncheckedSQLException(e);
			} catch (IllegalStateException e1) {
				lgr.error("commitOrRollback",e1);
			}
		}
		return isOk;
	}

	public boolean processServerMessage(ServerMessageEnvelope sEnv, OutboundMsgQ request) {
		List<ServerMessage> l = sEnv.getMessages();
		boolean delayed = false;

		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Process server message: " + sEnv.getLib_id());
		// reset message loop wait period, a PingMe message will change this though.
		pingDelay = DEFAULT_PING_DELAY;


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

			Map<String,String> attrMap = null;
			attrMap = sEnv.getServer_params_android().getOtherAttributes();
			String reinit = attrMap.get(MixzingConstants.SERVER_PARAM_REINIT_LIBRARY);
			if(reinit != null && reinit.equals(MixzingConstants.REINIT_LIB_TRUE)) {
				// XXX: We should never get a REINIT with a new lib ?
				if(Logger.IS_DEBUG_ENABLED)
					lgr.debug("Messages: Attempt reinit");
				reinitializeAndExit();
			}


			synchronized(DatabaseManager.class) {
				beginTransaction();
				try {

					updateLibraryStatus(sEnv,attrMap);
					for (ServerMessage o : l) {
						processMessage(o);
					}
					if(!delayed) {
						messService.deleteQueuedMessage(request);
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
						messService.deleteQueuedMessage(request);
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
		return !delayed;
	}

	/* (non-Javadoc)
	 * @see com.mixzing.servicelayer.impl.ServerCommunicationThread#run()
	 */
	public void run() {

		int contiguousFails = 0;
		while (!isShuttingDown && !processed) {
			try {
				OutboundMsgQ msg = messService.getNextQueuedMessage();
				contiguousFails=0;
				if(!isShuttingDown) {
					if(Logger.IS_TRACE_ENABLED)
						lgr.trace("SENDING:" + msg.getId() + ":lib:" + msg.getLibId() + ":type:" + msg.getMsgType() + ":count:" + msg.getMsgCount());
					sendMessageAndProcessResponse(msg);
				}

			} catch (Exception e) {
				contiguousFails++;
				if(contiguousFails >= FAIL_THRESHOLD) {
					/*
					 * There is a bug in code somewhere. It causes getNextMessage to fail saying we are opening a closed
					 * database.
					 */
					AndroidUtil.terminateProcess("Bug in code. getNextQueuedMessage failing repeatedly with exception. " + e.getMessage() );
				}
				lgr.error(e,e);
				waitForNotifyTimeout(30000); // sleep for 30 seconds to prevent error flood
			}
		}
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Exiting server comm thread");
	}

	protected void processNewLibrary(ServerNewLibraryResponse message) {	
		String serverId = message.getLibrary_id();
		libSvc.updateLibraryId(serverId);
		// this is for the case where the initial startup timed out of ensureLibrary - if we allow that case
		messService.updateLibraryId(serverId);
	}

	private synchronized void waitForNotifyTimeout(long time) {
		/*
		 * We  should wait for either timeout or when the next messsage 
		 * is queued.
		 */
		if(!isShuttingDown) {
			try {
				//lgr.trace("Going into notify timeout");
				wait(time);
			} catch (InterruptedException e) {
				if (Logger.IS_TRACE_ENABLED)
					lgr.trace("Waking up from notify timeout: " + this.isShuttingDown);
			}

		}

	}

	protected boolean sendMessageAndProcessResponse(OutboundMsgQ msg) {


		try {
			ServerMessageEnvelope sEnv = null;
			InputStream serverResponse = null;
			int loopCount = 0;
			while (!processed) {


				// note sending a message persists the state while waiting for the server response
				if(Logger.IS_TRACE_ENABLED)
					lgr.trace("Sending message to server: " + msg.getId());
				serverResponse = messageHandler.sendMessage(msg.getMsg());

				// if there was an error then don't send it again
				if (messageHandler.getResponseCode() == 500 || serverResponse == null){
					if(isShuttingDown && serverResponse == null) {
						break;
					}
					if(Logger.IS_TRACE_ENABLED)
						lgr.trace("Discarding errored message to server: " + msg.getId());
					erroredMessage(msg);
					break;
				}

				try {
					sEnv = (ServerMessageEnvelope) marshaller.unmarshall(serverResponse);
					if(sEnv == null) {
						if(Logger.IS_TRACE_ENABLED)
							lgr.trace("Server response resulted in a null envelope ?. Need to discard the message ?... Lets try a few more times" );
						waitForNotifyTimeout(WAIT_NOTIFY_TIMEOUT);                  
						if(loopCount++>10) {
							lgr.error("Server response resulted in a null envelope even after " + loopCount + " tries. Discarding." );
							erroredMessage(msg);
							break;
						}
					}
				} catch (Exception e) {
					lgr.error("objectFromTransportStream failed", e);
					erroredMessage(msg);
					break;
				}
				if (sEnv != null) {
					// a delayed response from the server will come back as not processed
					// everything else will have been processed
					processed = processServerMessage(sEnv,msg);
				}
			}
		} catch (Exception e) {
			lgr.error("sendMessageAndProcessResponse failed", e);
		}

		return processed;
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

	private void erroredMessage(OutboundMsgQ msg) {
		processErroredMessage(msg);
		// After any error message just wait for additional 30 seconds even if
		// it was a timeout
		waitForNotifyTimeout(WAIT_NOTIFY_TIMEOUT);		
	}


	/*
	 * 
	 * Called when there is a 500 error response from the server
	 * 
	 */
	public void processErroredMessage(OutboundMsgQ request) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Catastrophic error at boot strap. Cannot discard this message.");
		return;
	}

	private void updateLibraryStatus(ServerMessageEnvelope env, Map<String,String> attrMap) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("In process libstatus");
		if(attrMap != null) {
			libSvc.setServerParameters(attrMap);
			libSvc.parametersUpdated();
		}
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

	private void processMessage(ServerMessage o) {

		try {
			switch (ServerMessageEnum.valueOf(o.getType())) {

			case NEW_LIBRARY:
				if(Logger.IS_TRACE_ENABLED)
					lgr.debug("MSG: NEWLIB");
				processNewLibrary((ServerNewLibraryResponse) o);
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

	protected Logger getLogger() {
		return lgr;
	}

}
