package com.mixzing.message.messages.impl;

import com.mixzing.message.messages.ServerMessage;
import com.mixzing.message.messages.ServerMessageEnum;

//@XmlRootElement
public class ServerFileResponse implements ServerMessage {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[]  fileData;
    private String  uuencodedFileData;
    private boolean isError;
    private boolean isUuencoded;
    /* Name of the file requested */
    private String  fileRequested;
    
    public ServerFileResponse() {

    }

    public String getType() {
        return ServerMessageEnum.FILERESPONSE.toString();
    }

    public void setType(String s) {
        // TODO Auto-generated method stub

    }

    //@XmlElement
    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    //@XmlElement
    public boolean isUuencoded() {
        return isUuencoded;
    }

    public void setUuencoded(boolean isUuencoded) {
        this.isUuencoded = isUuencoded;
    }

    //@XmlElement
    public String getUuencodedFileData() {
        return uuencodedFileData;
    }

    public void setUuencodedFileData(String uuencodedFileData) {
        this.uuencodedFileData = uuencodedFileData;
    }

    //@XmlElement
    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    //@XmlElement
    public String getFileRequested() {
        return fileRequested;
    }

    public void setFileRequested(String fileRequested) {
        this.fileRequested = fileRequested;
    }
    

}
