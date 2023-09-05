package com.mixzing.servicelayer.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.log.Logger;
import com.mixzing.message.messages.impl.ClientMessageEnvelope;
import com.mixzing.message.messages.impl.JSONMap;
import com.mixzing.message.messages.impl.ServerMessageEnvelope;
import com.mixzing.servicelayer.MixzingMarshaller;

public class JSONMarshaller implements MixzingMarshaller {

	private static Logger lgr = Logger.getRootLogger();

	public byte[] marshall(Object o) {
		JSONStringer stringer = new JSONStringer();
		ClientMessageEnvelope env = (ClientMessageEnvelope) o;
		try {
			env.toJson(stringer);
			String json = stringer.toString();
			//			Log.i("MIXZINGJSON", o + "----->" + json);
			try {
				return json.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				lgr.error("UnsupportedEncodingException ",e);
			}
		} catch (JSONException e) {
			if(Logger.IS_TRACE_ENABLED) {
				lgr.trace("json exception = " + e);
			}
		}
		return null;
	}

	public ByteArrayOutputStream marshallToStream(Object o) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream os = new ByteArrayOutputStream(256);
		marshallToStream(o, os);
		return os;
	}

	public boolean marshallToStream(Object o, OutputStream out) {
		// TODO Auto-generated method stub
		byte[] b = marshall(o);
		if(Logger.IS_TRACE_ENABLED) {
			lgr.trace("json object len = " + (b != null ? b.length : 0));
		}
		if(b != null) {
			try {
				out.write(b);
				out.flush();
				out.close();
			} catch (IOException e) {
				lgr.trace("Got exception json marshalling " + e);
			}
		}
		return true;
	}

	public Object unmarshall(byte[] data) {
		ByteArrayInputStream in  = new ByteArrayInputStream(data);
		return unmarshall(in);	
	}

	public Object unmarshall(InputStream is) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		try {
			while ((len = is.read(buffer)) > 0) {
				bout.write(buffer,0,len);
			}
		} catch (IOException e) {
			lgr.error("JSONMarshaller.read:", e);
		}

		String s = null;
		try {
			s = bout.toString("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			lgr.error("JSONMarshaller.unmarshall:", e1);
		}

		try {
			if(s != null) {
				JSONObject json = new JSONObject(s);
				int type = json.getInt(JSONMap.JSON_TYPE);
				if(type == JSONMap.ClientMessageEnvelope) {
					return new ClientMessageEnvelope(json);
				} else if(type == JSONMap.ServerMessageEnvelope) {
					return new ServerMessageEnvelope(json);
				} else {
					lgr.error("JSONMarshaller.unmarshall : bad message");
				}
			}
		} catch (JSONException e) {
			lgr.error("JSONMarshaller.unmarshall:", e);
		}

		return null;
	}

	//	String s = "{\"__class_type__\":\"ClientMessageEnvelope\",\"seqno\":1899332970,\"lib_id\":\"-1\",\"environMent\":\"platform=Android;language=en;country=US;timezone=-360;pkg=com.mixzing.basic;vers=12;java.version=0;java.vendor=The Android Project;java.runtime.name=Android Runtime;java.runtime.version=0.9;java.home=/system;os.name=Linux;os.arch=OS_ARCH;os.version=2.6.27-00110-g132305e;user.language=en;user.region=US;ro.com.google.locationfeatures=1;ro.product.board=;imei=000000000000000;ro.build.fingerprint=generic/google_sdk/generic/:1.5/CUPCAKE/150240:eng/test-keys;ro.build.user=android-build;ro.build.version.sdk=3;ro.product.manufacturer=unknown;ro.product.brand=generic;ro.product.locale.language=en;ro.build.id=CUPCAKE;ro.product.name=google_sdk;ro.build.display.id=google_sdk-eng 1.5 CUPCAKE 150240 test-keys;ro.build.type=eng;ro.build.version.release=1.5;dalvik.vm.stack-trace-file=/data/anr/traces.txt;rild.libpath=/system/lib/libreference-ril.so;ro.board.platform=;ro.product.device=generic;ro.product.locale.region=US;ro.build.version.incremental=150240;devid=unknown-1262472215422-963797797;ro.build.tags=test-keys;ro.build.host=apa27.mtv.corp.google.com;ro.kernel.android.checkjni=1;ro.build.description=google_sdk-eng 1.5 CUPCAKE 150240 test-keys;ro.config.notification_sound=F1_New_SMS.ogg;ro.config.nocheckin=yes;xmpp.auto-presence=true;ro.product.model=google_sdk ;rild.libargs=-d /dev/ttyS0;ro.build.product=generic;net.bt.name=Android;ro.build.date=Tue Jun 30 16:59:00 PDT 2009;ro.build.date.utc=1246406340;\",\"priority\":false,\"messages\":[{\"__class_type__\":\"ClientNewLibrary\"}]}";

}
