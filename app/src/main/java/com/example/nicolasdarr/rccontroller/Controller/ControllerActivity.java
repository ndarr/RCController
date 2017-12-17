package com.example.nicolasdarr.rccontroller.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.nicolasdarr.rccontroller.Car.Car;
import com.example.nicolasdarr.rccontroller.MessageService.EStatusCode;
import com.example.nicolasdarr.rccontroller.MessageService.MessageService;
import com.example.nicolasdarr.rccontroller.MessageService.RCCPMessage;
import com.example.nicolasdarr.rccontroller.R;
import com.example.nicolasdarr.rccontroller.Util.Devices;


public class ControllerActivity extends AppCompatActivity {

    SeekBar seekBarThrottle;
    SeekBar seekBarSteer;
    TextView textViewOutput;
    Button buttonEmergencyBreak;

    MessageService messageService;

    Thread sendingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        //Init UI elements
        seekBarThrottle = (SeekBar) findViewById(R.id.seekBarThrottle);
        seekBarSteer = (SeekBar) findViewById(R.id.seekBarSteer);
        textViewOutput = (TextView) findViewById(R.id.textViewOutput);
        buttonEmergencyBreak = (Button) findViewById(R.id.buttonEmergencyBreak);

        //Make text scrollable
        textViewOutput.setMovementMethod(new ScrollingMovementMethod());

        messageService = (MessageService) getIntent().getSerializableExtra("messageService");

        //Configure SeekBars
        initSeekBars();

        //Set listeners to value change
        initSeekBarListeners();

        initEmergencyBreak();

        startSending();
    }

    protected void initEmergencyBreak() {

        buttonEmergencyBreak.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                messageService.sendMessage(new RCCPMessage(EStatusCode.EMERGENCY_BRAKE, 0));
            }
        });
    }

    private void initSeekBars() {
        seekBarSteer.setMax(250);
        seekBarThrottle.setMax(100);

        //set Steer to middle
        seekBarSteer.setProgress(seekBarSteer.getMax()/2);
        //set Throttle to lowest value
        seekBarThrottle.setProgress(seekBarThrottle.getBottom());
    }

    private void initSeekBarListeners(){
        seekBarSteer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Car.STEERING = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int middle = seekBarSteer.getMax();
                middle /= 2;
                Car.STEERING = middle;
                seekBarSteer.setProgress(middle);
            }
        });

        seekBarThrottle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Car.THROTTLE = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int noThrottle = 0;
                Car.THROTTLE = noThrottle;
                seekBarThrottle.setProgress(noThrottle);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Send disconnect messages
        try {
            sendingThread.join();
        } catch (InterruptedException e) {
            System.exit(123123);
        }
    }
    protected void startSending(){
        sendingThread = new Thread(){
            @Override
            public void run(){
                while(true){
                    //Create Steering method
                    
                    //Create Throttle method
                    //Send both messages
                }
            }
            };
            sendingThread.start();
    }

}
