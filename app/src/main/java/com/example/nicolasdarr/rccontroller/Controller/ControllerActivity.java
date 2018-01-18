package com.example.nicolasdarr.rccontroller.Controller;

import android.os.Looper;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolasdarr.rccontroller.Car.Car;
import com.example.nicolasdarr.rccontroller.Car.CarController;
import com.example.nicolasdarr.rccontroller.MessageService.EStatusCode;
import com.example.nicolasdarr.rccontroller.MessageService.MessageService;
import com.example.nicolasdarr.rccontroller.MessageService.RCCPMessage;
import com.example.nicolasdarr.rccontroller.R;
import com.example.nicolasdarr.rccontroller.Util.Devices;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.nicolasdarr.rccontroller.Util.Devices.uartDevice;


public class ControllerActivity extends AppCompatActivity {

    SeekBar seekBarThrottle;
    SeekBar seekBarSteer;
    TextView textViewOutput;
    Button buttonEmergencyBreak;
    ListView listViewMessages;

    ArrayList<RCCPMessage> list;
    ArrayAdapter<RCCPMessage> adapter;


    CarController carController;
    MessageService messageService;

    Thread sendingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        //Init UI elements
        seekBarThrottle = (SeekBar) findViewById(R.id.seekBarThrottle);
        seekBarSteer = (SeekBar) findViewById(R.id.seekBarSteer);
        listViewMessages = (ListView) findViewById(R.id.listViewMessages);
        buttonEmergencyBreak = (Button) findViewById(R.id.buttonEmergencyBreak);

        messageService = (MessageService) getIntent().getSerializableExtra("messageService");

        list = new ArrayList<>();
        adapter = new ArrayAdapter<RCCPMessage>(this, android.R.layout.simple_list_item_1, messageService.sentMessages);
        carController = new CarController();


        listViewMessages.setAdapter(adapter);
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
                updateListView();
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

    @Override
    protected void onStop() {
        super.onStop();
        //Send disconnect messages
        try {
            sendingThread.join();
        } catch (InterruptedException e) {
            System.exit(0x123);
        }
    }
    protected void startSending(){
        final int rate = 3000;
        sendingThread = new Thread(){
            @Override
            public void run(){
                RCCPMessage throttleMessage;
                RCCPMessage steeringMessage;
                while(true){
                    throttleMessage = carController.getThrottleMessage();
                    messageService.sendMessage(throttleMessage);
                    try {
                        sleep(rate / 2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateListView();
                    steeringMessage = carController.getSteeringMessage();
                    messageService.sendMessage(steeringMessage);
                    try {
                        sleep(rate / 2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateListView();
                }
            }
        };
        sendingThread.start();
    }
    private void startReceiverThread(){
        new Thread(){
            @Override
            public void run(){
                Looper.prepare();
                while(true){
                   RCCPMessage receivedMessage = messageService.readMessage(500);
                   if(receivedMessage != null){
                       messageService.addReceivedMessage(receivedMessage);
                   }
                   updateListView();
                }
            }
        }.start();
    }

    public void updateListView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
