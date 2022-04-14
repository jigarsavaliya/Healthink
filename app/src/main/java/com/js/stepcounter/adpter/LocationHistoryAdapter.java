package com.js.stepcounter.adpter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.js.stepcounter.GpsAdapterCallBack;
import com.js.stepcounter.R;
import com.js.stepcounter.fragment.HistoryFragment;
import com.js.stepcounter.model.GpsTrackerModel;
import com.js.stepcounter.utils.constant;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class LocationHistoryAdapter extends RecyclerView.Adapter<LocationHistoryAdapter.ViewHolder> {

    ArrayList<GpsTrackerModel> gpsTrackerModels;
    ArrayList<GpsTrackerModel> gpsTrackerModelArrayList = new ArrayList<>();
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
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        viewHolder.mTvType.setText(gpsTrackerModels.get(i).getAction());
        viewHolder.mTvMile.setText(gpsTrackerModels.get(i).getDistance() + " Mile");
        viewHolder.mTvDuration.setText(gpsTrackerModels.get(i).getDuration());
        viewHolder.mTvKcal.setText(gpsTrackerModels.get(i).getCalories() + " Kcal");
        viewHolder.mTvSteps.setText(gpsTrackerModels.get(i).getStep() + " Steps");
//        viewHolder.mCbDeleteItem.setChecked(false);

        if (constant.IsLocationHistoryDelete) {
            viewHolder.mCbDeleteItem.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mCbDeleteItem.setVisibility(View.GONE);
        }
        viewHolder.mCbDeleteItem.setChecked(false);

        viewHolder.mCbDeleteItem.setOnClickListener(view -> {
            if (!gpsTrackerModelArrayList.contains(gpsTrackerModels.get(i))) {
                gpsTrackerModelArrayList.add(gpsTrackerModels.get(i));
//                Logger.e("count" + gpsTrackerModelArrayList.size());
            } else {
                gpsTrackerModelArrayList.remove(gpsTrackerModels.get(i));
            }
//            Logger.e("count" + gpsTrackerModelArrayList.size());
            myAdapterListener.onMethodCallback(gpsTrackerModelArrayList);
        });


        Glide.with(activity).load("http://maps.google.com/maps/api/staticmap?center=" +
                gpsTrackerModels.get(i).getSlatitude() +
                "," +
                gpsTrackerModels.get(i).getSlogtitude() +
                "&zoom=15&size=200x200&sensor=false" +
                "&markers=color:red%7Clabel:C%" +
                gpsTrackerModels.get(i).getElatitude() +
                "," +
                gpsTrackerModels.get(i).getElongtitude())
                .centerCrop()
                .into(viewHolder.mivmap);

      /*  Logger.e("http://maps.google.com/maps/api/staticmap?center=" + gpsTrackerModels.get(i).getSlatitude() + "," +
                gpsTrackerModels.get(i).getSlogtitude() + "&zoom=15&size=200x200&sensor=false" + "&markers=color:red%7Clabel:C%" +
                gpsTrackerModels.get(i).getElatitude() + "," +
                gpsTrackerModels.get(i).getElongtitude() + "&key=AIzaSyD5hjR4XomLFZmUgwWKuGe-Ay5nBPg3FHU");*/
    }

   /* @Override
    public void onViewRecycled(ViewHolder holder) {
        // Cleanup MapView here?
        if (holder.map != null) {
            holder.map.clear();
            holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }*/

    @Override
    public int getItemCount() {
        return gpsTrackerModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvType, mTvMile, mTvDuration, mTvKcal, mTvSteps;
        AppCompatCheckBox mCbDeleteItem;
        ImageView mivmap;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tvType);
            mTvMile = itemView.findViewById(R.id.tvMile);
            mTvDuration = itemView.findViewById(R.id.tvDuration);
            mTvKcal = itemView.findViewById(R.id.tvKcal);
            mTvSteps = itemView.findViewById(R.id.tvSteps);
            mCbDeleteItem = itemView.findViewById(R.id.cbDeleteItem);
            mivmap = itemView.findViewById(R.id.ivmap);

        }
    }

    public void setMyAdapterListener(GpsAdapterCallBack adapterCallback) {
        this.myAdapterListener = adapterCallback;
    }

    public void updatelist(ArrayList<GpsTrackerModel> gpsTrackerModelArrayList) {
        gpsTrackerModels = gpsTrackerModelArrayList;
    }

}  