package com.mixzing.servicelayer.impl;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.mixzing.log.Logger;
import com.mixzing.servicelayer.MixzingMarshaller;

public class DebugTeeMarshaller implements MixzingMarshaller {

	protected static Logger lgr = Logger.getRootLogger();
	protected MixzingMarshaller parent;
    public DebugTeeMarshaller(MixzingMarshaller m) {
        parent =m;
    }

	public byte[] marshall(Object o) {
		return parent.marshall(o);
	}

	public ByteArrayOutputStream marshallToStream(Object o) {
		return parent.marshallToStream(o);
	}

	public boolean marshallToStream(Object o, OutputStream out) {
		ByteArrayOutputStream outStr = new ByteArrayOutputStream();
		boolean bl = parent.marshallToStream(o, outStr);
		lgr.trace("Out: " + new String(outStr.toByteArray()));
		byte[] b = outStr.toByteArray();
		try {
			out.write(b);
			out.flush();
			out.close();
		} catch (IOException e) {
			lgr.trace(e);
		}

		return bl;
	}

	public Object unmarshall(byte[] data) {
		lgr.trace("Input Msg:" + new String(data));
		return parent.unmarshall(data);
	}

	public Object unmarshall(InputStream is) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        
        while(true) {
        	int data;
        	try {
				data = is.read();
			} catch (IOException e) {
				lgr.trace("Got an exception in debug tee unmarshall " + e);
				data = -1;
			}
        	if(data == -1) {
        		break;
        	} else {
        		byte b = (byte) data;
        		bo.write(b);
        	}
        }

        String input = new String(bo.toByteArray());
        lgr.trace("Input Msg:" + input);
        
        ByteArrayInputStream is1 = new ByteArrayInputStream(bo.toByteArray());
		return parent.unmarshall(is1);
	}


}
