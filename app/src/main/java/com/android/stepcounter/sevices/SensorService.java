package com.android.stepcounter.sevices;

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
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.MainActivity;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.stepcountModel;
import com.android.stepcounter.senser.StepDetector;
import com.android.stepcounter.senser.StepListener;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.commanMethod;
import com.android.stepcounter.utils.constant;

import java.util.ArrayList;
import java.util.Calendar;

public class SensorService extends Service implements SensorEventListener, StepListener {

    public SensorService() {
    }

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int numSteps;
    int displaystep;
    private StepDetector simpleStepDetector;
    DBHandler dbManager;
    stepcountModel stepcountModel;
    private float userWeight;
    private float userHeight;
    ArrayList<stepcountModel> Steplist = new ArrayList<stepcountModel>();
    int hour, date, month, year;

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
        dbManager = new DBHandler(this);
        stepcountModel = new stepcountModel();

        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();

        // Get sensor manager on starting the service.
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Registering...
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

        // Get default sensor type
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int Display = 0;

        Steplist = dbManager.getCurrentDayHoursStepcountlist(date, month, year, hour);
        int TotalStepCount = dbManager.getSumOfStepList(date, month, year);

        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
                numSteps = Steplist.get(i).getStep();
            }
            Display = TotalStepCount;
        }

        String Calories = String.valueOf(commanMethod.calculateCalories(Display, userWeight, userHeight));
//        Log.e("TAG", Display + "onStartCommand: " + Calories);

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

     /*   if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

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
                    break;
                }
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM: {
                    break;
                }
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW: {
                    break;
                }
                default: {
                }
            }
        }
    }


    @Override
    public void step(long timeNs) {
        
        /*int TotalStepCount = dbManager.getSumOfStepList(date, month, year);
         int HourwiseTotalStep = dbManager.getSumOfHoursStepList(date, month, year, hour);

        int total = TotalStepCount - HourwiseTotalStep;
        numSteps++;
        int updatestep = HourwiseTotalStep + numSteps;
        int displaystep = updatestep + total;

        Log.e("total", TotalStepCount + " - " + HourwiseTotalStep);
        Log.e("numSteps", numSteps + "");
        Log.e("update", updatestep + "");
        Log.e("display", displaystep + "");*/

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

        String Calories = String.valueOf(commanMethod.calculateCalories(numSteps, userWeight, userHeight));
        String Distance = String.valueOf(commanMethod.calculateDistance(numSteps, userHeight));

        showNotification(Display, Integer.parseInt(Calories));

        Intent sendLevel = new Intent();
        sendLevel.setAction("GET_SIGNAL_STRENGTH");
        sendLevel.putExtra("stepdata", Display);
        sendBroadcast(sendLevel);

        String ts = String.valueOf(System.currentTimeMillis());

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
                dbManager.addStepcountData(stepcountModel);
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
            channel.setShowBadge(false);
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(constant.NOTIFICATION_ID_FOR_STEP, notification);

    }


}