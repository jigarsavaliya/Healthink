package com.android.stepcounter.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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

import com.android.stepcounter.MyMarkerView;
import com.android.stepcounter.R;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.StepCountModel;
import com.android.stepcounter.utils.CommanMethod;
import com.android.stepcounter.utils.StorageManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StepReportActivity extends AppCompatActivity implements OnChartValueSelectedListener, View.OnClickListener {
    Toolbar mToolbar;
    DBHandler dbManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_report);
        dbManager = new DBHandler(this);
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
        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawZeroLine(false);

        BarData data = new BarData(setDayData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Steps");
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

        return set;
    }

    private void SetMonthwiseStepChart() {

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

        LimitLine ll1 = new LimitLine(StepGoal);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(1f, 1f, 0f);

        rightAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        rightAxis.addLimitLine(ll1);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setAxisMaximum(6000);
        rightAxis.setAxisMinimum(0);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        BarData data = new BarData(setMonthData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Steps");
        mv.setChartView(chart);

//        chart.getXAxis().setEnabled(false);
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

        chart.invalidate();

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

        return set;
    }

    private void SetWeekwiseStepChart() {

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

        Legend L = chart.getLegend();
        L.setEnabled(false);


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

        BarData data = new BarData(setWeekData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view, "Steps");
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
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

//        leftAxis.setTextSize(10f);
//        leftAxis.setDrawLabels(false);
//        leftAxis.setDrawAxisLine(true);
//        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

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

        return set;
    }

    private void SetWeekwiseCaloriesChart() {

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
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setDrawLabels(false);

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

        //        LimitLine ll1 = new LimitLine(Integer.parseInt(Watergoal));
//        ll1.setLineWidth(1f);
//        ll1.enableDashedLine(1f, 1f, 0f);

        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(ll1);
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

        chart.invalidate();

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

        //        LimitLine ll1 = new LimitLine(Integer.parseInt(Watergoal));
//        ll1.setLineWidth(1f);
//        ll1.enableDashedLine(1f, 1f, 0f);

        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(ll1);
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

                                tvdate.setText(selectedday[0] + " - " + selectedmonth[0]);

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
                    String Distance = String.valueOf(CommanMethod.calculateDistance(Integer.parseInt(numSteps), userHeight));

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