package com.js.stepcounter.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.ProgressBar;
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

import com.ironsource.mediationsdk.IronSource;
import com.js.stepcounter.Application.AppController;
import com.js.stepcounter.MyMarkerView;
import com.js.stepcounter.R;
import com.js.stepcounter.adpter.StepWeekChartAdapter;
import com.js.stepcounter.adpter.WaterWeekChartAdapter;
import com.js.stepcounter.database.DatabaseManager;
import com.js.stepcounter.model.ArchivementModel;
import com.js.stepcounter.model.DashboardComponentModel;
import com.js.stepcounter.model.StepCountModel;
import com.js.stepcounter.model.WaterLevelModel;
import com.js.stepcounter.model.WeightModel;
import com.js.stepcounter.sevices.NotificationReceiver;
import com.js.stepcounter.sevices.SensorService;
import com.js.stepcounter.utils.CommanMethod;
import com.js.stepcounter.utils.Logger;
import com.js.stepcounter.utils.StorageManager;
import com.js.stepcounter.utils.constant;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends BaseActivity implements View.OnClickListener, DatePickerListener {

    final float[] value = {0};
    Toolbar mToolbar;
    ImageView mIvPlay, mIvPause;
    BottomNavigationView mbottomNavigation;
    CardView mcvStrat, mcvWater, mcvWeight, mCvArchivement;
    int StepGoal;
    String Watergoal, WaterUnit, Watercup;
    String distance, calories;
    LinearLayout llwaterSetting;
    ImageView ivaddwater;
    MyReceiver myReceiver;
    int temp = 0;
    int oldsteptotal = 0;
    ArrayList<StepCountModel> Steplist;
    ArrayList<StepCountModel> getoldSteplist;
    StepCountModel stepcountModel;
    CircleProgress mCpWaterCircleProgress;
    TextView addWeightDailog;
    LineChart mLcWeightChart;
    LinearLayout llContainer;
    WaterLevelModel waterlevel;
    Calendar rightNow;
    int hour, min, date, month, year;
    String[] DefultCupValue, WaterGoalValue;
    int waterml = 0;
    String selectedDate;
    String seletedMonth;
    String selecetedYear;
    EditText etweight;
    double covertinml;
    RecyclerView mRvSteplist, mRvWaterlist;
    StepWeekChartAdapter stepWeekChartAdapter;
    WaterWeekChartAdapter waterWeekChartAdapter;
    boolean IsAddWaterClick = false;
    ArrayList<StepCountModel> stepcountModelArrayList = new ArrayList<>();
    ArrayList<WaterLevelModel> DaywiseWaterlist = new ArrayList<>();
    ArrayList<WeightModel> arrayList = new ArrayList<>();
    ArrayList<ArchivementModel> archivementModelArrayList = new ArrayList<>();
    ArrayList<DashboardComponentModel> dashboardComponentModels = new ArrayList<>();
    DatabaseManager dbManager;
    int TotalStepCount;
    ProgressBar Displayprogress;
    long mlevelGoal, mLevelData, Distancegoal, DisplayDistance, TotalDaysgoal, mTotalDaysData, TotalDailygoal, TotalDailyStep;
    String LevelDesc, DistanceDesc, DayDesc, DailyDesc;
    ExtendedFloatingActionButton mExtFabAdjustOrder;
    View view1, view2, view3, view4;
    private TextView TvSteps, accuracyText, tvduration, tvkm, tvkcal, tvuserWeight, tvwatergoal, tvwaterlevel, mtvlastdaydiffvalue, mtvAvgstep, mTvDisplayLabel, mTvDisplayDesc, mTvDisplayPendingValue;
    private int numSteps, totalsec = 0, totalmin = 0;
    private CircularProgressBar progress;
    private float userWeight = constant.DEFAULT_WEIGHT;
    private float userHeight = constant.DEFAULT_HEIGHT;

    private void checkArchivement(int numSteps) {
        if (numSteps == mlevelGoal) {
            CommanMethod.showCompleteDailog(this, String.valueOf(mlevelGoal), LevelDesc);
        } else if (DisplayDistance == Distancegoal) {
            CommanMethod.showCompleteDailog(this, String.valueOf(Distancegoal), DistanceDesc);
        } else if (mTotalDaysData == TotalDaysgoal) {
            CommanMethod.showCompleteDailog(this, String.valueOf(TotalDaysgoal), DayDesc);
        } else if (TotalDailyStep == TotalDailygoal) {
            CommanMethod.showCompleteDailog(this, String.valueOf(TotalDailygoal), DailyDesc);
        }

        long mTotalStepData = dbManager.getTotalStepCount();
        long mTotalDisanceData = dbManager.getTotalDistanceCount();
        ChechArchivementdata(mTotalStepData, mlevelGoal, mTotalDisanceData, Distancegoal);
    }

    public void ChechArchivementdata(long currStep, long levelGoal, long CurrDistance, long distanceGoal) {

        float mLevelData = (float) currStep / levelGoal * 100;
        float mDisanceData = (float) CurrDistance / distanceGoal * 100;

        Logger.e(mLevelData + "--" + mDisanceData);

        ArrayList<Float> floats = new ArrayList<>();
        floats.remove(floats);
        floats.add(mLevelData);
        floats.add(mDisanceData);

        Float obj = Collections.max(floats);
        int index = floats.indexOf(obj);

//        if (index == 0 && floats.get(index) > 90) {
        if (index == 0) {
            if (currStep != 0) {
                int goal = (int) levelGoal;
                Displayprogress.setMax(goal);
                Displayprogress.setProgress((int) currStep);
                mTvDisplayPendingValue.setText(goal - currStep + " remians");
                archivementModelArrayList = dbManager.getArchivementData(constant.ARCHIVEMENT_LEVEL, goal);
                Logger.e(goal);
                for (int i = 0; i < archivementModelArrayList.size(); i++) {
                    Logger.e(archivementModelArrayList.get(i).getLabel());
                    mTvDisplayLabel.setText(archivementModelArrayList.get(i).getLabel());
                    mTvDisplayDesc.setText(archivementModelArrayList.get(i).getDescription());
                }
            }
//        } else if (index == 1 && floats.get(index) > 90) {
        } else if (index == 1) {
            int goal = (int) distanceGoal;
            int value = (int) CurrDistance;
            if (value != 0) {
                Displayprogress.setMax(goal);
                Displayprogress.setProgress(value);
                mTvDisplayPendingValue.setText(goal - value + " miles");
                archivementModelArrayList = dbManager.getArchivementData(constant.ARCHIVEMENT_TOTAL_DISTANCE, goal);
                for (int i = 0; i < archivementModelArrayList.size(); i++) {
                    mTvDisplayLabel.setText(archivementModelArrayList.get(i).getLabel());
                    mTvDisplayDesc.setText(archivementModelArrayList.get(i).getDescription());
                }
            }
        } else {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbManager = new DatabaseManager(this);

        if (StorageManager.getInstance().getIsStepService()) {
            Intent i = new Intent(MainActivity.this, SensorService.class);
            startService(i);
        }
        Steplist = new ArrayList<StepCountModel>();
        getoldSteplist = new ArrayList<StepCountModel>();

        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, new IntentFilter("GET_SIGNAL_STRENGTH"));
    }

    @SuppressLint("DefaultLocale")
    private void init() {
        llContainer = findViewById(R.id.llContainer);

        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view1 = vi.inflate(R.layout.main_gps_layout, null);
        view2 = vi.inflate(R.layout.main_water_layout, null);
        view3 = vi.inflate(R.layout.main_weigth_layout, null);

        view4 = vi.inflate(R.layout.main_level_layout, null);

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

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Health Tracker");
        setSupportActionBar(mToolbar);


        mRvSteplist = findViewById(R.id.rvStepChart);
        progress = findViewById(R.id.progressBar);

        TvSteps = findViewById(R.id.tv_steps);
        accuracyText = findViewById(R.id.tv_accuracy);
        tvduration = findViewById(R.id.tvduration);
        tvkm = findViewById(R.id.tvkm);
        tvkcal = findViewById(R.id.tvkcal);

        mtvAvgstep = findViewById(R.id.tvAvgstep);


        mExtFabAdjustOrder = findViewById(R.id.extFabAdjustOrder);
        mExtFabAdjustOrder.setOnClickListener(this);

        mIvPlay = findViewById(R.id.ivPlay);
        mIvPause = findViewById(R.id.ivPause);
        mbottomNavigation = findViewById(R.id.bottomNavigation);

        mIvPlay.setOnClickListener(this);
        mIvPause.setOnClickListener(this);

        if (StorageManager.getInstance().getIsStepService()) {
            mIvPause.setVisibility(View.VISIBLE);
            mIvPlay.setVisibility(View.GONE);
        } else {
            mIvPause.setVisibility(View.GONE);
            mIvPlay.setVisibility(View.VISIBLE);
        }

        llContainer.removeAllViews();
        String json = StorageManager.getInstance().getDashboardComponent();
        Type arrayListTypeToken = new TypeToken<ArrayList<DashboardComponentModel>>() {
        }.getType();
        dashboardComponentModels = new Gson().fromJson(json, arrayListTypeToken);

        for (int i = 0; i < dashboardComponentModels.size(); i++) {
            if (dashboardComponentModels.get(i).getShowonDashboard()) {
                switch (dashboardComponentModels.get(i).getComponentOrder()) {
                    case constant.DASHBORAD_GPS_TRACKER:
                        llContainer.addView(view1);
                        gpsviewData();
                        break;
                    case constant.DASHBORAD_WATER_TRACKER:
                        llContainer.addView(view2);
                        waterViewData();
                        setdatainprogress();
                        break;
                    case constant.DASHBORAD_WEIGHT_TRACKER:
                        llContainer.addView(view3);
                        setWeightdata();
                        break;
                    case constant.DASHBORAD_LEVEL_TRACKER:
                        llContainer.addView(view4);
                        setLeveldata();
                        break;
                    default:
                        break;
                }
            }
        }

        setRecyclerView();

        long prefdate = Long.parseLong(StorageManager.getInstance().getCurrentDay());

        Logger.e(rightNow.getTimeInMillis() + "---" + prefdate);
        if (rightNow.getTimeInMillis() > prefdate) {
            dbManager.updateArchivementReminLevel(constant.ARCHIVEMENT_LEVEL);
            dbManager.updateArchivementReminLevel(constant.ARCHIVEMENT_TOTAL_DISTANCE);
            dbManager.updateArchivementReminLevel(constant.ARCHIVEMENT_TOTAL_DAYS);
            dbManager.updateArchivementReminLevel(constant.ARCHIVEMENT_COMBO_DAY);
            dbManager.updateArchivementReminLevel(constant.ARCHIVEMENT_DAILY_STEP);
            ShowYesterdayHistoryDailog();
        }

//        Logger.e(rightNow.getTimeInMillis());
        if (rightNow.getTimeInMillis() > prefdate) {
            if (!StorageManager.getInstance().getIsStepService()) {
                mIvPause.setVisibility(View.VISIBLE);
                mIvPlay.setVisibility(View.GONE);
                Intent i = new Intent(MainActivity.this, SensorService.class);
                startService(i);
            }
        }

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
                        startActivity(new Intent(MainActivity.this, MoreSettingActivity.class));
                        return true;
                }
                return false;
            }
        });

        TotalStepCount = dbManager.getSumOfStepList(date, month, year);
        TvSteps.setText(TotalStepCount + "");

        totalmin = TotalStepCount / 100;

        if (totalmin > 60) {
            long hours = totalmin / 60;
            long minnutesRemaining = totalmin % 60;
            tvduration.setText(hours + "h " + minnutesRemaining + "m");
        } else {
            tvduration.setText(0 + "h " + totalmin + "m");
        }

        progress.setProgressMax(StorageManager.getInstance().getStepCountGoalUnit());
        progress.setProgressWithAnimation(TotalStepCount, (long) 1000);

        distance = String.valueOf(CommanMethod.calculateDistance(TotalStepCount));
        tvkm.setText(String.format("%.2f", Float.valueOf(distance)));

        calories = String.valueOf(CommanMethod.calculateCalories(TotalStepCount, userWeight, userHeight));
        tvkcal.setText(calories);

        getoldSteplistData(date, month, year, hour);


        ArrayList<WeightModel> WeightArrayList = new ArrayList<>();

        WeightArrayList = dbManager.getCurrentDayWeightlist(date, month, year);

        arrayList = dbManager.getCurrentDayWeightlist(date - 1, month, year);

        if (WeightArrayList == null) {
            String ts = String.valueOf(System.currentTimeMillis());
            WeightModel weightModel = new WeightModel();
            weightModel.setDate(date);
            weightModel.setMonth(month);
            weightModel.setYear(year);
            weightModel.setTimestemp(ts);
            weightModel.setKg((int) userWeight);
            dbManager.addWeightData(weightModel);
        } else if (arrayList == null) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
            c.set(Calendar.DATE, date - 1);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.YEAR, year);
            WeightModel weightModel = new WeightModel();
            weightModel.setDate(date - 1);
            weightModel.setMonth(month);
            weightModel.setYear(year);
            weightModel.setTimestemp(String.valueOf(c.getTimeInMillis()));
            weightModel.setKg((int) userWeight);
            dbManager.addWeightData(weightModel);
        }

        Logger.e(StorageManager.getInstance().getLevelArchivement());
        if (StorageManager.getInstance().getLevelArchivement()) {
            setArchivementData(date, month, year, hour);
        }
    }

    private void setLeveldata() {
        mTvDisplayLabel = findViewById(R.id.tvDisplayLabel);
        mTvDisplayDesc = findViewById(R.id.tvDisplayDesc);
        mTvDisplayPendingValue = findViewById(R.id.DisplayPendingValue);
        Displayprogress = findViewById(R.id.Displayprogress);
        mCvArchivement = findViewById(R.id.cvArchivement);

        mCvArchivement.setOnClickListener(this);
    }

    private void setWeightdata() {
        mLcWeightChart = view3.findViewById(R.id.cvWeightChart);
        addWeightDailog = view3.findViewById(R.id.addWeight);
        tvuserWeight = view3.findViewById(R.id.userWeight);
        mtvlastdaydiffvalue = view3.findViewById(R.id.tvlastdaydiffvalue);
        mcvWeight = view3.findViewById(R.id.cvWeight);

        tvuserWeight.setText(userWeight + "");

        mcvWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HeathActivity.class));
            }
        });

        addWeightDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddWeightDailog();
            }
        });
    }

    private void waterViewData() {
        mCpWaterCircleProgress = view2.findViewById(R.id.circle_progress);
        tvwatergoal = view2.findViewById(R.id.tvwatergoal);
        tvwaterlevel = view2.findViewById(R.id.tvwaterlevel);
        mcvWater = view2.findViewById(R.id.cvWater);
        mRvWaterlist = view2.findViewById(R.id.rvWaterChart);
        ivaddwater = view2.findViewById(R.id.ivaddwater);
        llwaterSetting = view2.findViewById(R.id.llwaterSetting);

        tvwatergoal.setText("/" + StorageManager.getInstance().getWaterGoal());

        mcvWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HeathActivity.class));
            }
        });

        ivaddwater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsAddWaterClick = true;
                String ts = String.valueOf(System.currentTimeMillis());

                waterlevel = new WaterLevelModel();
                waterlevel.setDate(date);
                waterlevel.setMonth(month);
                waterlevel.setYear(year);
                waterlevel.setHour(hour);
                waterlevel.setMin(min);
                waterlevel.setTimestemp(ts);

                float[] value = {0};

                if (DefultCupValue[1].contains("fl")) {
                    value[0] = value[0] + Float.parseFloat(DefultCupValue[0]);
                    covertinml = CommanMethod.getFlozToMl(value[0]);
                    waterlevel.setUnit(String.valueOf(covertinml));
                } else {
                    value[0] = value[0] + Integer.parseInt(DefultCupValue[0]);
                    waterlevel.setUnit(String.valueOf(value[0]));
                }
                dbManager.addWaterData(waterlevel);

                setdatainprogress();
            }
        });

        llwaterSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WaterSettingActivity.class));
            }
        });
    }

    private void gpsviewData() {
        mcvStrat = view1.findViewById(R.id.cvGPSStrat);
        mcvStrat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TrainingActivity.class));
            }
        });
    }

    private void getoldSteplistData(int date, int month, int year, int hour) {
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
    }

    private void setRecyclerView() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, rightNow.get(Calendar.DATE) - 6);

        Log.e("TAG", calendar.getTimeInMillis() + "setRecyclerView: " + rightNow.getTimeInMillis());

        String fristdate = String.valueOf(calendar.getTimeInMillis());
        String lastdate = String.valueOf(rightNow.getTimeInMillis());

        stepcountModelArrayList = dbManager.getweekstepdata(fristdate, lastdate);

        int avg = 0, j = 0;
        for (int i = 0; i < stepcountModelArrayList.size(); i++) {
            if (stepcountModelArrayList.get(i).getSumstep() != 0) {
                avg = avg + stepcountModelArrayList.get(i).getSumstep();
                j++;
            }
        }

        if (j > 0) {
            mtvAvgstep.setText(avg / j + "");
        } else {
            mtvAvgstep.setText(0 + "");
        }

        stepWeekChartAdapter = new StepWeekChartAdapter(this, stepcountModelArrayList);
        mRvSteplist.setHasFixedSize(true);
        mRvSteplist.setLayoutManager(new GridLayoutManager(this, 7));
        mRvSteplist.setAdapter(stepWeekChartAdapter);

        DaywiseWaterlist = dbManager.getweekWaterdata(fristdate, lastdate);

        waterWeekChartAdapter = new WaterWeekChartAdapter(this, DaywiseWaterlist);
        mRvWaterlist.setHasFixedSize(true);
        mRvWaterlist.setLayoutManager(new GridLayoutManager(this, 7));
        mRvWaterlist.setAdapter(waterWeekChartAdapter);

    }

    private void setWeightChart() {
        Calendar cal = Calendar.getInstance();
        long fristdate = cal.getTimeInMillis();
        cal.add(Calendar.DATE, -30);
        long last30day = cal.getTimeInMillis();

        ArrayList<WeightModel> modelArrayList = new ArrayList<>();

        ArrayList<Entry> weightModelArrayList = new ArrayList<>();

        modelArrayList = dbManager.getCurrentDayWeightlist(date, month, year);

        if (modelArrayList.size() != 0) {
            for (int i = 0; i < modelArrayList.size(); i++) {
                tvuserWeight.setText("" + modelArrayList.get(i).getKg());
            }
        } else {
            tvuserWeight.setText("" + 0);
        }

        int pervalue = 0;

        /*if (arrayList.size() != 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                pervalue = arrayList.get(i).getKg();
            }
        }*/

        Logger.e(pervalue);

        int cuurrvalue = Integer.parseInt(tvuserWeight.getText().toString());

        int diff = 0;
        diff = pervalue - cuurrvalue;

//        Log.e("TAG", pervalue + "setWeightChart: " + cuurrvalue);
        if (diff > 0) {
            mtvlastdaydiffvalue.setText(diff + "KG");
        } else {
            mtvlastdaydiffvalue.setText("0 KG");
        }

        mLcWeightChart.setBackgroundColor(Color.WHITE);

        // disable description text
        mLcWeightChart.getDescription().setEnabled(false);

        // enable touch gestures
        mLcWeightChart.setTouchEnabled(true);

        // set listeners
        mLcWeightChart.setDrawGridBackground(false);

        // create marker to display box when values are selected
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Kg");
        mv.setChartView(mLcWeightChart);
        mLcWeightChart.setMarker(mv);
        mLcWeightChart.setDragEnabled(false);
        mLcWeightChart.setScaleEnabled(false);
        mLcWeightChart.setPinchZoom(false);

        mLcWeightChart.getAxisLeft().setDrawGridLines(false);
        mLcWeightChart.getAxisRight().setDrawGridLines(false);
        mLcWeightChart.getXAxis().setDrawGridLines(false);

        XAxis xAxis;
        xAxis = mLcWeightChart.getXAxis();

        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        // axis range
        xAxis.setAxisMaximum(cal.get(Calendar.DAY_OF_MONTH));
//        xAxis.setAxisMinimum(cal.get(Calendar.DAY_OF_MONTH) - 30);

        YAxis yAxis;
        yAxis = mLcWeightChart.getAxisLeft();

        // disable dual axis (only use LEFT axis)
        mLcWeightChart.getAxisRight().setEnabled(false);

        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f);

        // axis range
        yAxis.setAxisMaximum(200f);
        yAxis.setAxisMinimum(0);

        ArrayList<WeightModel> waterlevelArrayList = new ArrayList<>();
        waterlevelArrayList = dbManager.getMonthWeightdata(String.valueOf(last30day), String.valueOf(fristdate));

        for (int i = 0; i < waterlevelArrayList.size(); i++) {
//            weightModelArrayList.add(new Entry(weightModels.get(i).getDate(), weightModels.get(i).getKg(), getResources().getDrawable(R.drawable.star)));
            weightModelArrayList.add(new Entry(waterlevelArrayList.get(i).getDate(), waterlevelArrayList.get(i).getKg(), getResources().getDrawable(R.drawable.star)));
        }

     /*   ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) - 30;
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
        }*/

        LineDataSet set1;

        if (mLcWeightChart.getData() != null && mLcWeightChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mLcWeightChart.getData().getDataSetByIndex(0);
            set1.setValues(weightModelArrayList);
            set1.notifyDataSetChanged();
            mLcWeightChart.getData().notifyDataChanged();
            mLcWeightChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(weightModelArrayList, "");

            set1.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(Color.BLACK);
//            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
//            set1.setCircleRadius(5f);

            // draw points as solid circles
//            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
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
                    return mLcWeightChart.getAxisLeft().getAxisMinimum();
                }
            });

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            mLcWeightChart.setData(data);
        }

        // draw points over time
        mLcWeightChart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = mLcWeightChart.getLegend();

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
                SensorService.IsServiceStart = true;
                Intent is = new Intent(MainActivity.this, SensorService.class);
                startService(is);
                mIvPlay.setVisibility(View.GONE);
                mIvPause.setVisibility(View.VISIBLE);
                StorageManager.getInstance().setIsStepService(true);
                break;
            case R.id.ivPause:
                SensorService.IsServiceStart = false;
                Intent intent1 = new Intent(MainActivity.this, SensorService.class);
                stopService(intent1);
                mIvPlay.setVisibility(View.VISIBLE);
                mIvPause.setVisibility(View.GONE);
                StorageManager.getInstance().setIsStepService(false);
                break;
            case R.id.cvArchivement:
                String s = mTvDisplayLabel.getText().toString();
                if (s.contains("Level")) {
                    Intent intent = new Intent(this, LevelActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, ArchivementDetailActivity.class);
                    intent.putExtra("TotalDistance", true);
                    startActivity(intent);
                }
                break;
            case R.id.extFabAdjustOrder:
                startActivity(new Intent(MainActivity.this, AdjustOrderActivity.class));
                break;
        }
    }

    private void setdatainprogress() {
//        mCpWaterCircleProgress.setMax(Integer.parseInt(WaterGoalValue[0]));
        mCpWaterCircleProgress.setMax(100);

        ArrayList<WaterLevelModel> waterlevelArrayList = new ArrayList<>();

        waterlevelArrayList = dbManager.getDayWaterdata(date, month, year);

        int lastentry = 0;
        double Fllastentry = 0;
        if (WaterUnit.equals("ml")) {
            if (waterlevelArrayList != null) {
                for (int i = 0; i < waterlevelArrayList.size(); i++) {
//                Log.e("TAG", "init: " + waterlevelArrayList.get(i).getSumwater());
                    lastentry = waterlevelArrayList.get(i).getSumwater();
                }

                if (lastentry != 0) {
                    if (lastentry < Integer.parseInt(WaterGoalValue[0])) {
                        float progress = (float) lastentry / Integer.parseInt(WaterGoalValue[0]) * 100;
                        mCpWaterCircleProgress.setProgress((int) progress);
//                        mCpWaterCircleProgress.setProgress(lastentry);
                    } else {
//                        mCpWaterCircleProgress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                        mCpWaterCircleProgress.setProgress(100);
                    }
                }
                tvwaterlevel.setText(lastentry + "");
            } else {
                mCpWaterCircleProgress.setProgress(0);
                tvwaterlevel.setText(0 + "");
            }
        } else {
            if (waterlevelArrayList != null) {
                for (int i = 0; i < waterlevelArrayList.size(); i++) {
//                Log.e("TAG", "init: " + waterlevelArrayList.get(i).getSumwater());
                    Fllastentry = Math.round(CommanMethod.getMlToFloz(Float.valueOf(waterlevelArrayList.get(i).getSumwater())));
                }

                if (Fllastentry != 0) {
                    if (Fllastentry < Integer.parseInt(WaterGoalValue[0])) {
                        double value = Fllastentry / Integer.parseInt(WaterGoalValue[0]) * 100;
                        //                        Log.e("TAG", "init: " + value);
                        mCpWaterCircleProgress.setProgress((int) value);
//                        mCpWaterCircleProgress.setProgress((int) Fllastentry);
                    } else {
//                        mCpWaterCircleProgress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                        mCpWaterCircleProgress.setProgress(100);
                    }
                }
                tvwaterlevel.setText(Fllastentry + "");
            } else {
                mCpWaterCircleProgress.setProgress(0);
                tvwaterlevel.setText(0 + "");
            }
        }

        int Target = StorageManager.getInstance().getWatergoalTarget();

        if (IsAddWaterClick) {
            if (Target < lastentry) {
                startActivity(new Intent(MainActivity.this, AddWaterActivity.class));
            } else if (Target < Fllastentry) {
                startActivity(new Intent(MainActivity.this, AddWaterActivity.class));
            }
            IsAddWaterClick = false;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showAddWeightDailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_add_weight, null);
        dialogBuilder.setView(d);
        AlertDialog alertDialog = dialogBuilder.create();
        Button mBtnSave = d.findViewById(R.id.btnSave);
        Button mBtnCancel = d.findViewById(R.id.btnCancel);

        CardView mllLb = d.findViewById(R.id.cvKm);
        CardView mllKB = d.findViewById(R.id.lcvMile);
        etweight = d.findViewById(R.id.etweight);

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
                int a = Integer.parseInt(etweight.getText().toString());
                if (a != 0) {
                    etweight.setText(Math.round(CommanMethod.kgToLbConverter(Double.parseDouble(etweight.getText().toString()))) + "");
                } else {
                    etweight.setText("0");
                }
            }
        });

        mllKB.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                iskg[0] = true;
                mllKB.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                mllLb.setCardBackgroundColor(getResources().getColor(R.color.transprant));
                int a = Integer.parseInt(etweight.getText().toString());
                if (a != 0) {
                    etweight.setText(Math.round(CommanMethod.lbToKgConverter(Double.parseDouble(etweight.getText().toString()))) + "");
                } else {
                    etweight.setText("0");
                }
            }
        });

        HorizontalPicker picker = d.findViewById(R.id.datePicker);

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
                    weightModel.setKg((int) Math.round(CommanMethod.lbToKgConverter(Double.parseDouble(etweight.getText().toString()))));
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
        if (AppController.IsAdOn)
            IronSource.showInterstitial();
        finishAffinity();
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
            case R.id.action_archivement:
                startActivity(new Intent(this, ArchivementActivity.class));
                break;
            case R.id.action_reset:
                showResetdataDailog();
                break;
            case R.id.action_history:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.action_instruction:
                startActivity(new Intent(this, InstructionActivity.class));
                break;
            case R.id.action_trunoff:
                onBackPressed();
                break;
            case R.id.action_profile:
                startActivity(new Intent(MainActivity.this, PersonalInfomationActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSharedPreferences();
        init();
        setWeightChart();
        getDatafromDatabase();
    }

    private void getDatafromDatabase() {

        ArrayList<ArchivementModel> mTotalDistanceList = new ArrayList<>();
        mTotalDistanceList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DISTANCE);
        long mTotalDisanceData = dbManager.getTotalDistanceCount();

//        -------------------------total distance
        long Distancegoal = 0;
        for (int i = 0; i < mTotalDistanceList.size(); i++) {
            if (i == 0) {
                Distancegoal = mTotalDistanceList.get(0).getValue();
            }
            if (mTotalDistanceList.get(i).isCompeleteStatus() && mTotalDistanceList.size() - 1 != i) {
                Distancegoal = mTotalDistanceList.get(i + 1).getValue();
            }
        }

        long mTotalStepData = dbManager.getTotalStepCount();
        ArrayList<ArchivementModel> mLevel = new ArrayList<>();
        mLevel = dbManager.getArchivementlist(constant.ARCHIVEMENT_LEVEL);

        //        -------------------------Level
        long mlevelGoal = 0;
        for (int i = 0; i < mLevel.size(); i++) {
            if (mLevel.get(i).isCompeleteStatus() && mLevel.size() - 1 != i) {
                mlevelGoal = mLevel.get(i + 1).getValue();
            }

        }
        ChechArchivementdata(mTotalStepData, mlevelGoal, mTotalDisanceData, Distancegoal);
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
                        dbManager.DeleteCurrentDayStepCountData(date, month, year);
                        dbManager.DeleteCurrentDayWaterData(date, month, year);
                        init();
                        archivementModelArrayList = dbManager.getArchivementData(constant.ARCHIVEMENT_LEVEL, 0);
                        for (int i = 0; i < archivementModelArrayList.size(); i++) {
                            Logger.e(archivementModelArrayList.get(i).getLabel());
                            mTvDisplayLabel.setText(archivementModelArrayList.get(i).getLabel());
                            mTvDisplayDesc.setText(archivementModelArrayList.get(i).getDescription());
                            Displayprogress.setMax(10000);
                            Displayprogress.setProgress(0);
                            mTvDisplayPendingValue.setText(10000 - 0 + " remians");
                        }
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
        stepcountModel = new StepCountModel();

        String[] Time = {"00:00 - 01:00", "01:00 - 02:00", "02:00 - 03:00", "03:00 - 04:00", "04:00 - 05:00", "05:00 - 06:00", "06:00 - 07:00", "07:00 - 08:00",
                "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00",
                "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00", "20:00 - 21:00", "21:00 - 22:00", "22:00 - 23:00", "23:00 - 24:00"};

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_edit, null);
        dialogBuilder.setView(d);

        AlertDialog alertDialog = dialogBuilder.create();
        Button mBtnSave = d.findViewById(R.id.btnSave);
        Button mBtnCancel = d.findViewById(R.id.btnCancel);
        LinearLayout llView = d.findViewById(R.id.datepicker);
        Spinner spinner = d.findViewById(R.id.spinner);
        EditText stepvalue = d.findViewById(R.id.stepvalue);
        TextView tvdate = d.findViewById(R.id.tvdate);

        Calendar rightNow = Calendar.getInstance();
        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
        final int[] dayOfMonth = {rightNow.get(Calendar.DAY_OF_MONTH)};
        final int[] mMonth = {rightNow.get(Calendar.MONTH) + 1};
        final int[] mYear = {rightNow.get(Calendar.YEAR)};

        final int[] saveHour = {hours};
        final int[] selectedyear = {mYear[0]};
        final int[] selectedmonth = {mMonth[0]};
        final int[] selectedday = {dayOfMonth[0]};

        ArrayList<String> stringArrayList = new ArrayList<>();
        if (selectedday[0] != dayOfMonth[0] && selectedmonth[0] != mMonth[0] && selectedyear[0] != mYear[0]) {
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
                                stringArrayList.clear();
                                selectedyear[0] = year;
                                selectedmonth[0] = month + 1;
                                selectedday[0] = day;

                                if (selectedday[0] == date) {
                                    tvdate.setText("Today");
                                } else {
                                    tvdate.setText(selectedday[0] + " - " + selectedmonth[0] + " - " + selectedyear[0]);
                                }

                                if (selectedday[0] != dayOfMonth[0]) {
                                    for (int i = 0; i < 24; i++) {
                                        stringArrayList.add(Time[i]);
                                    }
                                } else {
                                    for (int i = 0; i < hours + 1; i++) {
                                        stringArrayList.add(Time[i]);
                                    }
                                }

                                getoldSteplistData(selectedday[0], selectedmonth[0], selectedyear[0], saveHour[0]);
                                stepvalue.setText(oldsteptotal + "");
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }, selectedyear[0], selectedmonth[0] - 1, selectedday[0]);
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
                getoldSteplistData(selectedday[0], selectedmonth[0], selectedyear[0], saveHour[0]);
                stepvalue.setText(oldsteptotal + "");
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
                    String Calories = String.valueOf(CommanMethod.calculateCalories(Integer.parseInt(numSteps), userWeight, userHeight));
                    String Distance = String.valueOf(CommanMethod.calculateDistance(Integer.parseInt(numSteps)));

                    Calendar c = Calendar.getInstance();
                    c.clear();
                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                    c.set(Calendar.DATE, selectedday[0]);
                    c.set(Calendar.MONTH, selectedmonth[0] - 1);
                    c.set(Calendar.YEAR, selectedyear[0]);
                    c.set(Calendar.HOUR, saveHour[0]);

                    s.format(new Date(c.getTimeInMillis()));

                    Log.e("Start", "Start Date = " + selectedday[0]);
                    Log.e("Start", "Start Date = " + selectedmonth[0]);
                    Log.e("Start", "Start Date = " + selectedyear[0]);
                    Log.e("Start", "Start Date = " + saveHour[0]);
                    Log.e("Start", "Start Date = " + c.getTimeInMillis());
                    Log.e("Start", "Start Date = " + Calories);
                    Log.e("Start", "Start Date = " + Distance);

                    stepcountModel.setStep(Integer.parseInt(numSteps));
                    stepcountModel.setDate(selectedday[0]);
                    stepcountModel.setMonth(selectedmonth[0]);
                    stepcountModel.setYear(selectedyear[0]);
                    stepcountModel.setDistance(Distance);
                    stepcountModel.setCalorie(Calories);
                    stepcountModel.setDuration(saveHour[0]);
                    stepcountModel.setTimestemp(String.valueOf(c.getTimeInMillis()));

                    stepcountModel.setMaxStep(0);
