package com.android.stepcounter.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.adpter.HistoryAdapter;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.stepcountModel;
import com.android.stepcounter.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRvHistrory;
    HistoryAdapter mHistoryAdapter;
    DBHandler dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dbManager = new DBHandler(this);
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("History");
        setSupportActionBar(mToolbar);

        ArrayList<stepcountModel> stepcountModelArrayList = new ArrayList<>();

        stepcountModelArrayList = dbManager.getDaywiseStepdata();

        long startTimestamp = 0, endtimestamp = 0;
        if (!stepcountModelArrayList.isEmpty()) {
            startTimestamp = Long.parseLong(stepcountModelArrayList.get(0).getTimestemp());
            endtimestamp = Long.parseLong(stepcountModelArrayList.get(stepcountModelArrayList.size() - 1).getTimestemp());
        }

        Logger.e(startTimestamp);
        Logger.e(endtimestamp);

        Calendar c = Calendar.getInstance();
        long firstdate, lastdate;

        HashMap<String, ArrayList<stepcountModel>> stringArrayListHashMap = new HashMap<>();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(startTimestamp));
        String[] strings = dateString.split("/");
//        Log.e("TAG", "date: " + strings[0]);
//        Log.e("TAG", "date: " + (Integer.parseInt(strings[1]) - 1));
//        Log.e("TAG", "date: " + strings[2]);

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

            Logger.e(formatter.format(new Date(firstdate)) + " - " + formatter.format(new Date(lastdate)));

            for (stepcountModel data : stepcountModelArrayList) {
                ArrayList<stepcountModel> value = stringArrayListHashMap.get(formatter.format(new Date(firstdate)));
                if (value == null) {
                    value = new ArrayList<stepcountModel>();
                    stringArrayListHashMap.put(formatter.format(new Date(firstdate)), value);
//                    Log.e("TAG", "value: " + firstdate);
                }
                long timestamp = Long.parseLong(data.getTimestemp().trim());
                if (firstdate <= timestamp && lastdate >= timestamp) {
                    value.add(data);
                }
            }

            // do while 6e atle issue che  a check kro c.add thai atle last week add kre che
            c.add(Calendar.DAY_OF_YEAR, +7);
        }
        while (firstdate < endtimestamp);

        /*Iterator it = stringArrayListHashMap.keySet().iterator();
        while (it.hasNext()) {
            String item = (String) it.next();
            stringArrayListHashMap.remove(item);
        }*/

        /*Set<String> keyset = stringArrayListHashMap.keySet();
        for (int i = stringArrayListHashMap.keySet().size(); i > 0; i--) {
            if (keyset.) {
                stringArrayListHashMap.remove(stringArrayListHashMap.get(i));
            }
        }*/

        mRvHistrory = findViewById(R.id.rvHistrory);
        mHistoryAdapter = new HistoryAdapter(HistoryActivity.this, stringArrayListHashMap);
        mRvHistrory.setHasFixedSize(true);
        mRvHistrory.setLayoutManager(new LinearLayoutManager(this));
        mRvHistrory.setAdapter(mHistoryAdapter);
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

        long mDateMonday = mCalendar.getTimeInMillis();

        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
        long mDateSunday = mCalendar.getTimeInMillis();
        return mDateMonday + " - " + mDateSunday;
    }

}