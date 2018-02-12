package com.example.nicolasdarr.rccontroller.MessageService;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.example.nicolasdarr.rccontroller.Car.CarController;
import com.example.nicolasdarr.rccontroller.Controller.ControllerActivity;
import com.example.nicolasdarr.rccontroller.Util.Array;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.nicolasdarr.rccontroller.Util.Devices.uartDevice;

/**
 * Created by Nicolas on 22.11.2017.
 */

public class MessageService implements Serializable{

    public ArrayList<RCCPMessage> sentMessages = new ArrayList<>();

    private static SenderThread senderThread;





    public int numAck = 0;
    private byte[] byteBuffer;

    private Context context;

    private CarController carController;
    private Recorder recorder;



    public MessageService(Context context, CarController carController){
        this.context = context;
        this.carController = carController;
        this.recorder = new Recorder();
    }

    //TODO: Cleanup this method
    private UsbSerialInterface.UsbReadCallback mCallback = data -> {

        if(byteBuffer != null){
            data = Array.concatenate(byteBuffer, data);
            byteBuffer = null;
        }

        if(data.length < 12){
            byteBuffer = data;
            return;
        }

        System.out.println("Rcvd RawBytes: " + Arrays.toString(data));
        ArrayList<RCCPMessage> receivedMessages = new ArrayList<>();
        int numMessages = data.length / 12;

        byte[] subdata;
        int i;
        for(i = 0; i < numMessages; i++){
            subdata = Arrays.copyOfRange(data , 12*i, 12*(i+1));
            receivedMessages.add(RCCPMessage.parseByteArrayToRCCP(subdata));
        }

        if(data.length - 12*(i+1) > 0){
            //Take rest
            byteBuffer = Arrays.copyOfRange(data, 12*(i+1), data.length);

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
                    if(message.getCode() == EStatusCode.TRANSMIT_DISTANCE_SENSOR_VALUE){
                        updateDistance(message.getPayload());
                    }
                }
            }
        }
    };

    /**
     * Starts the sending and receiving of messages
     *
     */
    public void start(){
        //TODO: Remove comment after testing
        //uartDevice.read(mCallback);
        startSending();
    }

    /**
     * Stops the sending and receiving of messages
     */
    public void stop(){
        senderThread.stopSending();
        try {
            senderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes and starts the thread for sending messages
     */
    //TODO: Cleanup this method
    private void startSending() {
        //Get message rate from preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final int rate = 1000 / Integer.parseInt(preferences.getString("messages_per_second", "5"));

        //Setup sender thread and start it
        senderThread = new SenderThread(this, carController, rate);
        senderThread.startSending();
    }

    /**
     * Sends a message over the UART interface and adds the message to the sent messages
     * @param message   RCCPMessage to be sent
     */
    public void sendMessage(RCCPMessage message){
        if(recorder.isRecording()){
            recorder.addMessageToRecords(message);
        }
        sentMessages.add(message);
        notifyDataset();
        //TODO: Remove comment after testing
        //uartDevice.write(message.toByteArray());
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
                notifyDataset();
            }
            i--;
        }
    }

    /**
     *  Notifies the UI Thread that the message set has been changed
     */
    private void notifyDataset(){
        try{
            ControllerActivity activity = (ControllerActivity)context;
            activity.updateListView();
        }catch (ClassCastException e){
            Throwable t = new Throwable("MessageService initialized with wrong activity. Context must be of type ControllerActivity");
            e.initCause(t);
        }
    }


    public void startRecording(){
        recorder.startRecording();
    }

    public void stopRecording(){
        recorder.stopRecording();
    }

    public void startPlayback() {
        senderThread.playbackMessages(recorder.getRecordedMessages());
    }

    public boolean isRecording(){
        return recorder.isRecording();
    }

    private void updateDistance(int distance){
        try{
            ControllerActivity activity = (ControllerActivity)context;
            activity.updateDistanceView(distance);
        }catch (ClassCastException e) {
            Throwable t = new Throwable("MessageService initialized with wrong activity. Context must be of type ControllerActivity");
            e.initCause(t);
        }
    }

    void finishedPlayback(){
        try{
            ControllerActivity activity = (ControllerActivity)context;
            activity.finishedPlayback();
        }catch (ClassCastException e){
            Throwable t = new Throwable("MessageService initialized with wrong activity. Context must be of type ControllerActivity");
            e.initCause(t);
        }
    }
}
