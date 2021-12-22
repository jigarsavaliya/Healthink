package com.android.stepcounter.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.R;

import java.text.DecimalFormat;

public class CommanMethod {

    public static float calculateDistance(int num_steps) {
        double distance = num_steps * 0.0005;
        return (float) distance;
    }


//    public static float calculateDistance(int num_steps, float height) {
//        double distance = num_steps * height * 0.3937 * 0.414 * 2.54e-2;
//        return (float) distance;
//    }

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

    public static void showCompleteDailog(Activity mainActivity, long mlevelGoal, String levelDesc) {
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
}
