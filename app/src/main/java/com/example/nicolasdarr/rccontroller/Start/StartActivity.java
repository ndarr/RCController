package com.example.nicolasdarr.rccontroller.Start;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.example.nicolasdarr.rccontroller.Controller.ControllerActivity;
import com.example.nicolasdarr.rccontroller.R;
import com.example.nicolasdarr.rccontroller.Settings.SettingsActivity;
import com.example.nicolasdarr.rccontroller.Util.Devices;

public class StartActivity extends AppCompatActivity {

    Button btnPairing;
    Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //Initialize UI elements
        btnPairing = (Button) findViewById(R.id.btnPairing);
        btnSettings = (Button) findViewById(R.id.btnSettings);

        //Set Action Listeners
        btnPairing.setOnClickListener(v -> {
            //Init Device
            if(initDevice()){
                startControllerActivity();
            }
            else{
                makeToast("Could not open UART device");
            }

        });

        btnSettings.setOnClickListener(v -> startSettingsActivity());

    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    protected void startControllerActivity(){
        Intent intent = new Intent(this, ControllerActivity.class);
        startActivity(intent);
    }

    protected boolean initDevice(){
        return Devices.initDevice(this);
    }


    protected void makeToast(String message) {
        Toast t = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        t.show();
    }
}
