package com.android.stepcounter.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.IAxisValueFormatter;
import com.android.stepcounter.MyMarkerView;
import com.android.stepcounter.R;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.model.StepCountModel;
import com.android.stepcounter.sevices.NotificationReceiver;
import com.android.stepcounter.utils.CommanMethod;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.constant;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class StepReportActivity extends AppCompatActivity implements OnChartValueSelectedListener, View.OnClickListener {
    Toolbar mToolbar;
    DatabaseManager dbManager;
    private BarChart chart;
    CardView mcvDay, mcvWeek, mcvMonth, mcvStep, mcvCalories, mcvTime, mcvDistance;
    TextView tvTotal, tvchartdate;
    ImageView ivBackDate, ivForwardDate;
    ArrayList<StepCountModel> Steplist;
    ArrayList<StepCountModel> StepWeeklist;
    ArrayList<StepCountModel> Stepmonthlist;
    private static final String[] DAYS = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    StepCountModel stepcountModel;
    private float userWeight;
    private float userHeight;
    Calendar rightNow = Calendar.getInstance();
    boolean IsSelectedDay = true, IsSelectedMonth = false, IsSelectedWeek = false;
    boolean IsSelectedStep = true, IsSelectedCaleroie = false, IsSelectedTime = false, IsSelectedDistance = false;
    int date;
    int month;
    int year;
    int StepGoal;

    long firstdayofmonth, lastdayofmonth;
    ArrayList<StepCountModel> getoldSteplist;
    int oldsteptotal = 0;
    int TotalStepCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_report);
        dbManager = new DatabaseManager(this);
        Steplist = new ArrayList<StepCountModel>();
        StepWeeklist = new ArrayList<StepCountModel>();
        Stepmonthlist = new ArrayList<StepCountModel>();
        getoldSteplist = new ArrayList<StepCountModel>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            date = getIntent().getIntExtra("stepdate", 0);
            month = getIntent().getIntExtra("stepmonth", 0);
            year = getIntent().getIntExtra("stepyear", 0);
//            Logger.e(date + "/" + month + "/" + year);
        } else {
            date = rightNow.get(Calendar.DATE);
            month = rightNow.get(Calendar.MONTH) + 1;
            year = rightNow.get(Calendar.YEAR);
        }

