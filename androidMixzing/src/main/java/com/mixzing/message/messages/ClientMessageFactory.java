package com.mixzing.message.messages;

import java.io.Serializable;

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

public interface ClientMessageFactory  extends Serializable {


    /*
     * Creates an empty envelope
     */
    public ClientMessageEnvelope createNewEnvelope();

    /*
     * Creates a new library request message
     */
    public ClientNewLibrary createNewLibraryRequest();

    /*
     * creates a new library changes message
     */
    public ClientLibraryChanges createLibraryChanges();

    /*
     * creates a new playlist changes message
     */
    public ClientPlaylistChanges createPlaylistChanges();

    /*
     * creates a client ping message
     */
    public ClientPing createClientPing();

    /*
     * creates a ratings message
     */
    public ClientRatings createClientRatings();

    /*
     * creates a client track signatures message
     */
    public ClientTrackSignatures createClientTrackSignatures ();

    /*
     * creates a delete rating message
     */
	public ClientDeleteRatings createDeleteRatings();
    
    /*
     * Creates a new file download request
     */
    public ClientRequestFile createFileRequest();

    public ClientRequestRecommendations createClientRequestRecommendations();
    
    public ClientRequestDefaultRecommendations createClientRequestDefaultRecommendations();

	public ClientMessageEnvelope createNewEnvelope(boolean urgent);
}
