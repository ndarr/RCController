package com.example.nicolasdarr.rccontroller.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.nicolasdarr.rccontroller.Car.CarController;
import com.example.nicolasdarr.rccontroller.MessageService.EStatusCode;
import com.example.nicolasdarr.rccontroller.MessageService.MessageService;
import com.example.nicolasdarr.rccontroller.MessageService.RCCPMessage;
import com.example.nicolasdarr.rccontroller.R;
import java.util.ArrayList;

public class ControllerActivity extends AppCompatActivity {

    SeekBar seekBarThrottle;
    SeekBar seekBarSteer;

    ProgressBar progressBarDistanceSensor;

    Button buttonEmergencyBreak;

    Button btnRecord;
    Button btnPlay;

    TextView textViewAckPerc;

    ListView listViewMessages;
    ArrayList<RCCPMessage> messages;

    ArrayAdapter<RCCPMessage> adapter;


    CarController carController;
    MessageService messageService;

    boolean emergencyBreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Running OnCreate!");
        setContentView(R.layout.activity_controller);
        //Init UI elements
        seekBarThrottle = (SeekBar) findViewById(R.id.seekBarThrottle);
        seekBarSteer = (SeekBar) findViewById(R.id.seekBarSteer);

        progressBarDistanceSensor = (ProgressBar) findViewById(R.id.progressBarDistanceSensor);

        listViewMessages = (ListView) findViewById(R.id.listViewMessages);

        buttonEmergencyBreak = (Button) findViewById(R.id.buttonEmergencyBreak);
        //buttonLedTgl = (Button) findViewById(R.id.buttonLedTgl);

        textViewAckPerc = (TextView) findViewById(R.id.textViewAckPerc);

        carController = new CarController();

        messages = new ArrayList<>();
        messageService = new MessageService(this, carController);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        listViewMessages.setAdapter(adapter);
        //Configure SeekBars
        initSeekBars();

        //Set listeners to value change
        initSeekBarListeners();

        initEmergencyBreak();

        initPlayback();

        messageService.start();
    }

    private void initPlayback(){
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnRecord = (Button) findViewById(R.id.btnRecord);

        btnPlay.setEnabled(false);
        btnPlay.setBackground(this.getDrawable(R.drawable.ic_play_disabled_lightgrey_24dp));

        btnRecord.setOnClickListener(v -> {
            if(messageService.isRecording()){
                System.out.println("Already recording! Stopping record!");
                messageService.stopRecording();
                btnPlay.setEnabled(true);
                btnPlay.setBackground(this.getDrawable(R.drawable.ic_play_darkgrey_24dp));
                btnRecord.setBackground(this.getDrawable(R.drawable.ic_record_red_24dp));
            }
            else {
                System.out.println("Starting Recording");
                messageService.startRecording();
                btnRecord.setBackground(this.getDrawable(R.drawable.ic_stop_darkgrey_24dp));
            }
        } );

        btnPlay.setOnClickListener(v -> {
            System.out.println("Starting Playback!");
            messageService.startPlayback();
            seekBarSteer.setEnabled(false);
            seekBarThrottle.setEnabled(false);
            btnPlay.setEnabled(false);
            btnPlay.setBackground(this.getDrawable(R.drawable.ic_play_disabled_lightgrey_24dp));
            btnRecord.setEnabled(false);
            btnRecord.setBackground(this.getDrawable(R.drawable.ic_record_disabled_red_24dp));
        });
    }

    protected void initEmergencyBreak() {
        buttonEmergencyBreak.setOnClickListener((View view) -> {

            if(emergencyBreak){
                //Release Emergency break
                messageService.sendMessage(new RCCPMessage(EStatusCode.EMERGENCY_BRAKE_DISENGAGE, 0));
            }
            else{
                //Set Emergency break
                messageService.sendMessage(new RCCPMessage(EStatusCode.EMERGENCY_BRAKE_ENGAGE, 0));
            }
        });
    }

    private void initSeekBars() {
        seekBarSteer.setMax(200);
        seekBarThrottle.setMax(200);

        //set Steer to middle
        seekBarSteer.setProgress(seekBarSteer.getMax()/2);
        //set Throttle to lowest value
        seekBarThrottle.setProgress(seekBarThrottle.getMax()/2);
    }

    private void initSeekBarListeners(){
        seekBarSteer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int steering = i - (seekBarThrottle.getMax() / 2);
                carController.setSteering(steering);
                System.out.println("Setting steering to " + Integer.toString(steering));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int middle = seekBarSteer.getMax() / 2;
                carController.setSteering(middle);
                seekBarSteer.setProgress(middle);
            }
        });

        seekBarThrottle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int throttle = i - (seekBarThrottle.getMax() / 2);
                carController.setThrottle(throttle);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int middle = seekBarSteer.getMax() / 2;
                carController.setThrottle(middle);
                seekBarThrottle.setProgress(middle);
            }
        });
    }

    public void updateListView(){
        runOnUiThread(() -> {
            adapter.clear();
            adapter.addAll(messageService.sentMessages);
            int percentage = 100 - (int)((((double)messageService.numAck)/messageService.sentMessages.size())*100);

            String out = "Paket Loss: " + Integer.toString(percentage) + "%";
            textViewAckPerc.setText(out);
        });
    }


    public void updateDistanceView(int distance) {
        System.out.println("Setting distance sensor value to" + Integer.toString(distance));
        runOnUiThread(() -> progressBarDistanceSensor.setProgress(distance));
    }

    public void finishedPlayback() {
        System.out.println("Playback is finished!");
        runOnUiThread(() -> {
            seekBarSteer.setEnabled(true);
            seekBarThrottle.setEnabled(true);
            btnPlay.setEnabled(true);
            btnPlay.setBackground(this.getDrawable(R.drawable.ic_play_darkgrey_24dp));
            btnRecord.setEnabled(true);
            btnRecord.setBackground(this.getDrawable(R.drawable.ic_record_red_24dp));
        });
    }

    @Override
    protected void onStop(){
        messageService.stop();
        super.onStop();
    }

    @Override
    protected void onPause(){
        messageService.stop();
        super.onPause();
    }
}
