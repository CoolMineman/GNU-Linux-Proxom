package com.tudor.android.proxom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private final long ACTIVITY_HANDLER_TIME = 800;

    private final int PERMISSIONS_REQUEST_CODE = 173;


    private Button buttonStart = null;
    private Button buttonStop = null;
    private Button buttonStopBroadcasting = null;
    private EditText ipAddressServer = null;
    private TextView statusBroadcasting = null;
    private TextView statusProxy = null;

    private Handler activityHandler = null;

    private volatile boolean waitForRefresh = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        activityHandler = new Handler();

        buttonStart = findViewById(R.id.startButton);
        buttonStop = findViewById(R.id.stopButton);
        buttonStopBroadcasting = findViewById(R.id.stopBroadcastingButton);
        ipAddressServer = findViewById(R.id.serverIpAddress);
        statusBroadcasting = findViewById(R.id.broadcastingStatus);
        statusProxy = findViewById(R.id.proxyStatus);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!waitForRefresh) {
                    ProxomService.setServerAddress(ipAddressServer.getText().toString());
                    startService(new Intent(getApplicationContext(), ProxomService.class));

                    waitForRefresh = true;
                }
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

    }

    @Override
    protected void onPause(){
        super.onPause();

        stopActivityHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        stopActivityHandler();

        startActivityHandler();
    }


    private void startActivityHandler(){
        boolean currentBroadcastingStatus = ProxomService.getBroadcastingStatus();
        boolean currentProxyStatus = ProxomService.getProxyStatus();

        if (currentBroadcastingStatus) {
            statusBroadcasting.setText("Broadcasting: running");
            statusBroadcasting.setTypeface(null, Typeface.BOLD);
        }
        else {
            statusBroadcasting.setText("Broadcasting: stopped");
            statusBroadcasting.setTypeface(null, Typeface.BOLD);
        }

        if (currentProxyStatus){
            statusProxy.setText("Proxy: running");
            statusProxy.setTypeface(null, Typeface.BOLD);
        }
        else{
            statusProxy.setText("Proxy: stopped");
            statusProxy.setTypeface(null, Typeface.BOLD);
        }

        if (buttonStart.isEnabled() && (currentProxyStatus || currentBroadcastingStatus)){
            buttonStart.setEnabled(false);
        }

        if (ipAddressServer.isEnabled() && (currentProxyStatus || currentBroadcastingStatus)){
            ipAddressServer.setEnabled(false);
        }

        if (!buttonStopBroadcasting.isEnabled() && currentBroadcastingStatus){
            buttonStopBroadcasting.setEnabled(true);
        }

        if(!buttonStop.isEnabled() && currentProxyStatus){
            buttonStop.setEnabled(true);
        }

        if(buttonStopBroadcasting.isEnabled() && (!currentBroadcastingStatus)){
            buttonStopBroadcasting.setEnabled(false);
        }

        if (buttonStop.isEnabled() && (!currentProxyStatus)){
            buttonStop.setEnabled(false);
        }

        if (!ipAddressServer.isEnabled() && !(currentProxyStatus || currentBroadcastingStatus)) {
            ipAddressServer.setEnabled(true);
        }

        if (!buttonStart.isEnabled() && !(currentProxyStatus || currentBroadcastingStatus)){
            buttonStart.setEnabled(true);
        }


        waitForRefresh = false;

        activityHandler.postDelayed(new Thread() {
            @Override
            public void run() {
                boolean currentBroadcastingStatus = ProxomService.getBroadcastingStatus();
                boolean currentProxyStatus = ProxomService.getProxyStatus();

                if (currentBroadcastingStatus) {
                    statusBroadcasting.setText("Broadcasting: running");
                    statusBroadcasting.setTypeface(null, Typeface.BOLD);
                }
                else {
                    statusBroadcasting.setText("Broadcasting: stopped");
                    statusBroadcasting.setTypeface(null, Typeface.BOLD);
                }

                if (currentProxyStatus){
                    statusProxy.setText("Proxy: running");
                    statusProxy.setTypeface(null, Typeface.BOLD);
                }
                else{
                    statusProxy.setText("Proxy: stopped");
                    statusProxy.setTypeface(null, Typeface.BOLD);
                }

                if (buttonStart.isEnabled() && (currentProxyStatus || currentBroadcastingStatus)){
                    buttonStart.setEnabled(false);
                }

                if (ipAddressServer.isEnabled() && (currentProxyStatus || currentBroadcastingStatus)){
                    ipAddressServer.setEnabled(false);
                }

                if (!buttonStopBroadcasting.isEnabled() && currentBroadcastingStatus){
                    buttonStopBroadcasting.setEnabled(true);
                }

                if(!buttonStop.isEnabled() && currentProxyStatus){
                    buttonStop.setEnabled(true);
                }

                if(buttonStopBroadcasting.isEnabled() && (!currentBroadcastingStatus)){
                    buttonStopBroadcasting.setEnabled(false);
                }

                if (buttonStop.isEnabled() && (!currentProxyStatus)){
                    buttonStop.setEnabled(false);
                }

                if (!ipAddressServer.isEnabled() && !(currentProxyStatus || currentBroadcastingStatus)) {
                    ipAddressServer.setEnabled(true);
                }

                if (!buttonStart.isEnabled() && !(currentProxyStatus || currentBroadcastingStatus)){
                    buttonStart.setEnabled(true);
                }


                waitForRefresh = false;

                activityHandler.postDelayed(this, ACTIVITY_HANDLER_TIME);
            }
        }, ACTIVITY_HANDLER_TIME);
    }

    private void stopActivityHandler(){
       activityHandler.removeCallbacksAndMessages(null);
    }

    private void checkPermissions(){
        ArrayList <String> permissionsToRequest = new ArrayList<>();

        int permission;

        permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED)
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED)
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET);
        if (permission != PackageManager.PERMISSION_GRANTED)
            permissionsToRequest.add(Manifest.permission.INTERNET);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.FOREGROUND_SERVICE);
            if (permission != PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE);
        }

        if (permissionsToRequest.size() > 0) {
            String[] permissionsToRequestArray = permissionsToRequest.toArray(new String[0]);
            requestPermissions(permissionsToRequestArray);
        }

    }

    private void requestPermissions(String []permissionsToRequest){
        ActivityCompat.requestPermissions(MainActivity.this, permissionsToRequest, PERMISSIONS_REQUEST_CODE);
    }
}
