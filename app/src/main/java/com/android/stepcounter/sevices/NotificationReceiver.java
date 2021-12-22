package com.android.stepcounter.sevices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.ArchivementActivity;
import com.android.stepcounter.utils.constant;

public class NotificationReceiver extends BroadcastReceiver {
    Context mcontext;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        mcontext = context;
        Log.e("MyReceiver", "MyAction received!");

        if (intent.getAction().equals("Notification")) {
            long level = intent.getLongExtra("value", 0);
            String Type = intent.getStringExtra("Type");
            boolean Compeletelevel = intent.getBooleanExtra("Compeletelevel", false);
//            Log.e("TAG", Type + "onReceive: main " + level);

            if (level != 0 && level > 0) {
                showNotification(Type, level);
            }
        }
    }

    private void showNotification(String type, long level) {

        Intent notificationIntent = new Intent(mcontext, ArchivementActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mcontext, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext, constant.CHANNEL_ID_FOR_STEP)
                .setContentIntent(pendingIntent)
                .setContentTitle(type)
                .setContentText(level + " remainning for completed " + type + " Level")
                .setOngoing(true)
                .setSilent(true)
                .setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_launcher_background);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(constant.CHANNEL_ID_FOR_STEP,
                    constant.CHANNEL_NAME_FOR_STEP,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(constant.NOTIFICATION_ID_FOR_WATER, notification);
        }
    }
}