package com.android.stepcounter.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.adpter.AdjustOrderAdapter;
import com.android.stepcounter.adpter.ItemMoveCallback;
import com.android.stepcounter.adpter.StartDragListener;
import com.android.stepcounter.model.DashboardComponentModel;
import com.android.stepcounter.utils.StorageManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AdjustOrderActivity extends AppCompatActivity implements StartDragListener {
    Toolbar mToolbar;
    RecyclerView mRvAdjustOrder;
    ArrayList<DashboardComponentModel> dashboardComponentModels = new ArrayList<>();
    AdjustOrderAdapter mAdapter;
    ItemTouchHelper touchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_order);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Adjust Order");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRvAdjustOrder = findViewById(R.id.rvAdjustOrder);

        String json = StorageManager.getInstance().getDashboardComponent();
        Type arrayListTypeToken = new TypeToken<ArrayList<DashboardComponentModel>>() {
        }.getType();
        dashboardComponentModels = new Gson().fromJson(json, arrayListTypeToken);

        mAdapter = new AdjustOrderAdapter(dashboardComponentModels, this);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(mAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRvAdjustOrder);

        mRvAdjustOrder.setLayoutManager(new LinearLayoutManager(this));
        mRvAdjustOrder.setAdapter(mAdapter);

    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    @Override
    public void onBackPressed() {
        StorageManager.getInstance().setDashboardComponent(new Gson().toJson(mAdapter.getList()));
        super.onBackPressed();
    }
}