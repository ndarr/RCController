package com.example.nicolasdarr.rccontroller.MessageService;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedInteger;

import java.io.Serializable;

/**
 * Created by Nicolas on 22.11.2017.
 */

public class RCCPMessage implements Serializable{
    private static int lastSequenceNumber = 0;
    private int sequenceNumber;
    private EStatusCode code;
    private int payload;

    //public static RCCPMessage MESSAGE_PAIRING = new RCCPMessage(EStatusCode.HELLO, );

    public RCCPMessage(EStatusCode code, int payload){
        this.sequenceNumber = lastSequenceNumber + 1;
        lastSequenceNumber = this.sequenceNumber;
        this.code = code;
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RCCPMessage)) return false;

        RCCPMessage that = (RCCPMessage) o;

        if (sequenceNumber != that.sequenceNumber) return false;
        if (getPayload() != that.getPayload()) return false;
        return getCode() == that.getCode();
    }

    @Override
    public int hashCode() {
        int result = sequenceNumber;
        result = 31 * result + getCode().hashCode();
        result = 31 * result + getPayload();
        return result;
    }

    public EStatusCode getCode() {
        return code;
    }

    public void setCode(EStatusCode code) {
        this.code = code;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public byte[] toByteArray(){
        UnsignedInteger sequenceNumber = UnsignedInteger.valueOf(this.sequenceNumber);
        UnsignedInteger code = UnsignedInteger.valueOf(this.code.status);
        UnsignedInteger payload = UnsignedInteger.valueOf(this.payload);
        byte messageBytes[] = new byte[12];
        return messageBytes;
    }
}