//        Steplist = dbManager.getCurrentDayStepcountlist(date, month, year);

        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();

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
//        Log.e("Last", "Last Day of month: " + c.getTimeInMillis());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSharedPreferences();
        init();
    }

    private void setSharedPreferences() {
        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Report");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        chart = (BarChart) findViewById(R.id.chart1);
        tvTotal = findViewById(R.id.tvTotal);

        tvchartdate = findViewById(R.id.tvchartdate);
        ivBackDate = findViewById(R.id.ivBackDate);
        ivForwardDate = findViewById(R.id.ivForwardDate);

        ivBackDate.setOnClickListener(this);
        ivForwardDate.setOnClickListener(this);

        mcvDay = findViewById(R.id.cvDay);
        mcvWeek = findViewById(R.id.cvWeek);
        mcvMonth = findViewById(R.id.cvMonth);

        mcvStep = findViewById(R.id.cvStep);
        mcvCalories = findViewById(R.id.cvCalories);
        mcvTime = findViewById(R.id.cvTime);
        mcvDistance = findViewById(R.id.cvDistance);

        mcvDay.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcvStep.setCardBackgroundColor(Color.parseColor("#E6E6E6"));

        SetDaywiseStepChart();

        mcvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsSelectedDay = true;
                IsSelectedMonth = false;
                IsSelectedWeek = false;

                if (IsSelectedStep) {
                    SetDaywiseStepChart();
                } else if (IsSelectedCaleroie) {
                    SetDaywiseCaloriesChart();
                } else if (IsSelectedTime) {
                    SetDaywiseTimeChart();
                } else if (IsSelectedDistance) {
                    SetDaywiseDistanceChart();
                }
                mcvDay.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mcvWeek.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvMonth.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mcvWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsSelectedDay = false;
                IsSelectedMonth = false;
                IsSelectedWeek = true;

                if (IsSelectedStep) {
                    SetWeekwiseStepChart();
                } else if (IsSelectedCaleroie) {
                    SetWeekwiseCaloriesChart();
                } else if (IsSelectedTime) {
                    SetWeekwiseTimeChart();
                } else if (IsSelectedDistance) {
                    SetWeekwiseDistanceChart();
                }

                mcvDay.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvWeek.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mcvMonth.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mcvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsSelectedDay = false;
                IsSelectedMonth = true;
                IsSelectedWeek = false;

                if (IsSelectedStep) {
                    SetMonthwiseStepChart();
                } else if (IsSelectedCaleroie) {
                    SetMonthwiseCaloriesChart();
                } else if (IsSelectedTime) {
                    SetMonthwiseTimeChart();
                } else if (IsSelectedDistance) {
                    SetMonthwiseDistanceChart();
                }
                mcvDay.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvWeek.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvMonth.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            }
        });

        mcvStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsSelectedStep = true;
                IsSelectedCaleroie = false;
                IsSelectedTime = false;
                IsSelectedDistance = false;

                if (IsSelectedDay) {
                    SetDaywiseStepChart();
                } else if (IsSelectedWeek) {
                    SetWeekwiseStepChart();
                } else if (IsSelectedMonth) {
                    SetMonthwiseStepChart();
                }
                mcvStep.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mcvCalories.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvTime.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvDistance.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mcvCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsSelectedStep = false;
                IsSelectedCaleroie = true;
                IsSelectedTime = false;
                IsSelectedDistance = false;
                if (IsSelectedDay) {
                    SetDaywiseCaloriesChart();
                } else if (IsSelectedWeek) {
                    SetWeekwiseCaloriesChart();
                } else if (IsSelectedMonth) {
                    SetMonthwiseCaloriesChart();
                }
                mcvStep.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvCalories.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mcvTime.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvDistance.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mcvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsSelectedStep = false;
                IsSelectedCaleroie = false;
                IsSelectedTime = true;
                IsSelectedDistance = false;
                if (IsSelectedDay) {
                    SetDaywiseTimeChart();
                } else if (IsSelectedWeek) {
                    SetWeekwiseTimeChart();
                } else if (IsSelectedMonth) {
                    SetMonthwiseTimeChart();
                }
                mcvStep.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvCalories.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvTime.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mcvDistance.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mcvDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsSelectedDay) {
                    SetDaywiseDistanceChart();
                } else if (IsSelectedWeek) {
                    SetWeekwiseDistanceChart();
                } else if (IsSelectedMonth) {
                    SetMonthwiseDistanceChart();
                }
                IsSelectedStep = false;
                IsSelectedCaleroie = false;
                IsSelectedTime = false;
                IsSelectedDistance = true;
                mcvStep.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvCalories.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvTime.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvDistance.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            }
        });
    }

    private void SetDaywiseStepChart() {
        int TotalStepCount = dbManager.getSumOfStepList(date, month, year);
        tvTotal.setText(TotalStepCount + "");

        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        YAxis rightAxis = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawLabels(false);

        LimitLine ll1 = new LimitLine(StepGoal);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(1f, 1f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        rightAxis.setDrawLabels(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        rightAxis.addLimitLine(ll1);

        BarData data = new BarData(setDayData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Steps");
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.getXAxis().setEnabled(false);
//        chart.getAxisRight().setAxisMaximum(StepGoal);
//        chart.getAxisRight().setAxisMinimum(0);


        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDrawValueAboveBar(true);

    }

    private BarDataSet setDayData() {
        Steplist = dbManager.getCurrentDayStepcountlist(date, month, year);
        tvchartdate.setText(date + "-" + month + "-" + year);

        ArrayList<BarEntry> entries = new ArrayList<>();
        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
                entries.add(new BarEntry(i, Steplist.get(i).getStep(), Steplist.get(i).getDuration()));
            }
        }
        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    private void SetMonthwiseStepChart() {

        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        YAxis rightAxis = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);

        LimitLine ll1 = new LimitLine(StepGoal);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(1f, 1f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        rightAxis.setDrawZeroLine(false);
        rightAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        rightAxis.addLimitLine(ll1);

        rightAxis.setAxisMaximum(6000);
        rightAxis.setAxisMinimum(0);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawZeroLine(false);

        BarData data = new BarData(setMonthData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Steps");
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.getXAxis().setEnabled(false);
//        chart.getAxisRight().setAxisMaximum(StepGoal);
//        chart.getAxisRight().setAxisMinimum(0);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDrawValueAboveBar(true);

    }

    public static int getMaxDaysInMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        // Note: 0-based months
        cal.set(year, month, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private BarDataSet setMonthData() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime());

        tvchartdate.setText(month_name);

        int a = getMaxDaysInMonth(rightNow.get(Calendar.MONTH) + 1, rightNow.get(Calendar.YEAR));
//        Log.e("TAG", a + "total days");

        Stepmonthlist = dbManager.getMonthstepdata(String.valueOf(firstdayofmonth), String.valueOf(lastdayofmonth), a);

        int sumvalue = 0;
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (Stepmonthlist != null) {
            for (int i = 0; i < Stepmonthlist.size(); i++) {
//                Log.e("TAG", "hours: " + Stepmonthlist.get(i).getDistance() + "step" + Stepmonthlist.get(i).getStep() + "date" + Stepmonthlist.get(i).getDate());
                if (rightNow.get(Calendar.DATE) == Stepmonthlist.get(i).getDate()) {
                    entries.add(new BarEntry(Stepmonthlist.get(i).getDate(), Stepmonthlist.get(i).getSumstep(), getResources().getColor(R.color.colorPrimaryDark)));
                } else {
                    entries.add(new BarEntry(Stepmonthlist.get(i).getDate(), Stepmonthlist.get(i).getSumstep()));
                }
                sumvalue = sumvalue + Stepmonthlist.get(i).getSumstep();
            }
        }

        tvTotal.setText(sumvalue + "");

        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    private void SetWeekwiseStepChart() {
        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        chart.setDrawGridBackground(false);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisValues()));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setCenterAxisLabels(true);
        /*xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                try {
                    IBarDataSet dataSet = chart.getData().getDataSetByIndex(0);
                    return dataSet.getEntryForIndex((int) value).getData().toString();
                } catch (Exception e) {
                    return "";
                }
            }
        });*/

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getXAxisValues().get((int) value);
            }
        });


        YAxis leftAxis = chart.getAxisRight();

        LimitLine ll1 = new LimitLine(StepGoal);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(1f, 1f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);

        leftAxis.setLabelCount(4, false);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(7000);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        BarData data = new BarData(setWeekData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Steps");
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.getXAxis().setEnabled(false);
//        chart.getAxisRight().setAxisMaximum(StepGoal);
//        chart.getAxisRight().setAxisMinimum(0);


        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.setDrawValueAboveBar(true);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDrawValueAboveBar(true);

    }

    private BarDataSet setWeekData() {
        String s1 = getCurrentWeekdate(rightNow);
        String[] Weekdate1 = s1.split("-");
        tvchartdate.setText(Weekdate1[0] + " - " + Weekdate1[1]);

        String s = getCurrentWeek(rightNow);
        String[] Weekdate = s.split("-");
        Log.e("TAG", "date: " + Weekdate[0]);
        Log.e("TAG", "date: " + Weekdate[1]);
        String fristdate = Weekdate[0];
        String lastdate = Weekdate[1];

        StepWeeklist = dbManager.getweekstepdata(fristdate, lastdate);
        int sumvalue = 0;
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (StepWeeklist != null) {
            for (int i = 0; i < StepWeeklist.size(); i++) {
                Log.e("TAG", "step" + StepWeeklist.get(i).getDate() + "sum" + StepWeeklist.get(i).getSumstep());
                entries.add(new BarEntry(i, StepWeeklist.get(i).getSumstep(), StepWeeklist.get(i).getDate()));
                sumvalue = sumvalue + StepWeeklist.get(i).getSumstep();
            }
        }

        tvTotal.setText(sumvalue + "");

        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }


    //calories data

    private void SetDaywiseCaloriesChart() {
        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setCenterAxisLabels(true);

//        leftAxis.setTextSize(10f);
//        leftAxis.setDrawLabels(false);
//        leftAxis.setDrawAxisLine(true);
//        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawZeroLine(false);

        BarData data = new BarData(setDayCaloriesData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Calories");
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDrawValueAboveBar(true);

    }

    private BarDataSet setDayCaloriesData() {
        Steplist = dbManager.getCurrentDayStepcountlist(date, month, year);
        tvchartdate.setText(date + "-" + month + "-" + year);

        Float sumvalue = Float.valueOf(0);
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
//                Log.e("TAG", "hours: " + Steplist.get(i).getDuration() + "step" + Steplist.get(i).getStep() + "date" + Steplist.get(i).getDate());
                entries.add(new BarEntry(Steplist.get(i).getDuration(), Float.parseFloat(Steplist.get(i).getCalorie())));
                sumvalue = sumvalue + Float.valueOf(Steplist.get(i).getCalorie());
            }
        }
        tvTotal.setText(sumvalue + "");
        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    private void SetMonthwiseCaloriesChart() {

        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

//        leftAxis.setTextSize(10f);
//        leftAxis.setDrawLabels(false);
//        leftAxis.setDrawAxisLine(true);
//        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        BarData data = new BarData(setMonthCaloriesData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Calories");
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDrawValueAboveBar(true);

    }

    private BarDataSet setMonthCaloriesData() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime());

        tvchartdate.setText(month_name);
        int a = getMaxDaysInMonth(rightNow.get(Calendar.MONTH) + 1, rightNow.get(Calendar.YEAR));
        Log.e("TAG", a + "total days");

        Stepmonthlist = dbManager.getMonthCaloriesdata(String.valueOf(firstdayofmonth), String.valueOf(lastdayofmonth), a);
        int sumvalue = 0;
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (Stepmonthlist != null) {
            for (int i = 0; i < Stepmonthlist.size(); i++) {
//                Log.e("TAG", "hours: " + Stepmonthlist.get(i).getDistance() + "step" + Stepmonthlist.get(i).getStep() + "date" + Stepmonthlist.get(i).getDate());
                entries.add(new BarEntry(Stepmonthlist.get(i).getDate(), Stepmonthlist.get(i).getSumcalorie()));
                sumvalue = sumvalue + Stepmonthlist.get(i).getSumcalorie();
            }
        }
        tvTotal.setText(sumvalue + "");
        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Mon");
        xAxis.add("Tue");
        xAxis.add("Web");
        xAxis.add("Thu");
        xAxis.add("Fri");
        xAxis.add("Sat");
        xAxis.add("Sun");
        return xAxis;
    }

    private void SetWeekwiseCaloriesChart() {

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
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

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
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setDrawLabels(false);

//        BarData data = new BarData(getXAxisValues(), setWeekCaloriesData());
        BarData data = new BarData(setWeekCaloriesData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Calories");
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
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

    private BarDataSet setWeekCaloriesData() {
        String s1 = getCurrentWeekdate(rightNow);
        String[] Weekdate1 = s1.split("-");
        tvchartdate.setText(Weekdate1[0] + " - " + Weekdate1[1]);

        String s = getCurrentWeek(rightNow);
//        Log.e("TAG", "date: " + s);
        String[] Weekdate = s.split("-");
        int stepnumber;
        String fristdate = Weekdate[0];
        String lastdate = Weekdate[1];
        int sumvalue = 0;
        StepWeeklist = dbManager.getweekCaloriesdata(fristdate, lastdate);


        ArrayList<BarEntry> entries = new ArrayList<>();
        if (StepWeeklist != null) {
            for (int i = 0; i < StepWeeklist.size(); i++) {
//                Log.e("TAG", "hours: " + StepWeeklist.get(i).getDistance() + "step" + StepWeeklist.get(i).getStep() + "date" + StepWeeklist.get(i).getDate());
                entries.add(new BarEntry(i, Float.parseFloat(String.valueOf(StepWeeklist.get(i).getSumcalorie())), StepWeeklist.get(i).getDate()));
                sumvalue = sumvalue + StepWeeklist.get(i).getSumcalorie();
            }
        }

        tvTotal.setText(sumvalue + "");
        BarDataSet set = new BarDataSet(entries, "");


        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    ////////////////////////////////////time data//////////////////////////////////////////

    private void SetDaywiseTimeChart() {
        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

//        leftAxis.setTextSize(10f);
//        leftAxis.setDrawLabels(false);
//        leftAxis.setDrawAxisLine(true);
//        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        BarData data = new BarData(setDayTimeData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "m");
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.setDrawValueAboveBar(true);

    }

    private BarDataSet setDayTimeData() {
        Steplist = dbManager.getCurrentDayStepcountlist(date, month, year);
        tvchartdate.setText(date + "-" + month + "-" + year);

        int sumvalue = 0;
        int minitvalue = 0;
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
//                Log.e("TAG", "hours: " + Steplist.get(i).getDuration() + "step" + Steplist.get(i).getStep() + "date" + Steplist.get(i).getDate());
                int totalSecs = (int) (Steplist.get(i).getStep() * 1.66);
                if (totalSecs < 60) {
                    entries.add(new BarEntry(Steplist.get(i).getDuration(), totalSecs));
                    sumvalue = sumvalue + totalSecs;
                } else {
                    int min = totalSecs / 60;
//                    int sec = totalSecs % 60;
                    entries.add(new BarEntry(Steplist.get(i).getDuration(), min));
                    minitvalue = minitvalue + min;
                }

            }
        }
        tvTotal.setText(minitvalue + "m " + sumvalue + "s");
        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    private void SetMonthwiseTimeChart() {

        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();

        XAxis xAxis = chart.getXAxis();

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
//        xAxis.setValueFormatter((ValueFormatter) xAxisFormatter);

//        leftAxis.setTextSize(10f);
//        leftAxis.setDrawLabels(false);
//        leftAxis.setDrawAxisLine(true);
//        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        BarData data = new BarData(setMonthTimeData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "m");
        mv.setChartView(chart);

       /* XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(chart);*/ // For bounds control
        chart.setMarker(mv);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDrawValueAboveBar(true);
    }

    private BarDataSet setMonthTimeData() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime());

        tvchartdate.setText(month_name);
        int a = getMaxDaysInMonth(rightNow.get(Calendar.MONTH) + 1, rightNow.get(Calendar.YEAR));
        Stepmonthlist = dbManager.getMonthstepdata(String.valueOf(firstdayofmonth), String.valueOf(lastdayofmonth), a);

        int sumvalue = 0;
        int minitvalue = 0;
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (Stepmonthlist != null) {
            for (int i = 0; i < Stepmonthlist.size(); i++) {
                int totalSecs = (int) (Stepmonthlist.get(i).getSumstep() * 1.66);
                if (totalSecs < 60) {
                    entries.add(new BarEntry(Stepmonthlist.get(i).getDate(), totalSecs));
                    sumvalue = sumvalue + totalSecs;
                } else {
                    int min = totalSecs / 60;
//                    int sec = totalSecs % 60;
                    entries.add(new BarEntry(Stepmonthlist.get(i).getDate(), min));
                    minitvalue = minitvalue + min;
                }
            }
        }

        tvTotal.setText(minitvalue + "m " + sumvalue + "s");

        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    private void SetWeekwiseTimeChart() {

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

        leftAxis.setDrawLabels(false);

        BarData data = new BarData(setWeekTimeData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "m");
        mv.setChartView(chart);
        chart.setMarker(mv);


        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
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

    private BarDataSet setWeekTimeData() {
        String s1 = getCurrentWeekdate(rightNow);
        String[] Weekdate1 = s1.split("-");
        tvchartdate.setText(Weekdate1[0] + " - " + Weekdate1[1]);

        String s = getCurrentWeek(rightNow);
//        Log.e("TAG", "date: " + s);
        String[] Weekdate = s.split("-");
        int stepnumber;
        String fristdate = Weekdate[0];
        String lastdate = Weekdate[1];

        StepWeeklist = dbManager.getweekstepdata(fristdate, lastdate);

        int sumvalue = 0;
        int minitvalue = 0;

        ArrayList<BarEntry> entries = new ArrayList<>();
        if (StepWeeklist != null) {
            for (int i = 0; i < StepWeeklist.size(); i++) {
//                Log.e("TAG", "hours: " + StepWeeklist.get(i).getDistance() + "step" + StepWeeklist.get(i).getStep() + "date" + StepWeeklist.get(i).getDate());
                int totalSecs = (int) (StepWeeklist.get(i).getSumstep() * 1.66);
                if (totalSecs < 60) {
                    entries.add(new BarEntry(i, totalSecs, StepWeeklist.get(i).getDate()));
                    sumvalue = sumvalue + totalSecs;
                } else {
                    int min = totalSecs / 60;
//                    int sec = totalSecs % 60;
                    entries.add(new BarEntry(i, min, StepWeeklist.get(i).getDate()));
                    minitvalue = minitvalue + min;
                }
            }
        }
        tvTotal.setText(minitvalue + "m " + sumvalue + "s");
        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    ////////////////////////////////////Distance data//////////////////////////////////////////

    private void SetDaywiseDistanceChart() {
        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

//        leftAxis.setTextSize(10f);
//        leftAxis.setDrawLabels(false);
//        leftAxis.setDrawAxisLine(true);
//        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        BarData data = new BarData(setDayDistanceData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "km");
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.setDrawValueAboveBar(true);

    }

    private BarDataSet setDayDistanceData() {
        Steplist = dbManager.getCurrentDayStepcountlist(date, month, year);
        tvchartdate.setText(date + "-" + month + "-" + year);

        float sumvalue = 0;
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
//                Log.e("TAG", "hours: " + Steplist.get(i).getDuration() + "step" + Steplist.get(i).getStep() + "date" + Steplist.get(i).getDate());
                entries.add(new BarEntry(Steplist.get(i).getDuration(), Float.parseFloat(Steplist.get(i).getDistance())));
                sumvalue = sumvalue + Float.parseFloat(Steplist.get(i).getDistance());
            }
        }
        tvTotal.setText(String.format("%.2f", sumvalue) + "");
        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    private void SetMonthwiseDistanceChart() {

        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

//        leftAxis.setTextSize(10f);
//        leftAxis.setDrawLabels(false);
//        leftAxis.setDrawAxisLine(true);
//        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        BarData data = new BarData(setMonthDistanceData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "km");
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDrawValueAboveBar(true);

    }

    private BarDataSet setMonthDistanceData() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime());

        tvchartdate.setText(month_name);
        int a = getMaxDaysInMonth(rightNow.get(Calendar.MONTH) + 1, rightNow.get(Calendar.YEAR));

        float sumvalue = 0;
        Stepmonthlist = dbManager.getMonthDistancedata(String.valueOf(firstdayofmonth), String.valueOf(lastdayofmonth), a);

        ArrayList<BarEntry> entries = new ArrayList<>();
        if (Stepmonthlist != null) {
            for (int i = 0; i < Stepmonthlist.size(); i++) {
//                Log.e("TAG", "hours: " + Stepmonthlist.get(i).getDistance() + "step" + Stepmonthlist.get(i).getStep() + "date" + Stepmonthlist.get(i).getDate());
                entries.add(new BarEntry(Stepmonthlist.get(i).getDate(), Stepmonthlist.get(i).getSumdistance()));
                sumvalue = sumvalue + Stepmonthlist.get(i).getSumdistance();
            }
        }
        tvTotal.setText(String.format("%.2f", sumvalue) + "");
        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    private void SetWeekwiseDistanceChart() {

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
        leftAxis.setDrawLabels(false);

        BarData data = new BarData(setWeekDistanceData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Km");
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
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

    private BarDataSet setWeekDistanceData() {
        String s1 = getCurrentWeekdate(rightNow);
        String[] Weekdate1 = s1.split("-");
        tvchartdate.setText(Weekdate1[0] + " - " + Weekdate1[1]);

        String s = getCurrentWeek(rightNow);
//        Log.e("TAG", "date: " + s);
        String[] Weekdate = s.split("-");
        int stepnumber;
        String fristdate = Weekdate[0];
        String lastdate = Weekdate[1];
        float sumvalue = 0;
        StepWeeklist = dbManager.getweekDistancedata(fristdate, lastdate);

        ArrayList<BarEntry> entries = new ArrayList<>();
        if (StepWeeklist != null) {
            for (int i = 0; i < StepWeeklist.size(); i++) {
//                Log.e("TAG", "hours: " + StepWeeklist.get(i).getDistance() + "step" + StepWeeklist.get(i).getStep() + "date" + StepWeeklist.get(i).getDate());
                entries.add(new BarEntry(i, Float.parseFloat(String.valueOf(StepWeeklist.get(i).getSumdistance())), StepWeeklist.get(i).getDate()));
                sumvalue = sumvalue + StepWeeklist.get(i).getSumdistance();
            }
        }

        tvTotal.setText(String.format("%.2f", sumvalue) + "");
        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));
        set.setDrawValues(false);
        set.setDrawIcons(false);

        return set;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                showEditStepDailog();
                break;
        }
        return true;
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
        Button mBtnSave = (Button) d.findViewById(R.id.btnSave);
        Button mBtnCancel = (Button) d.findViewById(R.id.btnCancel);
        LinearLayout llView = (LinearLayout) d.findViewById(R.id.datepicker);
        Spinner spinner = (Spinner) d.findViewById(R.id.spinner);
        EditText stepvalue = (EditText) d.findViewById(R.id.stepvalue);
        TextView tvdate = (TextView) d.findViewById(R.id.tvdate);

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(StepReportActivity.this,
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
                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                    c.set(Calendar.DATE, selectedday[0]);
                    c.set(Calendar.MONTH, selectedmonth[0] - 1);
                    c.set(Calendar.YEAR, selectedyear[0]);
                    c.set(Calendar.HOUR, saveHour[0]);

                    s.format(new Date(c.getTimeInMillis()));

                    Log.e("Start", "Start Date = " + selectedday[0]);
                    Log.e("Start", "Start Date = " + selectedmonth[0]);
                    Log.e("Start", "Start Date = " + selectedyear[0]);
                    Log.e("Start", "Start Date = " + c.getTimeInMillis());

                    stepcountModel.setStep(Integer.valueOf(numSteps));
                    stepcountModel.setDate(selectedday[0]);
                    stepcountModel.setMonth(selectedmonth[0]);
                    stepcountModel.setYear(selectedyear[0]);
                    stepcountModel.setDistance(Distance);
                    stepcountModel.setCalorie(Calories);
                    stepcountModel.setDuration(saveHour[0]);
                    stepcountModel.setTimestemp(String.valueOf(c.getTimeInMillis()));
                    stepcountModel.setMaxStep(0);
                    dbManager.addStepcountData(stepcountModel);
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

//                Logger.e(mTotalStepData + ">" + mLevel.get(i).getValue());
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

                }
//                CommanMethod.showCompleteDailog(this, String.valueOf(mLevel.get(i).getValue()), mLevel.get(i).getDescription());
            }
        }


        // Archivement Daily Step
        ArrayList<ArchivementModel> mDailySteplist = new ArrayList<>();
        ArrayList<StepCountModel> MaxStepCount = dbManager.getsumofdayStep(mdate, month, year);

        Logger.e(MaxStepCount.get(0).getSumstep());
        Logger.e(MaxStepCount.get(0).getMaxStep());

