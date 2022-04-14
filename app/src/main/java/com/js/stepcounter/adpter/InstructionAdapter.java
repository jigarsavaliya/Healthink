package com.js.stepcounter.adpter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.js.stepcounter.R;
import com.js.stepcounter.activity.InstructionActivity;
import com.js.stepcounter.model.InstructionModel;

import java.util.ArrayList;


public class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.ViewHolder> {

    ArrayList<InstructionModel> instructionModels;
    Activity activity;

    public InstructionAdapter(InstructionActivity mainActivity, ArrayList<InstructionModel> instructionModelArrayList) {
        activity = mainActivity;
        instructionModels = instructionModelArrayList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_instruction, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        String uri = "drawable/" + instructionModels.get(i).getIcon();

        int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
        Drawable image = activity.getResources().getDrawable(imageResource);
        viewHolder.mIvImage.setImageDrawable(image);

        viewHolder.mTvTital.setText(instructionModels.get(i).getTital());

        viewHolder.llVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mTvcollspan.setVisibility(View.VISIBLE);
                viewHolder.mTvexpand.setVisibility(View.GONE);
                viewHolder.mTvDecription.setVisibility(View.VISIBLE);
                viewHolder.mTvDecription.setText(instructionModels.get(i).getDescription());

            }
        });

        viewHolder.mTvcollspan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mTvexpand.setVisibility(View.VISIBLE);
                viewHolder.mTvcollspan.setVisibility(View.GONE);
                viewHolder.mTvDecription.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return instructionModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTital, mTvexpand, mTvcollspan, mTvDecription;
        ImageView mIvImage;
        LinearLayout llVIew;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvImage = itemView.findViewById(R.id.iv_image);
            mTvTital = itemView.findViewById(R.id.tvTital);
            mTvexpand = (TextView) itemView.findViewById(R.id.tvexpand);
            mTvcollspan = (TextView) itemView.findViewById(R.id.tvcollspan);
            mTvDecription = (TextView) itemView.findViewById(R.id.tvDecription);
            llVIew = (LinearLayout) itemView.findViewById(R.id.llVIew);

        }
    }


}  