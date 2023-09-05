package com.mixzing.servicelayer.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.mixzing.log.Logger;
import com.mixzing.message.messages.impl.ClientMessageEnvelope;
import com.mixzing.message.messages.impl.ClientPing;
import com.mixzing.servicelayer.MixzingMarshaller;



public class SerializingMarshaller implements MixzingMarshaller {

	private static Logger lgr = Logger.getRootLogger();

	public byte[] marshall(Object o) {
		lgr.trace("Serialize: " + o);
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

	public boolean marshallToStream(Object o, OutputStream bo) {
		if(Logger.IS_TRACE_ENABLED)
			lgr.trace("Serialize: " + o);
		try {
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(o);
			oo.close();
			return true;
		} catch (IOException e) {
			if(lgr.IS_DEBUG_ENABLED) {
				lgr.debug(e);
			}
			lgr.error(e, e);
		}
		return false;
	}

	public Object unmarshall(byte[] data) {
		ByteArrayInputStream in  = new ByteArrayInputStream(data);
		return unmarshall(in);
	}

	public Object unmarshall(InputStream is) {

		ByteArrayOutputStream bos = read(is);
		byte[] data  = bos.toByteArray();
		//System.out.println("Read bytes: " + data.length);

		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		Object i = null;
		try {
			ObjectInputStream in = new ObjectInputStream(bis);
			i = in.readObject();
		} catch (IOException e) {
			lgr.error("SerializingMarshaller.unmarshall:", e);
		} catch (ClassNotFoundException e1) {
			lgr.error("SerializingMarshaller.unmarshall:", e1);
		}
		return i;
	}

	private ByteArrayOutputStream read(InputStream responseStream) {

		int len;
		byte[] buffer = new byte[256];
		ByteArrayOutputStream fullResponse = new ByteArrayOutputStream(256);
		try {
			while ((len = responseStream.read(buffer)) >= 0) {
				fullResponse.write(buffer,0,len);
			}

			responseStream.close();
			fullResponse.close();
		} catch (IOException e) {
			lgr.error("SerializingMarshaller.read:", e);
		}

		return fullResponse;
	}

	public static void main(String[] args) {
		ClientMessageEnvelope env = new ClientMessageEnvelope(4l,"hello");
		ClientPing ping = new ClientPing();
		env.addMessage(ping);

		SerializingMarshaller sm = new SerializingMarshaller();
		byte[] b = sm.marshall(env);
		System.out.println("Got back: " + b);

		ClientMessageEnvelope back = (ClientMessageEnvelope) sm.unmarshall(b);
		System.out.println(back.getLib_id() + " " + back.getSeqno() ); 
		for(Object m : back.getMessages()) {
			System.out.println("Got object: " + m);
		}


	}
}
