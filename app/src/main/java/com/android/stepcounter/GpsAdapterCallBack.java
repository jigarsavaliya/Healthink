package com.android.stepcounter;

import com.android.stepcounter.model.GpsTrackerModel;

import java.util.ArrayList;

public interface GpsAdapterCallBack {
    void onMethodCallback(ArrayList<GpsTrackerModel> countModelArrayList);
}
