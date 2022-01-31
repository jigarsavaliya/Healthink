package com.android.stepcounter.activity;

import static com.android.stepcounter.sevices.SensorService.IsNotiFlag;
import static com.android.stepcounter.sevices.SensorService.IsServiceStart;
import static com.android.stepcounter.sevices.SensorService.IsTargetType;
import static com.android.stepcounter.sevices.SensorService.IsTimerStart;
import static com.android.stepcounter.sevices.SensorService.gpsModel;
import static com.android.stepcounter.sevices.SensorService.isGpsService;
import static com.android.stepcounter.sevices.SensorService.mapStep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.R;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.GpsModel;
import com.android.stepcounter.model.GpsTrackerModel;
import com.android.stepcounter.model.location;
import com.android.stepcounter.sevices.LocationBgService;
import com.android.stepcounter.sevices.SensorService;
import com.android.stepcounter.utils.CommanMethod;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.constant;
import com.bikomobile.donutprogress.DonutProgress;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GPSStartActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar mToolbar;
    private int seconds = 0;
    CardView mCvPause, mCvFinish, mCvResume;
    TextView mTimerValue, mTimerText, mTvCurrentValue, mTvGoalValue, mStepValue, mStep, mTvKcalValue, mTvKcal, mTvmileValue, mTvmile;
    //    Boolean IsTimerStart = true;
    String TargetType;
    DonutProgress mCpGoalChart;
    MyReceiver myReceiver;
    int numStep = 0;
    private float userWeight = constant.DEFAULT_WEIGHT;
    private float userHeight = constant.DEFAULT_HEIGHT;
    String Distance = "0.0";
    String Calories = "0";
    Calendar calendar = Calendar.getInstance();
    DatabaseManager dbManager;
    Calendar rightnow = Calendar.getInstance();
    ArrayList<location> locationArrayList = new ArrayList<>();
    GpsModel mGpsModel;
    Calendar rightNow;

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("GET_SIGNAL")) {
                TargetType = IsTargetType;
//                mGpsModel = (GpsModel) intent.getExtras().getSerializable("gpsmodeldata");
                mGpsModel = gpsModel;
                numStep = mGpsModel.getCurrStep();
//                Logger.e("GSON" + new GsonBuilder().create().toJson(gpsModel));

                mStepValue.setText(numStep + "");

                Calories = String.valueOf(CommanMethod.calculateCalories(numStep, userWeight, userHeight));
                Distance = String.format("%.2f", CommanMethod.calculateDistance(numStep));

                setData();
            }

            if (intent.getAction().equals("LOCATION")) {
//                Logger.e("" + intent.getDoubleExtra("Latitude", 0));
//                Logger.e("" + intent.getDoubleExtra("Longitude", 0));

                location mLocation = new location();
                mLocation.setLatitude(intent.getDoubleExtra("Latitude", 0));
                mLocation.setLongtitude(intent.getDoubleExtra("Longitude", 0));
                locationArrayList.add(mLocation);

//                Logger.e(locationArrayList.size());
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mGpsModel = (GpsModel) getIntent().getExtras().getSerializable("Notigpsmodel");
        Logger.e("GSON activity" + new GsonBuilder().create().toJson(mGpsModel));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsstart);
        dbManager = new DatabaseManager(this);

        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, new IntentFilter("GET_SIGNAL"));
        registerReceiver(myReceiver, new IntentFilter("LOCATION"));

        TargetType = IsTargetType;


        if (IsNotiFlag) {
            gpsModel = gpsModel;
//            Logger.e("GSON activity" + new GsonBuilder().create().toJson(gpsModel));
            numStep = gpsModel.getCurrStep();
            seconds = gpsModel.getSecond();

            Calories = String.valueOf(CommanMethod.calculateCalories(numStep, userWeight, userHeight));
            Distance = String.format("%.2f", CommanMethod.calculateDistance(numStep));
            IsNotiFlag = false;

            Logger.e(numStep + "" + Calories);
        }

        rightnow.get(Calendar.HOUR);
        rightnow.get(Calendar.MINUTE);
        rightnow.get(Calendar.AM_PM);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSharedPreferences();
        init();
    }

    private void setSharedPreferences() {
        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();
    }

    private void init() {

        rightNow = Calendar.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mTvCurrentValue = findViewById(R.id.tvCurrentValue);
        mTvGoalValue = findViewById(R.id.tvGoalValue);
        mCpGoalChart = findViewById(R.id.cpGoalChart);
        mCvPause = findViewById(R.id.cvPause);
        mCvPause.setOnClickListener(this);
        mCvPause.setVisibility(View.VISIBLE);

        mTimerValue = findViewById(R.id.timervalue);
        mTimerText = findViewById(R.id.timer);
        mStepValue = findViewById(R.id.tvStepValue);
        mStep = findViewById(R.id.tvStep);
        mTvKcalValue = findViewById(R.id.mtvKcalValue);
        mTvKcal = findViewById(R.id.mtvKcal);
        mTvmileValue = findViewById(R.id.tvmileValue);
        mTvmile = findViewById(R.id.tvmile);

        mCpGoalChart.setMax(100);
        startTimer();

        setData();

    }

    private void setData() {
        if (TargetType != null && TargetType.equals("Target Distance")) {
            mTvCurrentValue.setText(Distance + "");
            mTvGoalValue.setText(StorageManager.getInstance().getTargetDistance() + " Mile");

            mTvmileValue.setVisibility(View.GONE);
            mTvmile.setVisibility(View.GONE);
            mTimerValue.setVisibility(View.VISIBLE);
            mTimerText.setVisibility(View.VISIBLE);

            mTimerText.setText("Duration");
            mStepValue.setText(numStep + "");
            mStep.setText("Steps");
            mTvKcalValue.setText(Calories + "");
            mTvKcal.setText("Kcal");

            float goal = Float.parseFloat(StorageManager.getInstance().getTargetDistance());
            float Current = Float.parseFloat(Distance);

            if (Current != 0) {
                if (Current < goal) {
                    float progress = Current / goal * 100;
                    mCpGoalChart.setProgress((int) progress);
                } else {
                    mCpGoalChart.setProgress(100);
                }
            }

        } else if (TargetType != null && TargetType.equals("Target Duration")) {
            calendar.clear();
            calendar.set(Calendar.MINUTE, Integer.parseInt(StorageManager.getInstance().getTargetDuration()));
            mTvGoalValue.setText(calendar.get(Calendar.HOUR) + " : " + calendar.get(Calendar.MINUTE) + " min");

            mTvmileValue.setVisibility(View.VISIBLE);
            mTvmile.setVisibility(View.VISIBLE);
            mTimerValue.setVisibility(View.GONE);
            mTimerText.setVisibility(View.GONE);

            mTvmileValue.setText(Distance + "");
            mTvmile.setText("Mile");
            mStepValue.setText(numStep + "");
            mStep.setText("Steps");
            mTvKcalValue.setText(Calories + "");
            mTvKcal.setText("Kcal");

            float goal = Float.parseFloat(StorageManager.getInstance().getTargetCalories());
            float Current = Float.parseFloat(Calories);

            if (Current != 0) {
                if (Current < goal) {
                    float progress = Current / goal * 100;
                    mCpGoalChart.setProgress((int) progress);
                } else {
                    mCpGoalChart.setProgress(100);
                }
            }

        } else if (TargetType != null && TargetType.equals("Target Calories")) {
            mTvCurrentValue.setText(Calories + "");
            mTvGoalValue.setText(StorageManager.getInstance().getTargetCalories() + " Kcal");

            mTvmileValue.setVisibility(View.GONE);
            mTvmile.setVisibility(View.GONE);
            mTimerValue.setVisibility(View.VISIBLE);
            mTimerText.setVisibility(View.VISIBLE);

            mTimerText.setText("Duration");
            mStepValue.setText(numStep + "");
            mStep.setText("Steps");
            mTvKcalValue.setText(Distance + "");
            mTvKcal.setText("Mile");

            float goal = Float.parseFloat(StorageManager.getInstance().getTargetCalories());
            float Current = Float.parseFloat(Calories);

            if (Current != 0) {
                if (Current < goal) {
                    float progress = Current / goal * 100;
                    mCpGoalChart.setProgress((int) progress);
                } else {
                    mCpGoalChart.setProgress(100);
                }
            }

        } else if (TargetType != null && TargetType.equals("Open Target")) {
            mTvCurrentValue.setText(Distance + "");
            mTvGoalValue.setText(0.0 + " Mile");

            mTvmileValue.setVisibility(View.GONE);
            mTvmile.setVisibility(View.GONE);
            mTimerValue.setVisibility(View.VISIBLE);
            mTimerText.setVisibility(View.VISIBLE);

            mTimerText.setText("Duration");
            mStepValue.setText(numStep + "");
            mStep.setText("Steps");
            mTvKcalValue.setText(Calories + "");
            mTvKcal.setText("Kcal");

            float goal = Float.parseFloat(StorageManager.getInstance().getTargetDistance());
            float Current = Float.parseFloat(Distance);

            if (Current != 0) {
                if (Current < goal) {
                    float progress = Current / goal * 100;
                    mCpGoalChart.setProgress((int) progress);
                } else {
                    mCpGoalChart.setProgress(100);
                }
            }
        }
    }

    private void startTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);

                if (TargetType != null && TargetType.equals("Target Duration")) {
                    mTvCurrentValue.setText(time);
                    mTimerValue.setVisibility(View.GONE);
                    mTimerText.setVisibility(View.GONE);
                } else {
                    mTimerValue.setVisibility(View.VISIBLE);
                    mTimerText.setVisibility(View.VISIBLE);
                    mTimerValue.setText(time);
                }

                int goal = Integer.parseInt(StorageManager.getInstance().getTargetDuration()) * 60;

                if (IsTimerStart) {
                    seconds++;
                    int Current = seconds;

                    if (TargetType != null && TargetType.equals("Target Duration")) {
                        if (Current != 0) {
                            if (Current < goal) {
                                float progress = (float) Current / goal * 100;
                                mCpGoalChart.setProgress((int) progress);
                            } else {
                                mCpGoalChart.setProgress(100);
                            }
                        }
                    }
                }

                handler.postDelayed(this, 1000);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvPause:
                isGpsService = false;
                IsTimerStart = false;
                IsServiceStart = false;
