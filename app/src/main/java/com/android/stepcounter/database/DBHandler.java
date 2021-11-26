package com.android.stepcounter.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.stepcounter.model.stepcountModel;
import com.android.stepcounter.model.waterlevel;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_WATER = "Table_WaterList";

    // Table columns
    public static final String _ID = "Water_id";
    public static final String KEY_WATER_DATE = "WaterDate";
    public static final String KEY_WATER_MONTH = "Watermonth";
    public static final String KEY_WATER_YEAR = "WaterYear";
    public static final String KEY_WATER_HOURS = "WaterHours";
    public static final String KEY_WATER_MINITS = "WaterMinits";
    public static final String KEY_WATER_ML = "WaterMl";


    // Table Name
    public static final String TABLE_STEPCOUNT = "Table_DayWiseStep";

    // Table columns
    public static final String STEP_ID = "Step_id";
    public static final String KEY_STEP_COUNT = "StepCount";
    public static final String KEY_STEP_DATE = "StepDate";
    public static final String KEY_STEP_MONTH = "Stepmonth";
    public static final String KEY_STEP_YEAR = "StepYear";
    public static final String KEY_STEP_CALORIES = "StepCalories";
    public static final String KEY_STEP_DISTANCE = "StepDistance";
    public static final String KEY_STEP_DURATION = "StepDuration";

    // Database Information
    static final String DB_NAME = "StepCounter.db";
    // database version
    static final int DB_VERSION = 1;


    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_WATER + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_WATER_DATE + " INTEGER,"
                + KEY_WATER_MONTH + " INTEGER,"
                + KEY_WATER_YEAR + " INTEGER,"
                + KEY_WATER_HOURS + " INTEGER,"
                + KEY_WATER_MINITS + " INTEGER,"
                + KEY_WATER_ML + " TEXT)";

        String query1 = "CREATE TABLE " + TABLE_STEPCOUNT + " ("
                + STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_STEP_COUNT + " INTEGER,"
                + KEY_STEP_DATE + " INTEGER,"
                + KEY_STEP_MONTH + " INTEGER,"
                + KEY_STEP_YEAR + " INTEGER,"
                + KEY_STEP_CALORIES + " TEXT,"
                + KEY_STEP_DISTANCE + " TEXT,"
                + KEY_STEP_DURATION + " INTEGER)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
        db.execSQL(query1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPCOUNT);
        onCreate(db);
    }

    public void addWaterData(waterlevel waterlevel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        Log.e("TAG", "insertdata: ");

        initialValues.put(KEY_WATER_DATE, waterlevel.getDate());
        initialValues.put(KEY_WATER_MONTH, waterlevel.getMonth());
        initialValues.put(KEY_WATER_YEAR, waterlevel.getYear());
        initialValues.put(KEY_WATER_HOURS, waterlevel.getHour());
        initialValues.put(KEY_WATER_MINITS, waterlevel.getMin());
        initialValues.put(KEY_WATER_ML, waterlevel.getUnit());

        try {
            int i = updateWaterData(waterlevel);
            if (i == 0) {
                db.insert(TABLE_WATER, null, initialValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
    }

    private int updateWaterData(waterlevel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(TABLE_WATER, getContentValuesForWaterCount(model), KEY_WATER_HOURS + "=? ",
                new String[]{String.valueOf(model.getHour())});

    }

    private ContentValues getContentValuesForWaterCount(waterlevel model) {
        ContentValues values = new ContentValues();
        values.put(KEY_WATER_DATE, model.getDate());
        values.put(KEY_WATER_MONTH, model.getMonth());
        values.put(KEY_WATER_YEAR, model.getYear());
        values.put(KEY_WATER_HOURS, model.getHour());
        values.put(KEY_WATER_MINITS, model.getMin());
        values.put(KEY_WATER_ML, model.getUnit());

        return values;
    }

    public ArrayList<waterlevel> getWatercountlist() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.query(TABLE_STEPCOUNT, getWaterlistColumns(), null,
                null,
                null, null, null, null);

        ArrayList<waterlevel> list = getWaterlistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    private String[] getWaterlistColumns() {
        return new String[]{KEY_STEP_DATE, KEY_STEP_MONTH, KEY_STEP_YEAR, KEY_WATER_HOURS, KEY_WATER_MINITS, KEY_WATER_ML};
    }

    private ArrayList<waterlevel> getWaterlistFromCursor(Cursor c) {
        ArrayList<waterlevel> list = new ArrayList<waterlevel>();

        try {
            int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);
            int iKEY_STEP_MONTH = c.getColumnIndex(KEY_STEP_MONTH);
            int iKEY_STEP_YEAR = c.getColumnIndex(KEY_STEP_YEAR);
            int iKEY_STEP_HOUR = c.getColumnIndex(KEY_WATER_HOURS);
            int iKEY_STEP_MINITS = c.getColumnIndex(KEY_WATER_MINITS);
            int iKEY_STEP_UNIT = c.getColumnIndex(KEY_WATER_ML);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                waterlevel model = new waterlevel();

                model.setDate(c.getInt(iKEY_STEP_DATE));
                model.setMonth(c.getInt(iKEY_STEP_MONTH));
                model.setYear(c.getInt(iKEY_STEP_YEAR));
                model.setHour(c.getInt(iKEY_STEP_HOUR));
                model.setMin(c.getInt(iKEY_STEP_MINITS));
                model.setUnit(c.getString(iKEY_STEP_UNIT));
                list.add(model);
            }
        } finally {
            if (c != null)
                c.close();
        }
        return list;
    }

    ///step count

    public void addStepcountData(stepcountModel stepcountModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

//        Log.e("TAG", "insertdata: ");

        initialValues.put(KEY_STEP_COUNT, stepcountModel.getStep());
        initialValues.put(KEY_STEP_DATE, stepcountModel.getDate());
        initialValues.put(KEY_STEP_MONTH, stepcountModel.getMonth());
        initialValues.put(KEY_STEP_YEAR, stepcountModel.getYear());
        initialValues.put(KEY_STEP_CALORIES, stepcountModel.getCalorie());
        initialValues.put(KEY_STEP_DISTANCE, stepcountModel.getDistance());
        initialValues.put(KEY_STEP_DURATION, stepcountModel.getDuration());
        int temp;
        try {
            int i = updateStepData(stepcountModel);
            if (i == 0) {
                db.insert(TABLE_STEPCOUNT, null, initialValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    private int updateStepData(stepcountModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(TABLE_STEPCOUNT, getContentValuesForStepCount(model), KEY_STEP_DURATION + "=? ",
                new String[]{String.valueOf(model.getDuration())});

    }

    private ContentValues getContentValuesForStepCount(stepcountModel model) {
        ContentValues values = new ContentValues();
        values.put(KEY_STEP_COUNT, model.getStep());
        values.put(KEY_STEP_DATE, model.getDate());
        values.put(KEY_STEP_MONTH, model.getMonth());
        values.put(KEY_STEP_YEAR, model.getYear());
        values.put(KEY_STEP_CALORIES, model.getCalorie());
        values.put(KEY_STEP_DISTANCE, model.getDistance());
        values.put(KEY_STEP_DURATION, model.getDuration());

        return values;
    }

    public ArrayList<stepcountModel> getStepcountlist() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.query(TABLE_STEPCOUNT, getCategorylistColumns(), null,
                null,
                null, null, null, null);

        ArrayList<stepcountModel> list = getCategorylistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    private String[] getCategorylistColumns() {
        return new String[]{KEY_STEP_COUNT, KEY_STEP_DATE, KEY_STEP_MONTH, KEY_STEP_YEAR, KEY_STEP_CALORIES, KEY_STEP_DISTANCE, KEY_STEP_DURATION};
    }

    private ArrayList<stepcountModel> getCategorylistFromCursor(Cursor c) {
        ArrayList<stepcountModel> list = new ArrayList<stepcountModel>();

        try {
            int iKEY_STEP_COUNT = c.getColumnIndex(KEY_STEP_COUNT);
            int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);
            int iKEY_STEP_MONTH = c.getColumnIndex(KEY_STEP_MONTH);
            int iKEY_STEP_YEAR = c.getColumnIndex(KEY_STEP_YEAR);
            int iKEY_STEP_CALORIES = c.getColumnIndex(KEY_STEP_CALORIES);
            int iKEY_STEP_DISTANCE = c.getColumnIndex(KEY_STEP_DISTANCE);
            int iKEY_STEP_DURATION = c.getColumnIndex(KEY_STEP_DURATION);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                stepcountModel model = new stepcountModel();

                model.setStep(c.getInt(iKEY_STEP_COUNT));
                model.setDate(c.getInt(iKEY_STEP_DATE));
                model.setMonth(c.getInt(iKEY_STEP_MONTH));
                model.setYear(c.getInt(iKEY_STEP_YEAR));
                model.setCalorie(c.getString(iKEY_STEP_CALORIES));
                model.setDistance(c.getString(iKEY_STEP_DISTANCE));
                model.setDuration(c.getInt(iKEY_STEP_DURATION));

                list.add(model);
            }
        } finally {
            if (c != null)
                c.close();
        }
        return list;
    }

    public ArrayList<stepcountModel> getCurrentDayStepcountlist(int date, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "select * from " + TABLE_STEPCOUNT + " where " + KEY_STEP_DATE + " = " + date + " AND " + KEY_STEP_MONTH +
                " = " + month + " AND  " + KEY_STEP_YEAR + " = " + year;

        Cursor c = db.rawQuery(s, null);

        ArrayList<stepcountModel> list = getCategorylistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    public ArrayList<stepcountModel> getCurrentDayHoursStepcountlist(int date, int month, int year, int hour) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "select * from " + TABLE_STEPCOUNT + " where " + KEY_STEP_DATE + " = " + date + " AND " + KEY_STEP_MONTH +
                " = " + month + " AND  " + KEY_STEP_YEAR + " = " + year + " AND  " + KEY_STEP_DURATION + " = " + hour;

        Cursor c = db.rawQuery(s, null);

        ArrayList<stepcountModel> list = getCategorylistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    @SuppressLint("Range")
    public int getSumOfStepList(int date, int month, int year) {
        int sum;
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "select sum(" + KEY_STEP_COUNT + ")  as total from " + TABLE_STEPCOUNT + " where " + KEY_STEP_DATE + " = " + date + " AND " + KEY_STEP_MONTH +
                " = " + month + " AND  " + KEY_STEP_YEAR + " = " + year;

        Cursor c = db.rawQuery(s, null);

        c.moveToFirst();
        do {
            sum = c.getInt(c.getColumnIndex("total"));
        } while (c.moveToNext());

        c.close();
        return sum;
    }

    @SuppressLint("Range")
    public int getSumOfHoursStepList(int date, int month, int year, int hours) {
        int sum;
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "select sum(" + KEY_STEP_COUNT + ")  as total from " + TABLE_STEPCOUNT + " where " + KEY_STEP_DATE + " = " + date + " AND " + KEY_STEP_MONTH +
                " = " + month + " AND  " + KEY_STEP_YEAR + " = " + year + " AND  " + KEY_STEP_DURATION + " = " + hours;

        Cursor c = db.rawQuery(s, null);

        c.moveToFirst();
        do {
            sum = c.getInt(c.getColumnIndex("total"));
        } while (c.moveToNext());


        c.close();
        return sum;
    }

    public void DeleteCurrentDayData(int date, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        int b = db.delete(TABLE_STEPCOUNT, KEY_STEP_DATE + " = ?  AND "
                        + KEY_STEP_MONTH + " =? AND "
                        + KEY_STEP_YEAR + " =?",
                new String[]{String.valueOf(date), String.valueOf(month), String.valueOf(year)});
        db.close();
    }
}
