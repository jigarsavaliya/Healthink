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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.MyMarkerView;
import com.android.stepcounter.R;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    ArrayList<Integer> DaywiseSteplist;
    ArrayList<Float> DaywiseWaterlist;
    CircleProgress Watercircle_progress;
    TextView addWeightDailog;
    LineChart WeightChart;
    waterlevel waterlevel;

    Calendar rightNow;
    int hour, min, date, month, year;
    String[] DefultCupValue, WaterGoalValue;
    final float[] value = {0};
    int waterml = 0;

    String selectedDate, seletedMonth, selecetedYear;

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
                tvkm.setText(distance);

                calories = String.valueOf(commanMethod.calculateCalories(numSteps, userWeight, userHeight));
                tvkcal.setText(calories);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setDailyGoal();
            }
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
        DaywiseSteplist = new ArrayList<Integer>();
        DaywiseWaterlist = new ArrayList<Float>();
        dbManager = new DBHandler(this);

        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, new IntentFilter("GET_SIGNAL_STRENGTH"));

    }

    private void init() {

        rightNow = Calendar.getInstance();
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        min = rightNow.get(Calendar.MINUTE);
        date = rightNow.get(Calendar.DATE);
        month = rightNow.get(Calendar.MONTH) + 1;
        year = rightNow.get(Calendar.YEAR);

        DefultCupValue = Watercup.split(" ");
        WaterGoalValue = Watergoal.split(" ");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Health Tracker");
        setSupportActionBar(mToolbar);

        mcvWater = findViewById(R.id.cvWater);

        progress = findViewById(R.id.progressBar);
        WeightChart = findViewById(R.id.WeightChart);
        sunday = findViewById(R.id.sunday);
        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thrusday = findViewById(R.id.thrusday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);

        sunday.setProgressMax(StepGoal);
        monday.setProgressMax(StepGoal);
        tuesday.setProgressMax(StepGoal);
        wednesday.setProgressMax(StepGoal);
        thrusday.setProgressMax(StepGoal);
        friday.setProgressMax(StepGoal);
        saturday.setProgressMax(StepGoal);

        //waterChart
        watersunday = findViewById(R.id.waterSunday);
        watermonday = findViewById(R.id.waterMonday);
        watertuesday = findViewById(R.id.waterTuesday);
        waterwednesday = findViewById(R.id.waterWednesday);
        waterthrusday = findViewById(R.id.waterThursday);
        waterfriday = findViewById(R.id.waterFriday);
        watersaturday = findViewById(R.id.waterSaturday);

        watersunday.setProgressMax(Float.parseFloat(WaterGoalValue[0]));
        watermonday.setProgressMax(Float.parseFloat(WaterGoalValue[0]));
        watertuesday.setProgressMax(Float.parseFloat(WaterGoalValue[0]));
        waterwednesday.setProgressMax(Float.parseFloat(WaterGoalValue[0]));
        waterthrusday.setProgressMax(Float.parseFloat(WaterGoalValue[0]));
        waterfriday.setProgressMax(Float.parseFloat(WaterGoalValue[0]));
        watersaturday.setProgressMax(Float.parseFloat(WaterGoalValue[0]));

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
        tvkm.setText(distance);

        calories = String.valueOf(commanMethod.calculateCalories(TotalStepCount, userWeight, userHeight));
        tvkcal.setText(calories);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setDailyGoal();
        }

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

        waterlist = dbManager.getCurrentDayWatercountlist(date, month, year);

        Watercircle_progress.setMax(Integer.parseInt(WaterGoalValue[0]));


        if (waterlist != null) {
            for (int i = 0; i < waterlist.size(); i++) {
                waterml = (int) Float.parseFloat(waterlist.get(i).getUnit());
                Log.e("TAG", "date: " + waterml);
            }
        } else {
            waterml = 0;
//            Log.e("TAG", "date: " + waterml);
        }

        if (WaterUnit.contains("ml")) {
            if (waterml == 0.0) {
                Watercircle_progress.setProgress(0);
                tvwaterlevel.setText(0 + "");
            } else {
                if (waterml < Integer.parseInt(WaterGoalValue[0])) {
                    Watercircle_progress.setProgress(waterml);
                    tvwaterlevel.setText(waterml + "");
                } else {
                    Watercircle_progress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                    tvwaterlevel.setText(Integer.parseInt(WaterGoalValue[0]) + "");
                }

            }
        } else {
            if (waterml == 0.0) {
                Watercircle_progress.setProgress(0);
                tvwaterlevel.setText(0 + "");
            } else {
                double watermlvalue = commanMethod.getMlToFloz(Float.valueOf(waterml));
                if (watermlvalue < Integer.parseInt(WaterGoalValue[0])) {
                    Watercircle_progress.setProgress((int) Math.round(watermlvalue));
                    tvwaterlevel.setText((int) Math.round(watermlvalue) + "");
                } else {
                    Watercircle_progress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                    tvwaterlevel.setText(Integer.parseInt(WaterGoalValue[0]) + "");
                }

            }
        }
        tvwatergoal.setText("/" + StorageManager.getInstance().getWaterGoal());

        WeightModel weightModel = new WeightModel();
        weightModel.setDate(date);
        weightModel.setMonth(month);
        weightModel.setYear(year);
        weightModel.setKg((int) userWeight);
        dbManager.addWeightData(weightModel);

        setWeightChart();
    }

    private void setWeightChart() {
        ArrayList<WeightModel> weightModels = new ArrayList<>();
        ArrayList<WeightModel> modelArrayList = new ArrayList<>();
        ArrayList<Entry> weightModelArrayList = new ArrayList<>();

        weightModels = dbManager.getWeightlist();

        modelArrayList = dbManager.getCurrentDayWeightlist(date, month, year);
        if (modelArrayList.size() != 0) {
            for (int i = 0; i < modelArrayList.size(); i++) {
                tvuserWeight.setText("" + modelArrayList.get(i).getKg());
            }
        } else {
            tvuserWeight.setText("" + 0);
        }
        int cuurrvalue = Integer.parseInt(tvuserWeight.getText().toString());
        int diff = cuurrvalue - weightModels.get(weightModels.size() - 1).getKg();
        mtvlastdaydiffvalue.setText(diff + "KG");

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
        WeightChart.setPinchZoom(true);

        XAxis xAxis;
        xAxis = WeightChart.getXAxis();

        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        // axis range
        xAxis.setAxisMaximum(31f);
        xAxis.setAxisMinimum(0);

        YAxis yAxis;
        yAxis = WeightChart.getAxisLeft();

        // disable dual axis (only use LEFT axis)
        WeightChart.getAxisRight().setEnabled(false);

        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f);

        // axis range
        yAxis.setAxisMaximum(200f);
        yAxis.setAxisMinimum(0);

        for (int i = 0; i < weightModels.size(); i++) {
            weightModelArrayList.add(new Entry(weightModels.get(i).getDate(), weightModels.get(i).getKg(), getResources().getDrawable(R.drawable.star)));
        }

     /*   ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) - 30;
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
        }*/

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

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDailyGoal() {

        String s1 = getCurrentDateWeek(rightNow);

        String[] strings = s1.split("-");
        String date1 = strings[0];
        String date2 = strings[1];

        /*String s = getCurrentWeek(rightNow);
//        Log.e("TAG", "date: " + s);
        String[] Weekdate = s.split("-");
        int stepnumber;
        String fristdate = Weekdate[0];
        String lastdate = Weekdate[1];

        ArrayList<stepcountModel> stepcountModelArrayList = new ArrayList<>();

        stepcountModelArrayList = dbManager.getSumOfStepList(fristdate, lastdate);

        for (int i = 0; i < 7; i++) {
//            Log.e("TAG", "date: " + i + " " + DaywiseSteplist.get(i));
            monday.setProgress(stepcountModelArrayList.get(0).getSumstep() != 0 ? stepcountModelArrayList.get(0).getSumstep() : 0);
            tuesday.setProgress(stepcountModelArrayList.get(1).getSumstep() != 0 ? stepcountModelArrayList.get(1).getSumstep() : 0);
            wednesday.setProgress(stepcountModelArrayList.get(2).getSumstep() != 0 ? stepcountModelArrayList.get(2).getSumstep() : 0);
            thrusday.setProgress(stepcountModelArrayList.get(3).getSumstep() != 0 ? stepcountModelArrayList.get(3).getSumstep() : 0);
            friday.setProgress(stepcountModelArrayList.get(4).getSumstep() != 0 ? stepcountModelArrayList.get(4).getSumstep() : 0);
            saturday.setProgress(stepcountModelArrayList.get(5).getSumstep() != 0 ? stepcountModelArrayList.get(5).getSumstep() : 0);
            sunday.setProgress(stepcountModelArrayList.get(6).getSumstep() != 0 ? stepcountModelArrayList.get(6).getSumstep() : 0);
        }
*/
       /* //waterChart value get from database
        int DayWiseWater;
        for (int i = fristdate; i <= lastdate; i++) {
            DayWiseWater = dbManager.getSumOfWaterList(i, month, year);
//            Log.e("TAG", "date: " + i + " " + DayWiseWater);
            DaywiseWaterlist.add(Float.valueOf(DayWiseWater));
        }

        for (int i = 0; i < DaywiseWaterlist.size(); i++) {
//            Log.e("TAG", "date: " + i + " " + DaywiseSteplist.get(i));
            watermonday.setProgress(DaywiseWaterlist.get(0));
            watertuesday.setProgress(DaywiseWaterlist.get(1));
            waterwednesday.setProgress(DaywiseWaterlist.get(2));
            waterthrusday.setProgress(DaywiseWaterlist.get(3));
            waterfriday.setProgress(DaywiseWaterlist.get(4));
            watersaturday.setProgress(DaywiseWaterlist.get(5));
            watersunday.setProgress(DaywiseWaterlist.get(6));
        }*/

    }

    public static String getCurrentWeek(Calendar mCalendar) {
        Date date = new Date();
        mCalendar.setTime(date);
        int day_of_week = mCalendar.get(Calendar.DAY_OF_WEEK);
        int monday_offset;
        if (day_of_week == 1) {
            monday_offset = -6;
        } else
            monday_offset = (2 - day_of_week); // need to minus back
        mCalendar.add(Calendar.DAY_OF_YEAR, monday_offset);

        long mDateMonday = mCalendar.getTimeInMillis();
        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
        long mDateSunday = mCalendar.getTimeInMillis();
        String strDateFormat = "dd MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

        String MONDAY = sdf.format(mDateMonday);
        String SUNDAY = sdf.format(mDateSunday);

        if ((MONDAY.substring(3, 6)).equals(SUNDAY.substring(3, 6))) {
            MONDAY = MONDAY.substring(0, 2);
        }
        return mDateMonday + " - " + mDateSunday;
    }

    public static String getCurrentDateWeek(Calendar mCalendar) {
        Date date = new Date();
        mCalendar.setTime(date);

        // 1 = Sunday, 2 = Monday, etc.
        int day_of_week = mCalendar.get(Calendar.DAY_OF_WEEK);

        int monday_offset;
        if (day_of_week == 1) {
            monday_offset = -6;
        } else
            monday_offset = (2 - day_of_week); // need to minus back
        mCalendar.add(Calendar.DAY_OF_YEAR, monday_offset);

        Date mDateMonday = mCalendar.getTime();

        // return 6 the next days of current day (object cal save current day)
        mCalendar.add(Calendar.DAY_OF_YEAR, 6);

        Date mDateSunday = mCalendar.getTime();

        //Get format date
        String strDateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

        String MONDAY = sdf.format(mDateMonday);
        String SUNDAY = sdf.format(mDateSunday);

        Log.e("getCurrentDateWeek", "getCurrentDateWeek: " + MONDAY + "getCurrentDateWeek: " + SUNDAY);

        return MONDAY + " - " + SUNDAY;
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
                waterlist = dbManager.getCurrentDayWatercountlist(date, month, year);

                String[] WaterGoal = StorageManager.getInstance().getWaterGoal().split(" ");

                int waterml = 0;
                if (waterlist != null) {
                    for (int j = 0; j < waterlist.size(); j++) {
                        waterml = (int) Float.parseFloat(waterlist.get(j).getUnit());
//                Log.e("TAG", "date: " + waterml);
                    }
                } else {
                    waterml = 0;
                }
                String WaterUnit = StorageManager.getInstance().getWaterUnit();

                if (WaterUnit.contains("ml")) {
                    tvwaterlevel.setText(waterml + "");
                    if (waterml < Integer.parseInt(WaterGoalValue[0])) {
                        waterlevel = new waterlevel();
                        waterlevel.setDate(date);
                        waterlevel.setMonth(month);
                        waterlevel.setYear(year);
                        waterlevel.setHour(hour);
                        waterlevel.setMin(min);
                        double covertinml;
                        if (DefultCupValue[1].contains("fl")) {
                            value[0] = value[0] + Float.parseFloat(DefultCupValue[0]);
                            if (value[0] < Integer.parseInt(WaterGoalValue[0])) {
                                Watercircle_progress.setProgress((int) value[0]);
                                tvwaterlevel.setText(value[0] + "");
                            } else {
                                Watercircle_progress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                                tvwaterlevel.setText(Integer.parseInt(WaterGoalValue[0]) + "");
                            }
                            covertinml = commanMethod.getFlozToMl(value[0]);
                            waterlevel.setUnit(String.valueOf(covertinml));
                        } else {
                            value[0] = value[0] + Integer.parseInt(DefultCupValue[0]);
                            if (value[0] < Integer.parseInt(WaterGoalValue[0])) {
                                Watercircle_progress.setProgress((int) value[0]);
                                tvwaterlevel.setText(value[0] + "");
                            } else {
                                Watercircle_progress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                                tvwaterlevel.setText(Integer.parseInt(WaterGoalValue[0]) + "");
                            }
                            waterlevel.setUnit(String.valueOf(value[0]));
                        }
                        dbManager.addWaterData(waterlevel);
                    }
                } else {
                    double watermlnewvalue = commanMethod.getMlToFloz(Float.valueOf(waterml));
                    tvwaterlevel.setText(watermlnewvalue + "");
                    if (watermlnewvalue < Integer.parseInt(WaterGoalValue[0])) {
                        waterlevel = new waterlevel();
                        waterlevel.setDate(date);
                        waterlevel.setMonth(month);
                        waterlevel.setYear(year);
                        waterlevel.setHour(hour);
                        waterlevel.setMin(min);
                        double covertinml;
                        if (DefultCupValue[1].contains("fl")) {
                            value[0] = value[0] + Float.parseFloat(DefultCupValue[0]);
                            if (value[0] < Integer.parseInt(WaterGoalValue[0])) {
                                Watercircle_progress.setProgress((int) value[0]);
                                tvwaterlevel.setText(value[0] + "");
                            } else {
                                Watercircle_progress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                                tvwaterlevel.setText(Integer.parseInt(WaterGoalValue[0]) + "");
                            }
                            covertinml = commanMethod.getFlozToMl(value[0]);
                            waterlevel.setUnit(String.valueOf(covertinml));
                        } else {
                            value[0] = value[0] + Integer.parseInt(DefultCupValue[0]);
                            if (value[0] < Integer.parseInt(WaterGoalValue[0])) {
                                Watercircle_progress.setProgress((int) value[0]);
                                tvwaterlevel.setText(value[0] + "");
                            } else {
                                Watercircle_progress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                                tvwaterlevel.setText(Integer.parseInt(WaterGoalValue[0]) + "");
                            }
                            waterlevel.setUnit(String.valueOf(value[0]));
                        }
                        dbManager.addWaterData(waterlevel);
                    }
                }
                break;
            case R.id.llwaterSetting:
                startActivity(new Intent(MainActivity.this, WaterSettingActivity.class));
                break;
            case R.id.addWeight:
                showAddWeightDailog();
                break;
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
        EditText etweight = (EditText) d.findViewById(R.id.etweight);

        final boolean[] iskg = {true};

        mllKB.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));

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
                WeightModel weightModel = new WeightModel();
                weightModel.setDate(Integer.parseInt(selectedDate));
                weightModel.setMonth(Integer.parseInt(seletedMonth));
                weightModel.setYear(Integer.parseInt(selecetedYear));
                if (iskg[0]) {
                    weightModel.setKg(Integer.parseInt(etweight.getText().toString()));
                } else {
                    weightModel.setKg((int) Math.round(commanMethod.lbToKgConverter(Double.parseDouble(etweight.getText().toString()))));
                }
                dbManager.addWeightData(weightModel);
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
                        dbManager.DeleteCurrentDayWaterData(date, month, year);
                        onResume();
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

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_edit, null);
        dialogBuilder.setView(d);

        AlertDialog alertDialog = dialogBuilder.create();
        Button mBtnSave = (Button) d.findViewById(R.id.btnSave);
        Button mBtnCancel = (Button) d.findViewById(R.id.btnCancel);
        LinearLayout llView = (LinearLayout) d.findViewById(R.id.datepicker);
        LinearLayout llTime = (LinearLayout) d.findViewById(R.id.llTime);
        EditText stepvalue = (EditText) d.findViewById(R.id.stepvalue);
        TextView time = (TextView) d.findViewById(R.id.time);

        Calendar rightNow = Calendar.getInstance();
        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
        int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
        int mMonth = rightNow.get(Calendar.MONTH) + 1;
        int mYear = rightNow.get(Calendar.YEAR);

        final int[] saveHour = {hours};
        for (int i = 0; i < 24; i++) {
            if (hours == i) {
                int a = hours + 1;
                time.setText(hours + ":00 - " + a + ":00");
                saveHour[0] = hours;
            }
        }

        final int[] selectedyear = {mYear};
        final int[] selectedmonth = {mMonth};
        final int[] selectedday = {dayOfMonth};


        llView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                selectedyear[0] = year;
                                selectedmonth[0] = month;
                                selectedday[0] = day;
                            }
                        }, mYear, mMonth, dayOfMonth);
                datePickerDialog.show();
            }
        });

        llTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(MainActivity.this, llTime);
                //inflating menu from xml resource
                popup.inflate(R.menu.hours_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.hr0:
                                time.setText("00:00 - 01:00");
                                saveHour[0] = 0;
                                return true;

                            case R.id.hr1:
                                time.setText("01:00 - 02:00");
                                saveHour[0] = 1;
                                return true;
                            case R.id.hr2:
                                time.setText("02:00 - 03:00");
                                saveHour[0] = 2;
                                return true;
                            case R.id.hr3:
                                time.setText("03:00 - 04:00");
                                saveHour[0] = 3;
                                return true;
                            case R.id.hr4:
                                time.setText("04:00 - 05:00");
                                saveHour[0] = 4;
                                return true;
                            case R.id.hr5:
                                time.setText("05:00 - 06:00");
                                saveHour[0] = 5;
                                return true;
                            case R.id.hr6:
                                time.setText("06:00 - 7:00");
                                saveHour[0] = 6;
                                return true;
                            case R.id.hr7:
                                time.setText("07:00 - 08:00");
                                saveHour[0] = 7;
                                return true;
                            case R.id.hr8:
                                time.setText("08:00 - 09:00");
                                saveHour[0] = 8;
                                return true;
                            case R.id.hr9:
                                time.setText("09:00 - 10:00");
                                saveHour[0] = 9;
                                return true;
                            case R.id.hr10:
                                time.setText("10:00 - 11:00");
                                saveHour[0] = 10;
                                return true;
                            case R.id.hr11:
                                time.setText("11:00 - 12:00");
                                saveHour[0] = 11;
                                return true;
                            case R.id.hr12:
                                time.setText("012:00 - 13:00");
                                saveHour[0] = 12;
                                return true;
                            case R.id.hr13:
                                time.setText("13:00 - 14:00");
                                saveHour[0] = 13;
                                return true;
                            case R.id.hr14:
                                time.setText("14:00 - 15:00");
                                saveHour[0] = 14;
                                return true;
                            case R.id.hr15:
                                time.setText("15:00 - 16:00");
                                saveHour[0] = 15;
                                return true;
                            case R.id.hr16:
                                time.setText("16:00 - 17:00");
                                saveHour[0] = 16;
                                return true;
                            case R.id.hr17:
                                time.setText("17:00 - 18:00");
                                saveHour[0] = 17;
                                return true;
                            case R.id.hr18:
                                time.setText("18:00 - 19:00");
                                saveHour[0] = 18;
                                return true;
                            case R.id.hr19:
                                time.setText("19:00 - 20:00");
                                saveHour[0] = 19;
                                return true;
                            case R.id.hr20:
                                time.setText("20:00 - 21:00");
                                saveHour[0] = 20;
                                return true;
                            case R.id.hr21:
                                time.setText("21:00 - 22:00");
                                saveHour[0] = 21;
                                return true;
                            case R.id.hr22:
                                time.setText("22:00 - 23:00");
                                saveHour[0] = 22;
                                return true;
                            case R.id.hr23:
                                time.setText("23:00 - 24:00");
                                saveHour[0] = 23;
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });


        int finalOldsteptotal = oldsteptotal;
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numSteps = stepvalue.getText().toString();
                if (numSteps.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter number of steps..!!", Toast.LENGTH_LONG).show();
                } else {
                    String Calories = String.valueOf(commanMethod.calculateCalories(Integer.parseInt(numSteps), userWeight, userHeight));
                    String Distance = String.valueOf(commanMethod.calculateDistance(Integer.parseInt(numSteps), userHeight));

                    String ts = String.valueOf(System.currentTimeMillis());

                    int sum = finalOldsteptotal + Integer.parseInt(numSteps);
                    stepcountModel.setStep(sum);
                    stepcountModel.setDate(selectedday[0]);
                    stepcountModel.setMonth(selectedmonth[0]);
                    stepcountModel.setYear(selectedyear[0]);
                    stepcountModel.setDistance(Distance);
                    stepcountModel.setCalorie(Calories);
                    stepcountModel.setDuration(saveHour[0]);
                    stepcountModel.setTimestemp(ts);
                    dbManager.addStepcountData(stepcountModel);
                }
                alertDialog.dismiss();
                onResume();
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