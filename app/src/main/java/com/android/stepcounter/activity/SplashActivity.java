package com.android.stepcounter.activity;

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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.stepcounter.R;
import com.android.stepcounter.database.DBHandler;
import com.android.stepcounter.model.stepcountModel;
import com.android.stepcounter.utils.StorageManager;
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

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            public void run() {
                handlePermission();
            }
        }, secondsDelayed * 3000);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private String[] permissionList() {
        return new String[]{
                Manifest.permission.ACTIVITY_RECOGNITION,
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