package com.android.stepcounter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.adpter.LocationHistoryAdapter;

public class HistoryFragment extends Fragment {

    View view;
    RecyclerView mRvHistoryData;
    LocationHistoryAdapter mTotalDistanceAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mRvHistoryData = view.findViewById(R.id.rvHistoryData);
        mTotalDistanceAdapter = new LocationHistoryAdapter(this, "");
        mRvHistoryData.setHasFixedSize(true);
        mRvHistoryData.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvHistoryData.setAdapter(mTotalDistanceAdapter);

    }

}