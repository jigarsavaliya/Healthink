package com.android.stepcounter.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.io.FileNotFoundException;
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

    int StepGoal = 3000, StepComboDayGoal, StepDayGoal, StepDistanceGoal = 5;
    String StepGoalLabel = "3k", CurrLavel = "3k", CurrDesc = "Away from",
            StepComboDayGoalLabel, CurrComboDayLavel = "1x", CurrComboDayDesc = "1 Day",
            StepDayGoalLabel, CurrDayLavel = "7", CurrDayDesc = "7 day",
            StepDistanceGoalLabel = "5", CurrDistanceLavel = "5", CurrDistanceDesc = "Short Hike";

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
            Logger.e((StepDistanceGoal - mTotalDisanceData));
            Logger.e(StepDistanceGoal + "----" + mTotalDisanceData);
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
            if (mDailySteplist.get(i).isCompeleteStatus()) {
                CurrLavel = mDailySteplist.get(i).getLabel();
                CurrDesc = mDailySteplist.get(i).getDescription();
                StepGoal = (int) mDailySteplist.get(i + 1).getValue();
                StepGoalLabel = mDailySteplist.get(i + 1).getLabel();
            }
        }

        for (int i = 0; i < mComboDayList.size(); i++) {
            if (mComboDayList.get(i).isCompeleteStatus()) {
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
            if (mTotalDaysList.get(i).isCompeleteStatus()) {
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
            if (mTotalDistanceList.get(i).isCompeleteStatus()) {
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
                Calendar calendar = Calendar.getInstance();
                try {
                    // image naming and path  to include sd card  appending name you choose for file
//                    String mPath = Environment.getExternalStorageDirectory() + "/" + calendar.getTimeInMillis() + ".jpg";
//
//                    // create bitmap screen capture
//                    View v1 = getWindow().getDecorView().getRootView();
//                    v1.setDrawingCacheEnabled(true);
//                    Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//                    v1.setDrawingCacheEnabled(false);
//
//                    File imageFile = new File(mPath);
//
//                    FileOutputStream outputStream = new FileOutputStream(imageFile);
//                    int quality = 100;
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//                    outputStream.flush();
//                    outputStream.close();
//
////                    File imageFile = screenshot(getWindow().getDecorView().getRootView(), String.valueOf(calendar.getTimeInMillis()));
//
//
//                    Uri uri = Uri.fromFile(imageFile);
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.setType("image/*");
//
//                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
//                    intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
//                    intent.putExtra(Intent.EXTRA_STREAM, uri);
//                    try {
//                        startActivity(Intent.createChooser(intent, "Share Screenshot"));
//                    } catch (ActivityNotFoundException e) {
//                        Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
//                    }

                    Bitmap bitmap = takeScreenshot();
                    saveBitmap(bitmap);
                    shareIt();

                } catch (Throwable e) {
                    // Several error may come out with file handling or DOM
                    e.printStackTrace();
                    Logger.e(e.getMessage());
                }

                break;
        }
        return true;
    }

    public Bitmap takeScreenshot() {
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png"); ////File imagePath
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }


    }

    private void shareIt() {
        Uri myUri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String shareBody = "My highest score with screen shot";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Catch score");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, myUri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    protected static File screenshot(View view, String filename) {
        Date date = new Date();

        // Here we are initialising the format of our image name
        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
        try {
            // Initialising the directory of storage
            String dirpath = Environment.getExternalStorageDirectory() + "";
            File file = new File(dirpath);
            if (!file.exists()) {
                boolean mkdir = file.mkdir();
            }

            // File name
            String path = dirpath + "/" + filename + ".jpeg";
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            File imageurl = new File(path);
            FileOutputStream outputStream = new FileOutputStream(imageurl);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
            return imageurl;

        } catch (FileNotFoundException io) {
            io.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}