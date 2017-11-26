package com.example.nicolasdarr.rccontroller;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nicolasdarr.rccontroller.Car.Car;
import com.example.nicolasdarr.rccontroller.Controller.ControllerActivity;
import com.example.nicolasdarr.rccontroller.MessageService.MessageService;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import java.io.Serializable;

public class StartActivity extends AppCompatActivity {

    Button btnPairing;
    FT_Device device;
    MessageService messageService;
    /**
     * Called when App is opened
     * @param savedInstanceState
     */
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
                //Pair
                //Start Controller Panel
                if(initDevice()){
                    initMessageService();
                    if(pair()){
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
        intent.putExtra("ftDevice", messageService);
        startActivity(intent);
    }

    protected void initMessageService(){
        messageService = new MessageService(device, new Car());
    }

    protected boolean initDevice(){
        //Open connected UART Device
        //If device is already open, return true
        if(device != null && device.isOpen()){
            return true;
        }
        //Try to open the device
        try {
            D2xxManager manager = D2xxManager.getInstance(this);
            //Check if there are devices available
            if(manager.createDeviceInfoList(this) > 0){
                //Open the UART device
                device = manager.openByIndex(this, 0);
                //Check if device has been opened
                if(device != null && device.isOpen()){
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

        return true;
    }

    protected void makeToast(String message){
        Toast t = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        t.show();
    }



}
