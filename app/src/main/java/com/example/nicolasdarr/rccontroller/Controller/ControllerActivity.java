package com.example.nicolasdarr.rccontroller.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.nicolasdarr.rccontroller.R;


public class ControllerActivity extends AppCompatActivity {

    SeekBar seekBarThrottle;
    SeekBar seekBarSteer;
    TextView textViewOutput;

    static int throttle;
    static int steering;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        //Init UI elements
        seekBarThrottle = (SeekBar) findViewById(R.id.seekBarThrottle);
        seekBarSteer = (SeekBar) findViewById(R.id.seekBarSteer);
        textViewOutput = (TextView) findViewById(R.id.textViewOutput);

        //Make text scrollable
        textViewOutput.setMovementMethod(new ScrollingMovementMethod());

        //Configure SeekBars
        initSeekBars();

        //Set listeners to value change
        initSeekBarListeners();


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
                final int steering = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewOutput.append("Steering: " + Integer.toString(steering)  + "\r\n");
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int middle = seekBarSteer.getMax();
                middle /= 2;
                steering = middle;
                seekBarSteer.setProgress(middle);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewOutput.append("Set Steering to Middle\r\n");
                    }
                });
            }
        });

        seekBarThrottle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                final int throttle = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewOutput.append("Throttle: " + Integer.toString(throttle)  + "\r\n");
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int noThrottle = 0;
                throttle = noThrottle;
                seekBarThrottle.setProgress(noThrottle);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewOutput.append("Set Throttle to " + Integer.toString(throttle) + "\r\n");
                    }
                });
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        //Send disconnect message
    }

}
