/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://jwsdp.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 */
/*
 * @(#)UUEncoderStream.java   1.3 02/03/27
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.mixzing.message.transport.impl;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class implements a UUEncoder. It is implemented as
 * a FilterOutputStream, so one can just wrap this class around
 * any output stream and write bytes into this filter. The Encoding
 * is done as the bytes are written out.
 *
 * @author John Mani
 */

public class UUEncoderStream extends FilterOutputStream {
    private byte[] buffer; 	// cache of bytes that are yet to be encoded
    private int bufsize = 0;	// size of the cache
    private boolean wrotePrefix = false;

    protected String name; 	// name of file
    protected int mode;		// permissions mode

    /**
     * Create a UUencoder that encodes the specified input stream
     * @param out        the output stream
     */
    public UUEncoderStream(OutputStream out) {
	this(out, "encoder.buf", 644);
    }

    /**
     * Create a UUencoder that encodes the specified input stream
     * @param out        the output stream
     * @param name	 Specifies a name for the encoded buffer
     */
    public UUEncoderStream(OutputStream out, String name) {
	this(out, name, 644);
    }

    /**
     * Create a UUencoder that encodes the specified input stream
     * @param out        the output stream
     * @param name       Specifies a name for the encoded buffer
     * @param mode	 Specifies permission mode for the encoded buffer
     */
    public UUEncoderStream(OutputStream out, String name, int mode) {
	super(out);
	this.name = name;
	this.mode = mode;
	buffer = new byte[45];
    }

    /**
     * Set up the buffer name and permission mode.
     * This method has any effect only if it is invoked before
     * you start writing into the output stream
     */
    public void setNameMode(String name, int mode) {
	this.name = name;
	this.mode = mode;
    }

    public void write(byte[] b, int off, int len) throws IOException {
	for (int i = 0; i < len; i++)
	    write(b[off + i]);
    }

    public void write(byte[] data) throws IOException {
	write(data, 0, data.length);
    }

    public void write(int c) throws IOException {
	/* buffer up characters till we get a line's worth, then encode
	 * and write them out. Max number of characters allowed per
	 * line is 45.
	 */
	buffer[bufsize++] = (byte)c;
	if (bufsize == 45) {
	    writePrefix();
	    encode();
	    bufsize = 0;
	}
    }

    public void flush() throws IOException {
        // GM 11/28/06 Modified from original since flushing stream should not be writing
        // the UUEncode suffix line. Now flush just flushes the underlying stream but does
        // not flush the current buffer since we don't yet know how much data will be in it.
        out.flush();
    }

    public void close() throws IOException {
        if (bufsize > 0) { // If there's unencoded characters in the buffer
            writePrefix();
            encode();      // .. encode them
        }
        writeSuffix();
	out.close();
    }

    /**
     * Write out the prefix: "begin <mode> <name>"
     */
    private void writePrefix() throws IOException {
	if (!wrotePrefix) {
            // GM 11/28/06 Don't use PrintStream since on windows it writes CRLF
            String s = "begin " + mode + " " + name + "\n";
            //out.write(s.getBytes());
	    wrotePrefix = true;
	}
    }

    /**
     * Write a single line containing space and the suffix line
     * containing the single word "end" (terminated by a newline)
     */
    private void writeSuffix() throws IOException {
        // GM 11/28/06 Don't use PrintStream since on windows it writes CRLF
	//out.write(" \nend\n".getBytes());
    }

    /**
     * Encode a line.
     * Start off with the character count, followed by the encoded atoms
     * and terminate with LF. (or is it CRLF or the local line-terminator ?)
     * Take three bytes and encodes them into 4 characters
     * If bufsize if not a multiple of 3, the remaining bytes are filled
     * with '1'. This insures that the last line won't end in spaces
     * and potentiallly be truncated.
     */
    private void encode() throws IOException {
	byte a, b, c;
	int c1, c2, c3, c4;
	int i = 0;

	// Start off with the count of characters in the line
	out.write((bufsize & 0x3f) + ' ');

	while (i < bufsize) {
	    a = buffer[i++];
	    if (i < bufsize) {
		b = buffer[i++];
		if (i < bufsize)
		    c = buffer[i++];
		else // default c to 1
		    c = 1;
	    }
	    else { // default b & c to 1
		b = 1;
		c = 1;
	    }

	    c1 = (a >>> 2) & 0x3f;
	    c2 = ((a << 4) & 0x30) | ((b >>> 4) & 0xf);
	    c3 = ((b << 2) & 0x3c) | ((c >>> 6) & 0x3);
	    c4 = c & 0x3f;
	    out.write(c1 + ' ');
	    out.write(c2 + ' ');
	    out.write(c3 + ' ');
	    out.write(c4 + ' ');
	}
	// Terminate with LF. (should it be CRLF or local line-terminator ?)
	out.write('\n');
    }

    /**** begin TEST program *****
    public static void main(String argv[]) throws Exception {
	FileInputStream infile = new FileInputStream(argv[0]);
	UUEncoderStream encoder = new UUEncoderStream(System.out);
	int c;

	while ((c = infile.read()) != -1)
	    encoder.write(c);
	encoder.close();
    }
    **** end TEST program *****/
}
