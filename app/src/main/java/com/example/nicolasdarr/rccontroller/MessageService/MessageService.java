package com.example.nicolasdarr.rccontroller.MessageService;

import com.example.nicolasdarr.rccontroller.Car.Car;
import com.ftdi.j2xx.FT_Device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicolas on 22.11.2017.
 */

public class MessageService implements Serializable{

    private static FT_Device device;
    private static int BUFFER_LENGTH;
    private Car car;
    private Thread senderThread;
    private Thread receiverThread;
    private List<RCCPMessage> messages = new ArrayList<>();

    public MessageService(FT_Device device, Car car){
        this.device = device;
        this.car = car;
    }

    public void pair() {
        //device.write();
    }

    private void startSenderThread(){
        senderThread = new Thread(){
            @Override
            public void run(){

            }
        };
        senderThread.start();
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
