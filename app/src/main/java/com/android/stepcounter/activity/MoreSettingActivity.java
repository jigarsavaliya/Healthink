package com.android.stepcounter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.R;
import com.android.stepcounter.utils.StorageManager;

import java.util.ArrayList;

public class MoreSettingActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar mToolbar;
    LinearLayout mLlArchivement, mLlPersonalInfo;
    AppCompatSpinner mSpGoal;
    CardView mCvReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_setting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Profile");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mLlArchivement = findViewById(R.id.llArchivement);
        mLlPersonalInfo = findViewById(R.id.llPersonalInfo);
        mCvReminder = findViewById(R.id.cvReminder);
        mLlArchivement.setOnClickListener(this);
        mLlPersonalInfo.setOnClickListener(this);
        mCvReminder.setOnClickListener(this);

        mSpGoal = findViewById(R.id.spStepGoal);
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 500; i <= 40000; ) {
            list.add(i);
            i = i + 500;
        }

//        Logger.e(list.size());
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpGoal.setAdapter(arrayAdapter);

        int spinnerPosition = arrayAdapter.getPosition(StorageManager.getInstance().getStepCountGoalUnit());
        mSpGoal.setSelection(spinnerPosition);

        mSpGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                StorageManager.getInstance().setStepCountGoalUnit(Integer.valueOf(value));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llArchivement:
                startActivity(new Intent(MoreSettingActivity.this, ArchivementActivity.class));
                break;
            case R.id.llPersonalInfo:
                startActivity(new Intent(MoreSettingActivity.this, PersonalInfomationActivity.class));
                break;
            case R.id.cvReminder:
                startActivity(new Intent(MoreSettingActivity.this, ReminderActivity.class));
                break;
        }
    }
}