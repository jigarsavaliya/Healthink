package com.js.stepcounter.adpter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.js.stepcounter.AdapterCallback;
import com.js.stepcounter.R;
import com.js.stepcounter.activity.HistoryActivity;
import com.js.stepcounter.model.StepCountModel;
import com.js.stepcounter.utils.constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class HistoryDetailsAdapter extends RecyclerView.Adapter<HistoryDetailsAdapter.ViewHolder> {

    ArrayList<StepCountModel> modelArrayList;
    ArrayList<StepCountModel> countModelArrayList = new ArrayList<>();
    Activity activity;
    private AdapterCallback myAdapterListener; // a initialize to nathi

    public HistoryDetailsAdapter(HistoryActivity mainActivity, ArrayList<StepCountModel> stepcountModelArrayList) {
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
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, modelArrayList.get(i).getMonth() - 1);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime());

        viewHolder.mtvdate.setText(modelArrayList.get(i).getDate() + " " + month_name);

        viewHolder.mtvstep.setText(modelArrayList.get(i).getSumstep() + "");
        viewHolder.mtvkcal.setText(modelArrayList.get(i).getCalorie() + " Kcal");

        int totalSecs = (int) (modelArrayList.get(i).getSumstep() * 1.66);

        if (totalSecs < 60) {
            viewHolder.mtvhours.setText("0 m " + totalSecs + "s");
        } else {
            int min = totalSecs / 60;
//                    int sec = totalSecs % 60;
            viewHolder.mtvhours.setText(min + "m 0 s");
        }

        Float f = Float.valueOf(modelArrayList.get(i).getDistance());
        String formattedNumber = String.format(Locale.US, "%.2f", f);
        viewHolder.mtvkm.setText(formattedNumber + " Km");

        if (constant.IsHistoryDelete) {
            viewHolder.mcbDeleteItem.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mcbDeleteItem.setVisibility(View.GONE);
        }

        viewHolder.mcbDeleteItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    countModelArrayList.add(modelArrayList.get(i));
//                    Logger.e(countModelArrayList.size() + "new");
                } else {
                    countModelArrayList.remove(modelArrayList.get(i));
//                    Logger.e(countModelArrayList.size() + "remove");
                }
                myAdapterListener.onMethodCallback(countModelArrayList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public void setCallback(AdapterCallback adapterCallback) {
        myAdapterListener = adapterCallback;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mtvdate, mtvstep, mtvkcal, mtvhours, mtvkm;
        AppCompatCheckBox mcbDeleteItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mtvdate = itemView.findViewById(R.id.tvdate);
            mtvstep = itemView.findViewById(R.id.tvstep);
            mtvkcal = itemView.findViewById(R.id.tvkcal);
            mtvhours = itemView.findViewById(R.id.tvhours);
            mtvkm = itemView.findViewById(R.id.tvkm);
            mcbDeleteItem = itemView.findViewById(R.id.cbDeleteItem);

        }
    }
}  