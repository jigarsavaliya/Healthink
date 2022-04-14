package com.js.stepcounter;

import com.js.stepcounter.model.StepCountModel;

import java.util.ArrayList;

public interface AdapterCallback {
    void onMethodCallback(ArrayList<StepCountModel> countModelArrayList);
}
