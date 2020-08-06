package com.tudor.android.proxom;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ProxomService extends Service {
    private MediaPlayer player;
    private final long FINISHING_PROXY_TIME = 1200;

    private static ProxomService thisService = null;

    private static BroadcastingThread broadcastingThread = null;
    private static ProxyThread proxyThread = null;

    private static String serverAddress = null;
    private static volatile boolean proxyRunning = false;
    private static volatile boolean broadcastingRunning = false;

    private Handler forceQuitHandler = null;

    static void setServerAddress(String serverAddress){
        ProxomService.serverAddress = serverAddress;
    }

    static String getServerAddress(){
        return serverAddress;
    }

    static void setBroadcastingStatus(boolean status){
        broadcastingRunning = status;
    }

    static boolean getBroadcastingStatus(){
        return broadcastingRunning;
    }

    static void setProxyStatus(boolean status){
        proxyRunning = status;
    }

    static boolean getProxyStatus(){
        return proxyRunning;
    }

    static void stopBroadcasting(){
        broadcastingThread.stopThread();
        Toast.makeText(thisService, "Stopping broadcasting", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public void onCreate() {
        thisService = this;

        broadcastingThread = new BroadcastingThread();
        proxyThread = new ProxyThread();
        forceQuitHandler = new Handler();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(true);
        player.start();

        broadcastingThread.startThread();
        proxyThread.startThread(serverAddress);

        Toast.makeText(thisService, "Starting proxy", Toast.LENGTH_SHORT).show();

        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        player.stop();
        broadcastingThread.stopThread();
        proxyThread.stopThread();


        Toast.makeText(thisService, "Stopping proxy", Toast.LENGTH_SHORT).show();


        forceQuitHandler.postDelayed(new Thread() {
            @Override
            public void run() {
                if (getProxyStatus())
                    System.exit(0);
            }
        }, FINISHING_PROXY_TIME);
    }
}
