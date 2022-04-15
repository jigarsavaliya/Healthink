package com.js.stepcounter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.js.stepcounter.R;
import com.js.stepcounter.adpter.ArchivementDataAdapter;
import com.js.stepcounter.database.DatabaseManager;
import com.js.stepcounter.model.ArchivementModel;
import com.js.stepcounter.utils.Logger;
import com.js.stepcounter.utils.StorageManager;
import com.js.stepcounter.utils.constant;
import com.js.stepcounter.sevices.NotificationReceiver;

import java.util.ArrayList;
import java.util.Calendar;

public class ArchivementActivity extends BaseActivity implements View.OnClickListener {
    Toolbar mToolbar;
    DatabaseManager dbManager;
    long mTotalDaysData, mTotalDisanceData, mTotalStepData;
    RecyclerView mRvDailystep, mRvComboDays, mRvTotalDays, mRvTotalDistance;
    ArchivementDataAdapter mDailyStepArchivementAdapter, mComboDaysAdapter, mTotalDaysAdapter, mTotalDistanceAdapter;
    ArrayList<ArchivementModel> mDailySteplist, mComboDayList, mTotalDaysList, mTotalDistanceList, mLevelList;
    CardView mCvDailyStep, mCvComboDays, mCvTotalDays, mCvTotalDistance, mCvLevel;
    ProgressBar mPbLevelCompletedBar;
    TextView mTvDetailslabel, mTvDailyLabel;
    int StepGoal;
    String StepGoalLabel, CurrLavel;
    private int numSteps;
    MyReceiver myReceiver;
    int CurrentStepData;

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("GET_SIGNAL_STRENGTH")) {
                int level = intent.getIntExtra("stepdata", 0);
                numSteps = level;
                mTotalStepData = dbManager.getTotalStepCount();
                CurrentStepData = (int) mTotalStepData;
                mPbLevelCompletedBar.setProgress(CurrentStepData);
                mTvDetailslabel.setText((StepGoal - CurrentStepData) + " more than to reach " + StepGoalLabel + " level");
            }
            setRecyclerViewData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivement);
        dbManager = new DatabaseManager(this);
        mDailySteplist = new ArrayList<>();
        mComboDayList = new ArrayList<>();
        mTotalDaysList = new ArrayList<>();
        mTotalDistanceList = new ArrayList<>();
        getDataFromDatabase();

        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, new IntentFilter("GET_SIGNAL_STRENGTH"));

