package com.mixzing.servicelayer.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.mixzing.log.Logger;
import com.mixzing.servicelayer.MixzingMarshaller;

public class GzipMarshaller implements MixzingMarshaller {

	private static Logger lgr = Logger.getRootLogger();
	
    protected MixzingMarshaller parent;
    public GzipMarshaller(MixzingMarshaller m) {
        parent =m;
    }

    public byte[] marshall(Object o) {
        if(Logger.IS_TRACE_ENABLED) {
        	lgr.trace("GZip object ");
        }
    	ByteArrayOutputStream out = this.marshallToStream(o);
        if(out != null) {
            byte[] b  = out.toByteArray();
            if(Logger.IS_TRACE_ENABLED) {
            	lgr.trace("GZipped data size = " + b.length);
            }
            return b;
        } else {
            return null;
        }
        
    }

    public ByteArrayOutputStream marshallToStream(Object o) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        this.marshallToStream(o,out);
        return out;
    }


    public Object unmarshall(byte[] data) {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        return this.unmarshall(stream);
    }

    public Object unmarshall(InputStream is) {
        try {
            return parent.unmarshall(new GZIPInputStream(is));
        } catch (IOException e) {
            
        }
        return null;
    }

    public boolean marshallToStream(Object o, OutputStream out) {
        if(Logger.IS_TRACE_ENABLED) {
        	lgr.trace("GZip object to stream");
        }
    	boolean isDone = false;
        try {
            GZIPOutputStream stream = new GZIPOutputStream(out);
            boolean ret = parent.marshallToStream(o,stream);
            try {
				stream.close();
			} catch (IOException e) {
			}
        } catch (IOException e) {
            
        }
        return isDone;
    }

}