//        ArrayList<StepCountModel> MaxStepCountModels = dbManager.getMaxStepCount();
        mDailySteplist = dbManager.getArchivementDailySteplist(constant.ARCHIVEMENT_DAILY_STEP, MaxStepCount.get(0).getMaxStep());
        for (int i = 0; i < mDailySteplist.size(); i++) {
            if (MaxStepCount.get(0).getSumstep() >= mDailySteplist.get(i).getValue()) {

//                Logger.e(TotalStepCount + ">" + mDailySteplist.get(0).getValue());
                Logger.e((TotalStepCount > mDailySteplist.get(i).getValue()));

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

                Logger.e(mdate + "**" + month + "**" + year + "**" + hour + "**" + MaxStepCount.get(0).getSumstep());
                dbManager.updatemaxStep(stepCountModel);

                DailogGoal = String.valueOf(mDailySteplist.get(i).getValue());
                DailogDesc = mDailySteplist.get(i).getDescription();

                NotificationValue = mDailySteplist.get(i).getValue();
                NotificationType = constant.ARCHIVEMENT_DAILY_STEP;
//                CommanMethod.showCompleteDailog(this, String.valueOf(mDailySteplist.get(i).getValue()), mDailySteplist.get(i).getDescription());
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

//                CommanMethod.showCompleteDailog(this, String.valueOf(mTotalDaysList.get(i).getValue()), mTotalDaysList.get(i).getDescription());
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
//                CommanMethod.showCompleteDailog(this, String.valueOf(mTotalDistanceList.get(i).getValue()), mTotalDistanceList.get(i).getDescription());
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

//        Logger.e("currDayStep" + currDayStep + "YestDayStep" + YestDayStep);
//        Logger.e((YestDayStep >= StepGoal));
//        Logger.e((currDayStep >= StepGoal));

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
//                CommanMethod.showCompleteDailog(this, String.valueOf(mComboDayList.get(i).getValue()), mComboDayList.get(i).getDescription());
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

            if (mTotalDistanceList.get(i).isCompeleteStatus()) {
                Distancegoal = mTotalDistanceList.get(i + 1).getValue();
                DistanceDesc = mTotalDistanceList.get(i + 1).getDescription();
            }
        }

//        Logger.e("Total Distance" + mTotalDisanceData + "---" + Distancegoal);

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

//        Logger.e("Total Days" + mTotalDaysData + "---" + TotalDaysgoal);

        //        -------------------------Daily Steps
        long TotalDailyStep = dbManager.getSumOfStepList(mdate, month, year);
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
                Logger.e(TotalDailygoal + "----new ------TotalDailygoal-----------");
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
//        Logger.e("Level" + mTotalStepData + "------" + mlevelGoal);

        float mLevelData = (float) mTotalStepData / mlevelGoal * 100;
        float mDisanceData = (float) mTotalDisanceData / Distancegoal * 100;
        float mDaysData = (float) mTotalDaysData / TotalDaysgoal * 100;
        float mDailyStep = (float) TotalDailyStep / TotalDailygoal * 100;

        Logger.e(mLevelData + "--" + mDisanceData + "--" + mDaysData + "--" + mDailyStep + "--");

        ArrayList<Float> floats = new ArrayList<>();
        floats.remove(floats);
        floats.add(mLevelData);
        floats.add(mDisanceData);
        floats.add(mDaysData);
        floats.add(mDailyStep);

        Float obj = Collections.max(floats);
        int index = floats.indexOf(obj);
//        Logger.e(obj + "--" + index);

        if (mdate == Calendar.DATE && floats.get(index) > 90) {
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
        } else if (TotalDailyStep >= TotalDailygoal && !mDailyStepDatalist.get(mDailyStepDatalist.size() - 1).isCompeleteStatus()) {
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

    private final RectF onValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = onValueSelectedRectF;
        chart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = chart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + chart.getLowestVisibleX() + ", high: "
                        + chart.getHighestVisibleX());

        MPPointF.recycleInstance(position);

    }

    @Override
    public void onNothingSelected() {

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

        Log.e("mDateMonday", "" + mCalendar.getTimeInMillis());
        // return 6 the next days of current day (object cal save current day)
        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
//        Date mDateSunday = mCalendar.getTime();
        long mDateSunday = mCalendar.getTimeInMillis();

        Log.e("mDateSunday", "" + mCalendar.getTimeInMillis());

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

        Log.e("mDateMonday", "" + mCalendar.getTimeInMillis());
        // return 6 the next days of current day (object cal save current day)
        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
        Date mDateSunday = mCalendar.getTime();

        Log.e("mDateSunday", "" + mCalendar.getTimeInMillis());

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBackDate:
                if (IsSelectedDay) {
                    rightNow.set(Calendar.DATE, rightNow.get(Calendar.DATE) - 1);
                    date = rightNow.get(Calendar.DATE);
                    month = rightNow.get(Calendar.MONTH) + 1;
                    year = rightNow.get(Calendar.YEAR);
                    Log.e("TAG", "onClick: " + date);
                    tvchartdate.setText(date + "-" + month + "-" + year);

                    if (IsSelectedStep) {
                        SetDaywiseStepChart();
                    } else if (IsSelectedCaleroie) {
                        SetDaywiseCaloriesChart();
                    } else if (IsSelectedTime) {
                        SetDaywiseTimeChart();
                    } else if (IsSelectedDistance) {
                        SetDaywiseDistanceChart();
                    }
                } else if (IsSelectedWeek) {

                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                    rightNow.add(Calendar.DAY_OF_YEAR, -7);
                    rightNow.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    date = rightNow.get(Calendar.DATE);
                    month = rightNow.get(Calendar.MONTH) + 1;
                    year = rightNow.get(Calendar.YEAR);
                    s.format(new Date(rightNow.getTimeInMillis()));
                    Log.e("Start", "Start Date = " + rightNow.getTimeInMillis());
                    Log.e("Start", "Start Date = " + s.format(new Date(rightNow.getTimeInMillis())));
//
//                    rightNow.set(Calendar.DATE, rightNow.get(Calendar.DATE) + 6);
//                    s.format(new Date(rightNow.getTimeInMillis()));
//                    Log.e("End", "End Date = " + rightNow.getTimeInMillis());
//                    Log.e("End", "End Date = " + s.format(new Date(rightNow.getTimeInMillis())));

                    if (IsSelectedStep) {
                        SetWeekwiseStepChart();
                    } else if (IsSelectedCaleroie) {
                        SetWeekwiseCaloriesChart();
                    } else if (IsSelectedTime) {
                        SetWeekwiseTimeChart();
                    } else if (IsSelectedDistance) {
                        SetWeekwiseDistanceChart();
                    }
                } else if (IsSelectedMonth) {
                    rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH) - 1);

                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
                    c.set(Calendar.YEAR, rightNow.get(Calendar.YEAR));
