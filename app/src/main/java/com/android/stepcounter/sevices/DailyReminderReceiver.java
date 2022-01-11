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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class DailyReminderReceiver extends BroadcastReceiver {
    Context mcontext;
    ArrayList<Integer> integerArrayList = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.e("MyReceiver", "DailyReminderReceiver received!");
        mcontext = context;

        Gson gson = new Gson();
        String json = StorageManager.getInstance().getDailyReminderDay();
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();

        integerArrayList = gson.fromJson(json, type);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        int value = calendar.get(Calendar.DAY_OF_WEEK);

        if (integerArrayList.contains(value)) {
            showNotification();
        }
    }

    private void showNotification() {

        Intent notificationIntent = new Intent(mcontext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mcontext, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext, constant.CHANNEL_ID_FOR_WATER)
                .setContentIntent(pendingIntent)
                .setContentTitle("Step Counter")
                .setContentText("You need to start step.")
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
            NotificationChannel channel = new NotificationChannel(constant.CHANNEL_ID_FOR_WATER,
                    constant.CHANNEL_NAME_FOR_WATER,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(constant.NOTIFICATION_ID_FOR_WATER, notification);
        }


    }
}