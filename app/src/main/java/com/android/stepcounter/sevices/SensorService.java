package com.android.stepcounter.sevices;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.MainActivity;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.model.StepCountModel;
import com.android.stepcounter.senser.StepDetector;
import com.android.stepcounter.senser.StepListener;
import com.android.stepcounter.utils.CommanMethod;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.constant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class SensorService extends Service implements SensorEventListener, StepListener {

    public SensorService() {
    }

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int numSteps;
    int displaystep;
    Context mcontext;
    private StepDetector simpleStepDetector;
    DatabaseManager dbManager;
    StepCountModel stepcountModel;
    private float userWeight;
    private float userHeight;
    ArrayList<StepCountModel> Steplist = new ArrayList<StepCountModel>();
    int hour, date, month, year;
    int StepGoal;
    public static int mapStep = 0;
    public static boolean isGpsService = false;
    public static String IsTargetType = "Target Distance";

    @Override
    public void onCreate() {
        super.onCreate();

        Calendar rightNow = Calendar.getInstance();
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        date = rightNow.get(Calendar.DATE);
        month = rightNow.get(Calendar.MONTH) + 1;
        year = rightNow.get(Calendar.YEAR);

        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        dbManager = new DatabaseManager(this);
        mcontext = this;
        stepcountModel = new StepCountModel();

        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();

        // Get sensor manager on starting the service.
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Registering...
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

        // Get default sensor type
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

//        Logger.e("service on create");
        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int Display = 0;

        int TotalStepCount = dbManager.getSumOfStepList(date, month, year);

        Display = TotalStepCount;

        String Calories = String.valueOf(CommanMethod.calculateCalories(Display, userWeight, userHeight));

        Log.e("TAG", Display + "onStartCommand: " + Calories);

        showNotification(Display, Integer.parseInt(Calories));

        // Get sensor manager on starting the service.
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        numSteps = 0;
        // Registering...
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

        // Get default sensor type
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        Sensor mySensor = sensorEvent.sensor;

        mSensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        /*if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float[] values = sensorEvent.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed
                        = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
//                    openWhatsApp();
                }
                last_x = x;
                last_y = y;
                last_z = z;

            }
            // Stop the sensor and service
            mSensorManager.unregisterListener(this);
            stopSelf();
        }*/


        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == mSensor) {
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH: {
//                    Logger.e("high");
                    break;
                }
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM: {
//                    Logger.e("Medium");
                    break;
                }
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW: {
//                    Logger.e("Low");
                    break;
                }
                default: {
                }
            }
        }
    }


    @Override
    public void step(long timeNs) {

        if (isGpsService) {
            Logger.e("mapStep" + mapStep);
            mapStep++;
            Intent intent = new Intent();
            intent.setAction("GET_SIGNAL");
            intent.putExtra("mapstepdata", mapStep);
            sendBroadcast(intent);

            String Calories = String.valueOf(CommanMethod.calculateCalories(mapStep, userWeight, userHeight));
            String Distance = String.format("%.2f", CommanMethod.calculateDistance(mapStep));

            if (IsTargetType != null && IsTargetType.equals("Target Distance")) {
                float goal = Float.parseFloat(StorageManager.getInstance().getTargetDistance());
                float Current = Float.parseFloat(Distance);

                float progress = 0;
                if (Current != 0) {
                    if (Current < goal) {
                        progress = Current / goal * 100;
                    } else {
                        progress = 100;
                    }
                }
                showGpsNotification(IsTargetType, 100, (int) progress, Current);
            } else if (IsTargetType != null && IsTargetType.equals("Target Duration")) {

                int progress;
                int goal = Integer.parseInt(StorageManager.getInstance().getTargetDuration()) * 60 * 1000;
                new CountDownTimer(goal, 1000) {

                    public void onTick(long millisUntilFinished) {
//                        mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                        Logger.e("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
//                        mTextField.setText("done!");
                    }

                }.start();

//                showGpsNotification(Distance);

            } else if (IsTargetType != null && IsTargetType.equals("Target Calories")) {

                float goal = Float.parseFloat(StorageManager.getInstance().getTargetCalories());
                float Current = Float.parseFloat(Calories);

                float progress = 0;
                if (Current != 0) {
                    if (Current < goal) {
                        progress = Current / goal * 100;
                    } else {
                        progress = 100;
                    }
                }
                showGpsNotification(IsTargetType, 100, (int) progress, Current);
            }

        }

        int Display = 0;

        Steplist = dbManager.getCurrentDayHoursStepcountlist(date, month, year, hour);
        int TotalStepCount = dbManager.getSumOfStepList(date, month, year);

        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
                numSteps = Steplist.get(i).getStep();
            }
            Display = TotalStepCount + 1;

        }

        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
                numSteps = Steplist.get(i).getStep();
            }
        } else {
            numSteps = 0;
        }

        numSteps++;

        String DisplayCalories = String.valueOf(CommanMethod.calculateCalories(Display, userWeight, userHeight));
        long DisplayDistance = (long) CommanMethod.calculateDistance(Display);

        String Calories = String.valueOf(CommanMethod.calculateCalories(numSteps, userWeight, userHeight));
        String Distance = String.valueOf(CommanMethod.calculateDistance(numSteps));

        showNotification(Display, Integer.parseInt(DisplayCalories));

        String ts = String.valueOf(System.currentTimeMillis());

        /*Intent sendLevel = new Intent();
        sendLevel.setAction("GET_SIGNAL_STRE0NGTH");
        sendLevel.putExtra("stepdata", Display);
        sendBroadcast(sendLevel);*/

        for (int i = 0; i < 24; i++) {
            if (hour == i) {
                stepcountModel.setStep(numSteps);
                stepcountModel.setDate(date);
                stepcountModel.setMonth(month);
                stepcountModel.setYear(year);
                stepcountModel.setDistance(Distance);
                stepcountModel.setCalorie(Calories);
                stepcountModel.setDuration(hour);
                stepcountModel.setTimestemp(ts);
                stepcountModel.setMaxStep(0);
                dbManager.addStepcountData(stepcountModel);
            }
        }

        //Archievement level Data
        long mTotalStepData = dbManager.getTotalStepCount();
        ArrayList<ArchivementModel> mLevel = new ArrayList<>();
        mLevel = dbManager.getArchivementlist(constant.ARCHIVEMENT_LEVEL);

        for (int i = 0; i < mLevel.size(); i++) {
            if (mTotalStepData == mLevel.get(i).getValue()) {

//                Logger.e(mTotalStepData + ">" + mLevel.get(i).getValue());
//                Logger.e((mTotalStepData > mLevel.get(i).getValue()));

                ArchivementModel archivementModel = new ArchivementModel();
                archivementModel.setValue(mLevel.get(i).getValue());
                archivementModel.setType(mLevel.get(i).getType());
                archivementModel.setCompeleteStatus(true);
                dbManager.updateArchivementTotalDistance(archivementModel);
            }
        }


        // Archivement Daily Step
        ArrayList<ArchivementModel> mDailySteplist = new ArrayList<>();
        ArrayList<StepCountModel> MaxStepCount = dbManager.getsumofdayStep(date, month, year);

