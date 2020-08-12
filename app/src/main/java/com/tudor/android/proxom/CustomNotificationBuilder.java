package com.tudor.android.proxom;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class CustomNotificationBuilder extends NotificationCompat.Builder {
    public CustomNotificationBuilder(@NonNull Context context, @NonNull String channelId) {
        super(context, channelId);
    }


    @SuppressLint("RestrictedApi")
    public void clearAllActions(){
        mActions.clear();
    }
}
