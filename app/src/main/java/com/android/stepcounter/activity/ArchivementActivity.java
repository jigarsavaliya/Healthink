package com.android.stepcounter.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.adpter.ComboDaysAdapter;
import com.android.stepcounter.adpter.DailyStepAdapter;
import com.android.stepcounter.adpter.TotalDaysAdapter;
import com.android.stepcounter.adpter.TotalDistanceAdapter;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.utils.constant;

import java.util.ArrayList;

public class ArchivementActivity extends AppCompatActivity {
    Toolbar mToolbar;
    DBHandler dbManager;
    long mTotalDaysData, mTotalDisanceData, mTotalStepData;
    RecyclerView mRvDailystep, mRvComboDays, mRvTotalDays, mRvTotalDistance;
    DailyStepAdapter mDailyStepArchivementAdapter;
    ComboDaysAdapter mComboDaysAdapter;
    TotalDaysAdapter mTotalDaysAdapter;
    TotalDistanceAdapter mTotalDistanceAdapter;
    ArrayList<ArchivementModel> mDailySteplist, mComboDayList, mTotalDaysList, mTotalDistanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivement);
        dbManager = new DBHandler(this);
        mDailySteplist = new ArrayList<>();
        mComboDayList = new ArrayList<>();
        mTotalDaysList = new ArrayList<>();
        mTotalDistanceList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
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

        getDataFromDatabase();

        mRvDailystep = findViewById(R.id.rvDailystep);
        mRvComboDays = findViewById(R.id.rvComboDays);
        mRvTotalDays = findViewById(R.id.rvTotalDays);
        mRvTotalDistance = findViewById(R.id.rvTotalDistance);

        setRecyclerViewData();
    }

    private void setRecyclerViewData() {

        mDailyStepArchivementAdapter = new DailyStepAdapter(this, mDailySteplist);
        mRvDailystep.setHasFixedSize(true);
        mRvDailystep.setLayoutManager(new LinearLayoutManager(ArchivementActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRvDailystep.setAdapter(mDailyStepArchivementAdapter);

        mComboDaysAdapter = new ComboDaysAdapter(this, mComboDayList);
        mRvComboDays.setHasFixedSize(true);
        mRvComboDays.setLayoutManager(new LinearLayoutManager(ArchivementActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRvComboDays.setAdapter(mComboDaysAdapter);

        mTotalDaysAdapter = new TotalDaysAdapter(this, mTotalDaysList);
        mRvTotalDays.setHasFixedSize(true);
        mRvTotalDays.setLayoutManager(new LinearLayoutManager(ArchivementActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRvTotalDays.setAdapter(mTotalDaysAdapter);

        mTotalDistanceAdapter = new TotalDistanceAdapter(this, mTotalDistanceList);
        mRvTotalDistance.setHasFixedSize(true);
        mRvTotalDistance.setLayoutManager(new LinearLayoutManager(ArchivementActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRvTotalDistance.setAdapter(mTotalDistanceAdapter);

    }

    private void getDataFromDatabase() {
        mTotalDaysData = dbManager.getTotalDaysCount();
        mTotalStepData = dbManager.getTotalStepCount();
        mTotalDisanceData = dbManager.getTotalDistanceCount();

//        Logger.e(mTotalDaysData + "mTotalDaysArrayList");
//        Logger.e(mTotalStepData + "mTotalStepArrayList");
//        Logger.e(mTotalDisanceData + "mTotalDisanceArrayList");

        mDailySteplist = dbManager.getArchivementlist(constant.ARCHIVEMENT_DAILY_STEP);
        mComboDayList = dbManager.getArchivementlist(constant.ARCHIVEMENT_COMBO_DAY);
        mTotalDaysList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DAYS);
        mTotalDistanceList = dbManager.getArchivementlist(constant.ARCHIVEMENT_TOTAL_DISTANCE);
    }
}