package com.js.stepcounter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import com.js.stepcounter.R;
import com.js.stepcounter.utils.StorageManager;

import java.util.ArrayList;

public class WaterSettingActivity extends BaseActivity {
    Toolbar mToolbar;
    String unitvalue = "ml";
    RelativeLayout rlReminder;
    int LAUNCH_SECOND_ACTIVITY = 101;
    TextView mtvReminder, mtvHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_setting);
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Water tracker setting");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mtvReminder = findViewById(R.id.tvreminder);

        if (StorageManager.getInstance().getReminder()) {
            mtvReminder.setText(StorageManager.getInstance().getWaterReminderStart() + " - " + StorageManager.getInstance().getWaterReminderEnd());
        } else {
            mtvReminder.setText("off");
        }
        mtvHours = findViewById(R.id.tvhours);
        rlReminder = findViewById(R.id.rlReminder);
        rlReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(WaterSettingActivity.this, WaterReminderSettingActivity.class));
                Intent i = new Intent(WaterSettingActivity.this, WaterReminderSettingActivity.class);
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        });

        goalmenu();

        StorageManager.getInstance().getWaterGoal();
        StorageManager.getInstance().getWaterCup();

        //Identifying spinner defined in xml layout
        AppCompatSpinner compatSpinner = findViewById(R.id.spunitscup);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("ml");
        arrayList.add("fl oz");


        final ArrayAdapter adapter1 = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        compatSpinner.setAdapter(adapter1);

        compatSpinner.setSelection(((ArrayAdapter<String>) compatSpinner.getAdapter()).getPosition(StorageManager.getInstance().getWaterUnit()));

        compatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                unitvalue = value;
                StorageManager.getInstance().setWaterUnit(value);
                goalmenu();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void goalmenu() {
        //Identifying spinner defined in xml layout
        AppCompatSpinner spin = findViewById(R.id.spGoal);
        ArrayList<String> list = new ArrayList<>();

        if (unitvalue == "fl oz") {
            list.add("16 fl oz");
            list.add("24 fl oz");
            list.add("32 fl oz");
            list.add("40 fl oz");
            list.add("48 fl oz");
            list.add("56 fl oz");
            list.add("64 fl oz");
            list.add("72 fl oz");
            list.add("80 fl oz");
            list.add("88 fl oz");
            list.add("96 fl oz");
            list.add("104 fl oz");
            list.add("112 fl oz");
            list.add("120 fl oz");
            list.add("128 fl oz");
            list.add("136 fl oz");
            list.add("144 fl oz");
            list.add("152 fl oz");
            list.add("160 fl oz");
        } else {
            list.add("250 ml");
            list.add("500 ml");
            list.add("750 ml");
            list.add("1000 ml");
            list.add("1250 ml");
            list.add("1500 ml");
            list.add("1750 ml");
            list.add("2000 ml");
            list.add("2250 ml");
            list.add("2500 ml");
            list.add("2750 ml");
            list.add("3000 ml");
            list.add("3250 ml");
            list.add("3500 ml");
            list.add("3750 ml");
            list.add("4000 ml");
            list.add("4250 ml");
            list.add("4500 ml");
            list.add("4750 ml");
            list.add("5000 ml");
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list);
        spin.setAdapter(adapter);

//        Log.e("TAG", "onItemSelected: " + StorageManager.getInstance().getgoalIndex());

        spin.setSelection(StorageManager.getInstance().getgoalIndex());

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                StorageManager.getInstance().setWaterGoal(value);
                long a = adapterView.getItemIdAtPosition(i);
                StorageManager.getInstance().setgoalIndex((int) a);
//                Log.e("TAG", "onItemSelected: " + (int) a);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Identifying spinner defined in xml layout
        AppCompatSpinner spinner = findViewById(R.id.spdefultcup);
        ArrayList<String> list1 = new ArrayList<>();
        if (unitvalue == "fl oz") {
            list1.add("3.5 fl oz");
            list1.add("7 fl oz");
            list1.add("8 fl oz");
            list1.add("9 fl oz");
            list1.add("12 fl oz");
            list1.add("16 fl oz");
            list1.add("20 fl oz");
            list1.add("22 fl oz");
        } else {
            list1.add("100 ml");
            list1.add("150 ml");
            list1.add("200 ml");
            list1.add("250 ml");
            list1.add("300 ml");
            list1.add("400 ml");
            list1.add("500 ml");
            list1.add("600 ml");
            list1.add("700 ml");
            list1.add("800 ml");
        }

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list1);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(StorageManager.getInstance().getDefultcupIndex());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                StorageManager.getInstance().setWaterCup(value);
                long a = adapterView.getItemIdAtPosition(i);
                StorageManager.getInstance().setDefultcupIndex((int) a);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {

                String result = data.getStringExtra("result");
                String hours = data.getStringExtra("hours");
                mtvReminder.setText(result);
                mtvHours.setText("Every " + hours + " hour");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}