package com.example.nicolasdarr.rccontroller.MessageService;


import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ListAdapter;

import com.example.nicolasdarr.rccontroller.Car.CarController;
import com.example.nicolasdarr.rccontroller.Controller.ControllerActivity;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /**
     * Starts the sending and receiving of messages
     *
     */
    public void start(){
        startSending();
        startReceiving();
    }

    /**
     * Stops the sending and receiving of messages
     */
    public void stop(){
        try{
            receiverThread.join();
            senderThread.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * Initializes and starts the thread for receiving messages
     */
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


    /**
     * Initializes and starts the thread for sending messages
     */
    private void startSending() {
        final int rate = 1500;
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

    /**
     * Reads the message from the UART interface
     */
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

    /**
     * Sends a message over the UART interface and adds the message to the sent messages
     * @param message   RCCPMessage to be sent
     */
    public void sendMessage(RCCPMessage message){
        sentMessages.add(message);
        notifyDataset("Added Message");
        uartDevice.write(message.toByteArray());
        System.out.println("Sent: " + Arrays.toString(message.toByteArray()));
    }


    /**
     * Acknowledges a sent message based on a received ACK message
     * @param acknowledgeMessage    Received ACK message
     */
    private void acknowledgeMessage(RCCPMessage acknowledgeMessage){
        //Quit if given message is no ACK message
        if(acknowledgeMessage.getCode() != EStatusCode.ACK){
            return;
        }
        //Get SequenceNum to be checked
        int ackSeqNum = acknowledgeMessage.getPayload();
        //Find Message with seqNum starting from newest messages
        int i = sentMessages.size() - 1;
        while(i >= 0){
            //Return the found message
            if(sentMessages.get(i).getSequenceNumber() == ackSeqNum){
                sentMessages.get(i).acknowledge();
                notifyDataset("Acknowledged Message");
            }
            i--;
        }
    }

    /**
     *
     */
    private void notifyDataset(String message){
        System.out.println(message);
        try{
            ControllerActivity activity = (ControllerActivity)context;
            activity.updateListView();
        }catch (ClassCastException e){
            Throwable t = new Throwable("MessageService initialized with wrong activity. Context must be of type ControllerActivity");
            e.initCause(t);
        }

    }
}
