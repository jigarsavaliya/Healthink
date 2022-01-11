package com.android.stepcounter.adpter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.ArchivementActivity;
import com.android.stepcounter.activity.ArchivementDetailActivity;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.constant;

import java.util.ArrayList;


public class ArchivementDataAdapter extends RecyclerView.Adapter<ArchivementDataAdapter.ViewHolder> {

    ArrayList<ArchivementModel> mDailyStepDatalist;
    Activity activity;
    int StepGoal;
    DatabaseManager dbManager;
    long progressMaxValue, progressValue;
    int CurrentProgressValue;
    public Boolean IsDailyPendingStatus = true;
    public Boolean IsComboPendingStatus = true;
    public Boolean IsTotalPendingStatus = true;
    public Boolean IsDistancePendingStatus = true;

    public ArchivementDataAdapter(ArchivementActivity mainActivity, ArrayList<ArchivementModel> archivementModels, int currentStepData) {
        activity = mainActivity;
        mDailyStepDatalist = archivementModels;
        dbManager = new DatabaseManager(mainActivity);
        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
        CurrentProgressValue = currentStepData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_daily_step, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
//        progressMaxValue = dbManager.getValueofTypeWiseData(mDailyStepDatalist.get(i).getType());
//        progressValue = dbManager.getValueTypeWiseData(mDailyStepDatalist.get(i).getType());

        if (mDailyStepDatalist.get(i).isCompeleteStatus()) {
            viewHolder.mTvDailyLabel.setText(mDailyStepDatalist.get(i).getLabel());
            viewHolder.mTvDailyLabel.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            if (mDailyStepDatalist.get(i).getType().equals("Daily Step")) {
                viewHolder.mLlLevalCount.setVisibility(View.VISIBLE);
                viewHolder.mTvlevelcount.setText(mDailyStepDatalist.get(i).getCount() + "x");
            } else {
                viewHolder.mLlLevalCount.setVisibility(View.GONE);
            }
            viewHolder.mPbCompletedBar.setVisibility(View.GONE);
            viewHolder.mTvProgressLabel.setVisibility(View.GONE);

        } else {

            viewHolder.mTvDailyLabel.setText(mDailyStepDatalist.get(i).getLabel());
            viewHolder.mTvDailyLabel.setTextColor(activity.getResources().getColor(R.color.black));
            viewHolder.mLlLevalCount.setVisibility(View.GONE);

            if (IsDailyPendingStatus) {
//                if (mDailyStepDatalist.get(i).getValue() == progressMaxValue) {
                progressMaxValue = (int) mDailyStepDatalist.get(i).getValue();
                viewHolder.mPbCompletedBar.setVisibility(View.VISIBLE);
                viewHolder.mPbCompletedBar.setMax((int) progressMaxValue);
                viewHolder.mPbCompletedBar.setProgress(CurrentProgressValue);
                viewHolder.mTvProgressLabel.setText((progressMaxValue - CurrentProgressValue) + " steps left");
                IsDailyPendingStatus = false;
//                }
            } else {
                viewHolder.mPbCompletedBar.setVisibility(View.GONE);
                viewHolder.mTvProgressLabel.setVisibility(View.GONE);
            }

            if (mDailyStepDatalist.get(i).getType().equals(constant.ARCHIVEMENT_COMBO_DAY)) {
//                if (CurrentProgressValue <= progressValue) {
                if (IsComboPendingStatus) {
                    progressValue = (int) mDailyStepDatalist.get(i).getValue();
                    viewHolder.mPbCompletedBar.setVisibility(View.VISIBLE);
                    viewHolder.mTvProgressLabel.setVisibility(View.VISIBLE);
                    viewHolder.mPbCompletedBar.setMax((int) mDailyStepDatalist.get(i).getValue());
                    viewHolder.mPbCompletedBar.setProgress(CurrentProgressValue);
                    viewHolder.mTvProgressLabel.setText((progressValue - CurrentProgressValue) + " days left");
                    IsComboPendingStatus = false;
                } else {
                    viewHolder.mPbCompletedBar.setVisibility(View.GONE);
                    viewHolder.mTvProgressLabel.setVisibility(View.GONE);
                }

//                }
            }

            if (mDailyStepDatalist.get(i).getType().equals(constant.ARCHIVEMENT_TOTAL_DAYS)) {
//                if (CurrentProgressValue <= progressValue) {
                if (IsTotalPendingStatus) {
                    progressValue = (int) mDailyStepDatalist.get(i).getValue();
                    viewHolder.mPbCompletedBar.setVisibility(View.VISIBLE);
                    viewHolder.mTvProgressLabel.setVisibility(View.VISIBLE);
                    viewHolder.mPbCompletedBar.setMax((int) progressValue);
                    viewHolder.mPbCompletedBar.setProgress(CurrentProgressValue);
                    viewHolder.mTvProgressLabel.setText((progressValue - CurrentProgressValue) + " days left");
                    IsTotalPendingStatus = false;
                } else {
                    viewHolder.mPbCompletedBar.setVisibility(View.GONE);
                    viewHolder.mTvProgressLabel.setVisibility(View.GONE);
                }
//                }
            }

//            if (constant.IsDistancePendingStatus) {
            if (mDailyStepDatalist.get(i).getType().equals(constant.ARCHIVEMENT_TOTAL_DISTANCE)) {
//                if (CurrentProgressValue <= progressValue) {
                if (IsDistancePendingStatus) {
                    progressValue = (int) mDailyStepDatalist.get(i).getValue();
                    viewHolder.mPbCompletedBar.setVisibility(View.VISIBLE);
                    viewHolder.mTvProgressLabel.setVisibility(View.VISIBLE);
                    viewHolder.mPbCompletedBar.setMax((int) progressValue);
                    viewHolder.mPbCompletedBar.setProgress(CurrentProgressValue);
                    viewHolder.mTvProgressLabel.setText((progressValue - CurrentProgressValue) + " Mile left");
                    IsDistancePendingStatus = false;
                } else {
                    viewHolder.mPbCompletedBar.setVisibility(View.GONE);
                    viewHolder.mTvProgressLabel.setVisibility(View.GONE);
                }
//                }
            }

        }

        viewHolder.mLlArchivementView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, ArchivementDetailActivity.class);
                if (mDailyStepDatalist.get(i).getType().equals("Daily Step")) {
                    intent.putExtra("DailyStep", true);
                } else if (mDailyStepDatalist.get(i).getType().equals("Combo Day")) {
                    intent.putExtra("ComboDay", true);
                } else if (mDailyStepDatalist.get(i).getType().equals("Total Days")) {
                    intent.putExtra("TotalDays", true);
                } else if (mDailyStepDatalist.get(i).getType().equals("Total Distance")) {
                    intent.putExtra("TotalDistance", true);
                }
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDailyStepDatalist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvDailyLabel, mTvlevelcount, mTvProgressLabel;
        LinearLayout mLlArchivementView, mLlLevalCount;
        ProgressBar mPbCompletedBar;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvDailyLabel = itemView.findViewById(R.id.tvDailyLabel);
            mTvProgressLabel = itemView.findViewById(R.id.tvProgressLabel);
            mTvlevelcount = itemView.findViewById(R.id.tvlevelcount);
            mLlArchivementView = itemView.findViewById(R.id.llArchivementView);
            mLlLevalCount = itemView.findViewById(R.id.llLevalCount);
            mPbCompletedBar = itemView.findViewById(R.id.pbCompletedBar);

        }
    }


}