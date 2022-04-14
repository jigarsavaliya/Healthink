package com.js.stepcounter.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.js.stepcounter.MyMarkerView;
import com.js.stepcounter.R;
import com.js.stepcounter.database.DatabaseManager;
import com.js.stepcounter.model.WaterLevelModel;
import com.js.stepcounter.model.WeightModel;
import com.js.stepcounter.sevices.AlarmReceiver;
import com.js.stepcounter.utils.CommanMethod;
import com.js.stepcounter.utils.Density;
import com.js.stepcounter.utils.Logger;
import com.js.stepcounter.utils.StorageManager;
import com.js.stepcounter.utils.constant;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HeathActivity extends AppCompatActivity implements DatePickerListener, View.OnClickListener {
    TextView muserWeight, mAddWeightDailog, meditHeightWeight, mtvlastdaydiff, mtvwaterCount;
    Calendar rightNow;
    int hour, min, date, month, year;
    DatabaseManager dbManager;
    LineChart WeightChart;
    private float userWeight = constant.DEFAULT_WEIGHT;
    private float userHeight = constant.DEFAULT_HEIGHT;
    Toolbar mToolbar;
    protected String[] BMIcategory = new String[]
            {
                    "Very Severly Underweight", "Severly Underweight", "Underweight", "Normal",
                    "Overweight", "Moderately Obese", "Severely Obese", "Very Severely Obese",
            };

    String selectedDate, seletedMonth, selecetedYear;

    ArcProgress arcProgress;
    ImageView llRemovewater, lladdwater;
    String waterGoal, DefualtCup;
    WaterLevelModel waterlevel;
    ArrayList<WaterLevelModel> waterlist;
    ArrayList<WaterLevelModel> watermonthlist;
    int StepGoal;
    String Watergoal, WaterUnit, Watercup;
    EditText etweight;
    private BarChart chart;
    TextView tvchartdate, tvMore, tvHighestKg, tvLowestkg;
    ImageView ivBackDate, ivForwardDate;
    long firstdayofmonth, lastdayofmonth;
    SwitchCompat scWaterNotification;
    double covertinml;
    int waterml = 0;
    float[] value = {0};
    String[] WaterGoalValue;
    PieChart mPcBmiChart;
    Boolean item_per = false, item_cap = true;
    RelativeLayout rlDrinkNoti;
    boolean isScnoti = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heath);
        dbManager = new DatabaseManager(this);
        waterlist = new ArrayList<>();
        watermonthlist = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = 1;

        c.set(year, month, day);
        int numOfDaysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//        Log.e("First", "First Day of month: " + c.getTimeInMillis());
        firstdayofmonth = c.getTimeInMillis();
        c.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
        lastdayofmonth = c.getTimeInMillis();
    }

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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Health Tracker");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        WeightChart = findViewById(R.id.cvWeightChart);
        muserWeight = findViewById(R.id.userWeight);
        mAddWeightDailog = findViewById(R.id.addWeight);
        meditHeightWeight = findViewById(R.id.editHeightWeight);
        mtvlastdaydiff = findViewById(R.id.tvlastdaydiff);
        mtvwaterCount = findViewById(R.id.tv_water);
        tvMore = findViewById(R.id.tvMore);
        tvHighestKg = findViewById(R.id.tvHighestKg);
        tvLowestkg = findViewById(R.id.tvLowestkg);

        scWaterNotification = findViewById(R.id.scWaterNoti);
        chart = (BarChart) findViewById(R.id.waterchart);
        tvchartdate = findViewById(R.id.tvchartdate);
        ivBackDate = findViewById(R.id.ivBackDate);
        ivForwardDate = findViewById(R.id.ivForwardDate);
        rlDrinkNoti = findViewById(R.id.rlDrinkNoti);

        ivBackDate.setOnClickListener(this);
        ivForwardDate.setOnClickListener(this);

        if (StorageManager.getInstance().getReminder()) {
            scWaterNotification.setChecked(true);
        } else {
            scWaterNotification.setChecked(false);
        }

        scWaterNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    StorageManager.getInstance().setReminder(b);
                    setNotification();
                } else {
                    StorageManager.getInstance().setReminder(false);
                }
            }
        });

        rlDrinkNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isScnoti) {
                    isScnoti=false;
                    StorageManager.getInstance().setReminder(true);
                    scWaterNotification.setChecked(true);
                    setNotification();
                } else {
                    isScnoti=true;
                    StorageManager.getInstance().setReminder(false);
                    scWaterNotification.setChecked(false);
                }
            }
        });

        mPcBmiChart = findViewById(R.id.pcBmiChart);

        muserWeight.setText(userWeight + "");

        arcProgress = findViewById(R.id.arc_progress);
        llRemovewater = findViewById(R.id.llRemovewater);
        lladdwater = findViewById(R.id.lladdwater);

        waterGoal = StorageManager.getInstance().getWaterGoal();
        DefualtCup = StorageManager.getInstance().getWaterCup();

        WaterGoalValue = waterGoal.split(" ");

        waterlist = dbManager.getCurrentDayWatercountlist(date, month, year);

        String[] WaterGoal = StorageManager.getInstance().getWaterGoal().split(" ");

        arcProgress.setMax(Integer.parseInt(WaterGoalValue[0]));

        String[] DefultCupValue = DefualtCup.split(" ");

        setdatainprogress();

        lladdwater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