//                    int year = c.get(Calendar.YEAR);
//                    int month = c.get(Calendar.MONTH);
                    int day = 1;

                    c.set(Calendar.DAY_OF_MONTH, day);

                    int numOfDaysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//        Log.e("First", "First Day of month: " + c.getTimeInMillis());
                    firstdayofmonth = c.getTimeInMillis();
                    c.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
                    lastdayofmonth = c.getTimeInMillis();

                    if (IsSelectedStep) {
                        SetMonthwiseStepChart();
                    } else if (IsSelectedCaleroie) {
                        SetMonthwiseCaloriesChart();
                    } else if (IsSelectedTime) {
                        SetMonthwiseTimeChart();
                    } else if (IsSelectedDistance) {
                        SetMonthwiseDistanceChart();
                    }
                }
                break;
            case R.id.ivForwardDate:
                if (IsSelectedDay) {
                    rightNow.set(Calendar.DATE, rightNow.get(Calendar.DATE) + 1);
                    date = rightNow.get(Calendar.DATE);
                    month = rightNow.get(Calendar.MONTH) + 1;
                    Log.e("TAG", "onClick: " + rightNow.get(Calendar.MONTH));
                    tvchartdate.setText(date + "-" + month + "-" + year);

                    if (IsSelectedDay) {
                        SetDaywiseStepChart();
                    } else if (IsSelectedCaleroie) {
                        SetDaywiseCaloriesChart();
                    } else if (IsSelectedTime) {
                        SetDaywiseTimeChart();
                    } else if (IsSelectedDistance) {
                        SetDaywiseDistanceChart();
                    }
                } else if (IsSelectedWeek) {

                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                    rightNow.add(Calendar.DAY_OF_YEAR, +7);
                    rightNow.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    date = rightNow.get(Calendar.DATE);
                    month = rightNow.get(Calendar.MONTH) + 1;
                    year = rightNow.get(Calendar.YEAR);
                    s.format(new Date(rightNow.getTimeInMillis()));
                    Log.e("Start", "Start Date = " + rightNow.getTimeInMillis());
                    Log.e("Start", "Start Date = " + s.format(new Date(rightNow.getTimeInMillis())));

                    if (IsSelectedStep) {
                        SetWeekwiseStepChart();
                    } else if (IsSelectedCaleroie) {
                        SetWeekwiseCaloriesChart();
                    } else if (IsSelectedTime) {
                        SetWeekwiseTimeChart();
                    } else if (IsSelectedDistance) {
                        SetWeekwiseDistanceChart();
                    }
                } else if (IsSelectedMonth) {
                    rightNow.set(Calendar.MONTH, rightNow.get(Calendar.MONTH) + 1);

                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
                    c.set(Calendar.YEAR, rightNow.get(Calendar.YEAR));
//                    int year = c.get(Calendar.YEAR);
//                    int month = c.get(Calendar.MONTH);
                    int day = 1;

                    c.set(Calendar.DAY_OF_MONTH, day);

                    int numOfDaysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//        Log.e("First", "First Day of month: " + c.getTimeInMillis());
                    firstdayofmonth = c.getTimeInMillis();
                    c.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
                    lastdayofmonth = c.getTimeInMillis();

                    if (IsSelectedStep) {
                        SetMonthwiseStepChart();
                    } else if (IsSelectedCaleroie) {
                        SetMonthwiseCaloriesChart();
                    } else if (IsSelectedTime) {
                        SetMonthwiseTimeChart();
                    } else if (IsSelectedDistance) {
                        SetMonthwiseDistanceChart();
                    }
                }
                break;
        }
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
}