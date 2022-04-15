package com.js.stepcounter.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.js.stepcounter.R;
import com.js.stepcounter.sevices.AlarmReceiver;
import com.js.stepcounter.utils.StorageManager;

import java.util.ArrayList;
import java.util.Calendar;

public class WaterReminderSettingActivity extends BaseActivity implements View.OnClickListener {
    Toolbar mToolbar;
    TextView mtvhourstext, mtvremindertime;
    int mHour, mMinute;
    int mStartHour = 9, mStartMinute = 0, mEndHour = 9, mEndMinute = 0;
    Calendar c = Calendar.getInstance();
    String starttimeSet = "AM", EndtimeSet = "PM";
    SwitchCompat mScReminder;
    Button mbtnReminderSave;
    String getStarttimeSet, getEndtimeSet, getReminderInterval;
    String mReminderIntervalvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_reminder_setting);
        setSharedPreferences();
        init();
    }

    private void setSharedPreferences() {
        getStarttimeSet = StorageManager.getInstance().getWaterReminderStart();
        getEndtimeSet = StorageManager.getInstance().getWaterReminderEnd();
        getReminderInterval = StorageManager.getInstance().getWaterinterval();
    }

    private void init() {
        String[] s = getStarttimeSet.split(":");
//        Log.e("TAG", s[0] + "init: " + s[1]);
        String[] s1 = s[1].split(" ");
//        Log.e("TAG", s1[0] + "init: " + s1[1]);
        mStartHour = Integer.parseInt(s[0]);
        mStartMinute = Integer.parseInt(s1[0]);

        String[] strings = getEndtimeSet.split(":");
//        Log.e("TAG", strings[0] + "init: " + strings[1]);
        String[] strings1 = strings[1].split(" ");
//        Log.e("TAG", strings1[0] + "init: " + strings1[1]);
        mEndHour = Integer.parseInt(strings[0]);
        mEndMinute = Integer.parseInt(strings1[0]);

        c.set(Calendar.HOUR, mStartHour);
        c.set(Calendar.MINUTE, mStartMinute);

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

        mbtnReminderSave = findViewById(R.id.btnReminderSave);
        mbtnReminderSave.setVisibility(View.GONE);
        mScReminder = findViewById(R.id.screminder);
        mtvremindertime = findViewById(R.id.tvremindertime);
        mtvhourstext = findViewById(R.id.tvhourstext);
        mtvhourstext.setText("Every " + getReminderInterval + "hour");

        mtvremindertime.setText(getStarttimeSet + " - " + getEndtimeSet);

        mbtnReminderSave.setOnClickListener(this);
        mtvremindertime.setOnClickListener(this);

        if (StorageManager.getInstance().getReminder()) {
            mScReminder.setChecked(true);
        } else {
            mScReminder.setChecked(false);
        }

        mScReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mbtnReminderSave.setVisibility(View.VISIBLE);
                    StorageManager.getInstance().setReminder(b);
                } else {
                    mbtnReminderSave.setVisibility(View.VISIBLE);
                    StorageManager.getInstance().setReminder(false);
                }
            }
        });


        //Identifying spinner defined in xml layout
        AppCompatSpinner spintervalhour = findViewById(R.id.spintervalhour);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("0.5");
        arrayList.add("1");
        arrayList.add("1.5");
        arrayList.add("2");
        arrayList.add("2.5");
        arrayList.add("3");
        arrayList.add("3.5");
        arrayList.add("4");
        arrayList.add("4.5");
        arrayList.add("5");

        final ArrayAdapter adapter1 = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        spintervalhour.setAdapter(adapter1);

        spintervalhour.setSelection(((ArrayAdapter<String>) spintervalhour.getAdapter()).getPosition(getReminderInterval));

        spintervalhour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mReminderIntervalvalue = adapterView.getItemAtPosition(i).toString();
                mtvhourstext.setText("Every " + mReminderIntervalvalue + "hour");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void showReminderDailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_reminder, null);
        dialogBuilder.setView(d);
        AlertDialog alertDialog = dialogBuilder.create();
        Button mBtnSave = (Button) d.findViewById(R.id.btnSave);
        Button mBtnCancel = (Button) d.findViewById(R.id.btnCancel);

        TextView mtvStartTime = (TextView) d.findViewById(R.id.tvStartTime);
        TextView mtvEndTime = (TextView) d.findViewById(R.id.tvEndTime);

        mHour = c.get(Calendar.HOUR);
        mMinute = c.get(Calendar.MINUTE);

        mtvStartTime.setText(getStarttimeSet);
        mtvEndTime.setText(getEndtimeSet);

        mtvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(WaterReminderSettingActivity.this,
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
                                mtvStartTime.setText(hourOfDay + ":" + minute + " " + starttimeSet);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        mtvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c.set(Calendar.HOUR_OF_DAY, mEndHour);
                c.set(Calendar.MINUTE, mEndMinute);

                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(WaterReminderSettingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                mEndHour = hourOfDay;
                                mEndMinute = minute;

                                if (mHour > 12) {
                                    mHour -= 12;
                                    EndtimeSet = "PM";
                                } else if (mHour == 0) {
                                    mHour += 12;
                                    EndtimeSet = "AM";
                                } else if (mHour == 12) {
                                    EndtimeSet = "PM";
                                } else {
                                    EndtimeSet = "AM";
                                }

                                mtvEndTime.setText(hourOfDay + ":" + minute + " " + EndtimeSet);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.e("TAG", "onClick: " + mStartHour + ":" + mStartMinute + " " + starttimeSet);
//                Log.e("TAG", "onClick: " + mEndHour + ":" + mEndMinute + " " + EndtimeSet);
                StorageManager.getInstance().setWaterReminderStart(mStartHour + ":" + mStartMinute + " " + starttimeSet);
                StorageManager.getInstance().setWaterReminderEnd(mEndHour + ":" + mEndMinute + " " + EndtimeSet);
                StorageManager.getInstance().setWaterinterval(mReminderIntervalvalue);
                mtvremindertime.setText(mStartHour + ":" + mStartMinute + " " + starttimeSet + " - " + mEndHour + ":" + mEndMinute + " " + EndtimeSet);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReminderSave:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", mtvremindertime.getText().toString());
                returnIntent.putExtra("hours", mReminderIntervalvalue);
                setResult(Activity.RESULT_OK, returnIntent);

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

                    long time = calendar.getTimeInMillis() + intervaltime;
                    Intent intent1 = new Intent(WaterReminderSettingActivity.this, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
                    am.setRepeating(AlarmManager.RTC_WAKEUP, time, intervaltime, pendingIntent);
//                    Logger.e(time);
                }

                finish();
                break;
            case R.id.tvremindertime:
                showReminderDailog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}