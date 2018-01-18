package com.example.nicolasdarr.rccontroller.MessageService;


import android.content.Context;
import android.os.Vibrator;

import com.example.nicolasdarr.rccontroller.Controller.ControllerActivity;
import com.example.nicolasdarr.rccontroller.Util.Devices;
import com.example.nicolasdarr.rccontroller.Util.Devices.*;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import static com.example.nicolasdarr.rccontroller.Util.Devices.uartDevice;
import com.example.nicolasdarr.rccontroller.Util.Array;

/**
 * Created by Nicolas on 22.11.2017.
 */

public class MessageService implements Serializable{

    private static int BUFFER_LENGTH;

    private ArrayList<RCCPMessage> receivedConMessages = new ArrayList<>();
    public ArrayList<RCCPMessage> receivedAckMessages = new ArrayList<>();
    public ArrayList<RCCPMessage> sentMessages = new ArrayList<>();


    public MessageService(){
        //startAcknowledgeThread();
    }

    public boolean pair() throws NullPointerException{
        if(true){
            return true;
        }
        if(uartDevice == null){
            throw new NullPointerException("UART Device is not initialized!");
        }
        //Create Pairing message
        RCCPMessage pairingMessage = new RCCPMessage(EStatusCode.HELLO, 0);
        //Send Pairing message
        sendMessage(pairingMessage);
        //Wait for response
        byte responseBuffer[] = new byte[12];

        uartDevice.read(responseBuffer);

        RCCPMessage response = RCCPMessage.parseByteArrayToRCCP(responseBuffer);

        if(response.getCode() == EStatusCode.ACK && pairingMessage.getSequenceNumber() == response.getPayload()){
            return true;
        }
        else{
            return false;
        }
    }


    public RCCPMessage readMessage(int waitms){
        byte data[] = new byte[12];
        uartDevice.read(data, 12, 30000);
        RCCPMessage message = RCCPMessage.parseByteArrayToRCCP(data);
        if(!message.isValid()){
            uartDevice.read(new byte[1], 1, 1000);
        }
        return message;
    }

    public void sendMessage(RCCPMessage message){
        sentMessages.add(message);
        uartDevice.write(message.toByteArray());
    }

    public void addReceivedMessage(RCCPMessage message){
        if(message.getCode() == EStatusCode.ACK){
            receivedAckMessages.add(message);
        }
        else{
            receivedConMessages.add(message);
        }
    }

    private void startAcknowledgeThread(){
        new Thread(){
            @Override
            public void run(){
                while(true){
                    if(!receivedAckMessages.isEmpty()){
                        //Get last message from ReceivedMessages
                        RCCPMessage ackMessage = receivedAckMessages.remove(0);
                        RCCPMessage message = null;
                        try{
                            message = findSentMessage(ackMessage);
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        if(message != null){
                            int i = sentMessages.lastIndexOf(message);
                            sentMessages.get(i).acknowledge();
                        }
                    }
                }
            }
        }.start();
    }


    private RCCPMessage findSentMessage(RCCPMessage receivedMessage){
        //Get SequenceNum to be checked
        int ackSeqNum = receivedMessage.getPayload();
        //Find Message with seqNum starting from newest messages
        int i = sentMessages.size() - 1;
        while(i >= 0){
            //Return the found message
            if(sentMessages.get(i).getSequenceNumber() == ackSeqNum){
                return sentMessages.get(i);
            }
            i--;
        }
        //No message found
        return null;
    }

    public ArrayList<RCCPMessage> getSentMessages(){
        return this.sentMessages;
    }
}
