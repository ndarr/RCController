package com.example.nicolasdarr.rccontroller.MessageService;

import com.example.nicolasdarr.rccontroller.Car.Car;
import com.example.nicolasdarr.rccontroller.Util.Devices;
import com.ftdi.j2xx.FT_Device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicolas on 22.11.2017.
 */

public class MessageService implements Serializable{

    private static int BUFFER_LENGTH;
    private Thread senderThread;
    private Thread receiverThread;
    private List<RCCPMessage> messages = new ArrayList<>();

    public boolean pair() {
        //Create Pairing message
        RCCPMessage pairingMessage = new RCCPMessage(EStatusCode.HELLO, 0);
        //Send Pairing message
        sendMessage(pairingMessage);
        //Wait for response
        byte responseBuffer[] = new byte[12];

        Devices.uartDevice.read(responseBuffer);

        RCCPMessage response = RCCPMessage.parseByteArrayToRCCP(responseBuffer);

        if(response.getCode() == EStatusCode.ACK){
            return true;
        }
        else{
            return false;
        }
    }


    public void sendMessage(RCCPMessage message){
        Devices.uartDevice.write(message.toByteArray());
    }

    private void startReceiverThread(){
        receiverThread = new Thread(){
            @Override
            public void run(){

            }
        };
        senderThread.start();
    }
}
