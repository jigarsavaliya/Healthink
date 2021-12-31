package com.android.stepcounter.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.AdapterCallback;
import com.android.stepcounter.GpsAdapterCallBack;
import com.android.stepcounter.R;
import com.android.stepcounter.fragment.HistoryFragment;
import com.android.stepcounter.model.GpsTrackerModel;
import com.android.stepcounter.utils.constant;

import java.util.ArrayList;


public class LocationHistoryAdapter extends RecyclerView.Adapter<LocationHistoryAdapter.ViewHolder> {

    ArrayList<GpsTrackerModel> gpsTrackerModels;
    HistoryFragment activity;
    private GpsAdapterCallBack myAdapterListener;

    public LocationHistoryAdapter(HistoryFragment mainActivity, ArrayList<GpsTrackerModel> archivementModels) {
        activity = mainActivity;
        gpsTrackerModels = archivementModels;
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
        viewHolder.mTvType.setText(gpsTrackerModels.get(i).getType());
        viewHolder.mTvMile.setText(gpsTrackerModels.get(i).getDistance()+" Mile");
        viewHolder.mTvDuration.setText(gpsTrackerModels.get(i).getDuration());
        viewHolder.mTvKcal.setText(gpsTrackerModels.get(i).getCalories() + " Kcal");
        viewHolder.mTvSteps.setText(gpsTrackerModels.get(i).getStep() + " Steps");

        if (constant.IsLocationHistoryDelete) {
            viewHolder.mCbDeleteItem.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mCbDeleteItem.setVisibility(View.GONE);
        }

        viewHolder.mCbDeleteItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
//                    countModelArrayList.add(gpsTrackerModels.get(i));
//                    Logger.e(countModelArrayList.size() + "new");
                } else {
//                    countModelArrayList.remove(gpsTrackerModels.get(i));
//                    Logger.e(countModelArrayList.size() + "remove");
                }
                myAdapterListener.onMethodCallback(gpsTrackerModels);
            }
        });

    }

    @Override
    public int getItemCount() {
        return gpsTrackerModels.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvType, mTvMile, mTvDuration, mTvKcal, mTvSteps;
        AppCompatCheckBox mCbDeleteItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tvType);
            mTvMile = itemView.findViewById(R.id.tvMile);
            mTvDuration = itemView.findViewById(R.id.tvDuration);
            mTvKcal = itemView.findViewById(R.id.tvKcal);
            mTvSteps = itemView.findViewById(R.id.tvSteps);
            mCbDeleteItem = itemView.findViewById(R.id.cbDeleteItem);

        }
    }

    public void setMyAdapterListener(GpsAdapterCallBack adapterCallback) {
        this.myAdapterListener = adapterCallback;
    }

    public void updateList() {

    }
}  