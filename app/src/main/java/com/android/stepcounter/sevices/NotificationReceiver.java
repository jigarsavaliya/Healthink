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
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.ArchivementActivity;
import com.android.stepcounter.activity.ArchivementDetailActivity;
import com.android.stepcounter.activity.LevelActivity;
import com.android.stepcounter.utils.constant;

public class NotificationReceiver extends BroadcastReceiver {
    Context mcontext;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        mcontext = context;
        Log.e("MyReceiver", "Notification received!");

        if (intent.getAction().equals("Notification")) {
            long level = intent.getLongExtra("value", 0);
            String Type = intent.getStringExtra("Type");
            boolean Compeletelevel = intent.getBooleanExtra("Compeletelevel", false);
            boolean CompeleteDistance = intent.getBooleanExtra("CompeleteDistance", false);
            boolean CompeleteDaysData = intent.getBooleanExtra("CompeleteDaysData", false);
            boolean CompeleteDailyStep = intent.getBooleanExtra("CompeleteDailyStep", false);
            boolean CompeleteDailyStepGoal = intent.getBooleanExtra("CompeleteDailyStepGoal", false);

            if (level != 0 && level > 0) {
                showNotification(Type, level);
            }

            if (Compeletelevel) {
                if (level != 0 && level > 0) {
                    showCompleteNotification(Type, level);
                }
            } else if (CompeleteDistance) {
                showCompleteNotification(Type, level);
            } else if (CompeleteDaysData) {
                showCompleteNotification(Type, level);
            } else if (CompeleteDailyStep) {
                showCompleteNotification(Type, level);
            } else if (CompeleteDailyStepGoal) {
                showCompleteNotification(Type, level);
            }
        }
    }

    private void showNotification(String type, long level) {

//        RemoteViews contentView = new RemoteViews(mcontext.getPackageName(), R.layout.custom_notification_layout);
//        contentView.setTextViewText(R.id.tvDailyLabel, level + "");
//        contentView.setTextViewText(R.id.tvlevel, "Great! New Archivement of " + type + " reached..!!");
//        contentView.setTextViewText(R.id.tvlevelDesc, "A new badge unloadked..!!");

        Intent notificationIntent = new Intent(mcontext, ArchivementActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mcontext, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext, constant.CHANNEL_ID_FOR_STEP)
//                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .setContentTitle(type)
                .setContentText(level + " remainning for completed " + type + " Level")
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
    }

    private void showCompleteNotification(String type, long level) {
        Intent notificationIntent = new Intent();
        if (type.equals("Daily Step")) {
            notificationIntent = new Intent(mcontext, ArchivementDetailActivity.class);
            notificationIntent.putExtra("DailyStep", true);
            notificationIntent.putExtra("ComboDay", false);
            notificationIntent.putExtra("TotalDays", false);
            notificationIntent.putExtra("TotalDistance", false);
            notificationIntent.putExtra("IsNofification", true);
        } else if (type.equals("Combo Day")) {
            notificationIntent = new Intent(mcontext, ArchivementDetailActivity.class);
            notificationIntent.putExtra("DailyStep", false);
            notificationIntent.putExtra("ComboDay", true);
            notificationIntent.putExtra("TotalDays", false);
            notificationIntent.putExtra("TotalDistance", false);
            notificationIntent.putExtra("IsNofification", true);
        } else if (type.equals("Total Days")) {
            notificationIntent = new Intent(mcontext, ArchivementDetailActivity.class);
            notificationIntent.putExtra("DailyStep", false);
            notificationIntent.putExtra("ComboDay", false);
            notificationIntent.putExtra("TotalDays", true);
            notificationIntent.putExtra("TotalDistance", false);
            notificationIntent.putExtra("IsNofification", true);
        } else if (type.equals("Total Distance")) {
            notificationIntent = new Intent(mcontext, ArchivementDetailActivity.class);
            notificationIntent.putExtra("DailyStep", false);
            notificationIntent.putExtra("ComboDay", false);
            notificationIntent.putExtra("TotalDays", false);
            notificationIntent.putExtra("TotalDistance", true);
            notificationIntent.putExtra("IsNofification", true);
        } else if (type.equals("Level")) {
            notificationIntent = new Intent(mcontext, LevelActivity.class);
            notificationIntent.putExtra("IsNofification", true);
        }

        RemoteViews contentView = new RemoteViews(mcontext.getPackageName(), R.layout.custom_notification_layout);
        contentView.setTextViewText(R.id.tvDailyLabel, level + "");
        contentView.setTextViewText(R.id.tvlevel, "Great! New Archivement of " + type + " reached..!!");
        contentView.setTextViewText(R.id.tvlevelDesc, "A new badge unloadked..!!");

        PendingIntent pendingIntent = PendingIntent.getActivity(mcontext, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext, constant.CHANNEL_ID_FOR_STEP)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .setContentTitle(type)
                .setContentText(level + " completed " + type)
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
    }
}