//        Logger.e(new GsonBuilder().create().toJson(MaxStepCount));
        Logger.e(MaxStepCount.get(0).getSumstep());
        Logger.e(MaxStepCount.get(0).getMaxStep());

//        ArrayList<StepCountModel> MaxStepCountModels = dbManager.getMaxStepCount();
        mDailySteplist = dbManager.getArchivementDailySteplist(constant.ARCHIVEMENT_DAILY_STEP, MaxStepCount.get(0).getMaxStep());
        for (int i = 0; i < mDailySteplist.size(); i++) {
            if (MaxStepCount.get(0).getSumstep() == mDailySteplist.get(i).getValue()) {

//                Logger.e(TotalStepCount + ">" + mDailySteplist.get(0).getValue());
//                Logger.e((TotalStepCount > mDailySteplist.get(i).getValue()));

                ArchivementModel archivementModel = new ArchivementModel();
                archivementModel.setValue(mDailySteplist.get(i).getValue());
                archivementModel.setCount(mDailySteplist.get(i).getCount());
                archivementModel.setType(mDailySteplist.get(i).getType());
                archivementModel.setCompeleteStatus(true);
                dbManager.updateArchivementDailyStep(archivementModel);

                StepCountModel stepCountModel = new StepCountModel();
                stepCountModel.setDate(date);
                stepCountModel.setMonth(month);
                stepCountModel.setYear(year);
                stepCountModel.setDuration(hour);
                stepCountModel.setMaxStep(MaxStepCount.get(0).getSumstep());

                Logger.e(date + "**" + month + "**" + year + "**" + hour + "**" + MaxStepCount.get(0).getSumstep());
                dbManager.updatemaxStep(stepCountModel);

            }
        }


        // Archivement Total Days
        long mTotalDaysData = dbManager.getTotalDaysCount();
        ArrayList<ArchivementModel> mTotalDaysList = new ArrayList<>();
        mTotalDaysList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DAYS);

        for (int i = 0; i < mTotalDaysList.size(); i++) {

//            Logger.e(mTotalDaysData + ">" + mTotalDaysList.get(i).getValue());
//            Logger.e((mTotalDaysData > mTotalDaysList.get(i).getValue()));

            if (mTotalDaysData == mTotalDaysList.get(i).getValue()) {
                ArchivementModel archivementModel = new ArchivementModel();
                archivementModel.setValue(mTotalDaysList.get(i).getValue());
                archivementModel.setType(mTotalDaysList.get(i).getType());
                archivementModel.setCompeleteStatus(true);
                dbManager.updateArchivementTotalDistance(archivementModel);
            }
        }

        // Archivement Total Distance
        ArrayList<ArchivementModel> mTotalDistanceList = new ArrayList<>();
        mTotalDistanceList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DISTANCE);
        long mTotalDisanceData = dbManager.getTotalDistanceCount();

        for (int i = 0; i < mTotalDistanceList.size(); i++) {

//            Logger.e(mTotalDaysData + ">" + mTotalDistanceList.get(i).getValue());
//            Logger.e((mTotalDaysData > mTotalDistanceList.get(i).getValue()));

            if (mTotalDisanceData == mTotalDistanceList.get(i).getValue()) {
                ArchivementModel archivementModel = new ArchivementModel();
                archivementModel.setValue(mTotalDistanceList.get(i).getValue());
                archivementModel.setType(mTotalDistanceList.get(i).getType());
                archivementModel.setCompeleteStatus(true);
                dbManager.updateArchivementTotalDistance(archivementModel);
            }
        }

