package com.tudor.android.proxom;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

public class ProxomService extends Service {
    private MediaPlayer player;

    private static BroadcastingThread broadcastingThread = null;
    private static ProxyThread proxyThread = null;

    static private String serverAddress = null;
    static volatile private boolean proxyRunning = false;
    static volatile private boolean broadcastingRunning = false;

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

    static  boolean getProxyStatus(){
        return proxyRunning;
    }

    static void stopBroadcasting(){
        broadcastingThread.stopThread();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }
    @Override
    public void onCreate() {
        broadcastingThread = new BroadcastingThread();
        proxyThread = new ProxyThread();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(true);
        player.start();

        broadcastingThread.startThread();

        Toast.makeText(this, "Proxy started", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        player.stop();
        stopBroadcasting();


        Toast.makeText(this, "Proxy stopped", Toast.LENGTH_SHORT).show();
    }
}
