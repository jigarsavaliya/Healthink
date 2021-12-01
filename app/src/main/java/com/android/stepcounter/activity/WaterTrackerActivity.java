package com.android.stepcounter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.stepcounter.R;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.waterlevel;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.commanMethod;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;
import java.util.Calendar;

public class WaterTrackerActivity extends AppCompatActivity {
    Toolbar mToolbar;
    ArcProgress arcProgress;
    LinearLayout llRemovewater, lladdwater;
    String waterGoal, DefualtCup;
    DBHandler dbManager;
    waterlevel waterlevel;
    ArrayList<waterlevel> waterlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracker);
        dbManager = new DBHandler(this);
        waterlist = new ArrayList<>();
        init();
    }

    private void init() {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int min = rightNow.get(Calendar.MINUTE);
        int date = rightNow.get(Calendar.DATE);
        int month = rightNow.get(Calendar.MONTH) + 1;
        int year = rightNow.get(Calendar.YEAR);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Water tracker");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        arcProgress = findViewById(R.id.arc_progress);
        llRemovewater = findViewById(R.id.llRemovewater);
        lladdwater = findViewById(R.id.lladdwater);

        waterGoal = StorageManager.getInstance().getWaterGoal();
        DefualtCup = StorageManager.getInstance().getWaterCup();

        String[] WaterGoalValue = waterGoal.split(" ");

        waterlist = dbManager.getCurrentDayWatercountlist(date, month, year);

        String[] WaterGoal = StorageManager.getInstance().getWaterGoal().split(" ");

        arcProgress.setMax(Integer.parseInt(WaterGoalValue[0]));

        int waterml = 0;
        if (waterlist != null) {
            for (int i = 0; i < waterlist.size(); i++) {
                waterml = (int) Float.parseFloat(waterlist.get(i).getUnit());
//                Log.e("TAG", "date: " + waterml);
            }
        } else {
            waterml = 0;
        }
        String WaterUnit = StorageManager.getInstance().getWaterUnit();

        final float[] value = {0};

        if (WaterUnit.contains("ml")) {
            if (waterml == 0.0) {
                arcProgress.setProgress(0);
                value[0] = 0;
            } else {
                if (waterml < Integer.parseInt(WaterGoal[0])) {
                    arcProgress.setProgress(waterml);
                    value[0] = waterml;
                } else {
                    arcProgress.setProgress(Integer.parseInt(WaterGoal[0]));
                    value[0] = Integer.parseInt(WaterGoal[0]);
                }
            }
        } else {
            if (waterml == 0.0) {
                arcProgress.setProgress(0);
                value[0] = 0;
            } else {
                double newMlValue = commanMethod.getMlToFloz(Float.valueOf(waterml));
                if (newMlValue < Integer.parseInt(WaterGoal[0])) {
                    arcProgress.setProgress((int) Math.round(newMlValue));
                    value[0] = (int) Math.round(newMlValue);
                } else {
                    arcProgress.setProgress(Integer.parseInt(WaterGoal[0]));
                    value[0] = Integer.parseInt(WaterGoal[0]);
                }
            }
        }

        String[] DefultCupValue = DefualtCup.split(" ");


        lladdwater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double covertinml;

                waterlevel = new waterlevel();
                waterlevel.setDate(date);
                waterlevel.setMonth(month);
                waterlevel.setYear(year);
                waterlevel.setHour(hour);
                waterlevel.setMin(min);

                if (DefultCupValue[1].contains("fl")) {
                    value[0] = value[0] + Float.parseFloat(DefultCupValue[0]);
                    if (value[0] < Integer.parseInt(WaterGoalValue[0])) {
                        arcProgress.setProgress((int) value[0]);
                    } else {
                        arcProgress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                    }
                    covertinml = commanMethod.getFlozToMl(value[0]);
                    waterlevel.setUnit(String.valueOf(covertinml));
                } else {
                    Log.e("TAG", "old: " + value[0]);
                    value[0] = value[0] + Integer.parseInt(DefultCupValue[0]);
                    Log.e("TAG", "new: " + value[0]);
                    if (value[0] < Integer.parseInt(WaterGoalValue[0])) {
                        arcProgress.setProgress((int) value[0]);
                    } else {
                        arcProgress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                    }
                    waterlevel.setUnit(String.valueOf(value[0]));
                }

                dbManager.addWaterData(waterlevel);
            }
        });

        final Float[] finalWaterml = {(float) waterml};
        double covertvalue = commanMethod.getMlToFloz(finalWaterml[0]);
        final float[] newvalue = {(int) Math.round(covertvalue)};
        final float ints = Float.parseFloat(String.valueOf(DefultCupValue[0]));
        Log.e("init", "init: " + newvalue[0] + ints);

        llRemovewater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waterlevel = new waterlevel();
                waterlevel.setDate(date);
                waterlevel.setMonth(month);
                waterlevel.setYear(year);
                waterlevel.setHour(hour);
                waterlevel.setMin(min);

                //database mathi mius krvanu baki
                if (DefultCupValue[1].contains("fl")) {
                    Log.e("TAG", "old: " + newvalue[0]);
                    newvalue[0] = newvalue[0] - ints;
                    Log.e("TAG", "new: " + newvalue[0]);
                    if (newvalue[0] < 0) {
                        arcProgress.setProgress(0);
                        waterlevel.setUnit(0 + "");
                    } else {
                        arcProgress.setProgress((int) newvalue[0]);
                        waterlevel.setUnit(String.valueOf(newvalue[0]));
                    }
                } else {
                    Log.e("TAG", "old: " + finalWaterml[0]);
                    finalWaterml[0] = finalWaterml[0] - ints;
                    Log.e("TAG", "new: " + finalWaterml[0]);
                    if (finalWaterml[0] < 0) {
                        arcProgress.setProgress(0);
                        waterlevel.setUnit(0 + "");
                    } else {
                        arcProgress.setProgress(Math.round(finalWaterml[0]));
                        waterlevel.setUnit(Math.round(finalWaterml[0]) + "");
                    }
                }
                dbManager.addWaterData(waterlevel);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                startActivity(new Intent(WaterTrackerActivity.this, WaterSettingActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}