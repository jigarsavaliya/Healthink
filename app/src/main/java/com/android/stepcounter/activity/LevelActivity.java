package com.android.stepcounter.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.adpter.LevelDataAdapter;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.utils.CommanMethod;
import com.android.stepcounter.utils.constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

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
    boolean IsNofification;

    public File imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        dbManager = new DatabaseManager(this);
        mLevellist = new ArrayList<>();
        IsNofification = getIntent().getBooleanExtra("IsNofification", false);
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

        if (IsNofification) {
            CommanMethod.showCompleteDailog(this, CurrLavel, CurrDescription);
        }
    }

    private void getDataFromDatabase() {
        mTotalStepData = dbManager.getTotalStepCount();
        mLevellist = dbManager.getArchivementlist(constant.ARCHIVEMENT_LEVEL);

        for (int i = 0; i < mLevellist.size(); i++) {
            if (mLevellist.get(i).isCompeleteStatus()) {
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
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap);
                shareIt();
                break;
        }
        return true;
    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        Calendar calendar = Calendar.getInstance();

//        File folder = new File(Environment.getExternalStorageDirectory() + "/StepCounter");
//        if (!folder.exists()) {
//            folder.mkdir();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            imagePath = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + calendar.getTimeInMillis() + ".jpeg");
        } else {
            imagePath = new File(Environment.getExternalStorageDirectory().toString() + "/" + calendar.getTimeInMillis() + ".jpeg");
        }

//        imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/StepCounter/" + calendar.getTimeInMillis() + ".jpg";
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void shareIt() {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "In Tweecher, My highest score with screen shot";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Tweecher score");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}