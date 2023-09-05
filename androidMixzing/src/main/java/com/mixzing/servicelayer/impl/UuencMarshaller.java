package com.mixzing.servicelayer.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.mixzing.log.Logger;
import com.mixzing.message.transport.impl.UUDecoderStream;
import com.mixzing.message.transport.impl.UUEncoderStream;
import com.mixzing.servicelayer.MixzingMarshaller;

public class UuencMarshaller implements MixzingMarshaller {

	private static Logger lgr = Logger.getRootLogger();
	
    protected MixzingMarshaller parent;
    public UuencMarshaller(MixzingMarshaller m) {
        parent =m;
    }

    public byte[] marshall(Object o) {
        if(Logger.IS_TRACE_ENABLED) {
        	lgr.trace("UUEnc object ");
        }
        ByteArrayOutputStream out = this.marshallToStream(o);
        if(out != null) {
            byte[] b  = out.toByteArray();
            if(Logger.IS_TRACE_ENABLED) {
            	lgr.trace("UUenc data size = " + b.length);
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
        return parent.unmarshall(new UUDecoderStream(is));
    }

    public boolean marshallToStream(Object o, OutputStream out) {   
            UUEncoderStream stream = new UUEncoderStream(out);
            return parent.marshallToStream(o,stream);
    }

}