//        -------------------------total distance
        long Distancegoal = 5;
        String DistanceDesc = "Short Hike";
        for (int i = 0; i < mTotalDistanceList.size(); i++) {
            if (mTotalDistanceList.get(i).isCompeleteStatus()) {
                Distancegoal = mTotalDistanceList.get(i + 1).getValue();
                DistanceDesc = mTotalDistanceList.get(i + 1).getDescription();
            }
        }

        Logger.e("Total Distance" + DisplayDistance + "---" + Distancegoal);

//        -------------------------total Days
        long TotalDaysgoal = 7;
        String DayDesc = "7 Day";
        for (int i = 0; i < mTotalDaysList.size(); i++) {
            if (mTotalDaysList.get(i).isCompeleteStatus()) {
                TotalDaysgoal = mTotalDaysList.get(i + 1).getValue();
                DayDesc = mTotalDaysList.get(i + 1).getDescription();
            }
        }

        Logger.e("Total Days" + mTotalDaysData + "---" + TotalDaysgoal);

        //        -------------------------Daily Steps
        long TotalDailyStep = Display;
        long TotalDailygoal = 3000;
        String DailyDesc = "Away from sofa";
        for (int i = 0; i < mDailySteplist.size(); i++) {
            if (mDailySteplist.get(i).isCompeleteStatus()) {
                TotalDailygoal = mDailySteplist.get(i + 1).getValue();
                Logger.e(TotalDailygoal + "----new ------TotalDailygoal-----------");
                DailyDesc = mDailySteplist.get(i + 1).getDescription();
            }
        }

        Logger.e("Daily steps" + TotalDailyStep + "---" + TotalDailygoal);

        //        -------------------------Level
        long mlevelGoal = 10000;
        String LevelDesc = "A good Strat!";
        for (int i = 0; i < mLevel.size(); i++) {
            if (mLevel.get(i).isCompeleteStatus()) {
                mlevelGoal = mLevel.get(i + 1).getValue();
                LevelDesc = mLevel.get(i + 1).getDescription();
            }

        }
        Logger.e("Level" + mTotalStepData + "------" + mlevelGoal);

        float mLevelData = (float) mTotalStepData / mlevelGoal * 100;
        float mDisanceData = (float) DisplayDistance / Distancegoal * 100;
        float mDaysData = (float) mTotalDaysData / TotalDaysgoal * 100;
        float mDailyStep = (float) TotalDailyStep / TotalDailygoal * 100;

        Logger.e(mLevelData + "--" + mDisanceData + "--" + mDaysData + "--" + mDailyStep + "--");
        ArrayList<Float> floats = new ArrayList<>();
        floats.remove(floats);
        floats.add(mLevelData);
        floats.add(mDisanceData);
        floats.add(mDaysData);
        floats.add(mDailyStep);

        Float obj = Collections.max(floats);
        int index = floats.indexOf(obj);
