package com.android.stepcounter.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.AdapterCallback;
import com.android.stepcounter.R;
import com.android.stepcounter.adpter.HistoryAdapter;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.StepCountModel;
import com.android.stepcounter.model.StepHistoryModel;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRvHistrory;
    HistoryAdapter mHistoryAdapter;
    DatabaseManager dbManager;
    Menu myMenu;
    TextView mTvNoDataFound;

    ArrayList<StepCountModel> stepcountModelArrayList;
    ArrayList<StepCountModel> mCountModelArrayList = new ArrayList<>();
    HashMap<Long, StepHistoryModel> headerMap;
    HashMap<Long, ArrayList<StepCountModel>> stringArrayListHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dbManager = new DatabaseManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("History");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mTvNoDataFound = findViewById(R.id.tvNoDataFound);
        mRvHistrory = findViewById(R.id.rvHistrory);

        getdataFromDatabase();

//        Logger.e(headerMap.keySet());
//        Logger.e(stringArrayListHashMap.keySet());

        if (stringArrayListHashMap != null) {
            mTvNoDataFound.setVisibility(View.GONE);
            mRvHistrory.setVisibility(View.VISIBLE);
            mHistoryAdapter = new HistoryAdapter(HistoryActivity.this, stringArrayListHashMap, headerMap);
            mHistoryAdapter.setMyAdapterListener(new AdapterCallback() {
                @Override
                public void onMethodCallback(ArrayList<StepCountModel> countModelArrayList) {
//                Logger.e(countModelArrayList.size());
                    mCountModelArrayList = countModelArrayList;
                    Logger.e(countModelArrayList.size());
                    if (countModelArrayList.size() != 0) {
                        myMenu.findItem(R.id.action_delete).setVisible(false);
                        myMenu.findItem(R.id.action_tick).setVisible(true);
                    } else {
                        myMenu.findItem(R.id.action_delete).setVisible(true);
                        myMenu.findItem(R.id.action_tick).setVisible(false);
                    }
                }
            });
            mRvHistrory.setHasFixedSize(true);
            mRvHistrory.setLayoutManager(new LinearLayoutManager(this));
            mRvHistrory.setAdapter(mHistoryAdapter);
        } else {
            mTvNoDataFound.setVisibility(View.VISIBLE);
            mRvHistrory.setVisibility(View.GONE);
        }
    }

    private void getdataFromDatabase() {
        stepcountModelArrayList = new ArrayList<>();

        stepcountModelArrayList = dbManager.getDaywiseStepdata();

        long startTimestamp = 0, endtimestamp = 0;
        if (stepcountModelArrayList != null) {
            mTvNoDataFound.setVisibility(View.GONE);
            mRvHistrory.setVisibility(View.VISIBLE);
            startTimestamp = Long.parseLong(stepcountModelArrayList.get(0).getTimestemp());
            endtimestamp = Long.parseLong(stepcountModelArrayList.get(stepcountModelArrayList.size() - 1).getTimestemp());

            Calendar c = Calendar.getInstance();
            long firstdate, lastdate;

            headerMap = new HashMap<>();
            stringArrayListHashMap = new HashMap<>();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = formatter.format(new Date(startTimestamp));
            String[] strings = dateString.split("/");

            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strings[0]));
            c.set(Calendar.MONTH, Integer.parseInt(strings[1]) - 1);
            c.set(Calendar.YEAR, Integer.parseInt(strings[2]));

            do {
                String s = getCurrentWeek(c);
//            Log.e("TAG", "date new : " + c.get(Calendar.DATE));

                String[] Weekdate = s.split("-");
//            Log.e("TAG", "date: " + Weekdate[0]);
//            Log.e("TAG", "date: " + Weekdate[1]);
                firstdate = Long.parseLong(Weekdate[0].trim());
                lastdate = Long.parseLong(Weekdate[1].trim());
//
                Logger.e(formatter.format(new Date(firstdate)) + " - " + formatter.format(new Date(lastdate)));
                Logger.e(firstdate + " - " + lastdate);

                StepHistoryModel waterHistoryModel = new StepHistoryModel();
                waterHistoryModel.setFirstdate(firstdate);
                waterHistoryModel.setLastdate(lastdate);

                for (StepCountModel data : stepcountModelArrayList) {
                    StepHistoryModel value = headerMap.get(waterHistoryModel.getFirstdate());
                    ArrayList<StepCountModel> valueModels = stringArrayListHashMap.get(waterHistoryModel.getFirstdate());

                    if (valueModels == null) {
                        value = new StepHistoryModel();
                        value.setFirstdate(firstdate);
                        value.setLastdate(lastdate);
                        valueModels = new ArrayList<>();
                        headerMap.put(waterHistoryModel.getFirstdate(), value);
                        stringArrayListHashMap.put(waterHistoryModel.getFirstdate(), valueModels);
//                    Logger.e(value.getFirstdate() + " - new value: " + value.getSumstep());
                    }

                    long timestamp = Long.parseLong(data.getTimestemp().trim());
                    Logger.e(" - new time: " + formatter.format(new Date(timestamp)) + "condition -----" + (firstdate <= timestamp && lastdate >= timestamp) + "firstdate -- " + firstdate + "lastdate -- " + lastdate + "timestamp -- " + timestamp);
                    if (firstdate <= timestamp && lastdate >= timestamp) {
                        valueModels.add(data);
                        value.setSumstep(value.getSumstep() + data.getSumstep());
//                    Logger.e(value.getFirstdate() + " - add value: " + value.getSumstep());
                    }
                }

                // do while 6e atle issue che  a check kro c.add thai atle last week add kre che
                c.add(Calendar.DAY_OF_YEAR, +7);
            }
            while (firstdate < endtimestamp);
        } else {
            mTvNoDataFound.setVisibility(View.VISIBLE);
            mRvHistrory.setVisibility(View.GONE);
        }

