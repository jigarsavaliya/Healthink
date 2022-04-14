package com.js.stepcounter.fragment;

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

import com.js.stepcounter.GpsAdapterCallBack;
import com.js.stepcounter.R;
import com.js.stepcounter.adpter.EventRVAdapter;
import com.js.stepcounter.database.DatabaseManager;
import com.js.stepcounter.model.EventItem;
import com.js.stepcounter.model.GpsTrackerModel;
import com.js.stepcounter.model.HeaderModel;
import com.js.stepcounter.model.ListEvent;
import com.js.stepcounter.utils.Logger;
import com.js.stepcounter.utils.constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class HistoryFragment extends Fragment implements View.OnClickListener {

    View view;
    RecyclerView mRvHistoryData;
    TextView mTvTotalDuration, mTvNoDataFound;
    //    LocationHistoryAdapter mlocationHistoryAdapter;
    EventRVAdapter mEventRVAdapter;
    DatabaseManager DbManger;
    ArrayList<GpsTrackerModel> gpsTrackerModelArrayList = new ArrayList<>();
    ArrayList<GpsTrackerModel> gpsTrackerModelList = new ArrayList<>();
    float Miles;
    ImageView mIvDelete, mIvClosed;
    HashMap<String, ArrayList<GpsTrackerModel>> GpsTrackerModelHashMap;
    List<ListEvent> eventHashMapArrayList = new ArrayList<>();
    Calendar rightNow;

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
        rightNow = Calendar.getInstance();
        getDataFromDataBase();

        mTvTotalDuration = view.findViewById(R.id.tvTotalDuration);
        mTvNoDataFound = view.findViewById(R.id.tvNoDataFound);

        mIvDelete = view.findViewById(R.id.ivDelete);
        mIvClosed = view.findViewById(R.id.ivClosed);

        mIvDelete.setOnClickListener(this);
        mIvClosed.setOnClickListener(this);

        mTvTotalDuration.setText(Miles + "");
        mRvHistoryData = view.findViewById(R.id.rvHistoryData);

        setRecyclerview();

    }

    private void setRecyclerview() {
        if (gpsTrackerModelArrayList != null) {
            mTvNoDataFound.setVisibility(View.GONE);

//            locationAdapter = new HistoryLocationAdapter(this, GpsTrackerModelHashMap);

            mEventRVAdapter = new EventRVAdapter(this, eventHashMapArrayList);

//            mlocationHistoryAdapter = new LocationHistoryAdapter(this, gpsTrackerModelArrayList);

            mRvHistoryData.setHasFixedSize(true);
            mRvHistoryData.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRvHistoryData.setAdapter(mEventRVAdapter);

            mEventRVAdapter.setMyAdapterListener(new GpsAdapterCallBack() {
                @Override
                public void onMethodCallback(ArrayList<GpsTrackerModel> gpsTrackerModels) {
                    gpsTrackerModelList = gpsTrackerModels;
                    Logger.e("count" + gpsTrackerModels.size());
                    if (gpsTrackerModels.size() != 0) {
                        mIvDelete.setVisibility(View.GONE);
                        mIvClosed.setVisibility(View.VISIBLE);
                    } else {
                        constant.IsLocationHistoryDelete = false;
                        mIvDelete.setVisibility(View.VISIBLE);
                        mIvClosed.setVisibility(View.GONE);
                        mEventRVAdapter.notifyDataSetChanged();
//                        mlocationHistoryAdapter.notifyDataSetChanged();
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

        if (gpsTrackerModelArrayList != null) {
            GpsTrackerModelHashMap = toMap(gpsTrackerModelArrayList);
            for (String date : GpsTrackerModelHashMap.keySet()) {
                HeaderModel header = new HeaderModel();
                header.setDate(date);
                eventHashMapArrayList.add(header);

                for (GpsTrackerModel eventModel : GpsTrackerModelHashMap.get(date)) {
                    EventItem item = new EventItem(eventModel);
                    Logger.e(item.getEventModel().getAction());
                    eventHashMapArrayList.add(item);
                }

            }
            Logger.e(eventHashMapArrayList.size());
        }

        for (String s : GpsTrackerModelHashMap.keySet()) {
            Logger.e(s + "Key Value " + GpsTrackerModelHashMap.get(s).size());
        }

    }

    private HashMap<String, ArrayList<GpsTrackerModel>> toMap(ArrayList<GpsTrackerModel> events) {
        Logger.e(events.size());

        HashMap<String, ArrayList<GpsTrackerModel>> map = new HashMap<>();
        for (GpsTrackerModel eventModel : events) {

            Logger.e("Date  " + eventModel.getDate());

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, eventModel.getMonth() - 1);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
            //simpleDateFormat.setCalendar(calendar);
            String monthName = simpleDateFormat.format(calendar.getTime());

            ArrayList<GpsTrackerModel> value = map.get(eventModel.getDate() + " " + monthName);
            if (value == null) {
                value = new ArrayList<>();
                map.put(eventModel.getDate() + " " + monthName, value);
            }
//            Logger.e("Action  " + eventModel.getAction());
            value.add(eventModel);
        }
        Logger.e("Size  " + map.size());
        return map;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivDelete:
                Logger.e("count" + gpsTrackerModelList.size());
//                if (gpsTrackerModelList != null) {
                constant.IsLocationHistoryDelete = true;
                mEventRVAdapter.notifyDataSetChanged();
//                    mlocationHistoryAdapter.notifyDataSetChanged();
                mIvDelete.setVisibility(View.GONE);
                mIvClosed.setVisibility(View.VISIBLE);
//                }
                break;
            case R.id.ivClosed:
                if (gpsTrackerModelList != null) {
                    constant.IsLocationHistoryDelete = false;
                    Logger.e("count" + gpsTrackerModelList.size());
                    if (gpsTrackerModelList.size() != 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setMessage("Are You sure you want to delete data?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "DELETE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        for (int i = 0; i < gpsTrackerModelList.size(); i++) {
                                           /* DbManger.DeleteGpsTrakerData(gpsTrackerModelList.get(i).getAction(), gpsTrackerModelList.get(i).getDistance(),
                                                    gpsTrackerModelList.get(i).getCalories(), gpsTrackerModelList.get(i).getDuration(),
                                                    gpsTrackerModelList.get(i).getStep(),gpsTrackerModelList.get(i).getSlatitude(),
                                                    gpsTrackerModelList.get(i).getSlogtitude(),gpsTrackerModelList.get(i).getElatitude(),
                                                    gpsTrackerModelList.get(i).getElongtitude());*/
                                            DbManger.DeleteGpsTrakerData(gpsTrackerModelList.get(i).getId());
                                        }
                                        gpsTrackerModelList.clear();
                                        eventHashMapArrayList.clear();
                                        getDataFromDataBase();
                                        if (eventHashMapArrayList.size() > 0) {
                                            mTvNoDataFound.setVisibility(View.GONE);
                                            mEventRVAdapter.updatelist(eventHashMapArrayList);
                                            mEventRVAdapter.notifyDataSetChanged();
                                        } else {
                                            mEventRVAdapter.updatelist(eventHashMapArrayList);
                                            mEventRVAdapter.notifyDataSetChanged();
                                            mTvNoDataFound.setVisibility(View.VISIBLE);
                                        }
                                        mTvTotalDuration.setText(Miles + "");
                                        mIvDelete.setVisibility(View.VISIBLE);
                                        mIvClosed.setVisibility(View.GONE);
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
                        mIvDelete.setVisibility(View.VISIBLE);
                        mIvClosed.setVisibility(View.GONE);
                        mEventRVAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }


}