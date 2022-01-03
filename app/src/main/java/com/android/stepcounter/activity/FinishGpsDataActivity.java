package com.android.stepcounter.activity;

import static com.android.stepcounter.activity.TrainingActivity.isGPSFinish;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.R;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.GpsTrackerModel;
import com.android.stepcounter.utils.CommanMethod;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;

import java.util.ArrayList;

public class FinishGpsDataActivity extends AppCompatActivity implements View.OnClickListener {

    String Date, TargetType;
    Integer AMPM, numStep;
    String formation;
    DatabaseManager dbManager;
    TextView mTvCurrDate, mTvEditDailog;
    TextView mTimerValue, mTimerText, mTvCurrentValue, mTvGoalValue, mStepValue, mStep, mTvKcalValue, mTvKcal, mTvmileValue, mTvmile;
    ArrayList<GpsTrackerModel> gpsTrackerModelArrayList = new ArrayList<>();
    String Distance = "0.0", Duration;
    int Calories = 0;
    ImageView mIvClosed;
    CardView mCvShare;
    EditText mEtFeelingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_gps_data);
        dbManager = new DatabaseManager(this);

        Date = getIntent().getStringExtra("Date");
        AMPM = getIntent().getIntExtra("AMPM", 0);

        if (AMPM.equals(1)) {
            formation = "PM";
        } else {
            formation = "AM";
        }

        Logger.e(Date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        mTvCurrDate = findViewById(R.id.tvCurrDate);
        mTvEditDailog = findViewById(R.id.tvEditDailog);

        mCvShare = findViewById(R.id.cvShare);
        mCvShare.setOnClickListener(this);
        mIvClosed = findViewById(R.id.ivClosed);
        mIvClosed.setOnClickListener(this);
        mTvCurrDate.setText("Today " + Date + " " + formation);
        mTvEditDailog.setOnClickListener(this);

        mEtFeelingData = findViewById(R.id.etFeelingData);
        mTvCurrentValue = findViewById(R.id.tvCurrentValue);
        mTvGoalValue = findViewById(R.id.tvGoalValue);
        mTimerValue = findViewById(R.id.timervalue);
        mTimerText = findViewById(R.id.timer);
        mStepValue = findViewById(R.id.tvStepValue);
        mStep = findViewById(R.id.tvStep);
        mTvKcalValue = findViewById(R.id.mtvKcalValue);
        mTvKcal = findViewById(R.id.mtvKcal);
        mTvmileValue = findViewById(R.id.tvmileValue);
        mTvmile = findViewById(R.id.tvmile);

        getDataFromDatabase();
        setData();

        mEtFeelingData.setText(StorageManager.getInstance().getFeelingData());

    }

    private void getDataFromDatabase() {
        gpsTrackerModelArrayList = dbManager.getGpsTrackerlist();

        for (int i = 0; i < gpsTrackerModelArrayList.size(); i++) {
            TargetType = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getType();
            numStep = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getStep();
            Calories = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getCalories();
            Distance = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getDistance();
            Duration = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getDuration();
        }
    }

    private void setData() {
        if (TargetType != null && TargetType.equals("Target Distance")) {
            mTvCurrentValue.setText(Distance + "");
            mTvGoalValue.setText("Mile");

            mTvmileValue.setVisibility(View.GONE);
            mTvmile.setVisibility(View.GONE);
            mTimerValue.setVisibility(View.VISIBLE);
            mTimerText.setVisibility(View.VISIBLE);

            mTimerValue.setText(Duration + "");
            mTimerText.setText("Duration");
            mStepValue.setText(numStep + "");
            mStep.setText("Steps");
            mTvKcalValue.setText(Calories + "");
            mTvKcal.setText("Kcal");

        } else if (TargetType != null && TargetType.equals("Target Duration")) {
            mTvCurrentValue.setText(Duration + "");
            mTvGoalValue.setText("Duration");

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
            mTvGoalValue.setText("Kcal");

            mTvmileValue.setVisibility(View.GONE);
            mTvmile.setVisibility(View.GONE);
            mTimerValue.setVisibility(View.VISIBLE);
            mTimerText.setVisibility(View.VISIBLE);

            mTimerValue.setText(Duration + "");
            mTimerText.setText("Duration");
            mStepValue.setText(numStep + "");
            mStep.setText("Steps");
            mTvKcalValue.setText(Distance + "");
            mTvKcal.setText("Mile");

        } else if (TargetType != null && TargetType.equals("Open Target")) {
            mTvCurrentValue.setText(Distance + "");
            mTvGoalValue.setText("Mile");

            mTvmileValue.setVisibility(View.GONE);
            mTvmile.setVisibility(View.GONE);
            mTimerValue.setVisibility(View.VISIBLE);
            mTimerText.setVisibility(View.VISIBLE);

            mTimerValue.setText(Duration + "");
            mTimerText.setText("Duration");
            mStepValue.setText(numStep + "");
            mStep.setText("Steps");
            mTvKcalValue.setText(Calories + "");
            mTvKcal.setText("Kcal");
        }
    }

    private void showEditWorkoutDailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_edit_workout, null);
        dialogBuilder.setView(d);
        AlertDialog alertDialog = dialogBuilder.create();
        Button mBtnSave = (Button) d.findViewById(R.id.btnSave);
        Button mBtnCancel = (Button) d.findViewById(R.id.btnCancel);

        CardView cvKm = (CardView) d.findViewById(R.id.cvKm);
        CardView cvMile = (CardView) d.findViewById(R.id.lcvMile);
        CardView cvKcal = (CardView) d.findViewById(R.id.cvKcal);

        EditText etDistance = (EditText) d.findViewById(R.id.etDistance);
        EditText etCalories = (EditText) d.findViewById(R.id.etCalories);

        final boolean[] isMile = {true};

        etDistance.setText(Distance + "");
        etCalories.setText(Calories + "");

