package com.android.stepcounter.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.android.stepcounter.R;
import com.android.stepcounter.sevices.DailyReminderReceiver;
import com.android.stepcounter.utils.StorageManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar mToolbar;
    String getDailyReminderTime;
    int mStartHour = 9, mStartMinute = 0;
    Calendar c = Calendar.getInstance();
    TextView mTvDailyRemindertime, mTvDayList;
    LinearLayout mLlDailyReminder, mLlRepeatDay;
    Button mBtnDailyReminder;
    int mHour, mMinute;
    String starttimeSet = "AM";
    SwitchCompat mScDailyReminder;
    String[] Days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    boolean[] checkedItems = {false, false, false, false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        setSharedPreferences();
        init();
    }

    private void setSharedPreferences() {
        getDailyReminderTime = StorageManager.getInstance().getDailyReminder();
    }

    private void init() {
        String[] s = getDailyReminderTime.split(":");
        Log.e("TAG", s[0] + "init: " + s[1]);
        String[] s1 = s[1].split(" ");
        Log.e("TAG", s1[0] + "init: " + s1[1]);

        mStartHour = Integer.parseInt(s[0]);
        mStartMinute = Integer.parseInt(s1[0]);


        c.set(Calendar.HOUR, mStartHour);
        c.set(Calendar.MINUTE, mStartMinute);
        if (s1[1] == "AM") {
            c.set(Calendar.AM_PM, Calendar.AM);
        } else {
            c.set(Calendar.AM_PM, Calendar.PM);
        }


        mHour = c.get(Calendar.HOUR);
        mMinute = c.get(Calendar.MINUTE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Reminder");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mLlDailyReminder = findViewById(R.id.llDailyReminder);
        mLlRepeatDay = findViewById(R.id.llRepeatDay);
        mTvDailyRemindertime = findViewById(R.id.tvDailyRemindertime);
        mTvDayList = findViewById(R.id.tvDayList);
        mTvDailyRemindertime.setText(getDailyReminderTime);
        mLlDailyReminder.setOnClickListener(this);
        mLlRepeatDay.setOnClickListener(this);

        mBtnDailyReminder = findViewById(R.id.btnDailyReminder);
        mBtnDailyReminder.setOnClickListener(this);

        mScDailyReminder = findViewById(R.id.scDailyReminder);

        if (StorageManager.getInstance().getDailyReminderFlag()) {
            mScDailyReminder.setChecked(true);
        } else {
            mScDailyReminder.setChecked(false);
        }

        mScDailyReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    StorageManager.getInstance().setDailyReminderFlag(b);
                } else {
                    StorageManager.getInstance().setDailyReminderFlag(b);
                }
            }
        });


        ArrayList<Integer> integerArrayList = new ArrayList<>();

        Gson gson = new Gson();
        String json = StorageManager.getInstance().getDailyReminderDay();
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();

        integerArrayList = gson.fromJson(json, type);

//        Logger.e(gson.fromJson(json, type));

        String Value = "";

        for (int i = 0; i < integerArrayList.size(); i++) {
            for (int j = 0; j < Days.length; j++) {
                if ((integerArrayList.get(i) - 1) == j) {
                    Value += Days[j].substring(0, 3) + ", ";
                }
            }
        }

        for (int i = 0; i < integerArrayList.size(); i++) {
            for (int j = 0; j < checkedItems.length; j++) {
                if ((integerArrayList.get(i) - 1) == j) {
                    checkedItems[j] = true;
                }
            }
        }
        mTvDayList.setText(Value + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDailyReminder:
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                mStartHour = hourOfDay;
                                mStartMinute = minute;

                                if (mHour > 12) {
                                    mHour -= 12;
                                    starttimeSet = "PM";
                                } else if (mHour == 0) {
                                    mHour += 12;
                                    starttimeSet = "AM";
                                } else if (mHour == 12) {
                                    starttimeSet = "PM";
                                } else {
                                    starttimeSet = "AM";
                                }

                                mTvDailyRemindertime.setText(hourOfDay + ":" + minute + " " + starttimeSet);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                break;
            case R.id.llRepeatDay:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Repeat");

                builder.setMultiChoiceItems(Days, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i, boolean isChecked) {
                        checkedItems[i] = isChecked;
                    }
                });

                ArrayList<Integer> finalIntegerArrayList = new ArrayList<>();

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String Value = "";
                        for (int i = 0; i < Days.length; i++) {
                            if (checkedItems[i]) {
                                Value += Days[i].substring(0, 3) + ", ";
//                                Logger.e(i);
                                finalIntegerArrayList.add(i + 1);
                            }
                            mTvDayList.setText(Value);
                        }

                        Gson gson = new Gson();
                        String json = gson.toJson(finalIntegerArrayList);
                        StorageManager.getInstance().setDailyReminderDay(json);
                    }
                });
                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.btnDailyReminder:
                StorageManager.getInstance().setDailyReminder(mStartHour + ":" + mStartMinute + " " + starttimeSet);

                if (StorageManager.getInstance().getDailyReminderFlag()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR, mStartHour);
                    calendar.set(Calendar.MINUTE, mStartMinute);
                    if (starttimeSet == "AM") {
                        calendar.set(Calendar.AM_PM, Calendar.AM);
                    } else {
                        calendar.set(Calendar.AM_PM, Calendar.PM);
                    }

                    Calendar cal = Calendar.getInstance();
                    long Rightnowtime = cal.getTimeInMillis();

//                    Logger.e(mStartHour + ":" + mStartMinute + " " + starttimeSet);
//                    Logger.e(calendar.getTimeInMillis());
                    long setTime = calendar.getTimeInMillis();

                    if (Rightnowtime > setTime) {
                        long time = calendar.getTimeInMillis() + 1000 * 60 * 60 * 24;
//                        Logger.e(time);

                        Intent intent1 = new Intent(ReminderActivity.this, DailyReminderReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, time, 1000 * 60 * 60 * 24, pendingIntent);
                    }
//                    Logger.e(calendar.getTimeInMillis());
                }
                finish();
                break;
        }
    }
}