package com.android.stepcounter.activity;

import static com.android.stepcounter.sevices.SensorService.isGpsService;
import static com.android.stepcounter.sevices.SensorService.mapStep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.R;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.GpsTrackerModel;
import com.android.stepcounter.utils.CommanMethod;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.constant;
import com.bikomobile.donutprogress.DonutProgress;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;

public class GPSStartActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar mToolbar;
    private int seconds = 0;
    CardView mCvPause, mCvFinish, mCvResume;
    TextView mTimerValue, mTimerText, mTvCurrentValue, mTvGoalValue, mStepValue, mStep, mTvKcalValue, mTvKcal, mTvmileValue, mTvmile;
    Boolean IsTimerStart = true;
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

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("GET_SIGNAL")) {
                numStep = intent.getIntExtra("mapstepdata", 0);
                mStepValue.setText(numStep + "");

                Calories = String.valueOf(CommanMethod.calculateCalories(numStep, userWeight, userHeight));
                Distance = String.format("%.2f", CommanMethod.calculateDistance(numStep));

                setData();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsstart);
        dbManager = new DatabaseManager(this);

        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, new IntentFilter("GET_SIGNAL"));

        TargetType = getIntent().getStringExtra("TargetType");

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
        mCvFinish = findViewById(R.id.cvFinish);
        mCvResume = findViewById(R.id.cvResume);
        mCvPause.setOnClickListener(this);
        mCvResume.setOnClickListener(this);
        mCvFinish.setOnClickListener(this);

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

                    if (Current != 0) {
                        if (Current < goal) {
                            float progress = (float) Current / goal * 100;
                            mCpGoalChart.setProgress((int) progress);
                        } else {
                            mCpGoalChart.setProgress(100);
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
                mCvResume.setVisibility(View.VISIBLE);
                mCvFinish.setVisibility(View.VISIBLE);
                mCvPause.setVisibility(View.GONE);
                break;
            case R.id.cvResume:
                isGpsService = true;
                IsTimerStart = true;
                mCvResume.setVisibility(View.GONE);
                mCvFinish.setVisibility(View.GONE);
                mCvPause.setVisibility(View.VISIBLE);
                break;
            case R.id.cvFinish:
                isGpsService = false;
                mapStep = 0;
                mCvResume.setVisibility(View.GONE);
                mCvFinish.setVisibility(View.GONE);
                mCvPause.setVisibility(View.VISIBLE);

                GpsTrackerModel gpsTrackerModel = new GpsTrackerModel();
                gpsTrackerModel.setType(TargetType);
                gpsTrackerModel.setAction(StorageManager.getInstance().getStepType());
                gpsTrackerModel.setStep(numStep);
                gpsTrackerModel.setDistance(Distance);
                gpsTrackerModel.setCalories(Integer.valueOf(Calories));
                gpsTrackerModel.setSlatitude("");
                gpsTrackerModel.setSlogtitude("");
                gpsTrackerModel.setElatitude("");
                gpsTrackerModel.setElongtitude("");

                if (TargetType != null && TargetType.equals("Target Duration")) {
                    gpsTrackerModel.setDuration(mTvCurrentValue.getText().toString());
                } else {
                    gpsTrackerModel.setDuration(mTimerValue.getText().toString());
                }

                Logger.e(new Gson().toJson(gpsTrackerModel));
                dbManager.addGpsData(gpsTrackerModel);

                Intent i = new Intent(this, FinishGpsDataActivity.class);
                i.putExtra("Date", rightnow.get(Calendar.HOUR) + ":" + rightnow.get(Calendar.MINUTE));
                i.putExtra("AMPM", rightnow.get(Calendar.AM_PM));
                startActivity(i);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}