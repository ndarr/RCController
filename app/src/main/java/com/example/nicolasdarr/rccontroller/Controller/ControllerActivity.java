package com.example.nicolasdarr.rccontroller.Controller;

import android.os.Looper;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolasdarr.rccontroller.Car.Car;
import com.example.nicolasdarr.rccontroller.MessageService.EStatusCode;
import com.example.nicolasdarr.rccontroller.MessageService.MessageService;
import com.example.nicolasdarr.rccontroller.MessageService.RCCPMessage;
import com.example.nicolasdarr.rccontroller.R;
import com.example.nicolasdarr.rccontroller.Util.Devices;

import java.util.Arrays;

import static com.example.nicolasdarr.rccontroller.Util.Devices.uartDevice;


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
        startReceiverThread();
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
                int count = 1;
                Looper.prepare();
                while(true){
                    if(count % 2000 == 0){
                        RCCPMessage message = new RCCPMessage(EStatusCode.LED_TOGGLE, 0);
                        messageService.sendMessage(message);
                        println("Sent: " + message.toMinString());
                        count = 1;
                    }
                    count++;
                    try {
                        sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            };
            sendingThread.start();
    }
    private void startReceiverThread(){
        new Thread(){
            @Override
            public void run(){
                while(true){
                    byte data[] = new byte[12];
                    uartDevice.read(data, 12, 30000);
                    RCCPMessage message = RCCPMessage.parseByteArrayToRCCP(data);
                    String out = message.toMinString();
                    if(out.isEmpty()){
                        uartDevice.read(new byte[1], 1, 1000);
                        out = Arrays.toString(data);
                    }
                    println("Received: " + out);
                    /*try{
                        if(!(message.getCode() == EStatusCode.ACK && message.getCode() != EStatusCode.HELLO)){
                            messageService.sendMessage(new RCCPMessage(EStatusCode.ACK, message.getPayload()));
                        }
                    }
                    catch(NullPointerException e){
                        e.printStackTrace();
                    }*/
                }
            }
        }.start();
    }

    public void println(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewOutput.append(message + "\r\n");
            }
        });
    }
}