//                mCvResume.setVisibility(View.VISIBLE);
//                mCvFinish.setVisibility(View.VISIBLE);
//                mCvPause.setVisibility(View.GONE);
                StorageManager.getInstance().setIsStepService(false);
//                Intent intent1 = new Intent(this, SensorService.class);
//                stopService(intent1);

                showDailog();
                break;
        }
    }

    public void showDailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GPSStartActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_gps_resume_pause, null);
        dialogBuilder.setView(d);
        AlertDialog alertDialog = dialogBuilder.create();

        CardView cvResume = d.findViewById(R.id.cvResume);
        CardView cvFinish = d.findViewById(R.id.cvFinish);

        cvResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGpsService = true;
                IsTimerStart = true;
                IsServiceStart = true;

                StorageManager.getInstance().setIsStepService(true);
//                Intent intent1 = new Intent(GPSStartActivity.this, SensorService.class);
//                startService(intent1);

                alertDialog.dismiss();
            }
        });

        cvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGpsService = false;
                IsServiceStart = false;
                IsNotiFlag = false;
                mapStep = 0;
                InsetDataInDatabase();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void InsetDataInDatabase() {
        double mSlatitude = 0, mSlogtitude = 0, mElatitude = 0, mElongtitude = 0;

        mSlatitude = locationArrayList.get(0).getLatitude();
        mSlogtitude = locationArrayList.get(0).getLongtitude();
        mElatitude = locationArrayList.get(locationArrayList.size() - 1).getLatitude();
        mElongtitude = locationArrayList.get(locationArrayList.size() - 1).getLongtitude();

        GpsTrackerModel gpsTrackerModel = new GpsTrackerModel();
        gpsTrackerModel.setType(TargetType);
        gpsTrackerModel.setAction(StorageManager.getInstance().getStepType());
        gpsTrackerModel.setStep(numStep);
        gpsTrackerModel.setDistance(Distance);
        gpsTrackerModel.setCalories(Integer.valueOf(Calories));
        gpsTrackerModel.setSlatitude(String.valueOf(mSlatitude));
        gpsTrackerModel.setSlogtitude(String.valueOf(mSlogtitude));
        gpsTrackerModel.setElatitude(String.valueOf(mElatitude));
        gpsTrackerModel.setElongtitude(String.valueOf(mElongtitude));
        gpsTrackerModel.setDate(rightNow.get(Calendar.DATE));
        gpsTrackerModel.setMonth(rightNow.get(Calendar.MONTH) + 1);
        gpsTrackerModel.setYear(rightNow.get(Calendar.YEAR));

        if (TargetType != null && TargetType.equals("Target Duration")) {
            gpsTrackerModel.setDuration(mTvCurrentValue.getText().toString());
        } else {
            gpsTrackerModel.setDuration(mTimerValue.getText().toString());
        }

//        Logger.e(new Gson().toJson(gpsTrackerModel));
        dbManager.addGpsData(gpsTrackerModel);

        Intent intent = new Intent(this, LocationBgService.class);
        stopService(intent);

        locationArrayList.clear();

        Intent intent1 = new Intent(this, SensorService.class);
        stopService(intent1);

        Intent i = new Intent(this, FinishGpsDataActivity.class);
        i.putExtra("Date", rightnow.get(Calendar.HOUR) + ":" + rightnow.get(Calendar.MINUTE));
        i.putExtra("AMPM", rightnow.get(Calendar.AM_PM));
        startActivity(i);

    }

    @Override
    public void onBackPressed() {
        if (isGpsService) {
            ShowQuictDailog();
        } else {
            super.onBackPressed();
        }
    }

    private void ShowQuictDailog() {
        isGpsService = false;
        IsTimerStart = false;
        mCvPause.setVisibility(View.VISIBLE);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("QUIT WORKOUT?");
        builder1.setMessage("If you get tired,take a break,but DON'T QUIT!");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "QUIT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isGpsService = false;
                        mapStep = 0;
                        InsetDataInDatabase();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isGpsService = true;
                        IsTimerStart = true;
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}