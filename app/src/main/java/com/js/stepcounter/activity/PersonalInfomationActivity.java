package com.js.stepcounter.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.js.stepcounter.R;
import com.js.stepcounter.utils.StorageManager;
import com.js.stepcounter.utils.constant;

import static android.content.ContentValues.TAG;

public class PersonalInfomationActivity extends BaseActivity {
    Toolbar mToolbar;
    LinearLayout llGender, llLenght, llWeight, llUnit;
    RelativeLayout mRlGender, mRlWeight, mRlUnit;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    TextView tvWeight, tvGender, tvUnit;
    String Gender;

    private float userWeight = constant.DEFAULT_WEIGHT;
    private float userHeight = constant.DEFAULT_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_infomation);
        setSharedPreferences();
        init();
    }

    private void setSharedPreferences() {
        Gender = StorageManager.getInstance().getGender();
        userHeight = StorageManager.getInstance().getHeight();
        userWeight = StorageManager.getInstance().getWeight();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Personal Information");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRlGender = findViewById(R.id.rlGender);
        mRlWeight = findViewById(R.id.rlWeight);
        mRlUnit = findViewById(R.id.rlUnit);

        llGender = findViewById(R.id.llGender);
        llLenght = findViewById(R.id.llLenght);
        llWeight = findViewById(R.id.llWeight);
        llUnit = findViewById(R.id.llUnit);
//        llweek = findViewById(R.id.llweek);

        tvWeight = findViewById(R.id.tvWeight);
        tvGender = findViewById(R.id.tvGender);
        tvUnit = findViewById(R.id.tvUnit);

        tvGender.setText(Gender);
        tvWeight.setText(userWeight + "");
        tvUnit.setText("lbs / ft");

        mRlGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(PersonalInfomationActivity.this, llGender);
                //inflating menu from xml resource
                popup.inflate(R.menu.gender_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.male:
                                tvGender.setText("Male");
                                editor.putString("Gender", "Male");
                                editor.commit();
                                return true;

                            case R.id.female:
                                tvGender.setText("Famale");
                                editor.putString("Gender", "Female");
                                editor.commit();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        mRlWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWeightDailog();
            }
        });

        mRlUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(PersonalInfomationActivity.this, llUnit);
                //inflating menu from xml resource
                popup.inflate(R.menu.unit_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.kg:
                                tvUnit.setText("kg / cm");
                                return true;

                            case R.id.lbs:
                                tvUnit.setText("lbs / ft");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
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
        np.setMaxValue(663); // max value 100
        np.setMinValue(33);   // min value 0
        np.setWrapSelectorWheel(false);
        np.setValue(154);

        np2.setMaxValue(9); // max value 100
        np2.setMinValue(0);   // min value 0
        np2.setWrapSelectorWheel(false);
        np2.setValue(3);

        np3.setMaxValue(2);
        np3.setMinValue(1);
        np3.setValue(2);
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
                } else {
                    s = "kg";
                }
                editor.putString("Weight", text + "." + text1);
                tvWeight.setText(text + "." + text1 + " " + s);
//                Toast.makeText(getApplicationContext(), text + "." + text1, Toast.LENGTH_SHORT).show();
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