//                    }
                    dbManager.addStepcountData(stepcountModel);
//                    Logger.e(new GsonBuilder().create().toJson(stepcountModel));
                }

                setArchivementData(selectedday[0], selectedmonth[0], selectedyear[0], saveHour[0]);
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

    private void setArchivementData(int mdate, int month, int year, int hour) {
        //Archievement level Data
        long mTotalStepData = dbManager.getTotalStepCount();
        ArrayList<ArchivementModel> mLevel = new ArrayList<>();
        mLevel = dbManager.getArchivementlist(constant.ARCHIVEMENT_LEVEL);

        String DailogGoal = null;
        String DailogDesc = null;

        long NotificationValue = 0;
        String NotificationType = null;

        for (int i = 0; i < mLevel.size(); i++) {
            if (mTotalStepData >= mLevel.get(i).getValue()) {

                Logger.e(mTotalStepData + ">" + mLevel.get(i).getValue());
//                Logger.e((mTotalStepData > mLevel.get(i).getValue()));

                ArchivementModel archivementModel = new ArchivementModel();
                archivementModel.setValue(mLevel.get(i).getValue());
                archivementModel.setType(mLevel.get(i).getType());
                archivementModel.setCompeleteStatus(true);
                dbManager.updateArchivementTotalDistance(archivementModel);

                if (mLevel.get(i).getValue() != 0) {
                    DailogGoal = String.valueOf(mLevel.get(i).getValue());
                    DailogDesc = mLevel.get(i).getDescription();

                    NotificationValue = mLevel.get(i).getValue();
                    NotificationType = constant.ARCHIVEMENT_LEVEL;

                    /*Intent intent = new Intent(this, NotificationReceiver.class);
                    intent.setAction("Notification");
                    intent.putExtra("value", mLevel.get(i).getValue());
                    intent.putExtra("Type", constant.ARCHIVEMENT_LEVEL);
                    intent.putExtra("Compeletelevel", true);
                    sendBroadcast(intent);*/
                }
                StorageManager.getInstance().setLevelArchivement(true);
            }
        }


        // Archivement Daily Step
        ArrayList<ArchivementModel> mDailySteplist = new ArrayList<>();
        ArrayList<StepCountModel> MaxStepCount = dbManager.getsumofdayStep(mdate, month, year);

//        Logger.e(MaxStepCount.get(0).getSumstep());
//        Logger.e(MaxStepCount.get(0).getMaxStep());

//        ArrayList<StepCountModel> MaxStepCountModels = dbManager.getMaxStepCount();
        mDailySteplist = dbManager.getArchivementDailySteplist(constant.ARCHIVEMENT_DAILY_STEP, MaxStepCount.get(0).getMaxStep());
        for (int i = 0; i < mDailySteplist.size(); i++) {
            if (MaxStepCount.get(0).getSumstep() >= mDailySteplist.get(i).getValue()) {

//                Logger.e(TotalStepCount + ">" + mDailySteplist.get(0).getValue());
//                Logger.e((TotalStepCount > mDailySteplist.get(i).getValue()));

                ArchivementModel archivementModel = new ArchivementModel();
                archivementModel.setValue(mDailySteplist.get(i).getValue());
                archivementModel.setCount(mDailySteplist.get(i).getCount());
                archivementModel.setType(mDailySteplist.get(i).getType());
                archivementModel.setCompeleteStatus(true);
                dbManager.updateArchivementDailyStep(archivementModel);

                StepCountModel stepCountModel = new StepCountModel();
                stepCountModel.setDate(mdate);
                stepCountModel.setMonth(month);
                stepCountModel.setYear(year);
                stepCountModel.setDuration(hour);
                stepCountModel.setMaxStep(MaxStepCount.get(0).getSumstep());

//                Logger.e(date + "**" + month + "**" + year + "**" + hour + "**" + MaxStepCount.get(0).getSumstep());
                dbManager.updatemaxStep(stepCountModel);

                DailogGoal = String.valueOf(mDailySteplist.get(i).getValue());
                DailogDesc = mDailySteplist.get(i).getDescription();

                NotificationValue = mDailySteplist.get(i).getValue();
                NotificationType = constant.ARCHIVEMENT_DAILY_STEP;

                /*Intent intent = new Intent(this, NotificationReceiver.class);
                intent.setAction("Notification");
                intent.putExtra("value", mDailySteplist.get(i).getValue());
                intent.putExtra("Type", constant.ARCHIVEMENT_DAILY_STEP);
                intent.putExtra("CompeleteDailyStep", true);
                sendBroadcast(intent);*/
                StorageManager.getInstance().setLevelArchivement(true);

            }
        }


        // Archivement Total Days
        long mTotalDaysData = dbManager.getTotalDaysCount();
        ArrayList<ArchivementModel> mTotalDaysList = new ArrayList<>();
        mTotalDaysList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DAYS);

        for (int i = 0; i < mTotalDaysList.size(); i++) {

//            Logger.e(mTotalDaysData + ">" + mTotalDaysList.get(i).getValue());
//            Logger.e((mTotalDaysData > mTotalDaysList.get(i).getValue()));

            if (mTotalDaysData >= mTotalDaysList.get(i).getValue()) {
                ArchivementModel archivementModel = new ArchivementModel();
                archivementModel.setValue(mTotalDaysList.get(i).getValue());
                archivementModel.setType(mTotalDaysList.get(i).getType());
                archivementModel.setCompeleteStatus(true);
                dbManager.updateArchivementTotalDistance(archivementModel);

                DailogGoal = String.valueOf(mTotalDaysList.get(i).getValue());
                DailogDesc = mTotalDaysList.get(i).getDescription();

                NotificationValue = TotalDaysgoal;
                NotificationType = constant.ARCHIVEMENT_TOTAL_DAYS;

                /*Intent intent = new Intent(this, NotificationReceiver.class);
                intent.setAction("Notification");
                intent.putExtra("value", TotalDaysgoal);
                intent.putExtra("Type", constant.ARCHIVEMENT_TOTAL_DAYS);
                intent.putExtra("CompeleteDaysData", true);
                sendBroadcast(intent);*/
                StorageManager.getInstance().setLevelArchivement(true);

            }
        }

        // Archivement Total Distance
        ArrayList<ArchivementModel> mTotalDistanceList = new ArrayList<>();
        mTotalDistanceList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DISTANCE);
        long mTotalDisanceData = dbManager.getTotalDistanceCount();

        for (int i = 0; i < mTotalDistanceList.size(); i++) {

//            Logger.e(mTotalDaysData + ">" + mTotalDistanceList.get(i).getValue());
//            Logger.e((mTotalDaysData > mTotalDistanceList.get(i).getValue()));

            if (mTotalDisanceData >= mTotalDistanceList.get(i).getValue()) {
                ArchivementModel archivementModel = new ArchivementModel();
                archivementModel.setValue(mTotalDistanceList.get(i).getValue());
                archivementModel.setType(mTotalDistanceList.get(i).getType());
                archivementModel.setCompeleteStatus(true);
                dbManager.updateArchivementTotalDistance(archivementModel);

                DailogGoal = String.valueOf(mTotalDistanceList.get(i).getValue());
                DailogDesc = mTotalDistanceList.get(i).getDescription();

                NotificationValue = mTotalDistanceList.get(i).getValue();
                NotificationType = constant.ARCHIVEMENT_TOTAL_DISTANCE;

                /*Intent intent = new Intent(this, NotificationReceiver.class);
                intent.setAction("Notification");
                intent.putExtra("value", mTotalDistanceList.get(i).getValue());
                intent.putExtra("Type", constant.ARCHIVEMENT_TOTAL_DISTANCE);
                intent.putExtra("CompeleteDistance", true);
                sendBroadcast(intent);*/

                StorageManager.getInstance().setLevelArchivement(true);

            }
        }

        //Archivement Combo Days
        int currDayStep = 0, YestDayStep = 0;
        ArrayList<StepCountModel> stepCountModelArrayList = new ArrayList<>();
        stepCountModelArrayList = dbManager.getCurrentDaySumofStepcountlist(date, month, year);
        if (stepCountModelArrayList != null) {
            for (int i = 0; i < stepCountModelArrayList.size(); i++) {
                currDayStep = stepCountModelArrayList.get(i).getSumstep();
            }
        }

        Steplist = dbManager.getYesterDayStepcountlist((date - 1), month, year);
        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
                YestDayStep = Steplist.get(i).getSumstep();
            }
        }

        Logger.e("currDayStep" + currDayStep + "YestDayStep" + YestDayStep);
        Logger.e((YestDayStep >= StepGoal));
        Logger.e((currDayStep >= StepGoal));

        int Count = StorageManager.getInstance().getComboDayCount();
        if (currDayStep >= StepGoal) {
            if (YestDayStep >= StepGoal) {
                StorageManager.getInstance().setComboDayCount(Count + 1);
            }
        } else {
            StorageManager.getInstance().setComboDayCount(0);
        }

        ArrayList<ArchivementModel> mComboDayList = new ArrayList<>();
        mComboDayList = dbManager.getArchivementlist(constant.ARCHIVEMENT_COMBO_DAY);

        for (int i = 0; i < mComboDayList.size(); i++) {
            if (StorageManager.getInstance().getComboDayCount() == mComboDayList.get(i).getValue()) {
                ArchivementModel archivementModel = new ArchivementModel();
                archivementModel.setValue(mComboDayList.get(i).getValue());
                archivementModel.setType(mComboDayList.get(i).getType());
                archivementModel.setCompeleteStatus(true);
                dbManager.updateArchivementTotalDistance(archivementModel);

                DailogGoal = String.valueOf(mComboDayList.get(i).getValue());
                DailogDesc = mComboDayList.get(i).getDescription();

            }
        }

