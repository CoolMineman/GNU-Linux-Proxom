package com.tudor.android.proxom;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ProxomService extends Service {
    private MediaPlayer player;
    private final long FINISHING_PROXY_TIME = 1500;

    private static ProxomService thisService = null;

    private static BroadcastingThread broadcastingThread = null;
    private static ProxyThread proxyThread = null;

    private static String serverAddress = null;
    private static volatile boolean proxyRunning = false;
    private static volatile boolean broadcastingRunning = false;

    private Handler forceQuitHandler = null;

    private Intent notificationIntent = null;
    private PendingIntent pendingIntent = null;
    private Notification notification = null;
    private CustomNotificationBuilder notificationBuilder = null;

    private Intent buttonIntent = null;
    private PendingIntent buttonPendingIntent = null;

    private final String CHANNEL_ID = "ProxomNotification";
    private final int NOTIFICATION_ID = 1;

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

    static ProxomService getInstance(){
        return thisService;
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

        notificationIntent = new Intent (thisService, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(thisService, 0, notificationIntent, 0);
        notificationBuilder = new CustomNotificationBuilder(thisService, CHANNEL_ID);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        createNotification();
        startNotification();

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


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Proxom Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void createNotification(){

        buttonIntent = new Intent(thisService, NotificationActionReceiver.class);
        buttonIntent.putExtra("action", "STOP_BROADCASTING");
        buttonPendingIntent = PendingIntent.getBroadcast(thisService, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.clearAllActions();

        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Proxy and broadcasting running")
                .setContentIntent(pendingIntent)
                .addAction(0, "Stop broadcasting", buttonPendingIntent);

        notification = notificationBuilder.build();

    }

    private void startNotification(){
        startForeground(NOTIFICATION_ID, notification);
    }

    private void updateNotificationAfterBroadcasting(){

    }

}