//        Logger.e(IsDailyStepArchivement);
//        Logger.e(IsCombodayArchivement);
//        Logger.e(IsTotalDayArchivement);
//        Logger.e(IsTotalDistanceArchivement);
//        Logger.e(IsNofificationArchivement);
        init();
    }

    private void setSharedPreferences() {
//        StepGoal = StorageManager.getInstance().getStepCountGoalUnit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSharedPreferences();
//        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Archivement");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRvDailystep = findViewById(R.id.rvDailystep);
        mRvComboDays = findViewById(R.id.rvComboDays);
        mRvTotalDays = findViewById(R.id.rvTotalDays);
        mRvTotalDistance = findViewById(R.id.rvTotalDistance);

        mCvDailyStep = findViewById(R.id.cvDailyStep);
        mCvComboDays = findViewById(R.id.cvComboDays);
        mCvTotalDays = findViewById(R.id.cvTotalDays);
        mCvTotalDistance = findViewById(R.id.cvTotalDistance);
        mCvLevel = findViewById(R.id.cvLevel);
        mPbLevelCompletedBar = findViewById(R.id.pbLevelCompletedBar);

        mTvDailyLabel = findViewById(R.id.tvDailyLabel);
        mTvDetailslabel = findViewById(R.id.tvDetailslabel);
        mTvDailyLabel.setText(CurrLavel);
        mTvDetailslabel.setText((StepGoal - mTotalStepData) + " more than to reach " + StepGoalLabel + " level");

        mPbLevelCompletedBar.setMax(StepGoal);

        mCvDailyStep.setOnClickListener(this);
        mCvComboDays.setOnClickListener(this);
        mCvTotalDays.setOnClickListener(this);
        mCvTotalDistance.setOnClickListener(this);
        mCvLevel.setOnClickListener(this);

        if (NotificationReceiver.IsDailyStepArchivement && NotificationReceiver.IsNofificationArchivement) {
            mCvDailyStep.performClick();
        }
        if (NotificationReceiver.IsCombodayArchivement && NotificationReceiver.IsNofificationArchivement) {
            mCvComboDays.performClick();
        }
        if (NotificationReceiver.IsTotalDayArchivement && NotificationReceiver.IsNofificationArchivement) {
            mCvTotalDays.performClick();
        }
        if (NotificationReceiver.IsTotalDistanceArchivement && NotificationReceiver.IsNofificationArchivement) {
            Logger.e("click1");
            mCvTotalDistance.performClick();
        }

        setRecyclerViewData();
    }

    private void setRecyclerViewData() {
        mPbLevelCompletedBar.setProgress((int) mTotalStepData);

        Calendar rightNow = Calendar.getInstance();

        int TotalStepCount = dbManager.getSumOfStepList(rightNow.get(Calendar.DATE), rightNow.get(Calendar.MONTH) + 1, rightNow.get(Calendar.YEAR));


//        Logger.e(TotalStepCount + "TotalStepCount");
//        Logger.e(numSteps + "numSteps");
//        Logger.e(mTotalDaysData + "mTotalDaysArrayList");
//        Logger.e(mTotalStepData + "mTotalStepArrayList");
//        Logger.e(mTotalDisanceData + "mTotalDisanceArrayList");

        if (numSteps == 0) {
            mDailyStepArchivementAdapter = new ArchivementDataAdapter(this, mDailySteplist, TotalStepCount);
        } else {
            mDailyStepArchivementAdapter = new ArchivementDataAdapter(this, mDailySteplist, numSteps);
        }

        mRvDailystep.setHasFixedSize(true);
        mRvDailystep.setLayoutManager(new LinearLayoutManager(ArchivementActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRvDailystep.setAdapter(mDailyStepArchivementAdapter);

        mComboDaysAdapter = new ArchivementDataAdapter(this, mComboDayList, StorageManager.getInstance().getComboDayCount());
        mRvComboDays.setHasFixedSize(true);
        mRvComboDays.setLayoutManager(new LinearLayoutManager(ArchivementActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRvComboDays.setAdapter(mComboDaysAdapter);

        mTotalDaysAdapter = new ArchivementDataAdapter(this, mTotalDaysList, (int) mTotalDaysData);
        mRvTotalDays.setHasFixedSize(true);
        mRvTotalDays.setLayoutManager(new LinearLayoutManager(ArchivementActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRvTotalDays.setAdapter(mTotalDaysAdapter);

        mTotalDistanceAdapter = new ArchivementDataAdapter(this, mTotalDistanceList, (int) mTotalDisanceData);
        mRvTotalDistance.setHasFixedSize(true);
        mRvTotalDistance.setLayoutManager(new LinearLayoutManager(ArchivementActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRvTotalDistance.setAdapter(mTotalDistanceAdapter);

    }

    private void getDataFromDatabase() {
        mTotalDaysData = dbManager.getTotalDaysCount();
        mTotalStepData = dbManager.getTotalStepCount();
        mTotalDisanceData = dbManager.getTotalDistanceCount();

        mLevelList = dbManager.getArchivementlist(constant.ARCHIVEMENT_LEVEL);
        mDailySteplist = dbManager.getArchivementlist(constant.ARCHIVEMENT_DAILY_STEP);
        mComboDayList = dbManager.getArchivementlist(constant.ARCHIVEMENT_COMBO_DAY);
        mTotalDaysList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DAYS);
        mTotalDistanceList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DISTANCE);

        for (int i = 0; i < mLevelList.size(); i++) {
            if (mLevelList.get(i).isCompeleteStatus() && mLevelList.size() - 1 != i) {
                CurrLavel = mLevelList.get(i).getLabel();
                StepGoal = (int) mLevelList.get(i + 1).getValue();
                StepGoalLabel = mLevelList.get(i + 1).getLabel();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(ArchivementActivity.this, ArchivementDetailActivity.class);
        switch (v.getId()) {
            case R.id.cvDailyStep:
                intent.putExtra("DailyStep", true);
                startActivity(intent);
                break;
            case R.id.cvComboDays:
                intent.putExtra("ComboDay", true);
                startActivity(intent);
                break;
            case R.id.cvTotalDays:
                intent.putExtra("TotalDays", true);
                startActivity(intent);
                break;
            case R.id.cvTotalDistance:
                intent.putExtra("TotalDistance", true);
                startActivity(intent);
                break;
            case R.id.cvLevel:
                Intent i = new Intent(ArchivementActivity.this, LevelActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}