package com.android.stepcounter.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.MyMarkerView;
import com.android.stepcounter.R;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.WeightModel;
import com.android.stepcounter.model.waterlevel;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.commanMethod;
import com.android.stepcounter.utils.constant;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.github.lzyzsd.circleprogress.ArcProgress;
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

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HeathActivity extends AppCompatActivity implements DatePickerListener {
    TextView muserWeight, mAddWeightDailog, meditHeightWeight, mtvlastdaydiff, mbmistatus;
    Calendar rightNow;
    int hour, min, date, month, year;
    DBHandler dbManager;
    LineChart WeightChart;
    private float userWeight = constant.DEFAULT_WEIGHT;
    private float userHeight = constant.DEFAULT_HEIGHT;
    Toolbar mToolbar;
    protected String[] BMIcategory = new String[]
            {
                    "Very Severly Underweight", "Severly Underweight", "Underweight", "Normal",
                    "Overweight", "Obese Class I", "Obese Class II", "Obese Class III",
            };

    String selectedDate, seletedMonth, selecetedYear;

    ArcProgress arcProgress, mbmiprogress;
    LinearLayout llRemovewater, lladdwater;
    String waterGoal, DefualtCup;
    waterlevel waterlevel;
    ArrayList<waterlevel> waterlist;
    int StepGoal;
    String Watergoal, WaterUnit, Watercup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heath);
        dbManager = new DBHandler(this);
        waterlist = new ArrayList<>();
    }

    private void init() {

        rightNow = Calendar.getInstance();
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        min = rightNow.get(Calendar.MINUTE);
        date = rightNow.get(Calendar.DATE);
        month = rightNow.get(Calendar.MONTH) + 1;
        year = rightNow.get(Calendar.YEAR);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Health Tracker");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        WeightChart = findViewById(R.id.WeightChart);
        muserWeight = findViewById(R.id.userWeight);
        mAddWeightDailog = findViewById(R.id.addWeight);
        meditHeightWeight = findViewById(R.id.editHeightWeight);
        mtvlastdaydiff = findViewById(R.id.tvlastdaydiff);

        mbmistatus = findViewById(R.id.bmistatus);

        mbmiprogress = findViewById(R.id.bmiprogress);
        mbmiprogress.setProgress(50);

        muserWeight.setText(userWeight + "");

      /*  ArrayList<WeightModel> modelArrayList = new ArrayList<>();
        modelArrayList = dbManager.getCurrentDayWeightlist(date, month, year);
        if (modelArrayList.size() != 0) {
            for (int i = 0; i < modelArrayList.size(); i++) {
                muserWeight.setText("" + modelArrayList.get(i).getKg());
            }
        }*/

        arcProgress = findViewById(R.id.arc_progress);
        llRemovewater = findViewById(R.id.llRemovewater);
        lladdwater = findViewById(R.id.lladdwater);

        waterGoal = StorageManager.getInstance().getWaterGoal();
        DefualtCup = StorageManager.getInstance().getWaterCup();

        String[] WaterGoalValue = waterGoal.split(" ");

        waterlist = dbManager.getCurrentDayWatercountlist(date, month, year);

        String[] WaterGoal = StorageManager.getInstance().getWaterGoal().split(" ");

        arcProgress.setMax(Integer.parseInt(WaterGoalValue[0]));

        int waterml = 0;
        if (waterlist != null) {
            for (int i = 0; i < waterlist.size(); i++) {
                waterml = (int) Float.parseFloat(waterlist.get(i).getUnit());
//                Log.e("TAG", "date: " + waterml);
            }
        } else {
            waterml = 0;
        }
        String WaterUnit = StorageManager.getInstance().getWaterUnit();

        final float[] value = {0};

        if (WaterUnit.contains("ml")) {
            if (waterml == 0.0) {
                arcProgress.setProgress(0);
                value[0] = 0;
            } else {
                if (waterml < Integer.parseInt(WaterGoal[0])) {
                    arcProgress.setProgress(waterml);
                    value[0] = waterml;
                } else {
                    arcProgress.setProgress(Integer.parseInt(WaterGoal[0]));
                    value[0] = Integer.parseInt(WaterGoal[0]);
                }
            }
        } else {
            if (waterml == 0.0) {
                arcProgress.setProgress(0);
                value[0] = 0;
            } else {
                double newMlValue = commanMethod.getMlToFloz(Float.valueOf(waterml));
                if (newMlValue < Integer.parseInt(WaterGoal[0])) {
                    arcProgress.setProgress((int) Math.round(newMlValue));
                    value[0] = (int) Math.round(newMlValue);
                } else {
                    arcProgress.setProgress(Integer.parseInt(WaterGoal[0]));
                    value[0] = Integer.parseInt(WaterGoal[0]);
                }
            }
        }

        String[] DefultCupValue = DefualtCup.split(" ");


        lladdwater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double covertinml;

                waterlevel = new waterlevel();
                waterlevel.setDate(date);
                waterlevel.setMonth(month);
                waterlevel.setYear(year);
                waterlevel.setHour(hour);
                waterlevel.setMin(min);

                if (DefultCupValue[1].contains("fl")) {
                    value[0] = value[0] + Float.parseFloat(DefultCupValue[0]);
                    if (value[0] < Integer.parseInt(WaterGoalValue[0])) {
                        arcProgress.setProgress((int) value[0]);
                    } else {
                        arcProgress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                    }
                    covertinml = commanMethod.getFlozToMl(value[0]);
                    waterlevel.setUnit(String.valueOf(covertinml));
                } else {
                    Log.e("TAG", "old: " + value[0]);
                    value[0] = value[0] + Integer.parseInt(DefultCupValue[0]);
                    Log.e("TAG", "new: " + value[0]);
                    if (value[0] < Integer.parseInt(WaterGoalValue[0])) {
                        arcProgress.setProgress((int) value[0]);
                    } else {
                        arcProgress.setProgress(Integer.parseInt(WaterGoalValue[0]));
                    }
                    waterlevel.setUnit(String.valueOf(value[0]));
                }

                dbManager.addWaterData(waterlevel);
            }
        });

        final Float[] finalWaterml = {(float) waterml};
        double covertvalue = commanMethod.getMlToFloz(finalWaterml[0]);
        final float[] newvalue = {(int) Math.round(covertvalue)};
        final float ints = Float.parseFloat(String.valueOf(DefultCupValue[0]));
        Log.e("init", "init: " + newvalue[0] + ints);

        llRemovewater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waterlevel = new waterlevel();
                waterlevel.setDate(date);
                waterlevel.setMonth(month);
                waterlevel.setYear(year);
                waterlevel.setHour(hour);
                waterlevel.setMin(min);

                //database mathi mius krvanu baki
                if (DefultCupValue[1].contains("fl")) {
                    Log.e("TAG", "old: " + newvalue[0]);
                    newvalue[0] = newvalue[0] - ints;
                    Log.e("TAG", "new: " + newvalue[0]);
                    if (newvalue[0] < 0) {
                        newvalue[0] = 0;
                        arcProgress.setProgress(0);
                        waterlevel.setUnit(0 + "");
                    } else {
                        arcProgress.setProgress((int) newvalue[0]);
                        waterlevel.setUnit(String.valueOf(newvalue[0]));
                    }
                } else {
                    Log.e("TAG", "old: " + finalWaterml[0]);
                    finalWaterml[0] = finalWaterml[0] - ints;
                    Log.e("TAG", "new: " + finalWaterml[0]);
                    if (finalWaterml[0] < 0) {
                        finalWaterml[0] = Float.valueOf(0);
                        arcProgress.setProgress(0);
                        waterlevel.setUnit(0 + "");
                    } else {
                        arcProgress.setProgress(Math.round(finalWaterml[0]));
                        waterlevel.setUnit(Math.round(finalWaterml[0]) + "");
                    }
                }
                dbManager.addWaterData(waterlevel);
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

        setWeightChart();
        BMIReport();
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
                double value = commanMethod.cmToInchConverter(Double.parseDouble(etHeight.getText().toString()),
                        Math.round(commanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString()))));
                etHeight.setText(Math.round(commanMethod.feetToCmConverter(commanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString())), value)) + "");
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
                etft.setText(Math.round(commanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString()))) + " ft");
                etin.setText(commanMethod.cmToInchConverter(Double.parseDouble(etHeight.getText().toString()),
                        commanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString()))) + " in ");
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double value = commanMethod.cmToInchConverter(Double.parseDouble(etHeight.getText().toString()),
                        Math.round(commanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString()))));

                StorageManager.getInstance().setHeight((float) Math.round(commanMethod.feetToCmConverter(commanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString())), value)));
                alertDialog.dismiss();
                onResume();
               /* WeightModel weightModel = new WeightModel();
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
                alertDialog.dismiss();*/
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

        picker.setListener(HeathActivity.this)
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
                weightModel.setDate(date);
                weightModel.setMonth(month);
                weightModel.setYear(year);
                if (iskg[0]) {
                    weightModel.setKg(Integer.parseInt(etweight.getText().toString()));
                } else {
                    weightModel.setKg((int) Math.round(commanMethod.lbToKgConverter(Double.parseDouble(etweight.getText().toString()))));
                }
                dbManager.addWeightData(weightModel);
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

    private void setWeightChart() {
        ArrayList<WeightModel> weightModels = new ArrayList<>();
        ArrayList<WeightModel> modelArrayList = new ArrayList<>();
        ArrayList<Entry> weightModelArrayList = new ArrayList<>();

        weightModels = dbManager.getWeightlist();

        modelArrayList = dbManager.getCurrentDayWeightlist(date, month, year);
        for (int i = 0; i < modelArrayList.size(); i++) {
            muserWeight.setText("" + modelArrayList.get(i).getKg());
        }

        Log.e("TAG", "setWeightChart: " + weightModels.get(weightModels.size() - 1).getKg());

        int cuurrvalue = Integer.parseInt(muserWeight.getText().toString());
        int diff = cuurrvalue - weightModels.get(weightModels.size() - 1).getKg();
        mtvlastdaydiff.setText(diff + "KG");

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
    }

    private void setSharedPreferences() {
        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();
        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
        Watergoal = StorageManager.getInstance().getWaterGoal();
        Watercup = StorageManager.getInstance().getWaterCup();
        WaterUnit = StorageManager.getInstance().getWaterUnit();
    }

    public void BMIReport() {
        double heightM = userHeight / 100;
        double BMI = (userWeight) / (heightM * heightM);
        DecimalFormat df = new DecimalFormat("#.#");
        double BMI_trimmed = Double.parseDouble(df.format(BMI));
        String BMI_Cat = null;
        if (BMI < 15) {
            BMI_Cat = "Very severely underweight";
            mbmiprogress.setProgress((int) BMI_trimmed);
        } else if (BMI >= 15 && BMI < 16) {
            BMI_Cat = "Severely underweight";
            mbmiprogress.setProgress((int) BMI_trimmed);
        } else if (BMI >= 16 && BMI < 18.5) {
            BMI_Cat = "Underweight";
            mbmiprogress.setProgress((int) BMI_trimmed);
        } else if (BMI >= 18.5 && BMI < 25) {
            BMI_Cat = "Normal";
            mbmiprogress.setProgress((int) BMI_trimmed);
        } else if (BMI >= 25 && BMI < 30) {
            BMI_Cat = "Overweight";
            mbmiprogress.setProgress((int) BMI_trimmed);
        } else if (BMI >= 30 && BMI < 35) {
            BMI_Cat = "Moderately Obese";
            mbmiprogress.setProgress((int) BMI_trimmed);
        } else if (BMI >= 35 && BMI < 40) {
            BMI_Cat = "Severely Obese";
            mbmiprogress.setProgress((int) BMI_trimmed);
        } else if (BMI < 40) {
            BMI_Cat = "Very Severely Obese";
            mbmiprogress.setProgress(40);
        }
        mbmistatus.setText(BMI_Cat);
    }
}