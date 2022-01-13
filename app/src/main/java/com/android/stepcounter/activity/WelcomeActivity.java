package com.android.stepcounter.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.stepcounter.R;
import com.android.stepcounter.utils.StorageManager;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {
    RelativeLayout mrlMale, mrlFemale;
    CardView cvHeight, cvWeight, cvDownload;
    String Gender = "male";
    float height = 168, weight = 60;
    TextView tvHeight, tvWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        StorageManager.getInstance().setIsProfile(false);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 12);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.AM_PM, Calendar.PM);
//        Logger.e(calendar.getTimeInMillis());
        StorageManager.getInstance().setCurrentDay(calendar.getTimeInMillis() + "");
        init();
    }


    private void init() {

        mrlMale = findViewById(R.id.rlMale);
        mrlFemale = findViewById(R.id.rlFemale);
        cvDownload = findViewById(R.id.cvDownload);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);

        cvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SharePreferecnce.setStringValue(SharePreferecnce.PREF_APP_KEY_GENDER, Gender);
//                SharePreferecnce.setFloatValue(SharePreferecnce.PREF_APP_KEY_WEIGHT, weight);
//                SharePreferecnce.setFloatValue(SharePreferecnce.PREF_APP_KEY_HEIGHT, height);

                StorageManager.getInstance().setGender(Gender);
                StorageManager.getInstance().setWeight(weight);
                StorageManager.getInstance().setHeight(height);

                Intent obj = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(obj);
                finish();
            }
        });

        mrlMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mrlMale.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selectablecricle));
                mrlFemale.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.cricle));
                Gender = "Male";
            }
        });

        mrlFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mrlFemale.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selectablecricle));
                mrlMale.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.cricle));
                Gender = "Female";
            }
        });

        cvHeight = findViewById(R.id.cvHeight);
        cvWeight = findViewById(R.id.cvWeight);
//        Float h = SharePreferecnce.getFloatValue(SharePreferecnce.PREF_APP_KEY_HEIGHT);
//        Float w = SharePreferecnce.getFloatValue(SharePreferecnce.PREF_APP_KEY_WEIGHT);

        Float h = StorageManager.getInstance().getHeight();
        Float w = StorageManager.getInstance().getWeight();
        tvHeight.setText(h + "");
        tvWeight.setText(w + "");

        cvHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHeightDailog();
            }
        });

        cvWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWeightDailog();
            }
        });

    }


    public void showHeightDailog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dialog, null);
        dialogBuilder.setView(d);

        AlertDialog alertDialog = dialogBuilder.create();
        Button b1 = (Button) d.findViewById(R.id.btnSave);
        Button b2 = (Button) d.findViewById(R.id.btnCancel);
        TextView tvTitle = (TextView) d.findViewById(R.id.tvTitle);
        tvTitle.setText("Height");
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        final NumberPicker np3 = (NumberPicker) d.findViewById(R.id.numberPicker3);

      /*  np.setMaxValue(100); // max value 100
        np.setMinValue(0);   // min value 0
        np.setWrapSelectorWheel(false);
        np.setValue(5);

        np2.setMaxValue(100); // max value 100
        np2.setMinValue(0);   // min value 0
        np2.setWrapSelectorWheel(false);
        np2.setValue(7);*/

        np.setMaxValue(250); // max value 100
        np.setMinValue(25);
        np.setValue(168);
        np2.setVisibility(View.GONE);

        np3.setMaxValue(2);
        np3.setMinValue(1);
        np3.setValue(1);
        np3.setDisplayedValues(new String[]{"cm", "ft+in"});
        np3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.e(TAG, "onValueChange: " + i1);
                if (i1 == 2) {
                    np.setMaxValue(100); // max value 100
                    np.setMinValue(0);
                    np.setValue(5);
                    np2.setVisibility(View.VISIBLE);
                    np2.setMaxValue(100); // max value 100
                    np2.setMinValue(0);
                    np2.setValue(7);
                } else {
                    np.setMaxValue(250); // max value 100
                    np.setMinValue(25);
                    np.setValue(168);
                    np2.setVisibility(View.GONE);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int text = np.getValue();
                int text1 = np2.getValue();
                int text2 = np3.getValue();
                height = Float.parseFloat(text + "." + text1);
                if (np3.getValue() == 2) {
                    tvHeight.setText(text + " ft " + text1 + " in");
                } else {
                    tvHeight.setText(text + " cm ");
                }
                StorageManager.getInstance().setHeight(Float.parseFloat(text + "." + text1));
                alertDialog.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // dismiss the dialog
            }
        });

        alertDialog.show();


    }

    public void showWeightDailog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View d = inflater.inflate(R.layout.dialog, null);
        dialogBuilder.setView(d);

        AlertDialog alertDialog = dialogBuilder.create();
        Button b1 = (Button) d.findViewById(R.id.btnSave);
        Button b2 = (Button) d.findViewById(R.id.btnCancel);
        TextView tvTitle = (TextView) d.findViewById(R.id.tvTitle);
        tvTitle.setText("Weight");
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        final NumberPicker np3 = (NumberPicker) d.findViewById(R.id.numberPicker3);

        np.setMaxValue(300); // max value 100
        np.setMinValue(15);
        np.setValue(60);
        np2.setMaxValue(9); // max value 100
        np2.setMinValue(0);
        np2.setValue(0);

        np3.setMaxValue(2);
        np3.setMinValue(1);
        np3.setValue(1);
        np3.setDisplayedValues(new String[]{"kg", "lbs"});
        np3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.e(TAG, "onValueChange: " + i1);
                if (i1 == 2) {
                    np.setMaxValue(663); // max value 100
                    np.setMinValue(33);
                    np.setValue(154);
                    np2.setMaxValue(9); // max value 100
                    np2.setMinValue(0);
                    np2.setValue(3);
                } else {
                    np.setMaxValue(300); // max value 100
                    np.setMinValue(15);
                    np.setValue(69);
                    np2.setMaxValue(9); // max value 100
                    np2.setMinValue(0);
                    np2.setValue(9);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s;
                int text = np.getValue();
                int text1 = np2.getValue();
                if (np3.getValue() == 2) {
                    s = "lbs";
                    tvWeight.setText(text + "." + text1 + " " + s);
                } else {
                    s = "kg";
                    tvWeight.setText(text + "." + text1 + " " + s);
                }
//                SharePreferecnce.setFloatValue(SharePreferecnce.PREF_APP_KEY_WEIGHT, Float.parseFloat(text + "." + text1));
                StorageManager.getInstance().setWeight(Float.parseFloat(text + "." + text1));
                weight = Float.parseFloat(text + "." + text1);
                alertDialog.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}