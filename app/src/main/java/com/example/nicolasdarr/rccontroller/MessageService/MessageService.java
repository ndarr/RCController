package com.example.nicolasdarr.rccontroller.MessageService;


import android.content.Context;

import com.example.nicolasdarr.rccontroller.Car.CarController;
import com.example.nicolasdarr.rccontroller.Controller.ControllerActivity;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.nicolasdarr.rccontroller.Util.Devices.uartDevice;

/**
 * Created by Nicolas on 22.11.2017.
 */

public class MessageService implements Serializable{

    public ArrayList<RCCPMessage> sentMessages = new ArrayList<>();

    private Thread senderThread;
    private Thread receiverThread;

    private Context context;

    private CarController carController;

    public MessageService(Context context, CarController carController){
        this.context = context;
        this.carController = carController;

    }

    public void start(){
        startSending();
        startReceiving();
    }

    public void stop(){
        try{
            receiverThread.join();
            senderThread.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void startReceiving(){
        receiverThread = new Thread(){
            @Override
            public void run(){
                while(true){
                    readMessage();
                }
            }
        };
        receiverThread.start();
    }


    private void startSending() {
        final int rate = 3000;
        senderThread = new Thread() {
            @Override
            public void run() {
                RCCPMessage throttleMessage;
                RCCPMessage steeringMessage;
                while (true) {
                    throttleMessage = carController.getThrottleMessage();
                    sendMessage(throttleMessage);
                    try {
                        sleep(rate / 2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    steeringMessage = carController.getSteeringMessage();
                    sendMessage(steeringMessage);
                    try {
                        sleep(rate / 2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        senderThread.start();
    }

    private void readMessage(){
        byte data[] = new byte[12];
        uartDevice.read(data, 12);
        System.out.println("Rcvd: " + Arrays.toString(data));
        RCCPMessage message = RCCPMessage.parseByteArrayToRCCP(data);
        //TODO: Change offset at first message
        if(!message.isValid()){
            uartDevice.read(new byte[1], 1);
        }
        else{
            if(message.getCode() == EStatusCode.ACK){
                acknowledgeMessage(message);
            }
            else{
                //TODO: Do something with other messages
            }
        }
    }

    public void sendMessage(RCCPMessage message){
        sentMessages.add(message);
        notifyDataset();
        uartDevice.write(message.toByteArray());
        System.out.println("Sent: " + Arrays.toString(message.toByteArray()));
    }


    private void acknowledgeMessage(RCCPMessage acknowledgeMessage){
        //Get SequenceNum to be checked
        int ackSeqNum = acknowledgeMessage.getPayload();
        //Find Message with seqNum starting from newest messages
        int i = sentMessages.size() - 1;
        while(i >= 0){
            //Return the found message
            if(sentMessages.get(i).getSequenceNumber() == ackSeqNum){
                sentMessages.get(i).acknowledge();
                notifyDataset();
            }
            i--;
        }
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



    private void notifyDataset(){
        ControllerActivity activity = (ControllerActivity)context;
        activity.updateListView();
    }
}
