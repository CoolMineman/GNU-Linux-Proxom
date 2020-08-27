package com.tudor.android.proxom;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");

        if (action.equals("STOP_BROADCASTING")) {
            ProxomService.getInstance().stopBroadcasting();
        } else {
            if (action.equals("STOP_PROXY")) {
                context.stopService(new Intent(context.getApplicationContext(), ProxomService.class));
            }else if (action.equals("START_BROADCASTING")){
                ProxomService.getInstance().startBroadcasting();
            }
        }
    }
}
