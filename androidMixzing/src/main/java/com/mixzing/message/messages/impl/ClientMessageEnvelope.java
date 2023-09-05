package com.mixzing.message.messages.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.SystemInfo;
import com.mixzing.android.AndroidUtil;
import com.mixzing.android.DeviceInfo;
import com.mixzing.message.messages.ClientMessage;

//@XmlRootElement
public class ClientMessageEnvelope <E extends Comparable<? super E>>
		implements Comparable<ClientMessageEnvelope<E>> , Serializable  {

	private static final long serialVersionUID = 1L;
	private static long envSentCount;
	private static int ENV_SKIP_COUNT = 100;
	private static String lastProps;

	private static String[] systemProps = {
		"java.version",
		"java.vendor",
		"java.runtime.name",
		"java.runtime.version",
		"java.home",
		"os.name",
		"os.arch",
		"os.version",
		"user.language",
		"user.region",
		"java.endorsed.dirs"
	};


	/*
	 * A Unique message id generated for each message sent by the client.
	 * Used to implement a reliable delivery protocol. The server will resend
	 * the last response if it sees a duplicate msgid.
	 *
	 */
	
	private long seqno;

	/*
	 * Library id identifying the client.
	 */
	
	private String lib_id;

	/*
	 * One or messages to be sent by the client
	 */
	private List<ClientMessage> messages;


	// used for sorting by priority queue
	private boolean priority;
	
	private String environMent;


	/**
	 * Called when fixing up queued messages after first time startup. This can happen when the
	 * Server is offline when we made the new library request to the server, and we went on to
	 * resolve the client library generating new track addition messages.
	 *
	 * @param libId Library Id received from the server
	 */
	public void updateLibraryId(String libId) {
		assert(libId.equals("-1"));
		lib_id = libId;
	}

	public ClientMessageEnvelope() {
		// must have no argument constructor for YAML
	}

	public ClientMessageEnvelope(long nextMsgId, String libraryId) {
		this.seqno = nextMsgId;
		this.lib_id = libraryId;
		messages = new ArrayList<ClientMessage>();

		// periodically add the environment data
		if((envSentCount % ENV_SKIP_COUNT) == 0) {
			addEnvironment(false);
		}
		envSentCount++;
	}

	//@XmlAttribute(name="lib_id")
	public String getLib_id() {
		return lib_id;
	}

	public void setLib_id(String library_id) {
		this.lib_id = library_id;
	}

	//@XmlAttribute
	public long getVersion () {
		return ClientMessageEnvelope.version;
	}
	
	//@XmlElement
	public List<ClientMessage> getMessages() {
		return messages;
	}

	//@XmlAttribute
	public long getSeqno() {
		return seqno;
	}

	public void setMessages(List<ClientMessage> messages) {
		this.messages = messages;
	}

	public void setSeqno(long seqno) {
		this.seqno = seqno;
	}

	public void setVersion (long version) {
		assert version == ClientMessageEnvelope.version;
	}

	//@XmlAttribute
	public String getEnvironMent() {
		return this.environMent;
	}
	
	public void setEnvironMent(String env) {
		this.environMent = env;
	}

	public void appendEnvironMent(String addEnv) {
		if (environMent == null) {
			environMent = addEnv;
		}
		else {
			environMent += addEnv;
		}
	}

	// set the environment from system props and device info
	// don't set it unless it's changed since last time or force is true
	//
	public void addEnvironment(boolean force) {
		Locale locale = Locale.getDefault();
		int timezone = TimeZone.getDefault().getRawOffset() / 60000;  // UTC offset in minutes
		StringBuilder sb = new StringBuilder();

		sb.append("platform=");
		sb.append(SystemInfo.OS());
		sb.append(";language=");
		sb.append(locale.getLanguage());
		sb.append(";country=");
		sb.append(locale.getCountry());
		sb.append(";timezone=");
		sb.append(timezone);
		sb.append(";pkg=");
		sb.append(AndroidUtil.getPackageName());
		sb.append(";vers=");
		sb.append(AndroidUtil.getVersionCode());
		sb.append(";");

		// add the system props
		getSystemProps(sb);

		// add the device info
		Properties devprops = DeviceInfo.getDeviceProps(force);
		Enumeration<?> keys = devprops.propertyNames();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			appendProp(sb, key, devprops.getProperty(key));
		}

		// set the env if the props have changed or force is true
		String props = sb.toString();
		if (force || !props.equals(lastProps)) {
			lastProps = environMent = props;
		}
	}

	private void getSystemProps(StringBuilder sb) {
		for (String key : systemProps) {
			appendProp(sb, key, System.getProperty(key));
		}
	}

	private void appendProp(StringBuilder sb, String key, String value) {
		if (value != null) {
			sb.append(key);
			sb.append("=");
			sb.append(value);
			sb.append(";");
		}
	}

	public void addMessage( ClientMessage msg) {
		this.messages.add(msg);
	}

	public void markPriority() {
		priority = true;
	}

	// do not name this method isPriority otherwise it will be saved by YAML
	// resulting in a filed that the server does not expect
	public boolean priorityMessage () {
		return priority;
	}

	/**
	 * This results in fifo behavior when inserting into the Message Queue
	 * with priority messages being ahead of normal messages.
	 */
	public int compareTo (ClientMessageEnvelope<E> other) {
		int rval;
		if (equals(other)) {
			rval = 0;
		}
		else if (priority == other.priority) {
			rval = seqno < other.seqno ? -1 : 1;
		} else {
			rval = priority ? -1 : 1;
		}
		return rval;
	}


	private static final long version = 10;
	
    public void toJson(JSONStringer stringer) throws JSONException  {
    	stringer.object();
    	
    	stringer.key(JSONMap.JSON_TYPE);
    	stringer.value(JSONMap.ClientMessageEnvelope);
    	
    	stringer.key("seqno");
    	stringer.value(seqno);
    	
    	stringer.key("lib_id");
    	stringer.value(lib_id);
    	
    	stringer.key("environMent");
    	stringer.value(environMent);
    	
    	stringer.key("priority");
    	stringer.value(priority);
    	
    	stringer.key("messages");
    	stringer.array();
    	for(ClientMessage tr : messages) {
    		tr.toJson(stringer);
    	}
    	stringer.endArray();
    	
    	stringer.endObject();
    }	
    
    public ClientMessageEnvelope(JSONObject json) throws JSONException {
    	messages = new ArrayList<ClientMessage>();
    	
    	seqno = json.getLong("seqno");
    	lib_id = json.getString("lib_id");
    	environMent = json.getString("environMent");
    	priority = json.getBoolean("priority");
    	
    	JSONArray clntmsgs = json.getJSONArray("messages");
    	for(int i=0;i<clntmsgs.length();i++) {
    		JSONObject clntmsg = clntmsgs.getJSONObject(i);
  
    		ClientMessage msg = null;
    	  		
    		int mtype = clntmsg.getInt(JSONMap.JSON_TYPE);
    		switch(mtype) {
    			case JSONMap.ClientDeleteRatings:
    				msg = new ClientDeleteRatings(clntmsg);
    				break;
    			case JSONMap.ClientLibraryChanges:
    				msg = new ClientLibraryChanges(clntmsg);
    				break;
    			case JSONMap.ClientNewLibrary:
    				msg = new ClientNewLibrary(clntmsg);
    				break;
    			case JSONMap.ClientPing:
    				msg = new ClientPing(clntmsg);
    				break;
    			case JSONMap.ClientPlaylistChanges:
    				msg = new ClientPlaylistChanges(clntmsg);
    				break;
    			case JSONMap.ClientRatings:
    				msg = new ClientRatings(clntmsg);
    				break;
    			case JSONMap.ClientRequestDefaultRecommendations:
    				msg = new ClientRequestDefaultRecommendations(clntmsg);
    				break;    				
    			case JSONMap.ClientRequestFile:
    				msg = new ClientRequestFile(clntmsg);
    				break;
    			case JSONMap.ClientRequestRecommendations:
    				msg = new ClientRequestRecommendations(clntmsg);
    				break;
    			case JSONMap.ClientTagRequest:
    				msg = new ClientTagRequest(clntmsg);
    				break;
    			case JSONMap.ClientTrackSignatures:
    				msg = new ClientTrackSignatures(clntmsg);
    				break;
    			default:
    				break;
    				
    		}
    			
    		messages.add(msg);
    	}
    
    }

}
