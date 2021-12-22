package com.android.stepcounter.adpter;

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

import com.android.stepcounter.R;
import com.android.stepcounter.activity.ArchivementDetailActivity;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.utils.StorageManager;

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
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (mDailyStepDatalist.get(i).isCompeleteStatus()) {
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

                    TextView mtvArchiveLabel = d.findViewById(R.id.tvArchiveLabel);
                    TextView mtvArchiveDescription = d.findViewById(R.id.tvArchiveDescription);
                    TextView mtvDetails = d.findViewById(R.id.tvDetails);
                    ImageView mIvClosed = d.findViewById(R.id.ivClosed);
                    CardView mcvClosed = d.findViewById(R.id.cvClosed);

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

                    alertDialog.show();
                }
            });

        } else {
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
        TextView mTvDailyLabel, mTvDiscription;
        LinearLayout mLlArchivementDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvDailyLabel = itemView.findViewById(R.id.tvDailyLabel);
            mTvDiscription = itemView.findViewById(R.id.tvDiscription);
            mLlArchivementDetail = itemView.findViewById(R.id.llArchivementDetail);

        }
    }


}  