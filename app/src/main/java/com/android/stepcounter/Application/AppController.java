package com.android.stepcounter.Application;

import android.app.Application;
import android.content.Context;

import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.utils.Logger;
import com.android.stepcounter.utils.StorageManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AppController extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        AppController.context = getApplicationContext();
        StorageManager.init(this);
        DatabaseManager.init(this);

        InsertArchivementData();
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
