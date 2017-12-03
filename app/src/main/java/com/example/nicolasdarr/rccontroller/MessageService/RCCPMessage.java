package com.example.nicolasdarr.rccontroller.MessageService;

import com.example.nicolasdarr.rccontroller.Util.Array;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class RCCPMessage implements Serializable{
    private static int lastSequenceNumber = 0;
    private int sequenceNumber;
    private EStatusCode code;
    private int payload;

    private RCCPMessage(int sequenceNumber, EStatusCode code, int payload){
        this.sequenceNumber = sequenceNumber;
        this.code = code;
        this.payload = payload;
    }

    RCCPMessage(EStatusCode code, int payload){
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

    public int getSequenceNumber(){
        return this.sequenceNumber;
    }

    byte[] toByteArray(){

        byte[] sequenceNumberBytes = ByteBuffer.allocate(4).putInt(sequenceNumber).array();
        byte[] statusBytes = ByteBuffer.allocate(4).putInt(code.status).array();
        byte[] payloadBytes = ByteBuffer.allocate(4).putInt(payload).array();

        byte messageBytes[] = new byte[12];
        Array.replacePart(messageBytes, sequenceNumberBytes, 0);
        Array.replacePart(messageBytes, statusBytes, 4);
        Array.replacePart(messageBytes, payloadBytes, 8);
        return messageBytes;
    }


    static RCCPMessage parseByteArrayToRCCP(byte[] byteMessage){
        if(byteMessage.length != 12){
            return null;
        }

        //Subarrays from byte array
        byte[] sequenceBytes = Arrays.copyOfRange(byteMessage, 0, 4);
        byte[] statusBytes = Arrays.copyOfRange(byteMessage, 4, 8);
        byte[] payloadBytes = Arrays.copyOfRange(byteMessage, 8, 12);


        //Convert to usable data types
        int sequenceNumber = ByteBuffer.wrap(sequenceBytes).getInt();
        EStatusCode status = EStatusCode.fromByteArray(statusBytes);
        int payload = ByteBuffer.wrap(payloadBytes).getInt();

        //Return parsed RCCPMessage
        return new RCCPMessage(sequenceNumber, status, payload);
    }
}
