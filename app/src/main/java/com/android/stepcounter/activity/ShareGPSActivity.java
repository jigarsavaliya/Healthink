package com.android.stepcounter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.stepcounter.R;
import com.android.stepcounter.database.DatabaseManager;
import com.android.stepcounter.model.GpsTrackerModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class ShareGPSActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar mToolbar;
    String TargetType;
    Integer numStep;
    DatabaseManager dbManager;
    TextView mTvCurrDate, mTvEditDailog;
    TextView mTimerValue, mTimerText, mTvCurrentValue, mTvGoalValue, mStepValue, mStep, mTvKcalValue, mTvKcal;
    ArrayList<GpsTrackerModel> gpsTrackerModelArrayList = new ArrayList<>();
    String Distance = "0.0", Duration;
    int Calories = 0;
    CardView mCvChangeCover;
    ImageView viewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_gpsactivity);
        dbManager = new DatabaseManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mTvCurrDate = findViewById(R.id.tvDate);
        mTvEditDailog = findViewById(R.id.tvEditDailog);

        mCvChangeCover = findViewById(R.id.cvChangeCover);
        mCvChangeCover.setOnClickListener(this);
        viewImage = (ImageView) findViewById(R.id.viewImage);

        mTvCurrentValue = findViewById(R.id.tvCurrentValue);
        mTvGoalValue = findViewById(R.id.tvGoalValue);
        mTimerValue = findViewById(R.id.timervalue);
        mTimerText = findViewById(R.id.timer);
        mStepValue = findViewById(R.id.tvStepValue);
        mStep = findViewById(R.id.tvStep);
        mTvKcalValue = findViewById(R.id.mtvKcalValue);
        mTvKcal = findViewById(R.id.mtvKcal);

        Date d = new Date();
        CharSequence s = DateFormat.format("MMMM d, yyyy ", d.getTime());
        mTvCurrDate.setText(s);
        getDataFromDatabase();
        setData();
    }

    private void getDataFromDatabase() {
        gpsTrackerModelArrayList = dbManager.getGpsTrackerlist();

        for (int i = 0; i < gpsTrackerModelArrayList.size(); i++) {
            TargetType = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getType();
            numStep = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getStep();
            Calories = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getCalories();
            Distance = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getDistance();
            Duration = gpsTrackerModelArrayList.get(gpsTrackerModelArrayList.size() - 1).getDuration();
        }
    }

    private void setData() {
        mTvCurrentValue.setText(Duration + "");
        mTvGoalValue.setText("Duration");
        mTimerValue.setText(Distance + "");
        mTimerText.setText("Mile");
        mStepValue.setText(numStep + "");
        mStep.setText("Steps");
        mTvKcalValue.setText(Calories + "");
        mTvKcal.setText("Kcal");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cvChangeCover:
                selectImage();
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    viewImage.setImageBitmap(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                viewImage.setImageBitmap(thumbnail);
            }
        }
    }
}