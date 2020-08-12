package com.tudor.android.proxom;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;


public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");

        if (action.equals("STOP_BROADCASTING")) {
            ProxomService.stopBroadcasting();
            ProxomService.getInstance().updateNotificationAfterBroadcasting();
        } else if (action.equals("STOP_PROXY")) {
            context.stopService(new Intent(context.getApplicationContext(), ProxomService.class));
        }
    }
}
