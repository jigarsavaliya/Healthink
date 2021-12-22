package com.android.stepcounter.activity;

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
import com.android.stepcounter.utils.constant;

import java.util.ArrayList;
import java.util.Calendar;

public class LevelActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRvLevelList;
    LevelDataAdapter adapter;
    ArrayList<ArchivementModel> mLevellist;
    DatabaseManager dbManager;
    ProgressBar mPbLevelBar;
    int StepGoal;
    String StepGoalLabel, CurrLavel;
    long mTotalStepData;
    TextView mTvDetailslabel, mTvDailyLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        dbManager = new DatabaseManager(this);
        mLevellist = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        getDataFromDatabase();

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

    }

    private void getDataFromDatabase() {
        mTotalStepData = dbManager.getTotalStepCount();
        mLevellist = dbManager.getArchivementlist(constant.ARCHIVEMENT_LEVEL);

        for (int i = 0; i < mLevellist.size(); i++) {
            if (mLevellist.get(i).isCompeleteStatus()) {
                CurrLavel = mLevellist.get(i).getLabel();
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
                Calendar calendar = Calendar.getInstance();
                try {
                  /*  // image naming and path  to include sd card  appending name you choose for file
                    String mPath = Environment.getExternalStorageDirectory() + "/" + calendar.getTimeInMillis() + ".jpg";

                    // create bitmap screen capture
                    View v1 = getWindow().getDecorView().getRootView();
                    v1.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                    v1.setDrawingCacheEnabled(false);

                    File imageFile = new File(mPath);

                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    Uri uri = Uri.fromFile(imageFile);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("image/*");

                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    try {
                        startActivity(Intent.createChooser(intent, "Share Screenshot"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
                    }*/

                } catch (Throwable e) {
                    // Several error may come out with file handling or DOM
                    e.printStackTrace();
                }

                break;
        }
        return true;
    }
}