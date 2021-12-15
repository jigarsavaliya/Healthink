package com.android.stepcounter;

import com.android.stepcounter.model.StepCountModel;

import java.util.ArrayList;

public interface AdapterCallback {
    void onMethodCallback(ArrayList<StepCountModel> countModelArrayList);
}
