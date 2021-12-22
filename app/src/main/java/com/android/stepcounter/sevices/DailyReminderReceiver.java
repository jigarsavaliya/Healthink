package com.android.stepcounter.sevices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.MainActivity;
import com.android.stepcounter.utils.constant;

public class DailyReminderReceiver extends BroadcastReceiver {
    Context mcontext;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        mcontext = context;
        showNotification();
    }

    private void showNotification() {

        Intent notificationIntent = new Intent(mcontext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mcontext, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext, constant.CHANNEL_ID_FOR_STEP)
                .setContentIntent(pendingIntent)
                .setContentTitle("Step Counter")
                .setContentText("You need to start step.")
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