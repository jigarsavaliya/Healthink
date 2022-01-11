package com.android.stepcounter.Application;

import android.app.Application;
import android.content.Context;

import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.model.DashboardComponentModel;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;
import com.android.stepcounter.utils.constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class AppController extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        AppController.context = getApplicationContext();
        StorageManager.init(this);
        DatabaseManager.init(this);

        InsertArchivementData();

        DashboardComponent();
    }

    private void DashboardComponent() {
        ArrayList<DashboardComponentModel> dashboardComponentModels = new ArrayList<>();

        DashboardComponentModel componentModel1 = new DashboardComponentModel();
        componentModel1.setComponentOrder(constant.DASHBORAD_GPS_TRACKER);
        componentModel1.setComponentName("GPS Traker");
        componentModel1.setShowonDashboard(true);
        dashboardComponentModels.add(componentModel1);

        DashboardComponentModel componentModel2 = new DashboardComponentModel();
        componentModel2.setComponentOrder(constant.DASHBORAD_WATER_TRACKER);
        componentModel2.setComponentName("Water Tracker");
        componentModel2.setShowonDashboard(true);
        dashboardComponentModels.add(componentModel2);

        DashboardComponentModel componentModel3 = new DashboardComponentModel();
        componentModel3.setComponentOrder(constant.DASHBORAD_WEIGHT_TRACKER);
        componentModel3.setComponentName("Weight Tracker");
        componentModel3.setShowonDashboard(true);
        dashboardComponentModels.add(componentModel3);

        StorageManager.getInstance().setDashboardComponent(new Gson().toJson(dashboardComponentModels));

    }

    private void InsertArchivementData() {
        DatabaseManager dbManager = new DatabaseManager(getApplicationContext());
        if (StorageManager.getInstance().getFirstTimeInstall()) {
            StorageManager.getInstance().setFirstTimeInstall(false);
            Gson gson = new Gson();
            try {
                InputStream stream = getApplicationContext().getAssets().open("Archivement.txt");

                int size = stream.available();
                byte[] buffer = new byte[size];
                stream.read(buffer);
                stream.close();
                String tContents = new String(buffer);
                Type arrayListTypeToken = new TypeToken<ArrayList<ArchivementModel>>() {
                }.getType();

                ArrayList<ArchivementModel> archivementModels = gson.fromJson(tContents, arrayListTypeToken);

                Logger.e(archivementModels.size() + "databse");

                dbManager.addArchivementData(archivementModels);
//
            } catch (IOException e) {
                // Handle exceptions here
            }

        }
    }


    public static Context getAppContext() {
        return AppController.context;
    }
}