//        -------------------------total distance
        long Distancegoal = 0;
        String DistanceDesc = "";
        for (int i = 0; i < mTotalDistanceList.size(); i++) {

            if (i == 0) {
                Distancegoal = mTotalDistanceList.get(0).getValue();
                DistanceDesc = mTotalDistanceList.get(0).getDescription();
            }

            if (mTotalDistanceList.get(i).isCompeleteStatus() && mTotalDistanceList.size() - 1 != i) {
                Distancegoal = mTotalDistanceList.get(i + 1).getValue();
                DistanceDesc = mTotalDistanceList.get(i + 1).getDescription();
            }
        }

        Logger.e("Total Distance" + DisplayDistance + "---" + Distancegoal);

//        -------------------------total Days
        long TotalDaysgoal = 0;
        String DayDesc = "";
        for (int i = 0; i < mTotalDaysList.size(); i++) {

            if (i == 0) {
                TotalDaysgoal = mTotalDaysList.get(0).getValue();
                DayDesc = mTotalDaysList.get(0).getDescription();
            }

            if (mTotalDaysList.get(i).isCompeleteStatus() && mTotalDaysList.size() - 1 != i) {
                TotalDaysgoal = mTotalDaysList.get(i + 1).getValue();
                DayDesc = mTotalDaysList.get(i + 1).getDescription();
            }
        }

        Logger.e("Total Days" + mTotalDaysData + "---" + TotalDaysgoal);

        //        -------------------------Daily Steps
        long TotalDailyStep = Long.parseLong(TvSteps.getText().toString());
        long TotalDailygoal = 0;
        String DailyDesc = "";

        ArrayList<ArchivementModel> mDailyStepDatalist = new ArrayList<>();

        mDailyStepDatalist = dbManager.getArchivementlist(constant.ARCHIVEMENT_DAILY_STEP);
        Logger.e(mDailyStepDatalist.size());
        for (int i = 0; i < mDailyStepDatalist.size(); i++) {
//            Logger.e(mDailySteplist.get(i).isCompeleteStatus() + mDailySteplist.get(i).getLabel());

            if (i == 0) {
                TotalDailygoal = mDailyStepDatalist.get(0).getValue();
                DailyDesc = mDailyStepDatalist.get(0).getDescription();
            }

            if (mDailyStepDatalist.get(i).isCompeleteStatus() && mDailyStepDatalist.size() - 1 != i) {
                TotalDailygoal = mDailyStepDatalist.get(i + 1).getValue();
//                Logger.e(TotalDailygoal + "----new ------TotalDailygoal-----------");
                DailyDesc = mDailyStepDatalist.get(i + 1).getDescription();
            }
        }

        Logger.e("Daily steps" + TotalDailyStep + "---" + TotalDailygoal);

        //        -------------------------Level
        long mlevelGoal = 0;
        String LevelDesc = "";
        for (int i = 0; i < mLevel.size(); i++) {
            if (mLevel.get(i).isCompeleteStatus() && mLevel.size() - 1 != i) {
                mlevelGoal = mLevel.get(i + 1).getValue();
                LevelDesc = mLevel.get(i + 1).getDescription();
            }

        }
        Logger.e("Level" + mTotalStepData + "------" + mlevelGoal);

        float mLevelData = (float) mTotalStepData / mlevelGoal * 100;
        float mDisanceData = (float) mTotalDisanceData / Distancegoal * 100;
        float mDaysData = (float) mTotalDaysData / TotalDaysgoal * 100;
        float mDailyStep = (float) TotalDailyStep / TotalDailygoal * 100;

        Logger.e(mLevelData + "--" + mDisanceData + "--" + mDaysData + "--" + mDailyStep + "--");

        Logger.e(mTotalStepData + "--" + mlevelGoal + "--" + mTotalDisanceData + "--" + Distancegoal);
        ChechArchivementdata(mTotalStepData, mlevelGoal, mTotalDisanceData, Distancegoal);

        ArrayList<Float> floats = new ArrayList<>();
        floats.remove(floats);
        floats.add(mLevelData);
        floats.add(mDisanceData);
        floats.add(mDaysData);
        floats.add(mDailyStep);

        Float obj = Collections.max(floats);
        int index = floats.indexOf(obj);
