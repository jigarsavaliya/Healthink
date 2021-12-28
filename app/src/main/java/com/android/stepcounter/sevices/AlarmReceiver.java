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
import com.android.stepcounter.activity.MainActivity;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.constant;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    long starttime, endtime, currentTimeMillis;
    Context mcontext;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.e("MyReceiver", "AlarmReceiver received!");
        mcontext = context;

        String[] waterReminderStart = StorageManager.getInstance().getWaterReminderStart().split(":");
        String[] min = waterReminderStart[1].split(" ");

        String[] waterReminderEnd = StorageManager.getInstance().getWaterReminderEnd().split(":");
        String[] endmin = waterReminderEnd[1].split(" ");

//        Log.e("TAG", min[0] + "onReceive: " + waterReminderStart[0]);
//        Log.e("TAG", endmin[0] + "onReceive: " + waterReminderEnd[0]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, Integer.parseInt(waterReminderStart[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(min[0]));
        calendar.set(Calendar.AM_PM, Calendar.AM);
        starttime = calendar.getTimeInMillis();

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR, Integer.parseInt(waterReminderEnd[0]));
        instance.set(Calendar.MINUTE, Integer.parseInt(endmin[0]));
        calendar.set(Calendar.AM_PM, Calendar.PM);
        endtime = instance.getTimeInMillis();

        currentTimeMillis = System.currentTimeMillis();

//        Log.e("TAG", (starttime <= currentTimeMillis) + "onReceive: " + (endtime >= currentTimeMillis));
//        Log.e("TAG", starttime + "onReceive: " + endtime + "--" + currentTimeMillis);
        if (starttime <= currentTimeMillis && endtime >= currentTimeMillis) {
            showNotification();
        }
    }

    private void showNotification() {

        Intent notificationIntent = new Intent(mcontext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mcontext, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext, constant.CHANNEL_ID_FOR_STEP)
                .setContentIntent(pendingIntent)
                .setContentTitle("Drink Water")
                .setContentText("Do you have to drink water yet?")
                .setOngoing(false)
                .setSilent(false)
                .setAutoCancel(true);

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


       /* Handler h = new Handler();
        long delayInMilliseconds = 10000;
        h.postDelayed(new Runnable() {
            public void run() {
                notificationManager.cancel(constant.NOTIFICATION_ID_FOR_WATER);
            }
        }, delayInMilliseconds);*/

    }
}