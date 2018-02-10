package com.example.nicolasdarr.rccontroller.MessageService;


import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ListAdapter;

import com.example.nicolasdarr.rccontroller.Car.CarController;
import com.example.nicolasdarr.rccontroller.Controller.ControllerActivity;
import com.example.nicolasdarr.rccontroller.Util.Array;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.nicolasdarr.rccontroller.Util.Devices.uartDevice;
import static java.util.Arrays.copyOfRange;

/**
 * Created by Nicolas on 22.11.2017.
 */

public class MessageService implements Serializable{

    public ArrayList<RCCPMessage> sentMessages = new ArrayList<>();

    private static Thread senderThread;
    private static Thread receiverThread;



    public int numAck = 0;
    private byte[] byteBuffer;

    private Context context;

    private CarController carController;

    private UsbSerialInterface.UsbReadCallback mCallback = data -> {
        if(byteBuffer != null){
            data = Array.concatenate(byteBuffer, data);
            byteBuffer = null;
        }
        System.out.println("Rcvd RawBytes: " + Arrays.toString(data));
        ArrayList<RCCPMessage> receivedMessages = new ArrayList<>();
        int numMessages = data.length / 12;
        if(data.length % 12 == 0){
            System.out.println("Even message count");
        }
        byte[] subdata;
        int i;
        for(i = 0; i < numMessages; i++){
            subdata = Arrays.copyOfRange(data , 0+12*i, 12+12*i);
            System.out.println("Subdata ("+ Integer.toString(i) +"): " + Arrays.toString(subdata));
            receivedMessages.add(RCCPMessage.parseByteArrayToRCCP(subdata));
        }
        System.out.println("ValueI:" + Integer.toString(i));
        if(12+12*i < data.length){
            //Take rest
            byteBuffer = Arrays.copyOfRange(data, 12+12*1, data.length);
            System.out.println("Rest: " + Arrays.toString(byteBuffer));

        }
        for (RCCPMessage message: receivedMessages) {
            if(message == null || !message.isValid()){
                //uartDevice.read(new byte[1], 1);
                System.out.println("Message not valid");
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
    };

    public MessageService(Context context, CarController carController){
        this.context = context;
        this.carController = carController;
    }

    /**
     * Starts the sending and receiving of messages
     *
     */
    public void start(){
        System.out.println("Starting Threads!");
        uartDevice.read(mCallback);
        startSending();
    }

    /**
     * Stops the sending and receiving of messages
     */
    public void stop(){
        receiverThread.interrupt();
        senderThread.interrupt();
    }

    public boolean isRunning(){
        return senderThread.isAlive() || receiverThread.isAlive();
    }

    /**
     * Initializes and starts the thread for sending messages
     */
    private void startSending() {
        final int rate = 200;
        senderThread = new Thread() {
            @Override
            public void run() {
                RCCPMessage throttleMessage;
                RCCPMessage steeringMessage;
                while (true) {
                    throttleMessage = carController.getThrottleMessage();
                    sendMessage(throttleMessage);
                    try {
                        sleep(rate);
                        System.out.println("Sleeping after throttle message");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    steeringMessage = carController.getSteeringMessage();
                    sendMessage(steeringMessage);
                    try {
                        sleep(rate);
                        System.out.println("Sleeping after steering message");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        senderThread.start();
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
                numAck++;
                notifyDataset("Acknowledged Message");
            }
            i--;
        }
    }


    int counter = 1;
    /**
     *
     */
    private void notifyDataset(String message){
        if(counter % 1 == 0){
            System.out.println(message);
            try{
                ControllerActivity activity = (ControllerActivity)context;
                activity.updateListView();
            }catch (ClassCastException e){
                Throwable t = new Throwable("MessageService initialized with wrong activity. Context must be of type ControllerActivity");
                e.initCause(t);
            }
        }
        counter++;
    }
}
