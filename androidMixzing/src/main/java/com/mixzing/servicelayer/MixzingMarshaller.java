package com.mixzing.servicelayer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public interface MixzingMarshaller {

    public byte[] marshall(Object o);
    
    public ByteArrayOutputStream marshallToStream(Object o);
    
    public boolean marshallToStream(Object o, OutputStream out);
    
    
    public Object unmarshall(byte[] data);
    
    public Object unmarshall(InputStream is);
    
}