//        Logger.e(etDistance.getText().toString());

        cvMile.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
        cvKcal.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));

        cvMile.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                isMile[0] = false;
                cvMile.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                cvKm.setCardBackgroundColor(getResources().getColor(R.color.transprant));
                float distance = Float.parseFloat(etDistance.getText().toString());
                Logger.e(distance + "------");
                Logger.e(CommanMethod.calculateMileToKM(distance));
                etDistance.setText(CommanMethod.calculateMileToKM(distance) + "");
            }
        });

        cvKm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                isMile[0] = true;
                cvKm.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                cvMile.setCardBackgroundColor(getResources().getColor(R.color.transprant));
                float distance = Float.parseFloat(etDistance.getText().toString());
                Logger.e(distance + "------");
                Logger.e(CommanMethod.calculateKmToMile(distance));
                etDistance.setText(CommanMethod.calculateKmToMile(distance) + "");
            }
        });

        etCalories.setText(etCalories.getText().toString());

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Distance = etDistance.getText().toString();
                Calories = Integer.parseInt(etCalories.getText().toString());
                setData();
                updateDataFromDatabase();
                alertDialog.dismiss();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void updateDataFromDatabase() {
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

        dbManager.updateGPSData(gpsTrackerModel);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvEditDailog:
                showEditWorkoutDailog();
                break;
            case R.id.ivClosed:
                String Feelingdata = mEtFeelingData.getText().toString();
                if (Feelingdata != null) {
                    StorageManager.getInstance().setFeelingData(Feelingdata);
                } else {
                    StorageManager.getInstance().setFeelingData("");
                }
                isGPSFinish = true;
                startActivity(new Intent(this, TrainingActivity.class));
                break;
            case R.id.cvShare:
                String Feeling = mEtFeelingData.getText().toString();
                if (Feeling != null) {
                    StorageManager.getInstance().setFeelingData(Feeling);
                } else {
                    StorageManager.getInstance().setFeelingData("");
                }
                startActivity(new Intent(this, ShareGPSActivity.class));
                break;
        }
    }
}