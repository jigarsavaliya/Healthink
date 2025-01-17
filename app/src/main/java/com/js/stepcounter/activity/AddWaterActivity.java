package com.js.stepcounter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.js.stepcounter.R;;
import com.js.stepcounter.database.DatabaseManager;
import com.js.stepcounter.model.WaterLevelModel;
import com.js.stepcounter.utils.CommanMethod;
import com.js.stepcounter.utils.StorageManager;
import com.bikomobile.donutprogress.DonutProgress;

import java.util.ArrayList;
import java.util.Calendar;

public class AddWaterActivity extends BaseActivity {
    Toolbar mToolbar;
    TextView mTvTargeGoalDisplay;
    DonutProgress mCpWaterChart;
    LinearLayout mLlDrinkView, mLlTargetView;
    CardView mCvDone, mCvHistory;
    String Watergoal, WaterUnit, Watercup;
    String[] WaterGoalValue;
    DatabaseManager dbManager;
    Calendar rightNow;
    int date, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_water);
        dbManager = new DatabaseManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSharedPreferences();
        init();
    }

    private void setSharedPreferences() {
        Watergoal = StorageManager.getInstance().getWaterGoal();
        Watercup = StorageManager.getInstance().getWaterCup();
        WaterUnit = StorageManager.getInstance().getWaterUnit();
    }

    private void init() {
        rightNow = Calendar.getInstance();
        date = rightNow.get(Calendar.DATE);
        month = rightNow.get(Calendar.MONTH) + 1;
        year = rightNow.get(Calendar.YEAR);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mTvTargeGoalDisplay = findViewById(R.id.tvTargeGoalDisplay);
        mCpWaterChart = findViewById(R.id.cpwaterchart);
        mLlDrinkView = findViewById(R.id.llDrinkView);
        mLlTargetView = findViewById(R.id.llTargetView);
        mCvDone = findViewById(R.id.cvDone);
        mCvHistory = findViewById(R.id.cvHistory);

        mCvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mCvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddWaterActivity.this, HeathActivity.class));
            }
        });

        WaterGoalValue = Watergoal.split(" ");

        setdatainprogress();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLlDrinkView.setVisibility(View.GONE);
                mLlTargetView.setVisibility(View.VISIBLE);
            }
        }, 2000);

    }

    private void setdatainprogress() {
        mCpWaterChart.setMax(100);
//        mCpWaterChart.setMax(Integer.parseInt(WaterGoalValue[0]));

        ArrayList<WaterLevelModel> waterlevelArrayList = new ArrayList<>();

        waterlevelArrayList = dbManager.getDayWaterdata(date, month, year);

        if (WaterUnit.equals("ml")) {
            int lastentry = 0;
            if (waterlevelArrayList != null) {
                for (int i = 0; i < waterlevelArrayList.size(); i++) {
//                Log.e("TAG", "init: " + waterlevelArrayList.get(i).getSumwater());
                    lastentry = waterlevelArrayList.get(i).getSumwater();
                }

                if (lastentry != 0) {
                    if (lastentry < Integer.parseInt(WaterGoalValue[0])) {
//                    Log.e("TAG", "init: " + lastentry);
                        float progress = (float) lastentry / Integer.parseInt(WaterGoalValue[0]) * 100;
                        mCpWaterChart.setProgress((int) progress);
                        mCpWaterChart.setProgressWithAnimation((int) progress, 5);
                    } else {
                        mCpWaterChart.setProgress(100);
                        mCpWaterChart.setProgressWithAnimation(100, 5);
                    }
                }
                mTvTargeGoalDisplay.setText(lastentry + " / " + Watergoal);
                StorageManager.getInstance().setWatergoalTarget(lastentry);
            } else {
                mCpWaterChart.setProgress(0);
                mTvTargeGoalDisplay.setText(0 + " / " + Watergoal);
            }
        } else {
            double lastentry = 0;
            if (waterlevelArrayList != null) {
                for (int i = 0; i < waterlevelArrayList.size(); i++) {
//                Log.e("TAG", "init: " + waterlevelArrayList.get(i).getSumwater());
                    lastentry = Math.round(CommanMethod.getMlToFloz(Float.valueOf(waterlevelArrayList.get(i).getSumwater())));
                }

                if (lastentry != 0) {
                    if (lastentry < Integer.parseInt(WaterGoalValue[0])) {
//                    Log.e("TAG", "init: " + lastentry);
                        double value = (double) lastentry / Integer.parseInt(WaterGoalValue[0]) * 100;
                        mCpWaterChart.setProgress((int) value);
                        mCpWaterChart.setProgressWithAnimation((int) value, 5);
                    } else {
                        mCpWaterChart.setProgress(100);
                        mCpWaterChart.setProgressWithAnimation(100, 5);
                    }
                }
                mTvTargeGoalDisplay.setText(lastentry + " / " + Watergoal);
                StorageManager.getInstance().setWatergoalTarget((int) lastentry);
            } else {
                mCpWaterChart.setProgress(0);
                mTvTargeGoalDisplay.setText(0 + " / " + Watergoal);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}