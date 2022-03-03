package com.android.stepcounter.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.android.stepcounter.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

public class CommanMethod {

    public static float calculateDistance(int num_steps) {
        double distance = num_steps * 0.0005;
        return (float) distance;
    }


    public static float calculateMileToKM(float value) {
        double Mile = value / 0.62137;
        return (float) Mile;
    }

    public static float calculateKmToMile(float value) {
        double Mile = value * 0.62137;
        return (float) Mile;
    }

    public float getDistanceRun(long steps) {
        float distance = (float) (steps * 78) / (float) 100000;
        return distance;
    }

    public static int calculateCalories(int stepCounts, float m, float h) {
        int a = 5;//m/s2
        float height = h / 100;
        return (int) (stepCounts * ((0.035 * m) + ((a / height) * (0.029 * m))) / 150);
    }

    /**
     * @param value double that is formatted
     * @return double that has 1 decimal place
     */
    private static double format(double value) {
        if (value != 0) {
            DecimalFormat df = new DecimalFormat("###.#");
            return Double.valueOf(df.format(value));
        } else {
            return -1;
        }
    }

    /**
     * @param lb - pounds
     * @return kg rounded to 1 decimal place
     */
    public static double lbToKgConverter(double lb) {
        return format(lb * 0.45359237);
    }

    /**
     * @param kg - kilograms
     * @return lb rounded to 1 decimal place
     */
    public static double kgToLbConverter(double kg) {
        return format(kg * 2.20462262);
    }

    /**
     * @param cm - centimeters
     * @return feet rounded to 1 decimal place
     */
    public static double cmToFeetConverter(double cm) {
//        return format(cm * 0.394);
        return Math.floor((cm / 2.54) / 12);
    }

    public static double cmToInchConverter(double cm, double feet) {
//        return format(cm * 0.394);
        return Math.floor((cm / 2.54) - (feet * 12));
    }

    /**
     * @param feet - feet
     * @return centimeters rounded to 1 decimal place
     */
    public static double feetToCmConverter(double feet, double In) {
//        return format(feet * 30.48);
        return (((feet * 12.0) + In) * 2.54);
    }


    /**
     * @param height in cm
     * @param weight in kilograms
     * @return BMI index with 1 decimal place
     */
    public double getBMIKg(double height, double weight) {
        double meters = height / 100;
        return format(weight / Math.pow(meters, 2));
    }

    /**
     * @param height in feet
     * @param weight in pounds
     * @return BMI index with 1 decimal place
     */
    public double getBMILb(double height, double weight) {
        int inch = (int) (height * 12);
        return format((weight * 703) / Math.pow(inch, 2));
    }

    /**
     * @param bmi (Body Mass Index)
     * @return BMI classification based on the bmi number
     */
    public String getBMIClassification(double bmi) {

        if (bmi <= 0) return "unknown";
        String classification;

        if (bmi < 18.5) {
            classification = "underweight";
        } else if (bmi < 25) {
            classification = "normal";
        } else if (bmi < 30) {
            classification = "overweight";
        } else {
            classification = "obese";
        }

        return classification;
    }

    public static double getFlozToMl(Float aFloat) {
        return aFloat * 29.57353;
    }

    public static double getMlToFloz(Float aFloat) {
        return aFloat * 0.03381;
    }

    public static void showCompleteDailog(Activity mainActivity, String mlevelGoal, String levelDesc) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity, R.style.full_screen_dialog);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View d = inflater.inflate(R.layout.dailog_archivement, null);
        dialogBuilder.setView(d);
        AlertDialog alertDialog = dialogBuilder.create();

        TextView mtvArchiveLabel = d.findViewById(R.id.tvArchiveLabel);
        TextView mtvArchiveDescription = d.findViewById(R.id.tvArchiveDescription);
        TextView mtvDetails = d.findViewById(R.id.tvDetails);
        ImageView mIvClosed = d.findViewById(R.id.ivClosed);
        CardView mcvClosed = d.findViewById(R.id.cvClosed);

        mtvArchiveLabel.setText(mlevelGoal + "");
        mtvArchiveDescription.setText(levelDesc);
        mtvDetails.setText("Great! You've Walked " + mlevelGoal + " steps today!");
        mcvClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        mIvClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     *
     * @param requestingLocationUpdates The location updates state.
     */
    public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    /**
     * Returns the {@code location} object as a human readable string.
     *
     * @param location The {@link Location}.
     */
    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }


    public static void TakeScreenShot(View view, Activity activity) {

        //This is used to provide file name with Date a format
        Date date = new Date();
        CharSequence format = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date);

        //It will make sure to store file to given below Directory and If the file Directory dosen't exist then it will create it.
        try {
            File mainDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), "FilShare");
            if (!mainDir.exists()) {
                boolean mkdir = mainDir.mkdir();
            }

            //Providing file name along with Bitmap to capture screenview
            String path = mainDir + "/" + "StepCounter" + "-" + format + ".jpeg";
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

//This logic is used to save file at given location with the given filename and compress the Image Quality.
            File imageFile = new File(path);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

//Create New Method to take ScreenShot with the imageFile.
            shareScreenShot(imageFile, activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void shareScreenShot(File imageFile, Activity activity) {
        //Using sub-class of Content provider
        Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", imageFile);

        //Explicit intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "This is Sample App to take ScreenShot");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        //It will show the application which are available to share Image; else Toast message will throw.
        try {
            activity.startActivity(Intent.createChooser(intent, "Share With"));
        } catch (ActivityNotFoundException e) {
//            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

}
