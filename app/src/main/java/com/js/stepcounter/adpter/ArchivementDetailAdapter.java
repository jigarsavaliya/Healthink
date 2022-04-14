package com.js.stepcounter.adpter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.js.stepcounter.R;
import com.js.stepcounter.activity.ArchivementDetailActivity;
import com.js.stepcounter.model.ArchivementModel;
import com.js.stepcounter.utils.CommanMethod;
import com.js.stepcounter.utils.StorageManager;

import java.util.ArrayList;


public class ArchivementDetailAdapter extends RecyclerView.Adapter<ArchivementDetailAdapter.ViewHolder> {

    ArrayList<ArchivementModel> mDailyStepDatalist;
    Activity activity;
    int StepGoal;

    public ArchivementDetailAdapter(ArchivementDetailActivity mainActivity, ArrayList<ArchivementModel> archivementModels) {
        activity = mainActivity;
        mDailyStepDatalist = archivementModels;
        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_archivement_detail, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        if (mDailyStepDatalist.get(i).isCompeleteStatus()) {
            if (mDailyStepDatalist.get(i).getType().equals("Daily Step")) {
                viewHolder.mLlLevalCount.setVisibility(View.VISIBLE);
                viewHolder.mTvlevelcount.setText(mDailyStepDatalist.get(i).getCount() + "x");
            } else {
                viewHolder.mLlLevalCount.setVisibility(View.GONE);
                viewHolder.mLlLevalCount.setVisibility(View.GONE);
            }

            viewHolder.mTvDailyLabel.setText(mDailyStepDatalist.get(i).getLabel());
            viewHolder.mTvDiscription.setText(mDailyStepDatalist.get(i).getDescription());
            viewHolder.mTvDailyLabel.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            viewHolder.mTvDiscription.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));

            viewHolder.mLlArchivementDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.full_screen_dialog);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View d = inflater.inflate(R.layout.dailog_archivement, null);
                    dialogBuilder.setView(d);
                    AlertDialog alertDialog = dialogBuilder.create();

                    LinearLayout mllArchivement = d.findViewById(R.id.llArchivement);
                    TextView mtvArchiveLabel = d.findViewById(R.id.tvArchiveLabel);
                    TextView mtvArchiveDescription = d.findViewById(R.id.tvArchiveDescription);
                    TextView mtvDetails = d.findViewById(R.id.tvDetails);
                    ImageView mIvClosed = d.findViewById(R.id.ivClosed);
                    CardView mcvClosed = d.findViewById(R.id.cvClosed);
                    CardView mcvShare = d.findViewById(R.id.cvShare);

                    mtvArchiveLabel.setText(mDailyStepDatalist.get(i).getLabel());
                    mtvArchiveDescription.setText(mDailyStepDatalist.get(i).getDescription());
                    mtvDetails.setText("Great! You've Walked " + mDailyStepDatalist.get(i).getLabel() + " steps today!");

                    mcvClosed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    mIvClosed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    mcvShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CommanMethod.TakeScreenShot(mllArchivement, activity);
                        }
                    });
                    alertDialog.show();
                }
            });

        } else {
            viewHolder.mLlLevalCount.setVisibility(View.GONE);
            viewHolder.mTvDailyLabel.setText(mDailyStepDatalist.get(i).getLabel());
            viewHolder.mTvDiscription.setText(mDailyStepDatalist.get(i).getDescription());
            viewHolder.mTvDailyLabel.setTextColor(activity.getResources().getColor(R.color.black));
            viewHolder.mTvDiscription.setTextColor(activity.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return mDailyStepDatalist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvDailyLabel, mTvDiscription, mTvlevelcount;
        LinearLayout mLlArchivementDetail, mLlLevalCount;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvDailyLabel = itemView.findViewById(R.id.tvDailyLabel);
            mTvDiscription = itemView.findViewById(R.id.tvDiscription);
            mTvlevelcount = itemView.findViewById(R.id.tvlevelcount);
            mLlArchivementDetail = itemView.findViewById(R.id.llArchivementDetail);
            mLlLevalCount = itemView.findViewById(R.id.llLevalCount);
        }
    }


}  