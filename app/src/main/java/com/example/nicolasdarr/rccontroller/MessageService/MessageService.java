package com.example.nicolasdarr.rccontroller.MessageService;


import com.example.nicolasdarr.rccontroller.Util.Devices.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.zip.CheckedOutputStream;

import static com.example.nicolasdarr.rccontroller.Util.Devices.uartDevice;

/**
 * Created by Nicolas on 22.11.2017.
 */

public class MessageService implements Serializable{

    private static int BUFFER_LENGTH;
    private Thread senderThread;
    private Thread receiverThread;
    private LinkedList<RCCPMessage> receivedMessages = new LinkedList<>();
    private LinkedList<RCCPMessage> sentMessages = new LinkedList<>();


    public boolean pair() {
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


    public void sendMessage(RCCPMessage message){
        uartDevice.write(message.toByteArray());
    }

    private void startReceiverThread(){
        receiverThread = new Thread(){
            @Override
            public void run(){
                while(true){
                    byte data[] = new byte[12];
                    uartDevice.read(data, 12);
                    RCCPMessage message = RCCPMessage.parseByteArrayToRCCP(data);
                    receivedMessages.add(message);
                }
            }
        };
        senderThread.start();
    }

    private void startAcknowledgeThread(){
        new Thread(){
            @Override
            public void run(){
                while(true){
                    if(!receivedMessages.isEmpty()){
                        //Get last message from ReceivedMessages
                        RCCPMessage message = receivedMessages.getFirst();
                        receivedMessages.removeFirst();
                        //Check if ACK Message
                        if(message.getCode() == EStatusCode.ACK){
                            findSentMessage(message).acknowledge();
                        }
                        else{
                            //Do Something
                        }
                    }
                }
            }
        };
    }


    private RCCPMessage findSentMessage(RCCPMessage receivedMessage){
        //Get SequenceNum to be checked
        int seqNum = receivedMessage.getSequenceNumber();
        //Find Message with seqNum starting from newest messages
        int i = sentMessages.size() - 1;
        while(i >= 0){
            //Return the found message
            if(sentMessages.get(i).getSequenceNumber() == seqNum){
                return sentMessages.get(i);
            }
            i--;
        }
        //No message found
        return null;
    }
}
