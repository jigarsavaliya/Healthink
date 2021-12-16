package com.android.stepcounter.adpter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.ArchivementActivity;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.utils.StorageManager;

import java.util.ArrayList;


public class TotalDistanceAdapter extends RecyclerView.Adapter<TotalDistanceAdapter.ViewHolder> {

    ArrayList<ArchivementModel> mDailyStepDatalist;
    Activity activity;
    int StepGoal;

    public TotalDistanceAdapter(ArchivementActivity mainActivity, ArrayList<ArchivementModel> archivementModels) {
        activity = mainActivity;
        mDailyStepDatalist = archivementModels;
        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_daily_step, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mTvDailyLabel.setText(mDailyStepDatalist.get(i).getLabel());
    }

    @Override
    public int getItemCount() {
        return mDailyStepDatalist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvDailyLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvDailyLabel = itemView.findViewById(R.id.tvDailyLabel);

        }
    }


}  