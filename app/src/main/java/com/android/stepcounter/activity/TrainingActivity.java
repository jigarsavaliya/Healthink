package com.android.stepcounter.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.stepcounter.R;
import com.android.stepcounter.utils.constant;
import com.android.stepcounter.viewpageradpter.MainViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class TrainingActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    MainViewPagerAdapter viewPagerAdapter;
    public static boolean isGPSFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGPSFinish) {
            isGPSFinish = false;
            constant.IsLocationHistoryDelete = false;
            viewPager.setCurrentItem(2);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(this, MainActivity.class));
    }
}