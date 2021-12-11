package com.android.stepcounter.adpter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.HistoryActivity;
import com.android.stepcounter.model.stepcountModel;
import com.android.stepcounter.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    HashMap<String, ArrayList<stepcountModel>> modelArrayList = new HashMap<>();
    Activity activity;
    HistoryDetailsAdapter HistoryDetailsAdapter;
    private ArrayList<String> mKeys;

    public HistoryAdapter(HistoryActivity mainActivity, HashMap<String, ArrayList<stepcountModel>> stepcountModelArrayList) {
        activity = mainActivity;
        modelArrayList = stepcountModelArrayList;
        mKeys = new ArrayList<String>(stepcountModelArrayList.keySet());
//        Log.e("TAG", "HistoryAdapter: " + stepcountModelArrayList.size());
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

        viewHolder.mtvdate.setText(mKeys.get(i) + "");

        Logger.e(modelArrayList.get(mKeys.get(i)).size());

//        if (modelArrayList.get(mKeys.get(i)).get(i).getSumstep() != 0) {
//            viewHolder.mtvstep.setText(modelArrayList.get(mKeys.get(i)).get(i).getSumstep() + "");
//        }

        HistoryDetailsAdapter = new HistoryDetailsAdapter((HistoryActivity) activity, modelArrayList.get(mKeys.get(i)));
        viewHolder.mrvdetails.setHasFixedSize(true);
        viewHolder.mrvdetails.setLayoutManager(new LinearLayoutManager(activity));
        viewHolder.mrvdetails.setAdapter(HistoryDetailsAdapter);
        viewHolder.mrvdetails.setAdapter(HistoryDetailsAdapter);
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
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

}  