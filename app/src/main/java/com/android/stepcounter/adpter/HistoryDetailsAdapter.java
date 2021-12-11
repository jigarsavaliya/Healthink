package com.android.stepcounter.adpter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.HistoryActivity;
import com.android.stepcounter.model.stepcountModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class HistoryDetailsAdapter extends RecyclerView.Adapter<HistoryDetailsAdapter.ViewHolder> {

    ArrayList<stepcountModel> modelArrayList;
    Activity activity;

    public HistoryDetailsAdapter(HistoryActivity mainActivity, ArrayList<stepcountModel> stepcountModelArrayList) {
        activity = mainActivity;
        modelArrayList = stepcountModelArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_step_history_details, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.mtvdate.setText(modelArrayList.get(i).getDate() + "");
        viewHolder.mtvstep.setText(modelArrayList.get(i).getSumstep() + "");
        viewHolder.mtvkcal.setText(modelArrayList.get(i).getCalorie() + "");
        viewHolder.mtvhours.setText("");
        viewHolder.mtvkm.setText(modelArrayList.get(i).getDistance() + "");

    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mtvdate, mtvstep, mtvkcal, mtvhours, mtvkm;

        public ViewHolder(View itemView) {
            super(itemView);
            mtvdate = itemView.findViewById(R.id.tvdate);
            mtvstep = itemView.findViewById(R.id.tvstep);
            mtvkcal = itemView.findViewById(R.id.tvkcal);
            mtvhours = itemView.findViewById(R.id.tvhours);
            mtvkm = itemView.findViewById(R.id.tvkm);

        }
    }

    public static String getCurrentWeek(Calendar mCalendar) {
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

//        Date mDateMonday = mCalendar.getTime();
        long mDateMonday = mCalendar.getTimeInMillis();

        Log.e("mDateMonday", "" + mCalendar.getTimeInMillis());
        // return 6 the next days of current day (object cal save current day)
        mCalendar.add(Calendar.DAY_OF_YEAR, 6);
//        Date mDateSunday = mCalendar.getTime();
        long mDateSunday = mCalendar.getTimeInMillis();

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

//        return MONDAY + " - " + SUNDAY;
        return mDateMonday + " - " + mDateSunday;
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
}  