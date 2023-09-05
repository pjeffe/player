package com.mixzing.message.messages;

import java.io.Serializable;

//@XmlTransient
public interface ServerMessage extends Serializable {


    public String getType();
    public void setType(String s);

}
