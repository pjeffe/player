package com.mixzing.message.transport;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 *
 * Abstraction to allow multiple transport protocols to the server. HTTP, FTP, EMAIL etc
 *
 * @author sandeep
 *
 */
public interface ServerTransport {

    public InputStream sendMessage(ByteArrayOutputStream s);
    public InputStream sendMessage(byte[] s);
    public InputStream sendMessage(byte[] s, int retries);
    public int getResponseCode();
    public void shutDown();
    public void networkStateChanged(boolean available);
}
