package com.android.stepcounter.adpter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.MainActivity;
import com.android.stepcounter.model.StepCountModel;
import com.android.stepcounter.utils.StorageManager;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class StepWeekChartAdapter extends RecyclerView.Adapter<StepWeekChartAdapter.ViewHolder> {

    ArrayList<StepCountModel> modelArrayList;
    Activity activity;
    int StepGoal;

    public StepWeekChartAdapter(MainActivity mainActivity, ArrayList<StepCountModel> stepcountModelArrayList) {
        activity = mainActivity;
        modelArrayList = stepcountModelArrayList;
        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_stepchart, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mStepchart.setProgressMax(StepGoal);
        viewHolder.mStepchart.setProgress(modelArrayList.get(i).getSumstep());

//        Log.e("TAG", "onBindViewHolder: "+modelArrayList.get(i).getDate() + "-" + modelArrayList.get(i).getMonth() + "-" + modelArrayList.get(i).getYear() );

        SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dayName = null;
        try {
            Date myDate = inFormat.parse(modelArrayList.get(i).getDate() + "-" + modelArrayList.get(i).getMonth() + "-" + modelArrayList.get(i).getYear());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
            dayName = simpleDateFormat.format(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.mstepday.setText(dayName.charAt(0) + "");

    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CircularProgressBar mStepchart;
        TextView mstepday;

        public ViewHolder(View itemView) {
            super(itemView);
            mStepchart = itemView.findViewById(R.id.stepchart);
            mstepday = itemView.findViewById(R.id.stepday);
        }
    }


}  