//                    Log.e("TAG", "old: " + value[0]);
                    value[0] = value[0] + Integer.parseInt(DefultCupValue[0]);
//                    Log.e("TAG", "new: " + value[0]);
                    waterlevel.setUnit(String.valueOf(value[0]));
                }
                dbManager.addWaterData(waterlevel);
                setdatainprogress();
                SetWeekwiseWaterChart("cap");
                item_cap = true;
            }
        });

        llRemovewater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<WaterLevelModel> waterlevelArrayList = new ArrayList<>();
                waterlevelArrayList = dbManager.getWatercountlist();
                String lastentry = null;

                if (waterlevelArrayList != null) {
                    for (int i = 0; i < waterlevelArrayList.size(); i++) {
                        Log.e("TAG", "init: " + waterlevelArrayList.get(waterlevelArrayList.size() - 1).getTimestemp());
                        lastentry = waterlevelArrayList.get(waterlevelArrayList.size() - 1).getTimestemp();
                    }
                } else {
                    setdatainprogress();
                }
                dbManager.DeletelastWaterData(lastentry);
                setdatainprogress();
                SetWeekwiseWaterChart("cap");
                item_cap = true;
            }
        });

        mAddWeightDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddWeightDailog();
            }
        });

        meditHeightWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditHeightWeightDailog();
            }
        });

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu();
            }
        });

    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(this, tvMore);
        popup.inflate(R.menu.menu_water);
        if (item_cap) {
            popup.getMenu().findItem(R.id.item_cap).setChecked(true);
        } else {
            popup.getMenu().findItem(R.id.item_per).setChecked(true);
        }
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) item -> {
            switch (item.getItemId()) {
                case R.id.item_per:
                    item_cap = false;
                    item_per = true;
                    if (item.isChecked()) item.setChecked(false);
                    else item.setChecked(true);
                    SetWeekwiseWaterChart("per");
                    break;
                case R.id.item_cap:
                    item_cap = true;
                    item_per = false;
                    if (item.isChecked()) {
                        item.setChecked(false);
                    } else {
                        item.setChecked(true);
                    }
                    SetWeekwiseWaterChart("cap");
                    break;
                default:
                    break;
            }
            return false;
        });
        popup.show();
    }

    private void setNotification() {
        String mReminderIntervalvalue = "1";
        long intervaltime = 1000 * 60 * 60;
        if (mReminderIntervalvalue.equals("0.5")) {
            intervaltime = 1000 * 60 * 30;
        } else if (mReminderIntervalvalue.equals("1")) {
            intervaltime = 1000 * 60 * 60;
        } else if (mReminderIntervalvalue.equals("1.5")) {
            intervaltime = 1000 * 60 * 90;
        } else if (mReminderIntervalvalue.equals("2")) {
            intervaltime = 1000 * 60 * 120;
        } else if (mReminderIntervalvalue.equals("2.5")) {
            intervaltime = 1000 * 60 * 150;
        } else if (mReminderIntervalvalue.equals("3")) {
            intervaltime = 1000 * 60 * 180;
        } else if (mReminderIntervalvalue.equals("3.5")) {
            intervaltime = 1000 * 60 * 210;
        } else if (mReminderIntervalvalue.equals("4")) {
            intervaltime = 1000 * 60 * 240;
        } else if (mReminderIntervalvalue.equals("4.5")) {
            intervaltime = 1000 * 60 * 270;
        } else if (mReminderIntervalvalue.equals("5")) {
            intervaltime = 1000 * 60 * 300;
        }

        if (StorageManager.getInstance().getReminder()) {

            Calendar calendar = Calendar.getInstance();

            Intent intent1 = new Intent(HeathActivity.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervaltime, pendingIntent);
            Logger.e(calendar.getTimeInMillis());
        }

    }


    private void setdatainprogress() {
        ArrayList<WaterLevelModel> waterlevelArrayList = new ArrayList<>();

        waterlevelArrayList = dbManager.getDayWaterdata(date, month, year);

        if (WaterUnit.equals("ml")) {
            int lastentry = 0;
            if (waterlevelArrayList != null) {
                for (int i = 0; i < waterlevelArrayList.size(); i++) {
                    Log.e("TAG", "init ml: " + waterlevelArrayList.get(i).getSumwater());
                    lastentry = waterlevelArrayList.get(i).getSumwater();
                }

                if (lastentry != 0) {
                    if (lastentry < Integer.parseInt(WaterGoalValue[0])) {
                        arcProgress.setProgress(lastentry);
                    } else {
                        arcProgress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                    }
                }
                mtvwaterCount.setText(lastentry + "");
            } else {
                arcProgress.setProgress(0);
                mtvwaterCount.setText(0 + "");
            }
        } else {
            double lastentry = 0;
            if (waterlevelArrayList != null) {
                for (int i = 0; i < waterlevelArrayList.size(); i++) {
                    Log.e("TAG", "init fl : " + waterlevelArrayList.get(i).getSumwater());
                    lastentry = Math.round(CommanMethod.getMlToFloz(Float.valueOf(waterlevelArrayList.get(i).getSumwater())));
                }

                if (lastentry != 0) {
                    if (lastentry < Integer.parseInt(WaterGoalValue[0])) {
                        arcProgress.setProgress((int) lastentry);
                    } else {
                        arcProgress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                    }
                }
                mtvwaterCount.setText(lastentry + "");
            } else {
                arcProgress.setProgress(0);
                mtvwaterCount.setText(0 + "");
            }
        }
    }

    private void showEditHeightWeightDailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_edit_heightweight, null);
        dialogBuilder.setView(d);
        AlertDialog alertDialog = dialogBuilder.create();
        Button mBtnSave = (Button) d.findViewById(R.id.btnSave);
        Button mBtnCancel = (Button) d.findViewById(R.id.btnCancel);

        CardView mllLb = (CardView) d.findViewById(R.id.llLb);
        CardView mllKB = (CardView) d.findViewById(R.id.llKB);
        CardView mllcm = (CardView) d.findViewById(R.id.llcm);
        CardView mllin = (CardView) d.findViewById(R.id.llIN);

        EditText etweight = (EditText) d.findViewById(R.id.etweight);
        EditText etHeight = (EditText) d.findViewById(R.id.etHeight);

        EditText etft = (EditText) d.findViewById(R.id.etft);
        EditText etin = (EditText) d.findViewById(R.id.etin);

        final boolean[] iskg = {true};
        final boolean[] iscm = {true};

        etweight.setText(userWeight + "");
        etHeight.setText(userHeight + "");

        mllKB.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
        mllcm.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));

        mllLb.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                iskg[0] = false;
                mllLb.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                mllKB.setCardBackgroundColor(getResources().getColor(R.color.transprant));
                etweight.setText(Math.round(CommanMethod.kgToLbConverter(Double.parseDouble(etweight.getText().toString()))) + "");
            }
        });

        mllKB.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                iskg[0] = true;
                mllKB.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                mllLb.setCardBackgroundColor(getResources().getColor(R.color.transprant));
                etweight.setText(Math.round(CommanMethod.lbToKgConverter(Double.parseDouble(etweight.getText().toString()))) + "");
            }
        });

        mllcm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                iscm[0] = false;
                etft.setVisibility(View.GONE);
                etin.setVisibility(View.GONE);
                etHeight.setVisibility(View.VISIBLE);

                mllcm.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                mllin.setCardBackgroundColor(getResources().getColor(R.color.transprant));
                double value = CommanMethod.cmToInchConverter(Double.parseDouble(etHeight.getText().toString()),
                        Math.round(CommanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString()))));
                etHeight.setText(Math.round(CommanMethod.feetToCmConverter(CommanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString())), value)) + "");
            }
        });

        mllin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                etft.setVisibility(View.VISIBLE);
                etin.setVisibility(View.VISIBLE);
                etHeight.setVisibility(View.GONE);

                iscm[0] = true;
                mllin.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                mllcm.setCardBackgroundColor(getResources().getColor(R.color.transprant));

