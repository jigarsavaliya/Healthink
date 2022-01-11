package com.android.stepcounter.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.adpter.ArchivementDetailAdapter;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.model.StepCountModel;
import com.android.stepcounter.utils.CommanMethod;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ArchivementDetailActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRvArchivementDetail;
    ArchivementDetailAdapter adapter;
    boolean IsDailyStep, IsComboDay, IsTotalDays, IsTotalDistance, IsNofification;
    ArrayList<ArchivementModel> mDailySteplist, mComboDayList, mTotalDaysList, mTotalDistanceList;
    DatabaseManager dbManager;
    TextView mTvDailyLabel, mTvDescription, mTvDetailslabel;
    ProgressBar mPbCompletedBar;
    long mTotalDaysData, mTotalDisanceData, mTotalStepData;

    int StepGoal, StepComboDayGoal, StepDayGoal, StepDistanceGoal;
    String StepGoalLabel, CurrLavel, CurrDesc,
            StepComboDayGoalLabel, CurrComboDayLavel, CurrComboDayDesc,
            StepDayGoalLabel, CurrDayLavel, CurrDayDesc,
            StepDistanceGoalLabel, CurrDistanceLavel, CurrDistanceDesc;

    File imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivement_detail);
        dbManager = new DatabaseManager(this);
        mDailySteplist = new ArrayList<>();
        mComboDayList = new ArrayList<>();
        mTotalDaysList = new ArrayList<>();
        mTotalDistanceList = new ArrayList<>();

        IsDailyStep = getIntent().getBooleanExtra("DailyStep", false);
        IsComboDay = getIntent().getBooleanExtra("ComboDay", false);
        IsTotalDays = getIntent().getBooleanExtra("TotalDays", false);
        IsTotalDistance = getIntent().getBooleanExtra("TotalDistance", false);
        IsNofification = getIntent().getBooleanExtra("IsNofification", false);

        Logger.e(IsDailyStep);
        Logger.e(IsComboDay);
        Logger.e(IsTotalDays);
        Logger.e(IsTotalDistance);
        Logger.e(IsNofification);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        getDataFromDatabase();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mTvDailyLabel = findViewById(R.id.tvDailyLabel);
        mTvDescription = findViewById(R.id.tvDescription);
        mTvDetailslabel = findViewById(R.id.tvDetailslabel);
        mPbCompletedBar = findViewById(R.id.pbCompletedBar);

        mRvArchivementDetail = findViewById(R.id.rvArchivementDetail);

        if (IsDailyStep && IsNofification) {
            CommanMethod.showCompleteDailog(this, CurrLavel, CurrDesc);
        }
        if (IsComboDay && IsNofification) {
            CommanMethod.showCompleteDailog(this, CurrComboDayLavel, CurrDayDesc);
        }
        if (IsTotalDays && IsNofification) {
            CommanMethod.showCompleteDailog(this, CurrDayLavel, CurrDayDesc);
        }
        if (IsTotalDistance && IsNofification) {
            CommanMethod.showCompleteDailog(this, CurrDistanceLavel, CurrDistanceDesc);
        }

        if (IsDailyStep) {
            mToolbar.setTitle("DAILY STEP");
            mPbCompletedBar.setMax(StepGoal);
            mPbCompletedBar.setProgress((int) mTotalStepData);
            mTvDailyLabel.setText(CurrLavel);
            mTvDescription.setText(CurrDesc);
            mTvDetailslabel.setText((StepGoal - mTotalStepData) + " more than to win the archievement " + StepGoalLabel);
            adapter = new ArchivementDetailAdapter(this, mDailySteplist);
        } else if (IsComboDay) {
            mToolbar.setTitle("COMBO DAYS");
            mPbCompletedBar.setMax(StepComboDayGoal);
            mPbCompletedBar.setProgress(StorageManager.getInstance().getComboDayCount());
            mTvDailyLabel.setText(CurrComboDayLavel);
            mTvDescription.setText(CurrComboDayDesc);
            mTvDetailslabel.setText((StepComboDayGoal - StorageManager.getInstance().getComboDayCount()) + " days left to hit target of " + StepComboDayGoalLabel + " days");
            adapter = new ArchivementDetailAdapter(this, mComboDayList);
        } else if (IsTotalDays) {
            mToolbar.setTitle("TOTAL DAYS");
            mPbCompletedBar.setMax(StepDayGoal);
            mPbCompletedBar.setProgress((int) mTotalDaysData);
            mTvDailyLabel.setText(CurrDayLavel);
            mTvDescription.setText(CurrDayDesc);
            mTvDetailslabel.setText((StepDayGoal - mTotalDaysData) + " more days,we'll be together for " + StepDayGoalLabel + " Days");
            adapter = new ArchivementDetailAdapter(this, mTotalDaysList);
        } else if (IsTotalDistance) {
            mToolbar.setTitle("TOTAL DISTANCE");
            mPbCompletedBar.setMax(StepDistanceGoal);
            mPbCompletedBar.setProgress((int) mTotalDisanceData);
            mTvDailyLabel.setText(CurrDistanceLavel);
            mTvDescription.setText(CurrDistanceDesc);
            mTvDetailslabel.setText((StepDistanceGoal - mTotalDisanceData) + " more than to finish your " + StepDistanceGoalLabel + " journey");
//            Logger.e((StepDistanceGoal - mTotalDisanceData));
//            Logger.e(StepDistanceGoal + "----" + mTotalDisanceData);
            adapter = new ArchivementDetailAdapter(this, mTotalDistanceList);
        }

        mRvArchivementDetail.setHasFixedSize(true);
        mRvArchivementDetail.setLayoutManager(new GridLayoutManager(this, 3));
        mRvArchivementDetail.setAdapter(adapter);

    }

    private void getDataFromDatabase() {

        Calendar rightNow = Calendar.getInstance();
        mTotalDaysData = dbManager.getTotalDaysCount();
        mTotalDisanceData = dbManager.getTotalDistanceCount();

        ArrayList<StepCountModel> stepCountModelArrayList = new ArrayList<>();
        stepCountModelArrayList = dbManager.getCurrentDaySumofStepcountlist(rightNow.get(Calendar.DATE), rightNow.get(Calendar.MONTH) + 1, rightNow.get(Calendar.YEAR));
        if (stepCountModelArrayList != null) {
            for (int i = 0; i < stepCountModelArrayList.size(); i++) {
                mTotalStepData = stepCountModelArrayList.get(i).getSumstep();
            }
        }
        mDailySteplist = dbManager.getArchivementlist(constant.ARCHIVEMENT_DAILY_STEP);

        mComboDayList = dbManager.getArchivementlist(constant.ARCHIVEMENT_COMBO_DAY);

        mTotalDaysList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DAYS);

        mTotalDistanceList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DISTANCE);

        for (int i = 0; i < mDailySteplist.size(); i++) {
            if (i == 0) {
                CurrLavel = mDailySteplist.get(0).getLabel();
                CurrDesc = mDailySteplist.get(0).getDescription();
                StepGoal = (int) mDailySteplist.get(0).getValue();
                StepGoalLabel = mDailySteplist.get(0).getLabel();
            }

            if (mDailySteplist.get(i).isCompeleteStatus() && mDailySteplist.size() - 1 != i) {
                CurrLavel = mDailySteplist.get(i).getLabel();
                CurrDesc = mDailySteplist.get(i).getDescription();
                StepGoal = (int) mDailySteplist.get(i + 1).getValue();
                StepGoalLabel = mDailySteplist.get(i + 1).getLabel();
            }
        }

        for (int i = 0; i < mComboDayList.size(); i++) {
            if (i == 0) {
                CurrComboDayLavel = mComboDayList.get(0).getLabel();
                CurrComboDayDesc = mComboDayList.get(0).getDescription();
                StepComboDayGoal = (int) mComboDayList.get(0).getValue();
                StepComboDayGoalLabel = mComboDayList.get(0).getLabel();
            }
            if (mComboDayList.get(i).isCompeleteStatus() && mComboDayList.size() - 1 != i) {
                CurrComboDayLavel = mComboDayList.get(i).getLabel();
                CurrComboDayDesc = mComboDayList.get(i).getDescription();
                StepComboDayGoal = (int) mComboDayList.get(i + 1).getValue();
                StepComboDayGoalLabel = mComboDayList.get(i + 1).getLabel();
            } else {
                StepComboDayGoal = (int) mComboDayList.get(0).getValue();
                StepComboDayGoalLabel = mComboDayList.get(0).getLabel();
            }
        }

        for (int i = 0; i < mTotalDaysList.size(); i++) {
            if (i == 0) {
                CurrDayLavel = mTotalDaysList.get(0).getLabel();
                CurrDayDesc = mTotalDaysList.get(0).getDescription();
                StepDayGoal = (int) mTotalDaysList.get(0).getValue();
                StepDayGoalLabel = mTotalDaysList.get(0).getLabel();
            }
            if (mTotalDaysList.get(i).isCompeleteStatus() && mTotalDaysList.size() - 1 != i) {
                CurrDayLavel = mTotalDaysList.get(i).getLabel();
                CurrDayDesc = mTotalDaysList.get(i).getDescription();
                StepDayGoal = (int) mTotalDaysList.get(i + 1).getValue();
                StepDayGoalLabel = mTotalDaysList.get(i + 1).getLabel();
            } else {
                StepDayGoal = (int) mTotalDaysList.get(0).getValue();
                StepDayGoalLabel = mTotalDaysList.get(0).getLabel();
            }
        }

        for (int i = 0; i < mTotalDistanceList.size(); i++) {
            if (i == 0) {
                CurrDistanceLavel = mTotalDistanceList.get(0).getLabel();
                CurrDistanceDesc = mTotalDistanceList.get(0).getDescription();
                StepDistanceGoal = (int) mTotalDistanceList.get(0).getValue();
                StepDistanceGoalLabel = mTotalDistanceList.get(0).getLabel();
            }

            if (mTotalDistanceList.get(i).isCompeleteStatus() && mTotalDistanceList.size() - 1 != i) {
                CurrDistanceLavel = mTotalDistanceList.get(i).getLabel();
                CurrDistanceDesc = mTotalDistanceList.get(i).getDescription();
                StepDistanceGoal = (int) mTotalDistanceList.get(i + 1).getValue();
                StepDistanceGoalLabel = mTotalDistanceList.get(i + 1).getLabel();

//                Logger.e(StepDistanceGoal);
//                Logger.e(StepDistanceGoalLabel);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                CommanMethod.TakeScreenShot(getWindow().getDecorView(),this);
                break;
        }
        return true;
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}