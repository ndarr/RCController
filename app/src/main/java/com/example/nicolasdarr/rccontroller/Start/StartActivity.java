package com.example.nicolasdarr.rccontroller.Start;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.nicolasdarr.rccontroller.Controller.ControllerActivity;
import com.example.nicolasdarr.rccontroller.MessageService.MessageService;
import com.example.nicolasdarr.rccontroller.R;
import com.example.nicolasdarr.rccontroller.Util.Devices;
import com.ftdi.j2xx.D2xxManager;

public class StartActivity extends AppCompatActivity {

    Button btnPairing;
    MessageService messageService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //Initialize UI elements
        btnPairing = (Button) findViewById(R.id.btnPairing);
        //Set Action Listeners
        btnPairing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Init Device
                if(initDevice()){
                    initMessageService();
                    //Pair
                    if(pair()){
                        //Start Controller Panel
                        startControllerActivity();
                    }
                    else{
                        makeToast("Could not pair with RC car");
                    }
                }
                else{
                    makeToast("Could not open UART device");
                }

            }
        });

    }

    protected void startControllerActivity(){
        Intent intent = new Intent(this, ControllerActivity.class);
        intent.putExtra("messageService", messageService);
        startActivity(intent);
    }

    protected void initMessageService(){
        messageService = new MessageService();
    }

    protected boolean initDevice(){
        //Delete after testing
        if(Devices.uartDevice == null || Devices.uartDevice != null){
            return true;
        }
        //Open connected UART Device
        //If device is already open, return true
        if(Devices.uartDevice != null && Devices.uartDevice.isOpen()){
            return true;
        }
        //Try to open the device
        try {
            D2xxManager manager = D2xxManager.getInstance(this);
            //Check if there are devices available
            if(manager.createDeviceInfoList(this) > 0){
                //Open the UART device
                Devices.uartDevice = manager.openByIndex(this, 0);
                //Check if device has been opened
                if(Devices.uartDevice != null && Devices.uartDevice.isOpen()){
                    //Device is now open and ready to use
                    return true;
                }
            }
        //Catch any exception occuring while opening the device
        } catch (D2xxManager.D2xxException e) {
            e.printStackTrace();
        }
        //Opening the device failed
        return false;
    }

    protected boolean pair(){
        try{
            return messageService.pair();
        }catch (NullPointerException e){
            return false;
        }
    }

    protected void makeToast(String message) {
        Toast t = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        t.show();
    }
}
