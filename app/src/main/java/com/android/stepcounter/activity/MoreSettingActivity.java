package com.android.stepcounter.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.R;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;

import java.util.ArrayList;

public class MoreSettingActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    Toolbar mToolbar;
    LinearLayout mLlArchivement, mLlPersonalInfo, mLlSensitivity, mLlHistory, mLlHistoryData, mLlInstruction;
    AppCompatSpinner mSpGoal;
    CardView mCvReminder;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    TextView mTvKcal, mTvSteps, mTvMiles, mTvSensitivity;
    long mTotalDisanceData, mTotalStepData, mTotalCaloriesData;
    DatabaseManager dbManager;
    int sensitivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_setting);
        dbManager = new DatabaseManager(this);
        // Get sensor manager on starting the service.
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Registering...
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

        // Get default sensor type
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDatabase();
        init();
    }

    private void getDataFromDatabase() {
        mTotalStepData = dbManager.getTotalStepCount();
        mTotalDisanceData = dbManager.getTotalDistanceCount();
        mTotalCaloriesData = dbManager.getTotalCaloriesCount();
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
        mLlSensitivity = findViewById(R.id.llSensitivity);
        mLlInstruction = findViewById(R.id.llInstruction);
        mCvReminder = findViewById(R.id.cvReminder);
        mLlHistory = findViewById(R.id.llHistory);
        mLlHistoryData = findViewById(R.id.llHistoryData);

        mLlArchivement.setOnClickListener(this);
        mLlPersonalInfo.setOnClickListener(this);
        mLlSensitivity.setOnClickListener(this);
        mLlHistory.setOnClickListener(this);
        mLlHistoryData.setOnClickListener(this);
        mLlInstruction.setOnClickListener(this);
        mCvReminder.setOnClickListener(this);

        mTvKcal = findViewById(R.id.tvAKcal);
        mTvSteps = findViewById(R.id.tvASteps);
        mTvMiles = findViewById(R.id.tvAMiles);
        mTvSensitivity = findViewById(R.id.tvSensitivity);

        mTvKcal.setText(mTotalCaloriesData + "");
        mTvSteps.setText(mTotalStepData + "");
        mTvMiles.setText(mTotalDisanceData + "");

        sensitivity = StorageManager.getInstance().getSetThreshold();
        if (sensitivity <= 20) {
            mTvSensitivity.setText("Low");
        } else if (sensitivity > 20 && sensitivity <= 40) {
            mTvSensitivity.setText("Medium");
        } else if (sensitivity > 40 && sensitivity <= 60) {
            mTvSensitivity.setText("Medium");
        } else if (sensitivity > 60 && sensitivity <= 80) {
            mTvSensitivity.setText("Medium");
        } else if (sensitivity > 80 && sensitivity <= 100) {
            mTvSensitivity.setText("High");
        }

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
            case R.id.llHistory:
                startActivity(new Intent(MoreSettingActivity.this, HistoryActivity.class));
                break;
            case R.id.llHistoryData:
                startActivity(new Intent(MoreSettingActivity.this, HistoryActivity.class));
                break;
            case R.id.llInstruction:
                startActivity(new Intent(MoreSettingActivity.this, InstructionActivity.class));
                break;
            case R.id.llSensitivity:
                ShowSensitivitydailog();
                break;
        }
    }

    private void ShowSensitivitydailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_sensitivity, null);
        dialogBuilder.setView(d);
        AlertDialog alertDialog = dialogBuilder.create();

        TextView mTvSensitivityCount = d.findViewById(R.id.tvSensitivityCount);
        SeekBar seekBar = d.findViewById(R.id.seekBar);
        CardView mcvCancel = d.findViewById(R.id.cvCancel);
        CardView mcvSave = d.findViewById(R.id.cvSave);
        seekBar.setMax(100);
        seekBar.setProgress(sensitivity);
        mTvSensitivityCount.setText("Sensitivity 3");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                seekBar.setProgress(progress);
                if (progress <= 20) {
                    mTvSensitivityCount.setText("Sensitivity 1");
                } else if (progress > 20 && progress <= 40) {
                    mTvSensitivityCount.setText("Sensitivity 2");
                } else if (progress > 40 && progress <= 60) {
                    mTvSensitivityCount.setText("Sensitivity 3");
                } else if (progress > 60 && progress <= 80) {
                    mTvSensitivityCount.setText("Sensitivity 4");
                } else if (progress > 80 && progress <= 100) {
                    mTvSensitivityCount.setText("Sensitivity 5");
                }
                StorageManager.getInstance().setSetThreshold(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mcvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        mcvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensitivity = StorageManager.getInstance().getSetThreshold();
                if (sensitivity <= 20) {
                    mTvSensitivity.setText("Low");
                } else if (sensitivity > 20 && sensitivity <= 40) {
                    mTvSensitivity.setText("Medium");
                } else if (sensitivity > 40 && sensitivity <= 60) {
                    mTvSensitivity.setText("Medium");
                } else if (sensitivity > 60 && sensitivity <= 80) {
                    mTvSensitivity.setText("Medium");
                } else if (sensitivity > 80 && sensitivity <= 100) {
                    mTvSensitivity.setText("High");
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if (sensor == mSensor) {
            switch (i) {
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH: {
                    Logger.e("high");
                    break;
                }
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM: {
                    Logger.e("Medium");
                    break;
                }
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW: {
                    Logger.e("low");
                    break;
                }
                default: {
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}