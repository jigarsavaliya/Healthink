package com.android.stepcounter.activity;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.R;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.stepcountModel;
import com.android.stepcounter.sevices.SensorService;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.commanMethod;
import com.android.stepcounter.utils.constant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView TvSteps, accuracyText, tvduration, tvkm, tvkcal, tvuserWeight, tvwatergoal, tvwaterlevel;
    private int numSteps;
    Toolbar mToolbar;
    ImageView ivPlay, ivPause;
    BottomNavigationView mbottomNavigation;
    private CircularProgressBar progress, sunday, monday, tuesday, wednesday, thrusday, friday, saturday;
    CardView mcvStrat;
    private float userWeight = constant.DEFAULT_WEIGHT;
    private float userHeight = constant.DEFAULT_HEIGHT;
    int StepGoal;
    String distance, calories;
    LinearLayout lladdwater, llwaterSetting;
    MyReceiver myReceiver;
    DBHandler dbManager;
    int temp = 0;
    int oldsteptotal = 0;
    ArrayList<stepcountModel> Steplist;
    ArrayList<stepcountModel> getoldSteplist;
    stepcountModel stepcountModel;
    ArrayList<Integer> DaywiseSteplist;


    private class MyReceiver extends BroadcastReceiver {
        DBHandler dbManager;
        ArrayList<stepcountModel> Steplist = new ArrayList<stepcountModel>();

        @Override
        public void onReceive(Context context, Intent intent) {

//            Calendar rightNow = Calendar.getInstance();
//            int date = rightNow.get(Calendar.DATE);
//            int month = rightNow.get(Calendar.MONTH);
//            int year = rightNow.get(Calendar.YEAR);
//
//            dbManager = new DBHandler(context);
//            Steplist = dbManager.getCurrentDayStepcountlist(date, month, year);
//
//            int temp = 0;
//            for (int i = 0; i < Steplist.size(); i++) {
//                Log.e("TAG", "onReceive: main " + Steplist.get(i).getStep());
//                temp = temp + Steplist.get(i).getStep();
//                numSteps = Steplist.get(i).getStep();
//            }

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

            setDailyGoal();
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
        dbManager = new DBHandler(this);

        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, new IntentFilter("GET_SIGNAL_STRENGTH"));

        setSharedPreferences();
        init();

    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Health Tracker");
        setSupportActionBar(mToolbar);

        progress = findViewById(R.id.progressBar);
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

        lladdwater = findViewById(R.id.lladdwater);
        llwaterSetting = findViewById(R.id.llwaterSetting);
        TvSteps = (TextView) findViewById(R.id.tv_steps);
        accuracyText = (TextView) findViewById(R.id.tv_accuracy);
        tvduration = (TextView) findViewById(R.id.tvduration);
        tvkm = (TextView) findViewById(R.id.tvkm);
        tvkcal = (TextView) findViewById(R.id.tvkcal);
        tvuserWeight = (TextView) findViewById(R.id.userWeight);
        tvwatergoal = (TextView) findViewById(R.id.tvwatergoal);
        tvwaterlevel = (TextView) findViewById(R.id.tvwaterlevel);

//        tvwatergoal.setText("/" + SharePreferecnce.getStringValue(SharePreferecnce.PREF_APP_KEY_WATER_GOAL));
        tvwatergoal.setText("/" + StorageManager.getInstance().getWaterGoal());

        tvuserWeight.setText(userWeight + "");

        ivPlay = findViewById(R.id.ivPlay);
        ivPause = findViewById(R.id.ivPause);
        mbottomNavigation = findViewById(R.id.bottomNavigation);

        mcvStrat = findViewById(R.id.cvGPSStrat);

        ivPlay.setOnClickListener(this);
        ivPause.setOnClickListener(this);
        mcvStrat.setOnClickListener(this);
        lladdwater.setOnClickListener(this);
        llwaterSetting.setOnClickListener(this);

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

        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int date = rightNow.get(Calendar.DATE);
        int month = rightNow.get(Calendar.MONTH) + 1;
        int year = rightNow.get(Calendar.YEAR);

        int TotalStepCount = dbManager.getSumOfStepList(date, month, year);
        TvSteps.setText(TotalStepCount + "");

        progress.setProgressWithAnimation(TotalStepCount, (long) 1000);

        distance = String.valueOf(commanMethod.calculateDistance(TotalStepCount, userHeight));
        tvkm.setText(distance);

        calories = String.valueOf(commanMethod.calculateCalories(TotalStepCount, userWeight, userHeight));
        tvkcal.setText(calories);

        setDailyGoal();

        getoldSteplist = dbManager.getCurrentDayHoursStepcountlist(date, month, year, hour);
        if (getoldSteplist != null) {
            for (int i = 0; i < getoldSteplist.size(); i++) {
                oldsteptotal = getoldSteplist.get(i).getStep();
                Log.e("TAG", "date: " + oldsteptotal);
            }
        } else {
            oldsteptotal = 0;
            Log.e("TAG", "date: " + oldsteptotal);
        }
    }

    private void setDailyGoal() {

        Calendar rightNow = Calendar.getInstance();
        int date = rightNow.get(Calendar.DATE);
        int month = rightNow.get(Calendar.MONTH) + 1;
        int year = rightNow.get(Calendar.YEAR);

        String s = getCurrentWeek(rightNow);
        String[] Weekdate = s.split(" ");
        int stepnumber;
        int fristdate = Integer.parseInt(Weekdate[0]);
        int lastdate = Integer.parseInt(Weekdate[2]);

        for (int i = fristdate; i <= lastdate; i++) {
            stepnumber = dbManager.getSumOfStepList(i, month, year);
//            Log.e("TAG", "date: " + i + " " + stepnumber);
            DaywiseSteplist.add(stepnumber);
        }

        for (int i = 0; i < DaywiseSteplist.size(); i++) {
//            Log.e("TAG", "date: " + i + " " + DaywiseSteplist.get(i));
            monday.setProgress(DaywiseSteplist.get(0));
            tuesday.setProgress(DaywiseSteplist.get(1));
            wednesday.setProgress(DaywiseSteplist.get(2));
            thrusday.setProgress(DaywiseSteplist.get(3));
            friday.setProgress(DaywiseSteplist.get(4));
            saturday.setProgress(DaywiseSteplist.get(5));
            sunday.setProgress(DaywiseSteplist.get(6));
        }
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


    private void setSharedPreferences() {
        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();
        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
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
            case R.id.lladdwater:
                startActivity(new Intent(MainActivity.this, WaterTrackerActivity.class));
                break;
            case R.id.llwaterSetting:
                startActivity(new Intent(MainActivity.this, WaterSettingActivity.class));
                break;
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
//                        dbManager.DeleteCurrentDayData(date, month, year);
//                        onResume();
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

                    int sum = finalOldsteptotal + Integer.parseInt(numSteps);
                    stepcountModel.setStep(sum);
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
}