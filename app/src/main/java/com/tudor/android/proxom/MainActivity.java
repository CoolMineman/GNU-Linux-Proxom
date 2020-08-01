package com.tudor.android.proxom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonStart = null;
    Button buttonStop = null;
    Button buttonStopBroadcasting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = findViewById(R.id.startButton);
        buttonStop = findViewById(R.id.stopButton);
        buttonStopBroadcasting = findViewById(R.id.stopBroadcastingButton);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(getApplicationContext(), ProxomService.class));
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                buttonStopBroadcasting.setEnabled(true);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(getApplicationContext(), ProxomService.class));
            }
        });

        buttonStopBroadcasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProxomService.stopBroadcasting();
            }
        });

        buttonStop.setEnabled(false);
        buttonStopBroadcasting.setEnabled(false);
    }
}
