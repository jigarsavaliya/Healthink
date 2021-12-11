package com.android.stepcounter.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.MyMarkerView;
import com.android.stepcounter.R;
import com.android.stepcounter.adpter.StepWeekChartAdapter;
import com.android.stepcounter.adpter.WaterWeekChartAdapter;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.WeightModel;
import com.android.stepcounter.model.stepcountModel;
import com.android.stepcounter.model.waterlevel;
import com.android.stepcounter.sevices.SensorService;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.commanMethod;
import com.android.stepcounter.utils.constant;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DatePickerListener {

    private TextView TvSteps, accuracyText, tvduration, tvkm, tvkcal, tvuserWeight, tvwatergoal, tvwaterlevel, mtvlastdaydiffvalue;
    private int numSteps;
    Toolbar mToolbar;
    ImageView ivPlay, ivPause;
    BottomNavigationView mbottomNavigation;
    private CircularProgressBar progress, sunday, monday, tuesday, wednesday, thrusday, friday, saturday,
            watersunday, watermonday, watertuesday, waterwednesday, waterthrusday, waterfriday, watersaturday;
    CardView mcvStrat, mcvWater, mcvWeight;
    private float userWeight = constant.DEFAULT_WEIGHT;
    private float userHeight = constant.DEFAULT_HEIGHT;
    int StepGoal;
    String Watergoal, WaterUnit, Watercup;
    String distance, calories;
    LinearLayout llwaterSetting;
    ImageView ivaddwater;
    MyReceiver myReceiver;
    DBHandler dbManager;
    int temp = 0;
    int oldsteptotal = 0;
    ArrayList<stepcountModel> Steplist;
    ArrayList<stepcountModel> getoldSteplist;
    ArrayList<waterlevel> waterlist;
    stepcountModel stepcountModel;
    CircleProgress Watercircle_progress;
    TextView addWeightDailog;
    LineChart WeightChart;
    waterlevel waterlevel;

    Calendar rightNow;
    int hour, min, date, month, year;
    String[] DefultCupValue, WaterGoalValue;
    final float[] value = {0};
    int waterml = 0;

    String selectedDate;
    String seletedMonth;
    String selecetedYear;

    EditText etweight;
    double covertinml;
    ArrayList<WeightModel> modelArrayList = new ArrayList<>();
    ArrayList<WeightModel> arrayList = new ArrayList<>();
    ArrayList<Entry> weightModelArrayList = new ArrayList<>();
    ArrayList<WeightModel> waterlevelArrayList = new ArrayList<>();
    RecyclerView rvSteplist, rvWaterlist;
    StepWeekChartAdapter stepWeekChartAdapter;
    WaterWeekChartAdapter waterWeekChartAdapter;

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("GET_SIGNAL_STRENGTH")) {
                int level = intent.getIntExtra("stepdata", 0);
                Log.e("TAG", "onReceive: main " + level);
                numSteps = level;
                TvSteps.setText(numSteps + "");
                progress.setProgressWithAnimation(numSteps, (long) 1000);

                distance = String.valueOf(commanMethod.calculateDistance(numSteps, userHeight));
//                tvkm.setText(distance);
                tvkm.setText(String.format("%.2f", Float.valueOf(distance)));

                calories = String.valueOf(commanMethod.calculateCalories(numSteps, userWeight, userHeight));
                tvkcal.setText(calories);
            }

            setRecyclerView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(MainActivity.this, SensorService.class);
        startService(i);
        Steplist = new ArrayList<stepcountModel>();
        getoldSteplist = new ArrayList<stepcountModel>();
        dbManager = new DBHandler(this);

        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, new IntentFilter("GET_SIGNAL_STRENGTH"));

    }

    @SuppressLint("DefaultLocale")
    private void init() {

        rightNow = Calendar.getInstance();
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        min = rightNow.get(Calendar.MINUTE);
        date = rightNow.get(Calendar.DATE);
        month = rightNow.get(Calendar.MONTH) + 1;
        year = rightNow.get(Calendar.YEAR);

        selectedDate = String.valueOf(date);
        seletedMonth = String.valueOf(month);
        selecetedYear = String.valueOf(year);

        DefultCupValue = Watercup.split(" ");
        WaterGoalValue = Watergoal.split(" ");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Health Tracker");
        setSupportActionBar(mToolbar);

        mcvWater = findViewById(R.id.cvWater);

        rvSteplist = findViewById(R.id.rvStepChart);
        rvWaterlist = findViewById(R.id.rvWaterChart);

        setRecyclerView();

        progress = findViewById(R.id.progressBar);
        WeightChart = findViewById(R.id.WeightChart);

        addWeightDailog = findViewById(R.id.addWeight);
        ivaddwater = findViewById(R.id.ivaddwater);
        llwaterSetting = findViewById(R.id.llwaterSetting);
        TvSteps = (TextView) findViewById(R.id.tv_steps);
        accuracyText = (TextView) findViewById(R.id.tv_accuracy);
        tvduration = (TextView) findViewById(R.id.tvduration);
        tvkm = (TextView) findViewById(R.id.tvkm);
        tvkcal = (TextView) findViewById(R.id.tvkcal);
        tvuserWeight = (TextView) findViewById(R.id.userWeight);
        mtvlastdaydiffvalue = (TextView) findViewById(R.id.tvlastdaydiffvalue);

        Watercircle_progress = (CircleProgress) findViewById(R.id.circle_progress);
        tvwatergoal = (TextView) findViewById(R.id.tvwatergoal);
        tvwaterlevel = (TextView) findViewById(R.id.tvwaterlevel);

        tvuserWeight.setText(userWeight + "");

        ivPlay = findViewById(R.id.ivPlay);
        ivPause = findViewById(R.id.ivPause);
        mbottomNavigation = findViewById(R.id.bottomNavigation);

        mcvStrat = findViewById(R.id.cvGPSStrat);
        mcvWeight = findViewById(R.id.cvWeight);

        ivPlay.setOnClickListener(this);
        ivPause.setOnClickListener(this);
        mcvStrat.setOnClickListener(this);
        mcvWater.setOnClickListener(this);
        mcvWeight.setOnClickListener(this);
        ivaddwater.setOnClickListener(this);
        llwaterSetting.setOnClickListener(this);
        addWeightDailog.setOnClickListener(this);

        ivPause.setVisibility(View.VISIBLE);
        ivPlay.setVisibility(View.GONE);

        mbottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.itemtoday:
//                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        return true;
                    case R.id.itemHeath:
                        startActivity(new Intent(MainActivity.this, HeathActivity.class));
                        return true;
                    case R.id.itemReport:
                        startActivity(new Intent(MainActivity.this, StepReportActivity.class));
                        return true;
                    case R.id.itemmore:

                        return true;
                }
                return false;
            }
        });

        int TotalStepCount = dbManager.getSumOfStepList(date, month, year);
        TvSteps.setText(TotalStepCount + "");

        progress.setProgressWithAnimation(TotalStepCount, (long) 1000);

        distance = String.valueOf(commanMethod.calculateDistance(TotalStepCount, userHeight));
        tvkm.setText(String.format("%.2f", Float.valueOf(distance)));

        calories = String.valueOf(commanMethod.calculateCalories(TotalStepCount, userWeight, userHeight));
        tvkcal.setText(calories);


        getoldSteplist = dbManager.getCurrentDayHoursStepcountlist(date, month, year, hour);
        if (getoldSteplist != null) {
            for (int i = 0; i < getoldSteplist.size(); i++) {
                oldsteptotal = getoldSteplist.get(i).getStep();
//                Log.e("TAG", "date: " + oldsteptotal);
            }
        } else {
            oldsteptotal = 0;
//            Log.e("TAG", "date: " + oldsteptotal);
        }

        setdatainprogress();

        tvwatergoal.setText("/" + StorageManager.getInstance().getWaterGoal());

        String ts = String.valueOf(System.currentTimeMillis());
        WeightModel weightModel = new WeightModel();
        weightModel.setDate(date);
        weightModel.setMonth(month);
        weightModel.setYear(year);
        weightModel.setTimestemp(ts);
        weightModel.setKg((int) userWeight);
        dbManager.addWeightData(weightModel);

        setWeightChart();
    }

    private void setRecyclerView() {
        ArrayList<stepcountModel> stepcountModelArrayList = new ArrayList<>();
        ArrayList<waterlevel> DaywiseWaterlist = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, rightNow.get(Calendar.DATE) - 6);

        Log.e("TAG", calendar.getTimeInMillis() + "setRecyclerView: " + rightNow.getTimeInMillis());

        String fristdate = String.valueOf(calendar.getTimeInMillis());
        String lastdate = String.valueOf(rightNow.getTimeInMillis());

        stepcountModelArrayList = dbManager.getweekstepdata(fristdate, lastdate);

        stepWeekChartAdapter = new StepWeekChartAdapter(this, stepcountModelArrayList);
        rvSteplist.setHasFixedSize(true);
        rvSteplist.setLayoutManager(new GridLayoutManager(this, 7));
        rvSteplist.setAdapter(stepWeekChartAdapter);

        DaywiseWaterlist = dbManager.getweekWaterdata(fristdate, lastdate);

        waterWeekChartAdapter = new WaterWeekChartAdapter(this, DaywiseWaterlist);
        rvWaterlist.setHasFixedSize(true);
        rvWaterlist.setLayoutManager(new GridLayoutManager(this, 7));
        rvWaterlist.setAdapter(waterWeekChartAdapter);
    }

    private void setWeightChart() {
        Calendar cal = Calendar.getInstance();
        long fristdate = cal.getTimeInMillis();
        cal.add(Calendar.DATE, -30);
        long last30day = cal.getTimeInMillis();

        modelArrayList = dbManager.getCurrentDayWeightlist(date, month, year);

        if (modelArrayList.size() != 0) {
            for (int i = 0; i < modelArrayList.size(); i++) {
                tvuserWeight.setText("" + modelArrayList.get(i).getKg());
            }
        } else {
            tvuserWeight.setText("" + 0);
        }

        int pervalue = 0;
        arrayList = dbManager.getCurrentDayWeightlist(date - 1, month, year);

        /*if (arrayList.size() != 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                pervalue = arrayList.get(i).getKg();
            }
        }*/

        int cuurrvalue = Integer.parseInt(tvuserWeight.getText().toString());
        int diff = 0;
        diff = cuurrvalue - pervalue;

//        Log.e("TAG", pervalue + "setWeightChart: " + cuurrvalue);
        if (diff < 0) {
            mtvlastdaydiffvalue.setText(diff + "KG");
        } else {
            mtvlastdaydiffvalue.setText("0 KG");
        }

        WeightChart.setBackgroundColor(Color.WHITE);

        // disable description text
        WeightChart.getDescription().setEnabled(false);

        // enable touch gestures
        WeightChart.setTouchEnabled(true);

        // set listeners
        WeightChart.setDrawGridBackground(false);

        // create marker to display box when values are selected
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(WeightChart);
        WeightChart.setMarker(mv);
        WeightChart.setDragEnabled(true);
        WeightChart.setScaleEnabled(true);
        WeightChart.setPinchZoom(false);

        WeightChart.getAxisLeft().setDrawGridLines(false);
        WeightChart.getAxisRight().setDrawGridLines(false);
        WeightChart.getXAxis().setDrawGridLines(false);

        XAxis xAxis;
        xAxis = WeightChart.getXAxis();

        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        // axis range
        xAxis.setAxisMaximum(cal.get(Calendar.DAY_OF_MONTH));
        xAxis.setAxisMinimum(cal.get(Calendar.DAY_OF_MONTH) - 30);

        YAxis yAxis;
        yAxis = WeightChart.getAxisLeft();

        // disable dual axis (only use LEFT axis)
        WeightChart.getAxisRight().setEnabled(false);

        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f);

        // axis range
        yAxis.setAxisMaximum(200f);
        yAxis.setAxisMinimum(0);

        waterlevelArrayList = dbManager.getMonthWeightdata(String.valueOf(last30day), String.valueOf(fristdate));

        for (int i = 0; i < waterlevelArrayList.size(); i++) {
//            Log.e("TAG", waterlevelArrayList.get(i).getKg() + "setWeightChart: " + waterlevelArrayList.get(i).getDate());
            weightModelArrayList.add(new Entry(waterlevelArrayList.get(i).getDate(), waterlevelArrayList.get(i).getKg(), getResources().getDrawable(R.drawable.star)));
        }

        LineDataSet set1;

        if (WeightChart.getData() != null && WeightChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) WeightChart.getData().getDataSetByIndex(0);
            set1.setValues(weightModelArrayList);
            set1.notifyDataSetChanged();
            WeightChart.getData().notifyDataChanged();
            WeightChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(weightModelArrayList, "");

            set1.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return WeightChart.getAxisLeft().getAxisMinimum();
                }
            });

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            WeightChart.setData(data);
        }

        // draw points over time
        WeightChart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = WeightChart.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
    }


    private void setSharedPreferences() {
        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();
        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
        Watergoal = StorageManager.getInstance().getWaterGoal();
        Watercup = StorageManager.getInstance().getWaterCup();
        WaterUnit = StorageManager.getInstance().getWaterUnit();
        Log.e("setSharedPreferences", "setSharedPreferences: " + WaterUnit);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(MainActivity.this, SensorService.class);
        switch (v.getId()) {
            case R.id.ivPlay:
                numSteps = 0;
//                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                startService(i);
                ivPlay.setVisibility(View.GONE);
                ivPause.setVisibility(View.VISIBLE);
                break;
            case R.id.ivPause:
//                sensorManager.unregisterListener(MainActivity.this);
                stopService(i);
                ivPlay.setVisibility(View.VISIBLE);
                ivPause.setVisibility(View.GONE);
                break;
            case R.id.cvGPSStrat:
                startActivity(new Intent(MainActivity.this, TrainingActivity.class));
                break;
            case R.id.cvWater:
                startActivity(new Intent(MainActivity.this, HeathActivity.class));
                break;
            case R.id.cvWeight:
                startActivity(new Intent(MainActivity.this, HeathActivity.class));
                break;
            case R.id.ivaddwater:
                String ts = String.valueOf(System.currentTimeMillis());

                waterlevel = new waterlevel();
                waterlevel.setDate(date);
                waterlevel.setMonth(month);
                waterlevel.setYear(year);
                waterlevel.setHour(hour);
                waterlevel.setMin(min);
                waterlevel.setTimestemp(ts);

                float[] value = {0};

                if (DefultCupValue[1].contains("fl")) {
                    value[0] = value[0] + Float.parseFloat(DefultCupValue[0]);
                    covertinml = commanMethod.getFlozToMl(value[0]);
                    waterlevel.setUnit(String.valueOf(covertinml));
                } else {
                    value[0] = value[0] + Integer.parseInt(DefultCupValue[0]);
                    waterlevel.setUnit(String.valueOf(value[0]));
                }

                dbManager.addWaterData(waterlevel);

                setdatainprogress();
                break;
            case R.id.llwaterSetting:
                startActivity(new Intent(MainActivity.this, WaterSettingActivity.class));
                break;
            case R.id.addWeight:
                showAddWeightDailog();
                break;
        }
    }

    private void setdatainprogress() {
        ArrayList<waterlevel> waterlevelArrayList = new ArrayList<>();

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
                        Watercircle_progress.setProgress(lastentry);
                    } else {
                        Watercircle_progress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                    }
                }
                tvwaterlevel.setText(lastentry + "");
            } else {
                Watercircle_progress.setProgress(0);
                tvwaterlevel.setText(0 + "");
            }
        } else {
            double lastentry = 0;
            if (waterlevelArrayList != null) {
                for (int i = 0; i < waterlevelArrayList.size(); i++) {
//                Log.e("TAG", "init: " + waterlevelArrayList.get(i).getSumwater());
                    lastentry = Math.round(commanMethod.getMlToFloz(Float.valueOf(waterlevelArrayList.get(i).getSumwater())));
                }

                if (lastentry != 0) {
                    if (lastentry < Integer.parseInt(WaterGoalValue[0])) {
//                    Log.e("TAG", "init: " + lastentry);
                        Watercircle_progress.setProgress((int) lastentry);
                    } else {
                        Watercircle_progress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                    }
                }
                tvwaterlevel.setText(lastentry + "");
            } else {
                Watercircle_progress.setProgress(0);
                tvwaterlevel.setText(0 + "");
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showAddWeightDailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_add_weight, null);
        dialogBuilder.setView(d);
        AlertDialog alertDialog = dialogBuilder.create();
        Button mBtnSave = (Button) d.findViewById(R.id.btnSave);
        Button mBtnCancel = (Button) d.findViewById(R.id.btnCancel);

        CardView mllLb = (CardView) d.findViewById(R.id.llLb);
        CardView mllKB = (CardView) d.findViewById(R.id.llKB);
        etweight = (EditText) d.findViewById(R.id.etweight);

        final boolean[] iskg = {true};

        mllKB.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
        etweight.setText("" + userWeight);

        mllLb.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                iskg[0] = false;
                mllLb.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                mllKB.setCardBackgroundColor(getResources().getColor(R.color.transprant));
                etweight.setText(Math.round(commanMethod.kgToLbConverter(Double.parseDouble(etweight.getText().toString()))) + "");
            }
        });

        mllKB.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                iskg[0] = true;
                mllKB.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                mllLb.setCardBackgroundColor(getResources().getColor(R.color.transprant));
                etweight.setText(Math.round(commanMethod.lbToKgConverter(Double.parseDouble(etweight.getText().toString()))) + "");
            }
        });

        HorizontalPicker picker = (HorizontalPicker) d.findViewById(R.id.datePicker);

        picker.setListener(MainActivity.this)
                .setDays(120)
                .setOffset(7)
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(Color.DKGRAY)
                .setTodayButtonTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .setTodayDateTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .setTodayDateBackgroundColor(Color.GRAY)
                .setUnselectedDayTextColor(Color.DKGRAY)
                .setDayOfWeekTextColor(Color.DKGRAY)
                .setUnselectedDayTextColor(getResources().getColor(R.color.primaryTextColor))
                .showTodayButton(false)
                .init();
        picker.setBackgroundColor(Color.LTGRAY);
        picker.setDate(new DateTime());

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                c.set(Calendar.DATE, Integer.parseInt(selectedDate));
                c.set(Calendar.MONTH, Integer.parseInt(seletedMonth) - 1);
                c.set(Calendar.YEAR, Integer.parseInt(selecetedYear));
                c.set(Calendar.HOUR, c.get(Calendar.HOUR));

                s.format(new Date(c.getTimeInMillis()));
                Log.e("TAG", "onClick: " + c.getTimeInMillis());

                WeightModel weightModel = new WeightModel();
                weightModel.setDate(Integer.parseInt(selectedDate));
                weightModel.setMonth(Integer.parseInt(seletedMonth));
                weightModel.setYear(Integer.parseInt(selecetedYear));
                weightModel.setTimestemp(String.valueOf(c.getTimeInMillis()));
                if (iskg[0]) {
                    weightModel.setKg(Integer.parseInt(etweight.getText().toString()));
                } else {
                    weightModel.setKg((int) Math.round(commanMethod.lbToKgConverter(Double.parseDouble(etweight.getText().toString()))));
                }
                dbManager.addWeightData(weightModel);
                if (Integer.parseInt(selectedDate) == Calendar.DATE) {
                    StorageManager.getInstance().setWeight(Float.parseFloat(etweight.getText().toString()));
                }
                setWeightChart();
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

    @Override
    public void onDateSelected(DateTime dateSelected) {
        Log.e("HorizontalPicker", "Fecha seleccionada=" + dateSelected.toString());

        String date = dateSelected.toString("yyyy-MM-dd");
        String[] newdate = date.split("-");
        Log.e("HorizontalPicker", "Fecha seleccionada=" + date);
        Log.e("HorizontalPicker", "Fecha seleccionada=" + newdate);
        selectedDate = newdate[2];
        selecetedYear = newdate[0];
        seletedMonth = newdate[1];
        Log.e("HorizontalPicker", "Fecha seleccionada=" + seletedMonth);
        ArrayList<WeightModel> modelArrayList = new ArrayList<>();

        modelArrayList = dbManager.getCurrentDayWeightlist(Integer.parseInt(selectedDate), Integer.parseInt(seletedMonth), Integer.parseInt(selecetedYear));
        if (modelArrayList != null) {
            for (int i = 0; i < modelArrayList.size(); i++) {
                etweight.setText("" + modelArrayList.get(i).getKg());
            }
        } else {
            etweight.setText("" + 0);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                showEditStepDailog();
                break;
            case R.id.action_reset:
                showResetdataDailog();
                break;
            case R.id.action_history:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.action_instruction:
                Toast.makeText(MainActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_trunoff:
                Toast.makeText(MainActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSharedPreferences();
        init();
    }

    private void showResetdataDailog() {
        Calendar rightNow = Calendar.getInstance();
        int date = rightNow.get(Calendar.DATE);
        int month = rightNow.get(Calendar.MONTH) + 1;
        int year = rightNow.get(Calendar.YEAR);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Reset today's steps?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "RESET",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        dbManager.DeleteCurrentDayStepCountData(date, month, year);
//                        dbManager.DeleteCurrentDayWaterData(date, month, year);
                        init();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void showEditStepDailog() {
        stepcountModel = new stepcountModel();

        String[] Time = {"00:00 - 01:00", "01:00 - 02:00", "02:00 - 03:00", "03:00 - 04:00", "04:00 - 05:00", "05:00 - 06:00", "06:00 - 07:00", "07:00 - 08:00",
                "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00",
                "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00", "20:00 - 21:00", "21:00 - 22:00", "22:00 - 23:00", "23:00 - 24:00"};

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_edit, null);
        dialogBuilder.setView(d);

        AlertDialog alertDialog = dialogBuilder.create();
        Button mBtnSave = (Button) d.findViewById(R.id.btnSave);
        Button mBtnCancel = (Button) d.findViewById(R.id.btnCancel);
        LinearLayout llView = (LinearLayout) d.findViewById(R.id.datepicker);
        Spinner spinner = (Spinner) d.findViewById(R.id.spinner);
        EditText stepvalue = (EditText) d.findViewById(R.id.stepvalue);
        TextView tvdate = (TextView) d.findViewById(R.id.tvdate);

        Calendar rightNow = Calendar.getInstance();
        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
        int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
        int mMonth = rightNow.get(Calendar.MONTH) + 1;
        int mYear = rightNow.get(Calendar.YEAR);

        final int[] saveHour = {hours};
        final int[] selectedyear = {mYear};
        final int[] selectedmonth = {mMonth};
        final int[] selectedday = {dayOfMonth};

        ArrayList<String> stringArrayList = new ArrayList<>();
        if (selectedday[0] != dayOfMonth && selectedmonth[0] != mMonth && selectedyear[0] != mYear) {
            for (int i = 0; i < 24; i++) {
                stringArrayList.add(Time[i]);
//            Log.e("TAG", "showEditStepDailog: " + Time[i]);
            }
        } else {
            for (int i = 0; i < hours + 1; i++) {
                stringArrayList.add(Time[i]);
//            Log.e("TAG", "showEditStepDailog: " + Time[i]);
            }
        }

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stringArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(arrayAdapter);


        llView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                selectedyear[0] = year;
                                selectedmonth[0] = month;
                                selectedday[0] = day;
                                tvdate.setText(selectedday[0] + " - " + selectedmonth[0]);

                                if (selectedday[0] != dayOfMonth) {
                                    for (int i = 0; i < 24; i++) {
                                        stringArrayList.add(Time[i]);
                                    }
                                } else {
                                    for (int i = 0; i < hours + 1; i++) {
                                        stringArrayList.add(Time[i]);
                                    }
                                }
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }, mYear, mMonth - 1, dayOfMonth);
                datePickerDialog.show();
            }
        });

        spinner.setSelection(stringArrayList.size() - 1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (item.equals("00:00 - 01:00")) {
                    saveHour[0] = 0;
                } else if (item.equals("01:00 - 02:00")) {
                    saveHour[0] = 1;
                } else if (item.equals("02:00 - 03:00")) {
                    saveHour[0] = 2;
                } else if (item.equals("03:00 - 04:00")) {
                    saveHour[0] = 3;
                } else if (item.equals("04:00 - 05:00")) {
                    saveHour[0] = 4;
                } else if (item.equals("05:00 - 06:00")) {
                    saveHour[0] = 5;
                } else if (item.equals("06:00 - 07:00")) {
                    saveHour[0] = 6;
                } else if (item.equals("07:00 - 08:00")) {
                    saveHour[0] = 7;
                } else if (item.equals("08:00 - 09:00")) {
                    saveHour[0] = 8;
                } else if (item.equals("09:00 - 10:00")) {
                    saveHour[0] = 9;
                } else if (item.equals("10:00 - 11:00")) {
                    saveHour[0] = 10;
                } else if (item.equals("11:00 - 12:00")) {
                    saveHour[0] = 11;
                } else if (item.equals("12:00 - 13:00")) {
                    saveHour[0] = 12;
                } else if (item.equals("13:00 - 14:00")) {
                    saveHour[0] = 13;
                } else if (item.equals("14:00 - 15:00")) {
                    saveHour[0] = 14;
                } else if (item.equals("15:00 - 16:00")) {
                    saveHour[0] = 15;
                } else if (item.equals("16:00 - 17:00")) {
                    saveHour[0] = 16;
                } else if (item.equals("17:00 - 18:00")) {
                    saveHour[0] = 17;
                } else if (item.equals("18:00 - 19:00")) {
                    saveHour[0] = 18;
                } else if (item.equals("19:00 - 20:00")) {
                    saveHour[0] = 19;
                } else if (item.equals("20:00 - 21:00")) {
                    saveHour[0] = 20;
                } else if (item.equals("21:00 - 22:00")) {
                    saveHour[0] = 21;
                } else if (item.equals("22:00 - 23:00")) {
                    saveHour[0] = 22;
                } else if (item.equals("23:00 - 24:00")) {
                    saveHour[0] = 23;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numSteps = stepvalue.getText().toString();
                if (numSteps.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter number of steps..!!", Toast.LENGTH_LONG).show();
                } else {
                    String Calories = String.valueOf(commanMethod.calculateCalories(Integer.parseInt(numSteps), userWeight, userHeight));
                    String Distance = String.valueOf(commanMethod.calculateDistance(Integer.parseInt(numSteps), userHeight));

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                    c.set(Calendar.DATE, selectedday[0]);
                    c.set(Calendar.MONTH, selectedmonth[0]);
                    c.set(Calendar.YEAR, selectedyear[0]);
                    c.set(Calendar.HOUR, saveHour[0]);

                    s.format(new Date(c.getTimeInMillis()));

                    Log.e("Start", "Start Date = " + selectedday[0]);
                    Log.e("Start", "Start Date = " + selectedmonth[0]);
                    Log.e("Start", "Start Date = " + selectedyear[0]);
                    Log.e("Start", "Start Date = " + c.getTimeInMillis());

                    stepcountModel.setStep(Integer.valueOf(numSteps));
                    stepcountModel.setDate(selectedday[0]);
                    stepcountModel.setMonth(selectedmonth[0] + 1);
                    stepcountModel.setYear(selectedyear[0]);
                    stepcountModel.setDistance(Distance);
                    stepcountModel.setCalorie(Calories);
                    stepcountModel.setDuration(saveHour[0]);
                    stepcountModel.setTimestemp(String.valueOf(c.getTimeInMillis()));
                    dbManager.addStepcountData(stepcountModel);
                }
                onResume();
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
}