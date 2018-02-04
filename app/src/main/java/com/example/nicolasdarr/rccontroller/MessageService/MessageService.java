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

    /**
     * Constructor setting up a MessageService with a CarController containing all values and a Context to display information
     * @param context       Context to enable notifying about UI changes. Must be of Type ControllerActivity due to compatibility issues
     * @param carController CarController holding the current state of the controlled car
     */
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
        final int messageOffset = 1000;
        senderThread = new Thread() {
            @Override
            public void run() {
                int distReqCounter = 1;
                while(true){
                    sendControlMessages(messageOffset);
                    if(distReqCounter % 10 == 0){
                        sendDistanceRequestMessage(messageOffset);
                    }
                    distReqCounter++;
                }
            }
        };
        senderThread.start();
    }


    private void sendDistanceRequestMessage(int offset){
        RCCPMessage distReqMessage = new RCCPMessage(EStatusCode.REQUEST_DISTANCE_SENSOR_VALUE, 0);
        sendMessage(distReqMessage);
        try {
            senderThread.sleep(offset);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendControlMessages(int offset){
        RCCPMessage throttleMessage;
        RCCPMessage steeringMessage;
            throttleMessage = carController.getThrottleMessage();
            sendMessage(throttleMessage);
            try {
                senderThread.sleep(offset);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            steeringMessage = carController.getSteeringMessage();
            sendMessage(steeringMessage);
            try {
                senderThread.sleep(offset);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
            handleReceivedMessage(message);
        }
    }


    private void handleReceivedMessage(final RCCPMessage receivedMessage){

        switch (receivedMessage.getCode()){
            case ACK: acknowledgeMessage(receivedMessage); break;
            case TRANSMIT_DISTANCE_SENSOR_VALUE: updateDistanceValue(receivedMessage.getPayload()); break;
            default: System.out.println("Could not find MessageType: " + receivedMessage.getCode().label);
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
     *  Notifies the UI Thread that the message set has been changed
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

    private void updateDistanceValue(int value){
        System.out.println("Distance Sensor Value:" + Integer.toString(value));
    }
}
