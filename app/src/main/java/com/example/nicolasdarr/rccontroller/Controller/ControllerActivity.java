package com.example.nicolasdarr.rccontroller.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import com.example.nicolasdarr.rccontroller.Car.CarController;
import com.example.nicolasdarr.rccontroller.MessageService.EStatusCode;
import com.example.nicolasdarr.rccontroller.MessageService.MessageService;
import com.example.nicolasdarr.rccontroller.MessageService.RCCPMessage;
import com.example.nicolasdarr.rccontroller.R;

public class ControllerActivity extends AppCompatActivity {

    SeekBar seekBarThrottle;
    SeekBar seekBarSteer;

    Button buttonEmergencyBreak;
    ListView listViewMessages;

    ArrayAdapter<RCCPMessage> adapter;


    CarController carController;
    MessageService messageService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        //Init UI elements
        seekBarThrottle = (SeekBar) findViewById(R.id.seekBarThrottle);
        seekBarSteer = (SeekBar) findViewById(R.id.seekBarSteer);
        listViewMessages = (ListView) findViewById(R.id.listViewMessages);
        buttonEmergencyBreak = (Button) findViewById(R.id.buttonEmergencyBreak);

        carController = new CarController();


        messageService = new MessageService(this, carController);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messageService.sentMessages);
        listViewMessages.setAdapter(adapter);
        //Configure SeekBars
        initSeekBars();

        //Set listeners to value change
        initSeekBarListeners();

        initEmergencyBreak();

        messageService.start();
    }

    protected void initEmergencyBreak() {

        buttonEmergencyBreak.setOnClickListener(view -> {
            messageService.sendMessage(new RCCPMessage(EStatusCode.EMERGENCY_BRAKE, 0));
            updateListView();
        });
    }

    @Override
    protected void onStop(){
        messageService.stop();
        super.onStop();
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




    public void updateListView(){
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }
}
