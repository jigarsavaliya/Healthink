package com.android.stepcounter.Application;

import android.app.Application;

import com.android.stepcounter.utils.StorageManager;

public class StepCounterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StorageManager.init(this);
    }
}
