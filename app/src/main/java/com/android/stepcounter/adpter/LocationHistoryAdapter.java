package com.android.stepcounter.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.fragment.HistoryFragment;
import com.android.stepcounter.utils.StorageManager;


public class LocationHistoryAdapter extends RecyclerView.Adapter<LocationHistoryAdapter.ViewHolder> {

    String mDailyStepDatalist;
    HistoryFragment activity;

    public LocationHistoryAdapter(HistoryFragment mainActivity, String archivementModels) {
        activity = mainActivity;
        mDailyStepDatalist = archivementModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_location_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvType, mTvMile, mTvDuration, mTvKcal, mTvSteps;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tvType);
            mTvMile = itemView.findViewById(R.id.tvMile);
            mTvDuration = itemView.findViewById(R.id.tvDuration);
            mTvKcal = itemView.findViewById(R.id.tvKcal);
            mTvSteps = itemView.findViewById(R.id.tvSteps);

        }
    }


}  