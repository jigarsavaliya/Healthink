package com.android.stepcounter.adpter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.AdapterCallback;
import com.android.stepcounter.R;
import com.android.stepcounter.activity.HistoryActivity;
import com.android.stepcounter.model.StepCountModel;
import com.android.stepcounter.model.StepHistoryModel;
import com.android.stepcounter.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    HashMap<Long, ArrayList<StepCountModel>> modelArrayList = new HashMap<>();
    HashMap<Long, StepHistoryModel> headerMap = new HashMap<>();
    Activity activity;
    HistoryDetailsAdapter HistoryDetailsAdapter;
    private ArrayList<Long> mKeys;
    private AdapterCallback myAdapterListener;

    public HistoryAdapter(HistoryActivity mainActivity, HashMap<Long, ArrayList<StepCountModel>> stepcountModelArrayList,
                          HashMap<Long, StepHistoryModel> stepArrayListHashMap) {
        activity = mainActivity;
        modelArrayList = stepcountModelArrayList;
        headerMap = stepArrayListHashMap;

        mKeys = new ArrayList<Long>(stepArrayListHashMap.keySet());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_step_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        long key = mKeys.get(i);
//        Logger.e(key);
        StepHistoryModel weekHeader = headerMap.get(key);

        Calendar c = Calendar.getInstance();

//        if (weekHeader.getSumstep() != 0) {
        // Aya week long to string date convert krvanu ha
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(weekHeader.getFirstdate()));
        String[] strings = dateString.split("/");

        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strings[0]));
        c.set(Calendar.MONTH, Integer.parseInt(strings[1]) - 1);
        c.set(Calendar.YEAR, Integer.parseInt(strings[2]));

        String s1 = getCurrentWeekdate(c);
        String[] Weekdate1 = s1.split("-");

        viewHolder.mtvdate.setText(Weekdate1[0] + " - " + Weekdate1[1]);

        viewHolder.mtvstep.setText(weekHeader.getSumstep() + " Steps");

        HistoryDetailsAdapter = new HistoryDetailsAdapter((HistoryActivity) activity, modelArrayList.get(key));
        HistoryDetailsAdapter.setCallback(new AdapterCallback() {
            @Override
            public void onMethodCallback(ArrayList<StepCountModel> countModelArrayList) {
                myAdapterListener.onMethodCallback(countModelArrayList);
            }
        });

        viewHolder.mrvdetails.setHasFixedSize(true);
        viewHolder.mrvdetails.setLayoutManager(new LinearLayoutManager(activity));
        viewHolder.mrvdetails.setAdapter(HistoryDetailsAdapter);

    }

    @Override
    public int getItemCount() {
        return mKeys.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mtvdate, mtvstep;
        RecyclerView mrvdetails;

        public ViewHolder(View itemView) {
            super(itemView);
            mtvdate = itemView.findViewById(R.id.tvdate);
            mtvstep = itemView.findViewById(R.id.tvtotalstep);
            mrvdetails = itemView.findViewById(R.id.rvdetails);

        }
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

    public void setMyAdapterListener(AdapterCallback adapterCallback) {
        this.myAdapterListener = adapterCallback;
    }

    public void updatelist(HashMap<Long, ArrayList<StepCountModel>> stepcountModelArrayList,
                           HashMap<Long, StepHistoryModel> stepArrayListHashMap) {
        modelArrayList = stepcountModelArrayList;
        headerMap = stepArrayListHashMap;

        mKeys = new ArrayList<Long>(stepArrayListHashMap.keySet());
    }

}  