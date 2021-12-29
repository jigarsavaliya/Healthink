package com.android.stepcounter.adpter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.model.DashboardComponentModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Collections;


public class AdjustOrderAdapter extends RecyclerView.Adapter<AdjustOrderAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    ArrayList<DashboardComponentModel> data;
    private final StartDragListener mStartDragListener;

    public AdjustOrderAdapter(ArrayList<DashboardComponentModel> stringArrayList, StartDragListener mStartDragListener) {
        data = stringArrayList;
        this.mStartDragListener = mStartDragListener;
//        Logger.e(data.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.cardview_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(listItem);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        DashboardComponentModel componentModel = data.get(i);
        viewHolder.mTitle.setText(componentModel.getComponentName());
        viewHolder.switchShowOnDB.setChecked(componentModel.getShowonDashboard());

        viewHolder.switchShowOnDB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                componentModel.setShowonDashboard(b);
            }
        });

        viewHolder.cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mStartDragListener.requestDrag(viewHolder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }

    public ArrayList<DashboardComponentModel> getList(){
        return data;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        View rowView;
        CardView cardView;
        SwitchMaterial switchShowOnDB;

        public MyViewHolder(View itemView) {
            super(itemView);
            rowView = itemView;
            cardView = itemView.findViewById(R.id.cardView);
            mTitle = itemView.findViewById(R.id.txtTitle);
            switchShowOnDB = itemView.findViewById(R.id.switchShowOnDB);

        }
    }



}