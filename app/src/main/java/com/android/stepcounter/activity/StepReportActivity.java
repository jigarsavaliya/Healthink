package com.android.stepcounter.activity;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.R;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.stepcountModel;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.commanMethod;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StepReportActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    Toolbar mToolbar;
    DBHandler dbManager;
    private BarChart chart;
    CardView mcvDay, mcvWeek, mcvMonth, mcvStep, mcvCalories, mcvTime, mcvDistance;
    TextView tvTotal;
    ArrayList<stepcountModel> Steplist;
    ArrayList<stepcountModel> StepHourlist;
    private static final String[] DAYS = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    stepcountModel stepcountModel;
    private float userWeight;
    private float userHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_report);
        dbManager = new DBHandler(this);
        Steplist = new ArrayList<stepcountModel>();
        StepHourlist = new ArrayList<stepcountModel>();

        Calendar rightNow = Calendar.getInstance();
        int date = rightNow.get(Calendar.DATE);
        int month = rightNow.get(Calendar.MONTH) + 1;
        int year = rightNow.get(Calendar.YEAR);

        Steplist = dbManager.getCurrentDayStepcountlist(date, month, year);

        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();

        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Report");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        chart = (BarChart) findViewById(R.id.chart1);
        tvTotal = findViewById(R.id.tvTotal);

        mcvDay = findViewById(R.id.cvDay);
        mcvWeek = findViewById(R.id.cvWeek);
        mcvMonth = findViewById(R.id.cvMonth);

        mcvStep = findViewById(R.id.cvStep);
        mcvCalories = findViewById(R.id.cvCalories);
        mcvTime = findViewById(R.id.cvTime);
        mcvDistance = findViewById(R.id.cvDistance);

        mcvDay.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcvStep.setCardBackgroundColor(Color.parseColor("#E6E6E6"));

        SetChart();

        mcvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetChart();
                mcvDay.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mcvWeek.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvMonth.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mcvWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetWeekChart();
                mcvDay.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvWeek.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mcvMonth.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mcvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetMonthChart();
                mcvDay.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvWeek.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvMonth.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            }
        });

        mcvStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcvStep.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mcvCalories.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvTime.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvDistance.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mcvCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcvStep.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvCalories.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mcvTime.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvDistance.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mcvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcvStep.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvCalories.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvTime.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mcvDistance.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mcvDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcvStep.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvCalories.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvTime.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                mcvDistance.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            }
        });
    }

    private void SetChart() {
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

        leftAxis.setTextSize(10f);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        BarData data = new BarData(setDayData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.setDrawValueAboveBar(true);

    }

    private BarDataSet setDayData() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
//                Log.e("TAG", "hours: " + Steplist.get(i).getDuration() + "step" + Steplist.get(i).getStep() + "date" + Steplist.get(i).getDate());
                entries.add(new BarEntry(Steplist.get(i).getDuration(), Steplist.get(i).getStep()));
            }
        }
        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));

        return set;
    }

    private void SetMonthChart() {

        Legend L;
        L = chart.getLegend();
        L.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
//        YAxis rightAxis = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        leftAxis.setTextSize(10f);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);

//        rightAxis.setDrawAxisLine(false);
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setDrawLabels(false);

        BarData data = new BarData(setMonthData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.setDrawValueAboveBar(true);

    }

    private BarDataSet setMonthData() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
//                Log.e("TAG", "hours: " + Steplist.get(i).getDate() + "step" + Steplist.get(i).getStep() + "date" + Steplist.get(i).getDate());
                entries.add(new BarEntry(Steplist.get(i).getDate(), Steplist.get(i).getStep()));
            }
        }

        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155, 155, 155));

        return set;
    }

    private void SetWeekChart() {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");
        xAxisLabel.add("Sun");

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
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabel.get((int) value);
            }
        });

        leftAxis.setTextSize(10f);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        BarData data = new BarData(setWeekData());
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.setDrawValueAboveBar(true);

    }

    private BarDataSet setWeekData() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (Steplist != null) {
            for (int i = 0; i < Steplist.size(); i++) {
//                Log.e("TAG", "hours: " + Steplist.get(i).getDate() + "step" + Steplist.get(i).getStep() + "date" + Steplist.get(i).getDate());
                entries.add(new BarEntry(Steplist.get(i).getYear(), Steplist.get(i).getStep()));
            }
        }

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

        final int[] saveHour = {0};
        for (int i = 0; i < 24; i++) {
            if (hours == i) {
                int a = hours + 1;
                time.setText(hours + ":00 - " + a + ":00");
                saveHour[0] = hours;
            }
        }

        final int[] selectedyear = {0};
        final int[] selectedmonth = {0};
        final int[] selectedday = {0};

        llView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(StepReportActivity.this,
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
                PopupMenu popup = new PopupMenu(StepReportActivity.this, llTime);
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


        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numSteps = stepvalue.getText().toString();
                if (numSteps.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter number of steps..!!", Toast.LENGTH_LONG).show();
                } else {
                    String Calories = String.valueOf(commanMethod.calculateCalories(Integer.parseInt(numSteps), userWeight, userHeight));
                    String Distance = String.valueOf(commanMethod.calculateDistance(Integer.parseInt(numSteps), userHeight));

                    stepcountModel.setStep(Integer.valueOf(numSteps));
                    stepcountModel.setDate(selectedday[0]);
                    stepcountModel.setMonth(selectedmonth[0]);
                    stepcountModel.setYear(selectedyear[0]);
                    stepcountModel.setDistance(Distance);
                    stepcountModel.setCalorie(Calories);
                    stepcountModel.setDuration(saveHour[0]);
//                    dbManager.addStepcountData(stepcountModel);
                }
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

}