package com.example.nicolasdarr.rccontroller.MessageService;

import com.example.nicolasdarr.rccontroller.Car.CarController;

import java.util.ArrayList;

/**
 * Created by nicolasdarr on 12.02.18.
 */

public class SenderThread extends Thread{
    private static int threadCounter = 0;

    private boolean running;
    private int rate;

    private boolean playbackMode;
    private ArrayList<RCCPMessage> recordedMessages;

    private MessageService messageService;
    private CarController carController;


    SenderThread(MessageService messageService, CarController carController, int rate){
        this.messageService = messageService;
        this.rate = rate;
        this.carController = carController;
        this.setName("Sender Thread" + Integer.toString(threadCounter));
        threadCounter++;
    }


    @Override
    public void run(){
        RCCPMessage throttleMessage;
        RCCPMessage steeringMessage;
        while (running) {
            if(playbackMode){
                startPlayback();
            }
            throttleMessage = carController.getThrottleMessage();
            messageService.sendMessage(throttleMessage);
            try {
                sleep(rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            steeringMessage = carController.getSteeringMessage();
            messageService.sendMessage(steeringMessage);
            try {
                sleep(rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void startSending(){
        this.running = true;
        this.start();
    }

    void stopSending(){
        this.running = false;
    }

    void playbackMessages(ArrayList<RCCPMessage> recordedMessages) {
        playbackMode = true;
        this.recordedMessages = recordedMessages;
    }

    private void startPlayback(){
        for(RCCPMessage message: recordedMessages){
            //TODO: Create new message with new seq number
            messageService.sendMessage(new RCCPMessage(message.getCode(), message.getPayload()));
            try {
                this.sleep(rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        playbackMode = false;
        messageService.finishedPlayback();
    }
}
