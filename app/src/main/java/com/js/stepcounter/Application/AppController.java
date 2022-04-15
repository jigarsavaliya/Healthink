package com.js.stepcounter.Application;

import android.app.Application;
import android.content.Context;

import com.js.stepcounter.database.DatabaseManager;
import com.js.stepcounter.model.ArchivementModel;
import com.js.stepcounter.model.DashboardComponentModel;
import com.js.stepcounter.utils.Logger;
import com.js.stepcounter.utils.StorageManager;
import com.js.stepcounter.utils.constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AppController extends Application {
    private static Context context;
     public static boolean IsAdOn = false;

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

        DashboardComponentModel componentModel4 = new DashboardComponentModel();
        componentModel4.setComponentOrder(constant.DASHBORAD_LEVEL_TRACKER);
        componentModel4.setComponentName("Level Tracker");
        componentModel4.setShowonDashboard(true);
        dashboardComponentModels.add(componentModel4);

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
                Type arrayListTypeToken = new TypeToken<ArrayList<ArchivementModel>>() {}.getType();

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
