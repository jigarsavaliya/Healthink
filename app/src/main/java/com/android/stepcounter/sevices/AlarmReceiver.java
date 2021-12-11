package com.android.stepcounter.sevices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.MainActivity;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.constant;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    long starttime, endtime, currentTimeMillis;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        String[] waterReminderStart = StorageManager.getInstance().getWaterReminderStart().split(":");
        String[] min = waterReminderStart[1].split(" ");

        String[] waterReminderEnd = StorageManager.getInstance().getWaterReminderEnd().split(":");
        String[] endmin = waterReminderEnd[1].split(" ");

        Log.e("TAG", min[0] + "onReceive: " + waterReminderStart[0]);
        Log.e("TAG", endmin[0] + "onReceive: " + waterReminderEnd[0]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, Integer.parseInt(waterReminderStart[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(min[0]));
        starttime = calendar.getTimeInMillis();

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR, Integer.parseInt(waterReminderEnd[0]));
        instance.set(Calendar.MINUTE, Integer.parseInt(endmin[0]));
        endtime = instance.getTimeInMillis();

        currentTimeMillis = System.currentTimeMillis();

        if (starttime <= currentTimeMillis && endtime >= currentTimeMillis) {
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Water")
                    .setContentText("Do you have to drink water?")
                    .setWhen(currentTimeMillis)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(constant.NOTIFICATION_ID_FOR_WATER, mNotifyBuilder.build());
        }
    }

}