package com.android.stepcounter.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.GpsAdapterCallBack;
import com.android.stepcounter.R;
import com.android.stepcounter.adpter.LocationHistoryAdapter;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.GpsTrackerModel;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.constant;

import java.util.ArrayList;

public class HistoryFragment extends Fragment implements View.OnClickListener {

    View view;
    RecyclerView mRvHistoryData;
    TextView mTvTotalDuration, mTvNoDataFound;
    LocationHistoryAdapter mlocationHistoryAdapter;
    DatabaseManager DbManger;
    ArrayList<GpsTrackerModel> gpsTrackerModelArrayList = new ArrayList<>();
    float Miles;
    ImageView mIvDelete, mIvClosed;

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
        DbManger = new DatabaseManager(getActivity());
        init();
    }

    private void init() {
        getDataFromDataBase();
        mTvTotalDuration = view.findViewById(R.id.tvTotalDuration);
        mTvNoDataFound = view.findViewById(R.id.tvNoDataFound);

        mIvDelete = view.findViewById(R.id.ivDelete);
        mIvClosed = view.findViewById(R.id.ivClosed);

        mIvDelete.setOnClickListener(this);
        mIvClosed.setOnClickListener(this);

        mTvTotalDuration.setText(Miles + "");
        mRvHistoryData = view.findViewById(R.id.rvHistoryData);

        if (gpsTrackerModelArrayList != null) {
            mTvNoDataFound.setVisibility(View.GONE);
            mlocationHistoryAdapter = new LocationHistoryAdapter(this, gpsTrackerModelArrayList);
            mRvHistoryData.setHasFixedSize(true);
            mRvHistoryData.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRvHistoryData.setAdapter(mlocationHistoryAdapter);
            mlocationHistoryAdapter.setMyAdapterListener(new GpsAdapterCallBack() {
                @Override
                public void onMethodCallback(ArrayList<GpsTrackerModel> gpsTrackerModels) {
                    gpsTrackerModelArrayList = gpsTrackerModels;
                    Logger.e(gpsTrackerModels.size());
                    if (gpsTrackerModels.size() != 0) {
                        mIvDelete.setVisibility(View.GONE);
                        mIvClosed.setVisibility(View.VISIBLE);
                    } else {
                        mIvDelete.setVisibility(View.VISIBLE);
                        mIvClosed.setVisibility(View.GONE);
                    }
                }
            });

        } else {
            mTvNoDataFound.setVisibility(View.VISIBLE);
        }


    }

    private void getDataFromDataBase() {
        gpsTrackerModelArrayList = DbManger.getGpsTrackerlist();
//        Logger.e(gpsTrackerModelArrayList.size());
        Miles = DbManger.getSumOfGpsMilesList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivDelete:
                constant.IsLocationHistoryDelete = true;
                mlocationHistoryAdapter.notifyDataSetChanged();
                mIvDelete.setVisibility(View.GONE);
                mIvClosed.setVisibility(View.VISIBLE);
                break;
            case R.id.ivClosed:
                constant.IsLocationHistoryDelete = false;
                mIvDelete.setVisibility(View.VISIBLE);
                mIvClosed.setVisibility(View.GONE);

                if (gpsTrackerModelArrayList.size() != 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Are You sure you want to delete data?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "DELETE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    for (int i = 0; i < gpsTrackerModelArrayList.size(); i++) {
//                                        dbManager.DeleteCurrentDayStepCountData();
                                    }
                                    getDataFromDataBase();
//                                    mlocationHistoryAdapter.updatelist(stringArrayListHashMap, headerMap);
//                                    mlocationHistoryAdapter.notifyDataSetChanged();
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    mlocationHistoryAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}