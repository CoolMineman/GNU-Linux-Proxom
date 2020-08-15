package com.tudor.android.proxom;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ProxomService extends Service {
    private final long FINISHING_PROXY_TIME = 1500;
    private final long CHANGE_BUTTON_TIME = 1000;
    private final long CHECK_TIME = 2500;

    private volatile static ProxomService thisService = null;

    private BroadcastingThread broadcastingThread = null;
    private ProxyThread proxyThread = null;

    private String serverAddress = null;
    private volatile boolean proxyRunning = false;
    private volatile boolean broadcastingRunning = false;

    private Handler forceQuitHandler = null;
    private Handler notificationUpdateHandler = null;

    private Intent notificationIntent = null;
    private PendingIntent pendingIntent = null;
    private Notification notification = null;
    private CustomNotificationBuilder notificationBuilder = null;

    private Intent buttonIntent = null;
    private PendingIntent buttonPendingIntent = null;

    private final String CHANNEL_ID = "ProxomNotification";
    private final int NOTIFICATION_ID = 1;

    private volatile boolean notificationBroadcastingButtonShown = false;

    static ProxomService getInstance(){
        return thisService;
    }

    String getServerAddress(){
        return serverAddress;
    }

    void setBroadcastingStatus(boolean status){
        broadcastingRunning = status;
    }

    boolean getBroadcastingStatus(){
        return broadcastingRunning;
    }

    void setProxyStatus(boolean status){
        proxyRunning = status;
    }

    boolean getProxyStatus(){
        return proxyRunning;
    }

    void stopBroadcasting(){
        broadcastingThread.stopThread();
        updateNotificationAfterBroadcasting();
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
        notificationUpdateHandler = new Handler();

        notificationIntent = new Intent (thisService, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(thisService, 0, notificationIntent, 0);
        notificationBuilder = new CustomNotificationBuilder(thisService, CHANNEL_ID);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        createNotification();

        serverAddress = intent.getStringExtra("serverAddress");
        broadcastingThread.startThread();
        proxyThread.startThread(serverAddress);

        Toast.makeText(thisService, "Starting proxy", Toast.LENGTH_SHORT).show();

        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        broadcastingThread.stopThread();
        proxyThread.stopThread();


        Toast.makeText(thisService, "Stopping proxy", Toast.LENGTH_SHORT).show();


        forceQuitHandler.postDelayed(new Thread() {
            @Override
            public void run() {
                if (getProxyStatus()) {
                    setProxyStatus(false);
                    System.exit(0);
                }
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
                .setContentTitle("Proxy and broadcasting are running")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(0, "Stop broadcasting", buttonPendingIntent);

        notification = notificationBuilder.build();

        startForeground(NOTIFICATION_ID, notification);
    }


    private void updateNotificationAfterBroadcasting(){
        buttonIntent = new Intent(thisService, NotificationActionReceiver.class);
        buttonIntent.putExtra("action", "STOP_PROXY");
        buttonPendingIntent = PendingIntent.getBroadcast(thisService, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.clearAllActions();
        notificationBroadcastingButtonShown = false;

        notificationBuilder
                .setContentTitle("Proxy is running");

        notification = notificationBuilder.build();


        startForeground(NOTIFICATION_ID, notification);

        notificationUpdateHandler.postDelayed(new Thread() {
            @Override
            public void run() {

                if (!notificationBroadcastingButtonShown) {
                    notificationBuilder
                            .addAction(0, "Stop proxy", buttonPendingIntent);

                    notification = notificationBuilder.build();

                    notificationBroadcastingButtonShown = true;

                    startForeground(NOTIFICATION_ID, notification);
                }

                notificationUpdateHandler.postDelayed(new Thread() {
                    @Override
                    public void run() {
                        if (getBroadcastingStatus())
                            createNotification();
                    }
                }, CHECK_TIME);
            }
        },CHANGE_BUTTON_TIME);
    }

}
