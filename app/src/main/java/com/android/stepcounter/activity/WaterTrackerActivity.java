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
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.Calendar;

public class WaterTrackerActivity extends AppCompatActivity {
    Toolbar mToolbar;
    ArcProgress arcProgress;
    LinearLayout llRemovewater, lladdwater;
    String waterGoal, DefualtCup;
    DBHandler dbManager;
    waterlevel waterlevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracker);
        dbManager = new DBHandler(this);
        init();
    }

    private void init() {
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

        arcProgress.setMax(Integer.parseInt(WaterGoalValue[0]));

        String[] DefultCupValue = DefualtCup.split(" ");

        Log.e("TAG", "new: " + Float.valueOf(DefultCupValue[0]));
        Log.e("TAG", "new: " + Integer.parseInt(WaterGoalValue[0]));

        final int[] value = {0};
        final int[] watermius = {Integer.parseInt(WaterGoalValue[0])};
        final Float[] ints = {Float.valueOf(DefultCupValue[0])};

        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int min = rightNow.get(Calendar.MINUTE);
        int date = rightNow.get(Calendar.DATE);
        int month = rightNow.get(Calendar.MONTH) + 1;
        int year = rightNow.get(Calendar.YEAR);

        lladdwater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DefultCupValue[1].contains("fl")) {
                    value[0] = (int) (value[0] + Float.valueOf(DefultCupValue[0]));
                    arcProgress.setProgress(value[0]);
                } else {
                    value[0] = value[0] + Integer.parseInt(DefultCupValue[0]);
                    arcProgress.setProgress(value[0]);
                }

               /* waterlevel = new waterlevel();
                waterlevel.setDate(date);
                waterlevel.setMonth(month);
                waterlevel.setYear(year);
                waterlevel.setHour(hour);
                waterlevel.setMin(min);
                waterlevel.setUnit(String.valueOf(value[0]));
                dbManager.addWaterData(waterlevel);*/
            }
        });

        llRemovewater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DefultCupValue[1].contains("fl")) {
                    watermius[0] = (int) (watermius[0] - Float.valueOf(DefultCupValue[0]));
                    arcProgress.setProgress(watermius[0]);
                } else {
                    watermius[0] = watermius[0] - Integer.parseInt(DefultCupValue[0]);
                    arcProgress.setProgress(watermius[0]);
                }
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