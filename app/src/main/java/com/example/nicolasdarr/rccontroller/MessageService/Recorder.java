package com.example.nicolasdarr.rccontroller.MessageService;

import java.util.ArrayList;

/**
 * Created by nicolasdarr on 12.02.18.
 * Used for recording messages
 */

class Recorder {

    private ArrayList<RCCPMessage> recordedMessages;

    private boolean recording;

    void startRecording() {
        recordedMessages = new ArrayList<>();
        recording = true;
    }

    void stopRecording(){
        recording = false;
    }

    boolean isRecording(){
        return recording;
    }

    void addMessageToRecords(RCCPMessage message){
        recordedMessages.add(message);

    }

    ArrayList<RCCPMessage> getRecordedMessages() {
        return recordedMessages;
    }
}
