package com.android.stepcounter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.stepcounter.R;

public class MoreSettingActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar mToolbar;
    LinearLayout mLlArchivement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_setting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Profile");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mLlArchivement = findViewById(R.id.llArchivement);
        mLlArchivement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llArchivement:
                startActivity(new Intent(MoreSettingActivity.this, ArchivementActivity.class));
                break;
        }
    }
}