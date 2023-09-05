package com.mixzing.servicelayer.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import com.mixzing.log.Logger;
import com.mixzing.servicelayer.MixzingMarshaller;

public class XMLMarshaller implements MixzingMarshaller {
    
    private static Logger lgr = Logger.getRootLogger();
    
    protected Class[] classes;

    
    public XMLMarshaller(Class[] classes) {
        this.classes = classes;
 
        
    }
    
    
    public XMLMarshaller() throws ClassNotFoundException {
        this(new Class[] {
                Class.forName("com.mixzing.message.messages.impl.ServerPingMe"), 
                Class.forName("com.mixzing.message.messages.impl.ServerGenreBasisVectors"),
                Class.forName("com.mixzing.message.messages.impl.ServerNewLibraryResponse"),
                Class.forName("com.mixzing.message.messages.impl.ServerMessageEnvelope"),
                Class.forName("com.mixzing.message.messages.impl.ServerTrackMapping"),
                Class.forName("com.mixzing.message.messages.impl.ServerGenreBasisVectors"),
                Class.forName("com.mixzing.message.messages.impl.ServerRecommendations"),
                Class.forName("com.mixzing.message.messages.impl.ServerRequestSignature"),
                Class.forName("com.mixzing.message.messages.impl.ServerResponseDelayed"),
                Class.forName("com.mixzing.message.messages.impl.ServerTagResponse"),
                Class.forName("com.mixzing.message.messages.impl.ServerFileResponse"),
                Class.forName("com.mixzing.message.messages.impl.ServerTrackEquivalence"),
                Class.forName("com.mixzing.message.messages.impl.ServerParameters"),
                
                Class.forName("com.mixzing.message.messages.impl.ClientDeleteRatings"),
                Class.forName("com.mixzing.message.messages.impl.ClientLibraryChanges"),
                Class.forName("com.mixzing.message.messages.impl.ClientMessageEnvelope"),
                Class.forName("com.mixzing.message.messages.impl.ClientNewLibrary"), 
                Class.forName("com.mixzing.message.messages.impl.ClientPing"), 
                Class.forName("com.mixzing.message.messages.impl.ClientPlaylistChanges"),
                Class.forName("com.mixzing.message.messages.impl.ClientRatings"), 
                Class.forName("com.mixzing.message.messages.impl.ClientRequestDefaultRecommendations"),
                Class.forName("com.mixzing.message.messages.impl.ClientRequestRecommendations"),
                Class.forName("com.mixzing.message.messages.impl.ClientTagRequest"),
                Class.forName("com.mixzing.message.messages.impl.ClientTrackSignatures"),
                Class.forName("com.mixzing.message.messages.impl.ClientRequestFile"),
                
                Class.forName("com.mixzing.message.messageobject.impl.ClientPlaylist"),
                Class.forName("com.mixzing.message.messageobject.impl.ClientTrack"), 
                Class.forName("com.mixzing.message.messageobject.impl.GlobalSongSpec"),
                Class.forName("com.mixzing.message.messageobject.impl.MPXTags"),
                Class.forName("com.mixzing.message.messageobject.impl.TrackEquivalence"),
                Class.forName("com.mixzing.message.messageobject.impl.TrackMapping"),
                Class.forName("com.mixzing.message.messageobject.impl.TrackRating"),
                Class.forName("com.mixzing.message.messageobject.impl.TrackRecommendation"),
                Class.forName("com.mixzing.message.messageobject.impl.TrackSignature"),
                Class.forName("com.mixzing.message.messageobject.impl.TrackSignatureRequest")
        });
    }
    
    public byte[] marshall(Object o) {
        ByteArrayOutputStream str = marshallToStream(o);
        if(str != null) {
            return str.toByteArray();
        } else {
            return null;
        }
    }
    
    public ByteArrayOutputStream marshallToStream(Object o) {
        ByteArrayOutputStream boStream = new ByteArrayOutputStream(256);
        boolean isMarshalled = marshallToStream(o,boStream);
        if(isMarshalled) {
            return boStream;
        } else {
            return null;
        }
    }


    
    protected Class[] getClasses() {
        return classes;
    }
        
    protected void setClasses(Class[] classes) {
        this.classes = classes;
    }
        
    public void marshall(Object o, Writer writer) {
        // TODO Auto-generated method stub
        
    }
    
    public Object unmarshall(byte[] data) {
        ByteArrayInputStream in  = new ByteArrayInputStream(data);
        return unmarshall(in);
    }


	public boolean marshallToStream(Object o, OutputStream out) {
		// TODO Auto-generated method stub
		return false;
	}


	
	public Object unmarshall(InputStream is) {
		// TODO Auto-generated method stub
		return null;
	}
    
 
    
    

    
}
