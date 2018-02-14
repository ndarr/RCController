package com.example.nicolasdarr.rccontroller.MessageService;

import java.util.ArrayList;

/**
 * Created by nicolasdarr on 12.02.18.
 */

public class Recorder {

    private ArrayList<RCCPMessage> recordedMessages;

    private boolean recording;
    private boolean playback;

    public void startRecording() {
        recordedMessages = new ArrayList<>();
        recording = true;
    }

    public void stopRecording(){
        recording = false;
    }

    public void startPlayback(){
        playback = true;
    }

    public boolean isRecording(){
        return recording;
    }

    void addMessageToRecords(RCCPMessage message){
        recordedMessages.add(message);

    }

    ArrayList<RCCPMessage> getRecordedMessages() {
        return recordedMessages;
    }

    public boolean isPlayback() {
        return playback;
    }

    public void stopPlayback() {
    }
}
