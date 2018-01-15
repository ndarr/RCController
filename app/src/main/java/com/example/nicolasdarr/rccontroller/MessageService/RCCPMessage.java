package com.example.nicolasdarr.rccontroller.MessageService;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
        this.sequenceNumber = lastSequenceNumber;
        lastSequenceNumber = this.sequenceNumber + 1;
        this.code = code;
        this.payload = payload;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SequenceNum: " + Integer.toString(sequenceNumber)+ "\r\n");
        stringBuilder.append("Code: " + code.label+ "\r\n");
        stringBuilder.append("Payload: " + Integer.toString(payload) + "\r\n");
        return stringBuilder.toString();
    }

    public String toMinString(){
        if(sequenceNumber==0 && payload == 0){
            return "No Message";
        }
        try{
            return Integer.toString(sequenceNumber) + " - " + code.label + " - " + Integer.toString(payload);
        }
        catch (Exception e){
        }
        return "";
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

    public byte[] toByteArray(){
        ByteBuffer messageBytes = ByteBuffer.allocate(12);
        messageBytes.order(ByteOrder.LITTLE_ENDIAN);
        System.out.println(messageBytes.capacity());

        messageBytes.putInt(this.sequenceNumber);
        messageBytes.putInt(this.code.status);
        messageBytes.putInt(this.payload);

        return messageBytes.array();
    }


    public static RCCPMessage parseByteArrayToRCCP(byte[] byteMessage){
                if(byteMessage.length != 12){
                    return null;
    }



        ByteBuffer sequenceBytes = ByteBuffer.wrap(Arrays.copyOfRange(byteMessage, 0, 4));
        ByteBuffer statusBytes = ByteBuffer.wrap(Arrays.copyOfRange(byteMessage, 4, 8));
        ByteBuffer payloadBytes = ByteBuffer.wrap(Arrays.copyOfRange(byteMessage, 8, 12));

        sequenceBytes.order(ByteOrder.LITTLE_ENDIAN);
        statusBytes.order(ByteOrder.LITTLE_ENDIAN);
        payloadBytes.order(ByteOrder.LITTLE_ENDIAN);

        //Convert to usable data types
        int sequenceNumber = sequenceBytes.getInt();
        EStatusCode status = EStatusCode.statusById(statusBytes.getInt());
        int payload = payloadBytes.getInt();

        //Return parsed RCCPMessage
        return new RCCPMessage(sequenceNumber, status, payload);
    }

    public void acknowledge() {
        acknowledged = true;
    }
}
