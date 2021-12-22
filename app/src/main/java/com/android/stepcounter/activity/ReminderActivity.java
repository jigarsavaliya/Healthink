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
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;

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

                String[] Days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                boolean[] checkedItems = {true, true, true, true, true, true, true};
                builder.setMultiChoiceItems(Days, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i, boolean isChecked) {
                        checkedItems[i] = isChecked;
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String Value = "";
                        for (int i = 0; i < Days.length; i++) {
                            if (checkedItems[i]) {
                                Value += Days[i].substring(0, 3) + ", ";
                            }
                            mTvDayList.setText(Value);
                        }
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


                    Intent intent1 = new Intent(ReminderActivity.this, DailyReminderReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
                    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
                    Logger.e(calendar.getTimeInMillis());
                }
                finish();
                break;
        }
    }
}