//        Logger.e(obj + "--" + index);

        Logger.e(TotalDailyStep >= TotalDailygoal);
        Logger.e(TotalDailyStep + ">=" + TotalDailygoal);
//        Logger.e(TotalDailyStep + ">=" + StorageManager.getInstance().getStepCountGoalUnit());

        if (mdate == rightNow.get(Calendar.DATE) && floats.get(index) > 90) {
            if (index == 0) {
                ArchivementModel archivementModels = dbManager.getArchivement(constant.ARCHIVEMENT_LEVEL, mlevelGoal);
                if (!archivementModels.isReminderStatus()) {
                    dbManager.updateArchivementReminingLevel(constant.ARCHIVEMENT_LEVEL, mlevelGoal);
                    Intent sendLevel = new Intent(this, NotificationReceiver.class);
                    sendLevel.setAction("Notification");
                    sendLevel.putExtra("value", mlevelGoal - mTotalStepData);
                    sendLevel.putExtra("Type", constant.ARCHIVEMENT_LEVEL);
                    sendLevel.putExtra("Compeletelevel", false);
                    sendBroadcast(sendLevel);
                }
            } else if (index == 1) {
                ArchivementModel archivementModels = dbManager.getArchivement(constant.ARCHIVEMENT_TOTAL_DISTANCE, Distancegoal);
                if (!archivementModels.isReminderStatus()) {
                    dbManager.updateArchivementReminingLevel(constant.ARCHIVEMENT_TOTAL_DISTANCE, Distancegoal);
                    Intent sendLevel = new Intent(this, NotificationReceiver.class);
                    sendLevel.setAction("Notification");
                    sendLevel.putExtra("value", Distancegoal - mTotalDisanceData);
                    sendLevel.putExtra("Type", constant.ARCHIVEMENT_TOTAL_DISTANCE);
                    sendLevel.putExtra("CompeleteDistance", false);
                    sendBroadcast(sendLevel);
                }
            } else if (index == 2) {
                ArchivementModel archivementModels = dbManager.getArchivement(constant.ARCHIVEMENT_TOTAL_DAYS, TotalDaysgoal);
                if (!archivementModels.isReminderStatus()) {
                    dbManager.updateArchivementReminingLevel(constant.ARCHIVEMENT_TOTAL_DAYS, TotalDaysgoal);
                    Intent sendLevel = new Intent(this, NotificationReceiver.class);
                    sendLevel.setAction("Notification");
                    sendLevel.putExtra("value", TotalDaysgoal - mTotalDaysData);
                    sendLevel.putExtra("Type", constant.ARCHIVEMENT_TOTAL_DAYS);
                    sendLevel.putExtra("CompeleteDaysData", false);
                    sendBroadcast(sendLevel);
                }
            } else if (index == 3) {
                ArchivementModel archivementModels = dbManager.getArchivement(constant.ARCHIVEMENT_DAILY_STEP, TotalDailygoal);
                if (!archivementModels.isReminderStatus()) {
                    dbManager.updateArchivementReminingLevel(constant.ARCHIVEMENT_DAILY_STEP, TotalDailygoal);
                    Intent sendLevel = new Intent(this, NotificationReceiver.class);
                    sendLevel.setAction("Notification");
                    sendLevel.putExtra("value", TotalDailygoal - TotalDailyStep);
                    sendLevel.putExtra("Type", constant.ARCHIVEMENT_DAILY_STEP);
                    sendLevel.putExtra("CompeleteDailyStep", false);
                    sendBroadcast(sendLevel);
                }
            }

            if (TotalDailyStep >= StorageManager.getInstance().getStepCountGoalUnit()) {
                Intent intent = new Intent(this, NotificationReceiver.class);
                intent.setAction("Notification");
                intent.putExtra("value", TotalDailygoal);
                intent.putExtra("Type", constant.ARCHIVEMENT_DAILY_STEP);
                intent.putExtra("CompeleteDailyStepGoal", true);
                sendBroadcast(intent);
            }

        }

        long NotiValue = 0;
        String NotiType = null;

        if (mTotalStepData >= mlevelGoal && !mLevel.get(mLevel.size() - 1).isCompeleteStatus()) {
            NotiValue = mlevelGoal;
            NotiType = constant.ARCHIVEMENT_LEVEL;
        } else if (mTotalDisanceData >= Distancegoal && !mTotalDistanceList.get(mTotalDistanceList.size() - 1).isCompeleteStatus()) {
            NotiValue = Distancegoal;
            NotiType = constant.ARCHIVEMENT_TOTAL_DISTANCE;
        } else if (mTotalDaysData >= TotalDaysgoal && !mTotalDaysList.get(mTotalDaysList.size() - 1).isCompeleteStatus()) {
            NotiValue = TotalDaysgoal;
            NotiType = constant.ARCHIVEMENT_TOTAL_DAYS;
        } else if (TotalDailyStep >= TotalDailygoal && !mDailySteplist.get(mDailyStepDatalist.size() - 1).isCompeleteStatus()) {
            NotiValue = TotalDailygoal;
            NotiType = constant.ARCHIVEMENT_DAILY_STEP;
        }

        if (NotiValue != 0 && NotiType != null) {
            Intent sendLevel = new Intent(this, NotificationReceiver.class);
            sendLevel.setAction("Notification");
            sendLevel.putExtra("value", TotalDailygoal);
            sendLevel.putExtra("Type", constant.ARCHIVEMENT_DAILY_STEP);
            sendLevel.putExtra("CompeleteDailyStep", true);
            sendBroadcast(sendLevel);
        }

        if (DailogGoal != null && DailogDesc != null) {
            CommanMethod.showCompleteDailog(this, DailogGoal, DailogDesc);
        }

        if (NotificationValue != 0 && NotificationType != null) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.setAction("Notification");
            intent.putExtra("value", NotificationValue);
            intent.putExtra("Type", NotificationType);
            sendBroadcast(intent);
        }

        StorageManager.getInstance().setLevelArchivement(false);

    }

    private void ShowYesterdayHistoryDailog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_history, null);
        dialogBuilder.setView(d);

        AlertDialog alertDialog = dialogBuilder.create();
        CardView mCvHistory = d.findViewById(R.id.cvHistory);
        CardView mCvShare = d.findViewById(R.id.cvShare);
        TextView mTvReminderSetting = d.findViewById(R.id.tvReminderSetting);
        TextView mTvYesterdayStep = d.findViewById(R.id.tvYesterdayStep);
        TextView mTvWeeklysteps = d.findViewById(R.id.tvWeeklysteps);
        Button mBtnCancel = d.findViewById(R.id.btn_cancel);
        mTvReminderSetting.setText(Html.fromHtml("<u>Reminder Setting</u>"));

        int avg = 0, j = 0;
        for (int i = 0; i < stepcountModelArrayList.size(); i++) {
            if (stepcountModelArrayList.get(i).getSumstep() != 0) {
                avg = avg + stepcountModelArrayList.get(i).getSumstep();
                j++;
            }
        }

        try {
            mTvWeeklysteps.setText("Weekly Average " + avg / j);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Steplist = dbManager.getYesterDayStepcountlist((date - 1), month, year);
        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
                mTvYesterdayStep.setText(Steplist.get(i).getSumstep() + "");
            }
        }

        mTvReminderSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WaterSettingActivity.class));
                alertDialog.dismiss();
            }
        });

        mCvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                alertDialog.dismiss();
            }
        });

        mCvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Step Counter");
                String shareMessage = "\nYour Yesterday step is " + mTvYesterdayStep.getText().toString()
                        + "\n Weekly Average is " + mTvWeeklysteps.getText().toString();
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));


                alertDialog.dismiss();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                cal.set(Calendar.HOUR, 12);
                cal.set(Calendar.MINUTE, 00);
                cal.set(Calendar.AM_PM, Calendar.PM);
                Logger.e(cal.getTimeInMillis());
