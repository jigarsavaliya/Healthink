package com.android.stepcounter.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.MyMarkerView;
import com.android.stepcounter.R;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.WeightModel;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.commanMethod;
import com.android.stepcounter.utils.constant;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
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

import java.util.ArrayList;
import java.util.Calendar;

public class HeathActivity extends AppCompatActivity implements DatePickerListener {
    TextView muserWeight, mAddWeightDailog, meditHeightWeight, mtvlastdaydiff;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heath);
        dbManager = new DBHandler(this);

        setSharedPreferences();
        init();
    }

    private void setSharedPreferences() {
        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();
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

        muserWeight.setText(userWeight + "");

        ArrayList<WeightModel> modelArrayList = new ArrayList<>();
        modelArrayList = dbManager.getCurrentDayWeightlist(date, month, year);
        for (int i = 0; i < modelArrayList.size(); i++) {
            muserWeight.setText("" + modelArrayList.get(i).getKg());
        }

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

        final boolean[] iskg = {true};
        final boolean[] iscm = {true};

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
                mllcm.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                mllin.setCardBackgroundColor(getResources().getColor(R.color.transprant));
//                etHeight.setText(Math.round(commanMethod.feetToCmConverter(Double.parseDouble(etHeight.getText().toString()))) + "");
            }
        });

        mllin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                iscm[0] = true;
                mllin.setCardBackgroundColor(getResources().getColor(R.color.colorBackgrond));
                mllcm.setCardBackgroundColor(getResources().getColor(R.color.transprant));
//                etHeight.setText(Math.round(commanMethod.cmToFeetConverter(Double.parseDouble(etHeight.getText().toString()))) + "");
            }
        });

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
/*        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(dateSelected.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = outputFormat.format(date);
        Log.e("HorizontalPicker", "Fecha seleccionada=" + formattedDate);*/
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

}