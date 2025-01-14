package com.js.stepcounter.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.ironsource.mediationsdk.IronSource;
import com.js.stepcounter.Application.AppController;
import com.js.stepcounter.R;
import com.js.stepcounter.utils.StorageManager;
import com.js.stepcounter.sevices.SensorService;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    public ProgressDialog dialog;
    private long _splashTime = 2000;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        int secondsDelayed = 1;

        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(900).build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        AppController.IsAdOn = remoteConfig.getBoolean("AD_ON");
//                        AppController.IsAdOn = true;
                        if (AppController.IsAdOn) {

                            IronSource.init(SplashActivity.this, "14317d8c1");
                        }
                    }
                }) ;
//                IpoApplication.isAdOn = true


        if (SensorService.IsNotiFlag) {
            startActivity(new Intent(this, GPSStartActivity.class));
        } else {
            new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                public void run() {
                    handlePermission();
                }
            }, secondsDelayed * 100);
        }
//        stepcountModel stepcountModel= new stepcountModel();
//        DatabaseManager dbManager= new DatabaseManager(this);
      /*  stepcountModel.setStep(8);
        stepcountModel.setDate(4);
        stepcountModel.setMonth(12);
        stepcountModel.setYear(2021);
        stepcountModel.setDistance("6.888946");
        stepcountModel.setCalorie("1");
        stepcountModel.setDuration(14);
        stepcountModel.setTimestemp("1638606748409");
        dbManager.addStepcountData(stepcountModel);*/


       /* WeightModel weightModel = new WeightModel();
        weightModel.setDate(22);
        weightModel.setMonth(11);
        weightModel.setYear(2021);
        weightModel.setTimestemp("1637552747000");
        weightModel.setKg(120);
        dbManager.addWeightData(weightModel);*/

//        GpsTrackerModel gpsTrackerModel = new GpsTrackerModel();
//        gpsTrackerModel.setAction("Target Distance");
//        gpsTrackerModel.setType("Walk");
//        gpsTrackerModel.setStep(10);
//        gpsTrackerModel.setDistance("0.01");
//        gpsTrackerModel.setDuration("00:00:05");
//        gpsTrackerModel.setCalories(0);
//        gpsTrackerModel.setSlatitude("");
//        gpsTrackerModel.setSlogtitude("");
//        gpsTrackerModel.setElatitude("");
//        gpsTrackerModel.setElongtitude("");
//        dbManager.addGpsData(gpsTrackerModel);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private String[] permissionList() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,

                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void handlePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        permissionList())
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.getDeniedPermissionResponses() != null
                                && report.getDeniedPermissionResponses().size() > 0) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        } else {
                            callNextScreen();
                        }
                    }

                    /**
                     * Method called whenever Android asks the application to inform the user of the need for the
                     * requested permissions. The request process won't continue until the token is properly used
                     *
                     * @param permissions The permissions that has been requested. Collections of values found in
                     *                    {@link Manifest.permission}
                     * @param token       Token used to continue or cancel the permission request process. The permission
                     */
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                int permissionCheck = PackageManager.PERMISSION_GRANTED;
                boolean shouldShowRequestPermissionRationale = false;
                String[] permissionList = permissionList();
                for (String permission : permissionList) {
                    permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
                    shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                }
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    callNextScreen();
                } else {
                    handlePermission();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void callNextScreen() {
//        StorageManager.getInstance().setHistoryDelete(false);
        boolean b = StorageManager.getInstance().getIsProfile();
        if (b) {
            Intent obj = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(obj);
            finish();
        } else {
            Intent obj = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(obj);
            finish();
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

}