//                StorageManager.getInstance().setCurrentDay(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
                StorageManager.getInstance().setCurrentDay(cal.getTimeInMillis() + "");
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("GET_SIGNAL_STRENGTH")) {
                int level = intent.getIntExtra("stepdata", 0);
                mlevelGoal = intent.getLongExtra("mlevelGoal", 0);
                mLevelData = intent.getLongExtra("mLevelData", 0);
                Distancegoal = intent.getLongExtra("Distancegoal", 0);
                DisplayDistance = intent.getLongExtra("DisplayDistance", 0);
                TotalDaysgoal = intent.getLongExtra("TotalDaysgoal", 0);
                mTotalDaysData = intent.getLongExtra("mTotalDaysData", 0);
                TotalDailygoal = intent.getLongExtra("TotalDailygoal", 0);
                TotalDailyStep = intent.getLongExtra("TotalDailyStep", 0);
                LevelDesc = intent.getStringExtra("LevelDesc");
                DistanceDesc = intent.getStringExtra("DistanceDesc");
                DayDesc = intent.getStringExtra("DayDesc");
                DailyDesc = intent.getStringExtra("DailyDesc");

//                Log.e("TAG", "onReceive: main " + level);
                numSteps = level;
                TvSteps.setText(numSteps + "");

                /*totalsec = (int) (numSteps * 1.66);
                totalmin = totalsec / 60;*/

                totalmin = numSteps / 100;
                Logger.e(totalsec + "sec");
                Logger.e(totalmin + "min");

                if (totalmin > 60) {
                    long hours = totalmin / 60;
                    long minnutesRemaining = totalmin % 60;
                    tvduration.setText(hours + "h " + minnutesRemaining + "m");
                } else {
                    tvduration.setText(0 + "h " + totalmin + "m");
                }

                progress.setProgressMax(StorageManager.getInstance().getStepCountGoalUnit());
                progress.setProgressWithAnimation(numSteps, (long) 1000);

                distance = String.valueOf(CommanMethod.calculateDistance(numSteps));
//                tvkm.setText(distance);
                tvkm.setText(String.format("%.2f", Float.valueOf(distance)));

                calories = String.valueOf(CommanMethod.calculateCalories(numSteps, userWeight, userHeight));
                tvkcal.setText(calories);
            }
            waterWeekChartAdapter.notifyDataSetChanged();
            checkArchivement(numSteps);
        }
    }

    
}