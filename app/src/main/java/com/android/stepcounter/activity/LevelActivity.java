package com.android.stepcounter.activity;

import static com.android.stepcounter.sevices.NotificationReceiver.IsNofificationArchivement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.adpter.LevelDataAdapter;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.utils.CommanMethod;
import com.android.stepcounter.utils.constant;

import java.util.ArrayList;

public class LevelActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRvLevelList;
    LevelDataAdapter adapter;
    ArrayList<ArchivementModel> mLevellist;
    DatabaseManager dbManager;
    ProgressBar mPbLevelBar;
    int StepGoal = 10000;
    String StepGoalLabel = "10000", CurrLavel = "Level 1", CurrDescription = "A good Start!";
    long mTotalStepData;
    TextView mTvDetailslabel, mTvDailyLabel;

    MyReceiver myReceiver;

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("GET_SIGNAL_STRENGTH")) {
                mTotalStepData = dbManager.getTotalStepCount();
                mTotalStepData = (int) mTotalStepData;
                mPbLevelBar.setMax(StepGoal);
                mPbLevelBar.setProgress((int) mTotalStepData);
                mTvDetailslabel.setText((StepGoal - mTotalStepData) + " more than to reach " + StepGoalLabel + " level");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        dbManager = new DatabaseManager(this);
        mLevellist = new ArrayList<>();

        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, new IntentFilter("GET_SIGNAL_STRENGTH"));

        getDataFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("LEVEL");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mTvDailyLabel = findViewById(R.id.tvDailyLabel);
        mTvDetailslabel = findViewById(R.id.tvDetailslabel);
        mTvDailyLabel.setText(CurrLavel);
        mTvDetailslabel.setText((StepGoal - mTotalStepData) + " more than to reach " + StepGoalLabel + " level");

        mPbLevelBar = findViewById(R.id.pbLevelBar);
        mPbLevelBar.setMax(StepGoal);

        mPbLevelBar.setProgress((int) mTotalStepData);
        mRvLevelList = findViewById(R.id.rvLevel);
        adapter = new LevelDataAdapter(this, mLevellist);
        mRvLevelList.setHasFixedSize(true);
        mRvLevelList.setLayoutManager(new LinearLayoutManager(this));
        mRvLevelList.setAdapter(adapter);

        if (IsNofificationArchivement) {
            CommanMethod.showCompleteDailog(this, CurrLavel, CurrDescription);
        }
    }

    private void getDataFromDatabase() {
        mTotalStepData = dbManager.getTotalStepCount();
        mLevellist = dbManager.getArchivementlist(constant.ARCHIVEMENT_LEVEL);

        for (int i = 0; i < mLevellist.size(); i++) {
            if (mLevellist.get(i).isCompeleteStatus() && mLevellist.size() - 1 != i) {
                CurrLavel = mLevellist.get(i).getLabel();
                CurrDescription = mLevellist.get(i).getDescription();
                StepGoal = (int) mLevellist.get(i + 1).getValue();
                StepGoalLabel = mLevellist.get(i + 1).getLabel();
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
                CommanMethod.TakeScreenShot(getWindow().getDecorView(), this);
                break;
        }
        return true;
    }
}