//        Logger.e(obj + "--" + index);

        Intent sendLevel = new Intent();
        sendLevel.setAction("GET_SIGNAL_STRENGTH");
        sendLevel.putExtra("stepdata", Display);
        sendLevel.putExtra("mlevelGoal", mlevelGoal);
        sendLevel.putExtra("mLevelData", mTotalStepData);
        sendLevel.putExtra("LevelDesc", LevelDesc);
        sendLevel.putExtra("Distancegoal", Distancegoal);
        sendLevel.putExtra("DisplayDistance", DisplayDistance);
        sendLevel.putExtra("DistanceDesc", DistanceDesc);
        sendLevel.putExtra("TotalDaysgoal", TotalDaysgoal);
        sendLevel.putExtra("mTotalDaysData", mTotalDaysData);
        sendLevel.putExtra("DayDesc", DayDesc);
        sendLevel.putExtra("TotalDailygoal", TotalDailygoal);
        sendLevel.putExtra("TotalDailyStep", TotalDailyStep);
        sendLevel.putExtra("DailyDesc", DailyDesc);
        sendBroadcast(sendLevel);

        if (date == Calendar.DATE) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            if (index == 0) {
                intent.setAction("Notification");
                intent.putExtra("value", mlevelGoal - mTotalStepData);
                intent.putExtra("Type", constant.ARCHIVEMENT_LEVEL);
                intent.putExtra("Compeletelevel", false);
                sendBroadcast(intent);
            } else if (index == 1) {
                intent.setAction("Notification");
                intent.putExtra("value", Distancegoal - DisplayDistance);
                intent.putExtra("Type", constant.ARCHIVEMENT_TOTAL_DISTANCE);
                intent.putExtra("CompeleteDistance", false);
                sendBroadcast(intent);
            } else if (index == 2) {
                intent.setAction("Notification");
                intent.putExtra("value", TotalDaysgoal - mTotalDaysData);
                intent.putExtra("Type", constant.ARCHIVEMENT_TOTAL_DAYS);
                intent.putExtra("CompeleteDaysData", false);
                sendBroadcast(intent);
            } else if (index == 3) {
                intent.setAction("Notification");
                intent.putExtra("value", TotalDailygoal - TotalDailyStep);
                intent.putExtra("Type", constant.ARCHIVEMENT_DAILY_STEP);
                intent.putExtra("CompeleteDailyStep", false);
                sendBroadcast(intent);
            }
        } else {
            Intent sendlevel = new Intent(this, NotificationReceiver.class);
            if (mTotalStepData >= mlevelGoal) {
                sendlevel.setAction("Notification");
                sendlevel.putExtra("value", mlevelGoal);
                sendlevel.putExtra("Type", constant.ARCHIVEMENT_LEVEL);
                sendlevel.putExtra("Compeletelevel", true);
                sendBroadcast(sendlevel);
            } else if (DisplayDistance >= Distancegoal) {
                sendlevel.setAction("Notification");
                sendlevel.putExtra("value", Distancegoal);
                sendlevel.putExtra("Type", constant.ARCHIVEMENT_TOTAL_DISTANCE);
                sendlevel.putExtra("CompeleteDistance", true);
                sendBroadcast(sendlevel);
            } else if (mTotalDaysData >= TotalDaysgoal) {
                sendlevel.setAction("Notification");
                sendlevel.putExtra("value", TotalDaysgoal);
                sendlevel.putExtra("Type", constant.ARCHIVEMENT_TOTAL_DAYS);
                sendlevel.putExtra("CompeleteDaysData", true);
                sendBroadcast(sendlevel);
            } else if (TotalDailyStep >= TotalDailygoal) {
                sendlevel.setAction("Notification");
                sendlevel.putExtra("value", TotalDailygoal);
                sendlevel.putExtra("Type", constant.ARCHIVEMENT_DAILY_STEP);
                sendlevel.putExtra("CompeleteDailyStep", true);
                sendBroadcast(sendlevel);
            }
        }

    }

    private void showNotification(int i, int calories) {

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.drawable.ic_baseline_directions_walk_24);
        contentView.setImageViewResource(R.id.ivcalorie, R.drawable.ic_baseline_local_fire_department_24);
        contentView.setTextViewText(R.id.title, i + "");
        contentView.setTextViewText(R.id.tvcalorie, calories + "");
        contentView.setProgressBar(R.id.progress, 1000, i, false);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, constant.CHANNEL_ID_FOR_STEP)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSilent(true)
                .setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_launcher_background);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }

        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(constant.CHANNEL_ID_FOR_STEP,
                    constant.CHANNEL_NAME_FOR_STEP,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            channel.setShowBadge(false);
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(constant.NOTIFICATION_ID_FOR_STEP, notification);

    }

    private void showGpsNotification(String type, int goal, int progress, float currvalue) {

        @SuppressLint("RemoteViewLayout")
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_gps_notification);
        contentView.setTextViewText(R.id.type, type + "");
        contentView.setTextViewText(R.id.currtvalue, currvalue + "");
        contentView.setProgressBar(R.id.gpsprogress, goal, progress, false);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, constant.CHANNEL_ID_FOR_STEP)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSilent(true)
                .setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_launcher_background);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }

        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(constant.CHANNEL_ID_FOR_STEP,
                    constant.CHANNEL_NAME_FOR_STEP,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            channel.setShowBadge(false);
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(constant.NOTIFICATION_ID_FOR_GPS, notification);

    }


}