package com.cearo.owlganizer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cearo.owlganizer.R;

public class AlarmBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Alarm Broadcast", "Alarm received");
        final int NOTIFICAITON_ID = 1;

        final String ACTION = intent.getAction();

        if (ACTION != null && ACTION.equals("SET_ALARM")) {
            final String ALARM_OBJ_NAME = intent.getStringExtra("NAME");
            final String CHANNEL_ID = "1";
            final String TITLE = String.format("%s alarm!", ALARM_OBJ_NAME);
            final String MESSAGE = String.format("%s is now due!", ALARM_OBJ_NAME);
            final NotificationCompat.Builder BUILDER = new NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                    .setContentTitle(TITLE)
                    .setContentText(MESSAGE)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            final NotificationManagerCompat MANAGER = NotificationManagerCompat.from(context);
            MANAGER.notify(NOTIFICAITON_ID, BUILDER.build());
        }
    }
}
