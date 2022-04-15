package com.js.stepcounter.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.js.stepcounter.R;
import com.js.stepcounter.database.DatabaseManager;
import com.js.stepcounter.model.GpsTrackerModel;
import com.js.stepcounter.utils.CommanMethod;
import com.js.stepcounter.utils.Logger;
import com.js.stepcounter.utils.StorageManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class FinishGpsDataActivity extends BaseActivity implements View.OnClickListener, OnMapReadyCallback {

    String Date, TargetType;
    Integer AMPM, numStep;
    String formation;
    DatabaseManager dbManager;
    TextView mTvCurrDate, mTvEditDailog;
    TextView mTimerValue, mTimerText, mTvCurrentValue, mTvGoalValue, mStepValue, mStep, mTvKcalValue, mTvKcal, mTvmileValue, mTvmile;
    ArrayList<GpsTrackerModel> gpsTrackerModelArrayList = new ArrayList<>();
    String Distance = "0.0", Duration, sLatetitue, sLongtitude, Elatitude, Elongtitude;
    int Calories = 0;
    ImageView mIvClosed;
    CardView mCvShare;
    private GoogleMap mMap;

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

//        Logger.e(Date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_view_map);

        mapFragment.getMapAsync(this);

        mTvCurrDate = findViewById(R.id.tvCurrDate);
        mTvEditDailog = findViewById(R.id.tvEditDailog);

        mCvShare = findViewById(R.id.cvShare);
        mCvShare.setOnClickListener(this);
        mIvClosed = findViewById(R.id.ivClosed);
        mIvClosed.setOnClickListener(this);
        mTvCurrDate.setText("Today " + Date + " " + formation);
        mTvEditDailog.setOnClickListener(this);

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


    }

    private void getDataFromDatabase() {
        gpsTrackerModelArrayList = dbManager.getGpsTrackerlist();
        if (gpsTrackerModelArrayList != null) {
            for (int i = 0; i < gpsTrackerModelArrayList.size(); i++) {
                TargetType = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getType();
                numStep = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getStep();
                Calories = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getCalories();
                Distance = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getDistance();
                Duration = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getDuration();
                sLatetitue = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getSlatitude();
                sLongtitude = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getSlogtitude();
                Elatitude = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getElatitude();
                Elongtitude = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getElongtitude();
            }
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
                TrainingActivity.isGPSFinish = true;
                startActivity(new Intent(this, TrainingActivity.class));
                break;
            case R.id.cvShare:
                startActivity(new Intent(this, ShareGPSActivity.class));
                break;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Logger.e(Double.parseDouble(sLatetitue) + "-" + Double.parseDouble(sLongtitude) + "-" + Double.parseDouble(Elatitude) + "-" + Double.parseDouble(Elongtitude));

        mMap = googleMap;
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(new LatLng(Double.parseDouble(sLatetitue), Double.parseDouble(sLongtitude)), new LatLng(Double.parseDouble(Elatitude), Double.parseDouble(Elongtitude)))
                .width(5).color(Color.BLUE);
        mMap.addPolyline(polylineOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(sLatetitue), Double.parseDouble(sLongtitude)), 18));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Position");
        LatLng latLng = new LatLng(Double.parseDouble(sLatetitue), Double.parseDouble(sLongtitude));
        markerOptions.position(latLng);

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, TrainingActivity.class));
    }
}