//        Logger.e(startTimestamp);
//        Logger.e(endtimestamp);
    }

    public static String getCurrentWeek(Calendar mCalendar) {
        // 1 = Sunday, 2 = Monday, etc.
        int day_of_week = mCalendar.get(Calendar.DAY_OF_WEEK);

        int monday_offset;
        if (day_of_week == 1) {
            monday_offset = -6;
        } else
            monday_offset = (2 - day_of_week); // need to minus back
        mCalendar.add(Calendar.DAY_OF_YEAR, monday_offset);
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.AM_PM, Calendar.AM);
        long mDateMonday = mCalendar.getTimeInMillis();

        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.AM_PM, Calendar.PM);
        long mDateSunday = mCalendar.getTimeInMillis();
        return mDateMonday + " - " + mDateSunday;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        constant.IsHistoryDelete = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        myMenu = menu;
        menu.findItem(R.id.action_delete).setVisible(true);
        menu.findItem(R.id.action_tick).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                constant.IsHistoryDelete = true;
                mHistoryAdapter.notifyDataSetChanged();
                myMenu.findItem(R.id.action_delete).setVisible(false);
                myMenu.findItem(R.id.action_tick).setVisible(true);
                break;
            case R.id.action_tick:
                constant.IsHistoryDelete = false;
                myMenu.findItem(R.id.action_delete).setVisible(true);
                myMenu.findItem(R.id.action_tick).setVisible(false);

                if (mCountModelArrayList.size() != 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("Are You sure you want to delete data?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "DELETE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    for (int i = 0; i < mCountModelArrayList.size(); i++) {
                                        dbManager.DeleteCurrentDayStepCountData(mCountModelArrayList.get(i).getDate(), mCountModelArrayList.get(i).getMonth(), mCountModelArrayList.get(i).getYear());
                                    }
                                    getdataFromDatabase();
                                    mHistoryAdapter.updatelist(stringArrayListHashMap, headerMap);
                                    mHistoryAdapter.notifyDataSetChanged();
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
                } else {
                    mHistoryAdapter.notifyDataSetChanged();
                }
                break;
        }
        return true;
    }
}