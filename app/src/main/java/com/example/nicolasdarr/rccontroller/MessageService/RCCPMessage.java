package com.example.nicolasdarr.rccontroller.MessageService;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class RCCPMessage implements Serializable{
    private static int lastSequenceNumber = 0;
    private boolean acknowledged;
    private int sequenceNumber;
    private EStatusCode code;
    private int payload;

    public RCCPMessage(int sequenceNumber, EStatusCode code, int payload){
        this.sequenceNumber = sequenceNumber;
        this.code = code;
        this.payload = payload;
    }

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

        return sequenceNumber == that.sequenceNumber && getPayload() == that.getPayload() && getCode() == that.getCode();
    }

    @Override
    public int hashCode() {
        int result = sequenceNumber;
        result = 31 * result + getCode().hashCode();
        result = 31 * result + getPayload();
        return result;
    }

    EStatusCode getCode() {
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

    public byte[] toByteArray(){

        byte[] sequenceNumberBytes = ByteBuffer.allocate(4).putInt(sequenceNumber).array();
        byte[] statusBytes = ByteBuffer.allocate(4).putInt(code.status).array();
        byte[] payloadBytes = ByteBuffer.allocate(4).putInt(payload).array();

        ByteBuffer messageBytes = ByteBuffer.allocate(12);

        System.out.println(messageBytes.capacity());
        messageBytes.put(sequenceNumberBytes);
        messageBytes.put(statusBytes);
        messageBytes.put(payloadBytes);

        return messageBytes.array();
    }


    public static RCCPMessage parseByteArrayToRCCP(byte[] byteMessage){
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

    public void acknowledge() {
        acknowledged = true;
    }
}
