package com.example.nicolasdarr.rccontroller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nicolasdarr.rccontroller.Controller.ControllerActivity;

public class StartActivity extends AppCompatActivity {

    Button btnPairing;


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
        startActivity(intent);
    }

    protected boolean initDevice(){
        //Open connected UART Device
        return true;
    }

    protected boolean pair(){
        //Send Pairing messages
        return true;
    }

    protected void makeToast(String message){
        Toast t = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        t.show();
    }



}
