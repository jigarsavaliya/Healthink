package com.js.stepcounter;

import com.js.stepcounter.model.GpsTrackerModel;

import java.util.ArrayList;

public interface GpsAdapterCallBack {
    void onMethodCallback(ArrayList<GpsTrackerModel> countModelArrayList);
}
