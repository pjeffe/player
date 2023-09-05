package com.mixzing.message.messages.impl;

import com.mixzing.message.messages.ClientMessageFactory;
import com.mixzing.musicobject.Library;

public class MixzingClientMessageFactoryImpl implements ClientMessageFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Library lib;

	public MixzingClientMessageFactoryImpl(Library lib) {
		this.lib = lib;
	}

	private int urgentCounter = 1;
	
	private long getNextId() {
		long l = System.currentTimeMillis();
		long l1 = System.currentTimeMillis();
		while ( l == l1) {
			try {
				Thread.sleep(1);
			} catch (Exception e) {

			}
			l1 = System.currentTimeMillis();
		}

		l= l % 2147483647;

		if(l==0)
			l = 1;

		return l;
	}

	private long getNextId(boolean urgent) {
		long id = getNextId();
		return urgent ? (id / 2 + ++urgentCounter) : id ;
	}
	
	public ClientMessageEnvelope createNewEnvelope() {
		return createNewEnvelope(false);
	}

	public ClientMessageEnvelope createNewEnvelope(boolean urgent) {
		return new ClientMessageEnvelope(getNextId(urgent),getLibraryId());
	}

	public ClientNewLibrary createNewLibraryRequest() {
		return new ClientNewLibrary();
	}

	public ClientLibraryChanges createLibraryChanges() {
		return new ClientLibraryChanges();
	}

	public ClientPlaylistChanges createPlaylistChanges() {
		return new ClientPlaylistChanges();
	}

	public ClientPing createClientPing() {
		return new ClientPing();
	}

	public ClientPlaylistChanges createClientPlaylistChanges() {
		return new ClientPlaylistChanges();
	}

	public ClientRequestRecommendations createClientRequestRecommendations() {
		return new ClientRequestRecommendations();
	}

	public ClientRatings createClientRatings() {
		return new ClientRatings();
	}

	public ClientTrackSignatures createClientTrackSignatures () {
		return new ClientTrackSignatures();
	}


	protected String getLibraryId() {
		return lib.getServerId();
	}

	public ClientDeleteRatings createDeleteRatings() {
		return new ClientDeleteRatings();
	}

	public ClientRequestDefaultRecommendations createClientRequestDefaultRecommendations() {
		return new ClientRequestDefaultRecommendations();
	}

    public ClientRequestFile createFileRequest() {
        return new ClientRequestFile();
    }
   
}