//                etHeight.setText(Math.round(commanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString()))) + " ft " +
//                        commanMethod.cmToInchConverter(Double.parseDouble(etHeight.getText().toString()),
//                        Math.round(commanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString())))) + " in ");
                etft.setText(Math.round(CommanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString()))) + " ft");
                etin.setText(CommanMethod.cmToInchConverter(Double.parseDouble(etHeight.getText().toString()),
                        CommanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString()))) + " in ");
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double value = CommanMethod.cmToInchConverter(Double.parseDouble(etHeight.getText().toString()),
                        Math.round(CommanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString()))));

                StorageManager.getInstance().setHeight((float) Math.round(CommanMethod.feetToCmConverter(CommanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString())), value)));

                WeightModel weightModel = new WeightModel();
                weightModel.setDate(Integer.parseInt(selectedDate));
                weightModel.setMonth(Integer.parseInt(seletedMonth));
                weightModel.setYear(Integer.parseInt(selecetedYear));
                if (iskg[0]) {
                    weightModel.setKg(Math.round(Float.parseFloat(etweight.getText().toString())));
                } else {
                    weightModel.setKg((int) Math.round(CommanMethod.lbToKgConverter(Double.parseDouble(etweight.getText().toString()))));
                }
                dbManager.addWeightData(weightModel);
                if (Integer.parseInt(selectedDate) == Calendar.DATE) {
                    StorageManager.getInstance().setHeight(Float.parseFloat(etHeight.getText().toString()));
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

        picker.setListener(this)
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

    private void setWeightChart() {
        String Value = dbManager.getMinMaxWeight();
        String[] MinMax = Value.split("-");
        tvLowestkg.setText(MinMax[0] + "Kg");
        tvHighestKg.setText(MinMax[1] + "Kg");

        Calendar cal = Calendar.getInstance();
        long fristdate = cal.getTimeInMillis();
        cal.add(Calendar.DATE, -30);
        long last30day = cal.getTimeInMillis();

        ArrayList<WeightModel> modelArrayList = new ArrayList<>();
        ArrayList<WeightModel> arrayList = new ArrayList<>();
        ArrayList<Entry> weightModelArrayList = new ArrayList<>();

        modelArrayList = dbManager.getCurrentDayWeightlist(date, month, year);

        if (modelArrayList.size() != 0) {
            for (int i = 0; i < modelArrayList.size(); i++) {
                muserWeight.setText("" + modelArrayList.get(i).getKg());
            }
        } else {
            muserWeight.setText("" + 0);
        }

        int pervalue = 0;
        arrayList = dbManager.getCurrentDayWeightlist(date - 1, month, year);

        if (arrayList.size() != 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                pervalue = arrayList.get(i).getKg();
            }
        }

        Logger.e(pervalue);
        int cuurrvalue = Integer.parseInt(muserWeight.getText().toString());

        int diff = 0;
        diff = pervalue - cuurrvalue;

//        Log.e("TAG", pervalue + "setWeightChart: " + cuurrvalue);
        if (diff > 0) {
            mtvlastdaydiff.setText(diff + "KG");
        } else {
            mtvlastdaydiff.setText("0 KG");
        }

        WeightChart.setBackgroundColor(Color.WHITE);

        // disable description text
        WeightChart.getDescription().setEnabled(false);

        // enable touch gestures
        WeightChart.setTouchEnabled(true);

        // set listeners
        WeightChart.setDrawGridBackground(false);

        // create marker to display box when values are selected
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Kg");
        mv.setChartView(WeightChart);
        WeightChart.setMarker(mv);
        WeightChart.setDragEnabled(false);
        WeightChart.setScaleEnabled(false);
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
//        xAxis.setAxisMinimum(cal.get(Calendar.DAY_OF_MONTH) - 30);

        YAxis yAxis;
        yAxis = WeightChart.getAxisLeft();

        // disable dual axis (only use LEFT axis)
        WeightChart.getAxisRight().setEnabled(false);

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
                startActivity(new Intent(HeathActivity.this, WaterSettingActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSharedPreferences();
        init();
        SetWeekwiseWaterChart("cap");
        setWeightChart();
        setBMIReportChart();
    }

    private void setBMIReportChart() {
        mPcBmiChart.setBackgroundColor(Color.WHITE);

        mPcBmiChart.setUsePercentValues(true);
        mPcBmiChart.getDescription().setEnabled(false);
        double heightM = userHeight / 100;
        double BMI = (userWeight) / (heightM * heightM);
        DecimalFormat df = new DecimalFormat("#.#");
        double BMItrimmed = Double.parseDouble(df.format(BMI));
        String BMI_Cat = null;
        if (BMI < 15) {
            BMI_Cat = "Very severely underweight";
        } else if (BMI >= 15 && BMI < 16) {
            BMI_Cat = "Severely underweight";
        } else if (BMI >= 16 && BMI < 18.5) {
            BMI_Cat = "Underweight";
        } else if (BMI >= 18.5 && BMI < 25) {
            BMI_Cat = "Normal";
        } else if (BMI >= 25 && BMI < 30) {
            BMI_Cat = "Overweight";
        } else if (BMI >= 30 && BMI < 35) {
            BMI_Cat = "Moderately Obese";
        } else if (BMI >= 35 && BMI < 40) {
            BMI_Cat = "Severely Obese";
        } else if (BMI < 40) {
            BMI_Cat = "Very Severely Obese";
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;

        int offset = (int) (height * 0.18); /* percent to move */

        RelativeLayout.LayoutParams rlParams =
                (RelativeLayout.LayoutParams) mPcBmiChart.getLayoutParams();

        rlParams.setMargins(Density.dp2px(this, 10), 0, Density.dp2px(this, 10), -offset);
        mPcBmiChart.setLayoutParams(rlParams);
        mPcBmiChart.setCenterTextOffset(0, -offset);

        mPcBmiChart.setCenterText(BMItrimmed + "\n " + BMI_Cat);

        mPcBmiChart.setDrawHoleEnabled(true);
        mPcBmiChart.setHoleColor(Color.WHITE);

        mPcBmiChart.setTransparentCircleColor(Color.WHITE);
        mPcBmiChart.setTransparentCircleAlpha(110);

        mPcBmiChart.setHoleRadius(58f);
        mPcBmiChart.setTransparentCircleRadius(61f);

        mPcBmiChart.setDrawCenterText(true);

        mPcBmiChart.setRotationEnabled(false);
        mPcBmiChart.setHighlightPerTapEnabled(true);

        mPcBmiChart.setMaxAngle(180f); // HALF CHART
        mPcBmiChart.setRotationAngle(180f);
        mPcBmiChart.setCenterTextOffset(0, -20);

        ArrayList<PieEntry> values = new ArrayList<>();

        ArrayList<Float> BMI_trimmed = new ArrayList<>();
        BMI_trimmed.add((float) 15.0);
        BMI_trimmed.add((float) 16.0);
        BMI_trimmed.add((float) 18.5);
        BMI_trimmed.add((float) 25.0);
        BMI_trimmed.add((float) 30.0);
        BMI_trimmed.add((float) 35.0);
        BMI_trimmed.add((float) 40.0);

        for (int i = 0; i < BMI_trimmed.size(); i++) {
            values.add(new PieEntry(i, BMIcategory[i % BMIcategory.length], BMI_trimmed.get(i)));
        }

        PieDataSet dataSet = new PieDataSet(values, "BMI");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mPcBmiChart.setData(data);

        mPcBmiChart.invalidate();
        mPcBmiChart.setDrawSliceText(false);
        mPcBmiChart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = mPcBmiChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setWordWrapEnabled(true);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(0f);
//        l.setEnabled(false);

        // entry label styling
        mPcBmiChart.setEntryLabelColor(Color.WHITE);
        mPcBmiChart.setEntryLabelTextSize(0f);
        mPcBmiChart.getDescription().setEnabled(false);

        mPcBmiChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
//                mPcBmiChart.highlightValue(h);
                Logger.e(h);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void setSharedPreferences() {
        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();
        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
        Watergoal = StorageManager.getInstance().getWaterGoal();
        Watercup = StorageManager.getInstance().getWaterCup();
        WaterUnit = StorageManager.getInstance().getWaterUnit();
    }


    public static String getCurrentWeekdate(Calendar mCalendar) {
//        Date date = new Date();
//        mCalendar.setTime(date);

        // 1 = Sunday, 2 = Monday, etc.
        int day_of_week = mCalendar.get(Calendar.DAY_OF_WEEK);

        int monday_offset;
        if (day_of_week == 1) {
            monday_offset = -6;
        } else
            monday_offset = (2 - day_of_week); // need to minus back
        mCalendar.add(Calendar.DAY_OF_YEAR, monday_offset);

        Date mDateMonday = mCalendar.getTime();

//        Log.e("mDateMonday", "" + mCalendar.getTimeInMillis());
        // return 6 the next days of current day (object cal save current day)
        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
        Date mDateSunday = mCalendar.getTime();

//        Log.e("mDateSunday", "" + mCalendar.getTimeInMillis());

        //Get format date
        String strDateFormat = "dd MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

        String MONDAY = sdf.format(mDateMonday);
        String SUNDAY = sdf.format(mDateSunday);

        // Sub String
        if ((MONDAY.substring(3, 6)).equals(SUNDAY.substring(3, 6))) {
            MONDAY = MONDAY.substring(0, 2);
        }

        return MONDAY + " - " + SUNDAY;
    }


    private void SetWeekwiseWaterChart(String type) {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");
        xAxisLabel.add("Sun");

        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                try {
                    IBarDataSet dataSet = chart.getData().getDataSetByIndex(0);
                    return dataSet.getEntryForIndex((int) value).getData().toString();
                } catch (Exception e) {
                    return "";
                }
            }
        });

        Legend L = chart.getLegend();
        L.setEnabled(false);

        YAxis leftAxis = chart.getAxisRight();
        leftAxis.setLabelCount(4, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawZeroLine(false); // draw a zero line
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(6000F);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        //        LimitLine ll1 = new LimitLine(Integer.parseInt(Watergoal));
//        ll1.setLineWidth(1f);
//        ll1.enableDashedLine(1f, 1f, 0f);

        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(ll1);
        leftAxis.setDrawLabels(false);


        BarData data = new BarData(setWeekData(type));
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Kg");
        mv.setChartView(chart);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
//        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDrawValueAboveBar(true);

        chart.invalidate();

    }

    public static String getCurrentWeek(Calendar mCalendar) {
//        Date date = new Date();
//        mCalendar.setTime(date);

        // 1 = Sunday, 2 = Monday, etc.
        int day_of_week = mCalendar.get(Calendar.DAY_OF_WEEK);

        int monday_offset;
        if (day_of_week == 1) {
            monday_offset = -6;
        } else
            monday_offset = (2 - day_of_week); // need to minus back
        mCalendar.add(Calendar.DAY_OF_YEAR, monday_offset);

//        Date mDateMonday = mCalendar.getTime();
        long mDateMonday = mCalendar.getTimeInMillis();

//        Log.e("mDateMonday", "" + mCalendar.getTimeInMillis());
        // return 6 the next days of current day (object cal save current day)
        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
//        Date mDateSunday = mCalendar.getTime();
        long mDateSunday = mCalendar.getTimeInMillis();

//        Log.e("mDateSunday", "" + mCalendar.getTimeInMillis());

        //Get format date
        String strDateFormat = "dd MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

        String MONDAY = sdf.format(mDateMonday);
        String SUNDAY = sdf.format(mDateSunday);

        // Sub String
        if ((MONDAY.substring(3, 6)).equals(SUNDAY.substring(3, 6))) {
            MONDAY = MONDAY.substring(0, 2);
        }

//        return MONDAY + " - " + SUNDAY;
        return mDateMonday + " - " + mDateSunday;
    }

    private BarDataSet setWeekData(String type) {
        WaterGoalValue = Watergoal.split(" ");

        String s1 = getCurrentWeekdate(rightNow);
        String[] Weekdate1 = s1.split("-");
        tvchartdate.setText(Weekdate1[0] + " - " + Weekdate1[1]);

        String s = getCurrentWeek(rightNow);
        String[] Weekdate = s.split("-");
//        Log.e("TAG", "date: " + Weekdate[0]);
//        Log.e("TAG", "date: " + Weekdate[1]);
        String fristdate = Weekdate[0];
        String lastdate = Weekdate[1];

        watermonthlist = dbManager.getweekWaterdata(fristdate, lastdate);

        ArrayList<BarEntry> entries = new ArrayList<>();
        if (type.equals("cap")) {
            if (watermonthlist != null) {
                for (int i = 0; i < watermonthlist.size(); i++) {
//                Log.e("TAG", watermonthlist.get(i).getDate() + "total days" + watermonthlist.get(i).getSumwater());
                /*SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
                String dayName = null;
                try {
                    Date myDate = inFormat.parse(watermonthlist.get(i).getDate() + "-" + watermonthlist.get(i).getMonth() + "-" + watermonthlist.get(i).getYear());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
                    dayName = simpleDateFormat.format(myDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                    entries.add(new BarEntry(i, watermonthlist.get(i).getSumwater(), watermonthlist.get(i).getDate()));
                }
            }
        } else {
            if (watermonthlist != null) {
                for (int i = 0; i < watermonthlist.size(); i++) {
                    float value = Float.parseFloat(WaterGoalValue[0]);
                    float finalValue = watermonthlist.get(i).getSumwater() / value * 100;
//                    Logger.e(watermonthlist.get(i).getSumwater());
//                    Logger.e(value);
//                    Logger.e(finalValue);
                    entries.add(new BarEntry(i, finalValue, watermonthlist.get(i).getDate()));
                }
            }
        }

        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);


        return set;
    }

    public static int getMaxDaysInMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        // Note: 0-based months
        cal.set(year, month, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBackDate:
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                rightNow.add(Calendar.DAY_OF_YEAR, -7);
                rightNow.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                date = rightNow.get(Calendar.DATE);
                month = rightNow.get(Calendar.MONTH) + 1;
                year = rightNow.get(Calendar.YEAR);
                s.format(new Date(rightNow.getTimeInMillis()));
                Log.e("Start", "Start Date = " + rightNow.getTimeInMillis());
                Log.e("Start", "Start Date = " + s.format(new Date(rightNow.getTimeInMillis())));

                SetWeekwiseWaterChart("cap");

                break;
            case R.id.ivForwardDate:

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                rightNow.add(Calendar.DAY_OF_YEAR, +7);
                rightNow.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                date = rightNow.get(Calendar.DATE);
                month = rightNow.get(Calendar.MONTH) + 1;
                year = rightNow.get(Calendar.YEAR);
                simpleDateFormat.format(new Date(rightNow.getTimeInMillis()));
                Log.e("Start", "Start Date = " + rightNow.getTimeInMillis());
                Log.e("Start", "Start Date = " + simpleDateFormat.format(new Date(rightNow.getTimeInMillis())));

                SetWeekwiseWaterChart("cap");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}