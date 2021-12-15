package com.android.stepcounter.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.stepcounter.model.StepCountModel;
import com.android.stepcounter.model.WaterLevelModel;
import com.android.stepcounter.model.WeightModel;
import com.android.stepcounter.utils.Logger;

import java.util.ArrayList;
import java.util.Calendar;

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
    public static final String KEY_WATER_TIMESTMP = "WaterTimeStemp";

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
    public static final String KEY_STEP_TIMESTMP = "StepTimeStemp";

    // Table Name
    public static final String TABLE_WEIGHT = "Table_Weight";
    // Table columns
    public static final String WEIGHT_ID = "Weight_id";
    public static final String KEY_WEIGHT_DATE = "WeightDate";
    public static final String KEY_WEIGHT_MONTH = "Weightmonth";
    public static final String KEY_WEIGHT_YEAR = "WeightYear";
    public static final String KEY_WEIGHT_KG = "WeightKg";
    public static final String KEY_WEIGHT_TIMESTMP = "WeightTimeStemp";

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
        String WaterTable = "CREATE TABLE " + TABLE_WATER + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_WATER_DATE + " INTEGER,"
                + KEY_WATER_MONTH + " INTEGER,"
                + KEY_WATER_YEAR + " INTEGER,"
                + KEY_WATER_HOURS + " INTEGER,"
                + KEY_WATER_MINITS + " INTEGER,"
                + KEY_WATER_TIMESTMP + " TEXT,"
                + KEY_WATER_ML + " TEXT)";

        String StepTable = "CREATE TABLE " + TABLE_STEPCOUNT + " ("
                + STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_STEP_COUNT + " INTEGER,"
                + KEY_STEP_DATE + " INTEGER,"
                + KEY_STEP_MONTH + " INTEGER,"
                + KEY_STEP_YEAR + " INTEGER,"
                + KEY_STEP_CALORIES + " TEXT,"
                + KEY_STEP_DISTANCE + " TEXT,"
                + KEY_STEP_TIMESTMP + " TEXT,"
                + KEY_STEP_DURATION + " INTEGER)";

        String WeightTable = "CREATE TABLE " + TABLE_WEIGHT + " ("
                + WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_WEIGHT_DATE + " INTEGER,"
                + KEY_WEIGHT_MONTH + " INTEGER,"
                + KEY_WEIGHT_YEAR + " INTEGER,"
                + KEY_WEIGHT_TIMESTMP + " TEXT,"
                + KEY_WEIGHT_KG + " INTEGER)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(WaterTable);
        db.execSQL(StepTable);
        db.execSQL(WeightTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT);
        onCreate(db);
    }

    public void addWaterData(WaterLevelModel waterlevel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        Log.e("TAG", "insertdata: ");

        initialValues.put(KEY_WATER_DATE, waterlevel.getDate());
        initialValues.put(KEY_WATER_MONTH, waterlevel.getMonth());
        initialValues.put(KEY_WATER_YEAR, waterlevel.getYear());
        initialValues.put(KEY_WATER_HOURS, waterlevel.getHour());
        initialValues.put(KEY_WATER_MINITS, waterlevel.getMin());
        initialValues.put(KEY_WATER_ML, waterlevel.getUnit());
        initialValues.put(KEY_WATER_TIMESTMP, waterlevel.getTimestemp());

        try {
//            int i = updateWaterData(waterlevel);
//            if (i == 0) {
            db.insert(TABLE_WATER, null, initialValues);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
    }

    private int updateWaterData(WaterLevelModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(TABLE_WATER, getContentValuesForWaterCount(model), KEY_WATER_HOURS + "=? ",
                new String[]{String.valueOf(model.getHour())});

    }

    private ContentValues getContentValuesForWaterCount(WaterLevelModel model) {
        ContentValues values = new ContentValues();
        values.put(KEY_WATER_DATE, model.getDate());
        values.put(KEY_WATER_MONTH, model.getMonth());
        values.put(KEY_WATER_YEAR, model.getYear());
        values.put(KEY_WATER_HOURS, model.getHour());
        values.put(KEY_WATER_MINITS, model.getMin());
        values.put(KEY_WATER_ML, model.getUnit());
        values.put(KEY_WATER_TIMESTMP, model.getTimestemp());

        return values;
    }

    public ArrayList<WaterLevelModel> getWatercountlist() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.query(TABLE_WATER, getWaterlistColumns(), null,
                null,
                null, null, null, null);

        ArrayList<WaterLevelModel> list = getWaterlistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    private String[] getWaterlistColumns() {
        return new String[]{KEY_WATER_DATE, KEY_WATER_MONTH, KEY_WATER_YEAR, KEY_WATER_HOURS, KEY_WATER_MINITS, KEY_WATER_ML, KEY_WATER_TIMESTMP};
    }

    private ArrayList<WaterLevelModel> getWaterlistFromCursor(Cursor c) {
        ArrayList<WaterLevelModel> list = new ArrayList<WaterLevelModel>();

        try {
            int iKEY_STEP_DATE = c.getColumnIndex(KEY_WATER_DATE);
            int iKEY_STEP_MONTH = c.getColumnIndex(KEY_WATER_MONTH);
            int iKEY_STEP_YEAR = c.getColumnIndex(KEY_WATER_YEAR);
            int iKEY_STEP_HOUR = c.getColumnIndex(KEY_WATER_HOURS);
            int iKEY_STEP_MINITS = c.getColumnIndex(KEY_WATER_MINITS);
            int iKEY_STEP_UNIT = c.getColumnIndex(KEY_WATER_ML);
            int iKEY_STEP_TIMESTMP = c.getColumnIndex(KEY_WATER_TIMESTMP);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                WaterLevelModel model = new WaterLevelModel();

                model.setDate(c.getInt(iKEY_STEP_DATE));
                model.setMonth(c.getInt(iKEY_STEP_MONTH));
                model.setYear(c.getInt(iKEY_STEP_YEAR));
                model.setHour(c.getInt(iKEY_STEP_HOUR));
                model.setMin(c.getInt(iKEY_STEP_MINITS));
                model.setUnit(c.getString(iKEY_STEP_UNIT));
                model.setTimestemp(c.getString(iKEY_STEP_TIMESTMP));

                list.add(model);
            }
        } finally {
            if (c != null)
                c.close();
        }
        return list;
    }

    public ArrayList<WaterLevelModel> getCurrentDayWatercountlist(int date, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "select * from " + TABLE_WATER + " where " + KEY_WATER_DATE + " = " + date + " AND " + KEY_WATER_MONTH +
                " = " + month + " AND  " + KEY_WATER_YEAR + " = " + year;

        Cursor c = db.rawQuery(s, null);

        ArrayList<WaterLevelModel> list = getWaterlistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    @SuppressLint("Range")
    public ArrayList<WaterLevelModel> getDayWaterdata(int date, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "select " + KEY_WATER_DATE + " ,sum(" + KEY_WATER_ML + ")  as total from " + TABLE_WATER + " where " + KEY_WATER_DATE + " = " + date + " AND " + KEY_WATER_MONTH +
                " = " + month + " AND  " + KEY_WATER_YEAR + " = " + year + " GROUP BY " + KEY_WATER_DATE;

        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<WaterLevelModel> list = new ArrayList<WaterLevelModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(KEY_WATER_DATE);
        int sum;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            WaterLevelModel model = new WaterLevelModel();
            sum = c.getInt(c.getColumnIndex("total"));

            model.setDate(c.getInt(iKEY_STEP_DATE));
            model.setSumwater(sum);
            list.add(model);
        }

        c.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<WaterLevelModel> getweekWaterdata(String fristdate, String lastdate) {
        SQLiteDatabase db = this.getWritableDatabase();

        /*String s = "SELECT " + KEY_WATER_DATE + " , sum(" + KEY_WATER_ML + ") as total FROM " + TABLE_WATER + " where " + KEY_WATER_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + KEY_WATER_DATE;*/

        String s = "SELECT * , sum(" + KEY_WATER_ML + ") as total FROM " + TABLE_WATER + " where " + KEY_WATER_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + KEY_WATER_DATE;

//        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<WaterLevelModel> temp = new ArrayList<WaterLevelModel>();
        ArrayList<WaterLevelModel> list = new ArrayList<WaterLevelModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(KEY_WATER_DATE);
        int iKEY_WATER_MONTH = c.getColumnIndex(KEY_WATER_MONTH);
        int iKEY_WATER_YEAR = c.getColumnIndex(KEY_WATER_YEAR);
        int sum;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            WaterLevelModel model = new WaterLevelModel();
            sum = c.getInt(c.getColumnIndex("total"));

            model.setDate(c.getInt(iKEY_STEP_DATE));
            model.setMonth(c.getInt(iKEY_WATER_MONTH));
            model.setYear(c.getInt(iKEY_WATER_YEAR));
            model.setSumwater(sum);
            temp.add(model);
        }
//        Log.e("list", "" + temp.size());

        int j = 0;

        Calendar cal = Calendar.getInstance();
        long a = Long.parseLong(fristdate.trim());
        cal.setTimeInMillis(a);

        for (int i = 0; i < 7; i++) {

            if (j < temp.size() && cal.get(Calendar.DATE) == temp.get(j).getDate()) {
                list.add(temp.get(j));
                j++;
            } else {
                WaterLevelModel model = new WaterLevelModel();
                model.setDate(cal.get(Calendar.DATE));
                model.setMonth(cal.get(Calendar.MONTH) + 1);
                model.setYear(cal.get(Calendar.YEAR));
                model.setTimestemp("0");
                model.setSumwater(0);
                list.add(model);
            }
            cal.add(Calendar.DATE, 1);
//            Log.e("list", "" + list.get(i).getSumwater());
        }


        c.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<WaterLevelModel> getMonthWaterdata(String fristdate, String lastdate, int a) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "SELECT " + KEY_WATER_DATE + " , sum(" + KEY_WATER_ML + ") as total FROM " + TABLE_WATER + " where " + KEY_WATER_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + KEY_WATER_DATE;

        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<WaterLevelModel> temp = new ArrayList<WaterLevelModel>();
        ArrayList<WaterLevelModel> list = new ArrayList<WaterLevelModel>();

        int iKEY_WATER_DATE = c.getColumnIndex(KEY_WATER_DATE);
        int sum;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            WaterLevelModel model = new WaterLevelModel();
            sum = c.getInt(c.getColumnIndex("total"));

            model.setDate(c.getInt(iKEY_WATER_DATE));
            model.setSumwater(sum);
            temp.add(model);
        }
//        Log.e("list", "" + list.size());

        int j = 0;
        for (int i = 0; i < a; i++) {
            if (j < temp.size() && i == temp.get(j).getDate()) {
                list.add(temp.get(j));
                j++;
            } else {
                WaterLevelModel model = new WaterLevelModel();
                model.setDate(i);
                model.setMonth(0);
                model.setYear(0);
                model.setHour(0);
                model.setMin(0);
                model.setTimestemp("0");
                model.setSumwater(0);
                list.add(model);
            }
        }

        c.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    public void DeleteCurrentDayWaterData(int date, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        int b = db.delete(TABLE_WATER, KEY_WATER_DATE + " = ?  AND "
                        + KEY_WATER_MONTH + " =? AND "
                        + KEY_WATER_YEAR + " =?",
                new String[]{String.valueOf(date), String.valueOf(month), String.valueOf(year)});
        db.close();
    }

    public void DeletelastWaterData(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int b = db.delete(TABLE_WATER,
                KEY_WATER_TIMESTMP + " =?",
                new String[]{time});
        db.close();
    }
    ///step count

    public void addStepcountData(StepCountModel stepcountModel) {
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
        initialValues.put(KEY_STEP_TIMESTMP, stepcountModel.getTimestemp());
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

    private int updateStepData(StepCountModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(TABLE_STEPCOUNT, getContentValuesForStepCount(model), KEY_STEP_DURATION + "=? ",
                new String[]{String.valueOf(model.getDuration())});

    }

    private ContentValues getContentValuesForStepCount(StepCountModel model) {
        ContentValues values = new ContentValues();
        values.put(KEY_STEP_COUNT, model.getStep());
        values.put(KEY_STEP_DATE, model.getDate());
        values.put(KEY_STEP_MONTH, model.getMonth());
        values.put(KEY_STEP_YEAR, model.getYear());
        values.put(KEY_STEP_CALORIES, model.getCalorie());
        values.put(KEY_STEP_DISTANCE, model.getDistance());
        values.put(KEY_STEP_DURATION, model.getDuration());
        values.put(KEY_STEP_TIMESTMP, model.getTimestemp());

        return values;
    }

    public ArrayList<StepCountModel> getStepcountlist() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.query(TABLE_STEPCOUNT, getCategorylistColumns(), null,
                null,
                null, null, null, null);

        ArrayList<StepCountModel> list = getCategorylistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    private String[] getCategorylistColumns() {
        return new String[]{KEY_STEP_COUNT, KEY_STEP_DATE, KEY_STEP_MONTH, KEY_STEP_YEAR, KEY_STEP_CALORIES, KEY_STEP_DISTANCE, KEY_STEP_DURATION, KEY_STEP_TIMESTMP};
    }

    @SuppressLint("Range")
    private ArrayList<StepCountModel> getCategorylistFromCursor(Cursor c) {
        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();
        try {
            int iKEY_STEP_COUNT = c.getColumnIndex(KEY_STEP_COUNT);
            int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);
            int iKEY_STEP_MONTH = c.getColumnIndex(KEY_STEP_MONTH);
            int iKEY_STEP_YEAR = c.getColumnIndex(KEY_STEP_YEAR);
            int iKEY_STEP_CALORIES = c.getColumnIndex(KEY_STEP_CALORIES);
            int iKEY_STEP_DISTANCE = c.getColumnIndex(KEY_STEP_DISTANCE);
            int iKEY_STEP_DURATION = c.getColumnIndex(KEY_STEP_DURATION);
            int iKEY_STEP_TIMESTMP = c.getColumnIndex(KEY_STEP_TIMESTMP);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                StepCountModel model = new StepCountModel();

                model.setStep(c.getInt(iKEY_STEP_COUNT));
                model.setDate(c.getInt(iKEY_STEP_DATE));
                model.setMonth(c.getInt(iKEY_STEP_MONTH));
                model.setYear(c.getInt(iKEY_STEP_YEAR));
                model.setCalorie(c.getString(iKEY_STEP_CALORIES));
                model.setDistance(c.getString(iKEY_STEP_DISTANCE));
                model.setDuration(c.getInt(iKEY_STEP_DURATION));
                model.setTimestemp(c.getString(iKEY_STEP_TIMESTMP));
                temp.add(model);
            }

            int j = 0;
            for (int i = 0; i < 24; i++) {
                if (j < temp.size() && i == temp.get(j).getDuration()) {
                    list.add(temp.get(j));
                    j++;
                } else {
                    StepCountModel model = new StepCountModel();
                    model.setStep(0);
                    model.setDate(0);
                    model.setMonth(0);
                    model.setYear(0);
                    model.setCalorie("0");
                    model.setDistance("0");
                    model.setDuration(i);
                    model.setTimestemp("0");
                    list.add(model);
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "getCategorylistFromCursor: " + e.getMessage());
        } finally {
            if (c != null)
                c.close();
        }
        return list;
    }

    public ArrayList<StepCountModel> getDaywiseStepdata() {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "SELECT * , sum(" + KEY_STEP_COUNT + ") as total FROM " + TABLE_STEPCOUNT + " GROUP BY " + KEY_STEP_DATE + "," + KEY_STEP_MONTH + "," + KEY_STEP_YEAR
                + " ORDER BY " + KEY_STEP_TIMESTMP + " ASC";

        Log.e("TAG", "getDaywiseStepdata: " + s);
        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getSteplistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    public ArrayList<StepCountModel> getCurrentDayStepcountlist(int date, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "select * from " + TABLE_STEPCOUNT + " where " + KEY_STEP_DATE + " = " + date + " AND " + KEY_STEP_MONTH +
                " = " + month + " AND  " + KEY_STEP_YEAR + " = " + year + " ORDER BY " + KEY_STEP_DURATION + " ASC ";

        Log.e("TAG", "getCurrentDayStepcountlist: " + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getCategorylistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    public ArrayList<StepCountModel> getYesterDayStepcountlist(int date, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "SELECT * , sum(" + KEY_STEP_COUNT + ") as total FROM " + TABLE_STEPCOUNT + " where " + KEY_STEP_DATE + " = " + date + " AND " + KEY_STEP_MONTH +
                " = " + month + " AND  " + KEY_STEP_YEAR + " = " + year + " GROUP BY " + KEY_STEP_DATE;

        Log.e("TAG", "getDaywiseStepdata: " + s);
        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getSteplistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    public ArrayList<StepCountModel> getCurrentDayHoursStepcountlist(int date, int month, int year, int hour) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "select * from " + TABLE_STEPCOUNT + " where " + KEY_STEP_DATE + " = " + date + " AND " + KEY_STEP_MONTH +
                " = " + month + " AND  " + KEY_STEP_YEAR + " = " + year + " AND  " + KEY_STEP_DURATION + " = " + hour;

        Logger.e(s);
        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getHoursSteplistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    private ArrayList<StepCountModel> getHoursSteplistFromCursor(Cursor c) {
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();
        try {
            int iKEY_STEP_COUNT = c.getColumnIndex(KEY_STEP_COUNT);
            int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);
            int iKEY_STEP_MONTH = c.getColumnIndex(KEY_STEP_MONTH);
            int iKEY_STEP_YEAR = c.getColumnIndex(KEY_STEP_YEAR);
            int iKEY_STEP_CALORIES = c.getColumnIndex(KEY_STEP_CALORIES);
            int iKEY_STEP_DISTANCE = c.getColumnIndex(KEY_STEP_DISTANCE);
            int iKEY_STEP_DURATION = c.getColumnIndex(KEY_STEP_DURATION);
            int iKEY_STEP_TIMESTMP = c.getColumnIndex(KEY_STEP_TIMESTMP);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                StepCountModel model = new StepCountModel();

                model.setStep(c.getInt(iKEY_STEP_COUNT));
                model.setDate(c.getInt(iKEY_STEP_DATE));
                model.setMonth(c.getInt(iKEY_STEP_MONTH));
                model.setYear(c.getInt(iKEY_STEP_YEAR));
                model.setCalorie(c.getString(iKEY_STEP_CALORIES));
                model.setDistance(c.getString(iKEY_STEP_DISTANCE));
                model.setDuration(c.getInt(iKEY_STEP_DURATION));
                model.setTimestemp(c.getString(iKEY_STEP_TIMESTMP));

                list.add(model);
            }
        } finally {
            if (c != null)
                c.close();
        }
        return list;
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getweekstepdata(String fristdate, String lastdate) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "SELECT * , sum(" + KEY_STEP_COUNT + ") as total FROM " + TABLE_STEPCOUNT + " where " + KEY_STEP_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + KEY_STEP_DATE;

//        Log.e("list", "" + s);
        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);
        int iKEY_STEP_MONTH = c.getColumnIndex(KEY_STEP_MONTH);
        int iKEY_STEP_YEAR = c.getColumnIndex(KEY_STEP_YEAR);
        int sum;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            StepCountModel model = new StepCountModel();
            sum = c.getInt(c.getColumnIndex("total"));

            model.setDate(c.getInt(iKEY_STEP_DATE));
            model.setMonth(c.getInt(iKEY_STEP_MONTH));
            model.setYear(c.getInt(iKEY_STEP_YEAR));
            model.setSumstep(sum);
            temp.add(model);
        }
//        Log.e("list", "" + list.size());

        Calendar cal = Calendar.getInstance();
        long a = Long.parseLong(fristdate.trim());
        cal.setTimeInMillis(a);

        int j = 0;
        for (int i = 0; i < 7; i++) {
            if (j < temp.size() && cal.get(Calendar.DATE) == temp.get(j).getDate()) {
                list.add(temp.get(j));
                j++;
            } else {
                StepCountModel model = new StepCountModel();
                model.setStep(0);
                model.setDate(cal.get(Calendar.DATE));
                model.setMonth(cal.get(Calendar.MONTH) + 1);
                model.setYear(cal.get(Calendar.YEAR));
                model.setCalorie("0");
                model.setDistance("0");
                model.setDuration(0);
                model.setTimestemp("0");
                model.setSumstep(0);
                list.add(model);
            }
            cal.add(Calendar.DATE, 1);
        }

        c.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getweekCaloriesdata(String fristdate, String lastdate) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "SELECT *, sum(" + KEY_STEP_CALORIES + ") as total FROM " + TABLE_STEPCOUNT + " where " + KEY_STEP_TIMESTMP + " BETWEEN " + fristdate + " AND " +
                lastdate + " GROUP BY " + KEY_STEP_DATE;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);
        int iKEY_STEP_MONTH = c.getColumnIndex(KEY_STEP_MONTH);
        int iKEY_STEP_YEAR = c.getColumnIndex(KEY_STEP_YEAR);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            StepCountModel model = new StepCountModel();
            int sum = c.getInt(c.getColumnIndex("total"));

            model.setDate(c.getInt(iKEY_STEP_DATE));
            model.setMonth(c.getInt(iKEY_STEP_MONTH));
            model.setYear(c.getInt(iKEY_STEP_YEAR));
            model.setSumcalorie(sum);
            temp.add(model);
        }

        Calendar cal = Calendar.getInstance();
        long a = Long.parseLong(fristdate.trim());
        cal.setTimeInMillis(a);

        int j = 0;
        for (int i = 0; i < 7; i++) {
            if (j < temp.size() && cal.get(Calendar.DATE) == temp.get(j).getDate()) {
                list.add(temp.get(j));
                j++;
            } else {
                StepCountModel model = new StepCountModel();
                model.setStep(0);
                model.setDate(cal.get(Calendar.DATE));
                model.setMonth(cal.get(Calendar.MONTH) + 1);
                model.setYear(cal.get(Calendar.YEAR));
                model.setCalorie("0");
                model.setDistance("0");
                model.setDuration(0);
                model.setTimestemp("0");
                model.setSumcalorie(0);
                list.add(model);
            }
            cal.add(Calendar.DATE, 1);
        }
        c.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getweekDistancedata(String fristdate, String lastdate) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "SELECT *, sum (" + KEY_STEP_DISTANCE + ") as total FROM " + TABLE_STEPCOUNT + " where " + KEY_STEP_TIMESTMP + " BETWEEN " + fristdate + " AND " +
                lastdate + " GROUP BY " + KEY_STEP_DATE;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);
        int iKEY_STEP_MONTH = c.getColumnIndex(KEY_STEP_MONTH);
        int iKEY_STEP_YEAR = c.getColumnIndex(KEY_STEP_YEAR);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            StepCountModel model = new StepCountModel();

            int sum = c.getInt(c.getColumnIndex("total"));

            model.setDate(c.getInt(iKEY_STEP_DATE));
            model.setMonth(c.getInt(iKEY_STEP_MONTH));
            model.setYear(c.getInt(iKEY_STEP_YEAR));
            model.setSumdistance(sum);
            temp.add(model);
        }

        Calendar cal = Calendar.getInstance();
        long a = Long.parseLong(fristdate.trim());
        cal.setTimeInMillis(a);

        int j = 0;
        for (int i = 0; i < 7; i++) {
            if (j < temp.size() && cal.get(Calendar.DATE) == temp.get(j).getDate()) {
                list.add(temp.get(j));
                j++;
            } else {
                StepCountModel model = new StepCountModel();
                model.setStep(0);
                model.setDate(cal.get(Calendar.DATE));
                model.setMonth(cal.get(Calendar.MONTH) + 1);
                model.setYear(cal.get(Calendar.YEAR));
                model.setCalorie("0");
                model.setDistance("0");
                model.setDuration(0);
                model.setTimestemp("0");
                model.setSumdistance(0);
                list.add(model);
            }
            cal.add(Calendar.DATE, 1);
        }
        c.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getMonthstepdata(String fristdate, String lastdate, int a) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "SELECT " + KEY_STEP_DATE + " , sum(" + KEY_STEP_COUNT + ") as total FROM " + TABLE_STEPCOUNT + " where " + KEY_STEP_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + KEY_STEP_DATE;

//        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);
        int sum;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            StepCountModel model = new StepCountModel();
            sum = c.getInt(c.getColumnIndex("total"));

            model.setDate(c.getInt(iKEY_STEP_DATE));
            model.setSumstep(sum);
            temp.add(model);
        }
//        Log.e("list", "" + list.size());

        int j = 0;
        for (int i = 0; i < a; i++) {
            if (j < temp.size() && i == temp.get(j).getDate()) {
                list.add(temp.get(j));
                j++;
            } else {
                StepCountModel model = new StepCountModel();
                model.setStep(0);
                model.setDate(i);
                model.setMonth(0);
                model.setYear(0);
                model.setCalorie("0");
                model.setDistance("0");
                model.setDuration(0);
                model.setTimestemp("0");
                model.setSumstep(0);
                list.add(model);
            }
        }

        c.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getMonthCaloriesdata(String fristdate, String lastdate, int a) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "SELECT " + KEY_STEP_DATE + ", sum(" + KEY_STEP_CALORIES + ") as total FROM " + TABLE_STEPCOUNT + " where " + KEY_STEP_TIMESTMP + " BETWEEN " + fristdate + " AND " +
                lastdate + " GROUP BY " + KEY_STEP_DATE;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            StepCountModel model = new StepCountModel();
            int sum = c.getInt(c.getColumnIndex("total"));

            model.setDate(c.getInt(iKEY_STEP_DATE));
            model.setSumcalorie(sum);
            temp.add(model);
        }

        int j = 0;
        for (int i = 0; i < a; i++) {
            if (j < temp.size() && i == temp.get(j).getDate()) {
                list.add(temp.get(j));
                j++;
            } else {
                StepCountModel model = new StepCountModel();
                model.setStep(0);
                model.setDate(i);
                model.setMonth(0);
                model.setYear(0);
                model.setCalorie("0");
                model.setDistance("0");
                model.setDuration(0);
                model.setTimestemp("0");
                model.setSumcalorie(0);
                list.add(model);
            }
        }
        c.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getMonthDistancedata(String fristdate, String lastdate, int a) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "SELECT " + KEY_STEP_DATE + ", sum (" + KEY_STEP_DISTANCE + ") as total FROM " + TABLE_STEPCOUNT + " where " + KEY_STEP_TIMESTMP + " BETWEEN " + fristdate + " AND " +
                lastdate + " GROUP BY " + KEY_STEP_DATE;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            StepCountModel model = new StepCountModel();

            int sum = c.getInt(c.getColumnIndex("total"));

            model.setDate(c.getInt(iKEY_STEP_DATE));
            model.setSumdistance(sum);
            temp.add(model);
        }

        int j = 0;
        for (int i = 0; i < a; i++) {
            if (j < temp.size() && i == temp.get(j).getDate()) {
                list.add(temp.get(j));
                j++;
            } else {
                StepCountModel model = new StepCountModel();
                model.setStep(0);
                model.setDate(i);
                model.setMonth(0);
                model.setYear(0);
                model.setCalorie("0");
                model.setDistance("0");
                model.setDuration(0);
                model.setTimestemp("0");
                model.setSumdistance(0);
                list.add(model);
            }
        }
        c.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }


    @SuppressLint("Range")
    public int getSumOfStepList(int date, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        int sum;
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
    public ArrayList<StepCountModel> getSumOfStepList(String fristdate, String lastdate) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "select *, sum(" + KEY_STEP_COUNT + ")  as total from " + TABLE_STEPCOUNT + " where " + KEY_STEP_TIMESTMP + " BETWEEN " + fristdate + " AND " +
                lastdate + " GROUP BY " + KEY_STEP_DATE;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getSteplistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    private ArrayList<StepCountModel> getSteplistFromCursor(Cursor c) {
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();
        int sum;
        try {
            int iKEY_STEP_COUNT = c.getColumnIndex(KEY_STEP_COUNT);
            int iKEY_STEP_DATE = c.getColumnIndex(KEY_STEP_DATE);
            int iKEY_STEP_MONTH = c.getColumnIndex(KEY_STEP_MONTH);
            int iKEY_STEP_YEAR = c.getColumnIndex(KEY_STEP_YEAR);
            int iKEY_STEP_CALORIES = c.getColumnIndex(KEY_STEP_CALORIES);
            int iKEY_STEP_DISTANCE = c.getColumnIndex(KEY_STEP_DISTANCE);
            int iKEY_STEP_DURATION = c.getColumnIndex(KEY_STEP_DURATION);
            int iKEY_STEP_TIMESTMP = c.getColumnIndex(KEY_STEP_TIMESTMP);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                StepCountModel model = new StepCountModel();

                sum = c.getInt(c.getColumnIndex("total"));

                model.setStep(c.getInt(iKEY_STEP_COUNT));
                model.setDate(c.getInt(iKEY_STEP_DATE));
                model.setMonth(c.getInt(iKEY_STEP_MONTH));
                model.setYear(c.getInt(iKEY_STEP_YEAR));
                model.setCalorie(c.getString(iKEY_STEP_CALORIES));
                model.setDistance(c.getString(iKEY_STEP_DISTANCE));
                model.setDuration(c.getInt(iKEY_STEP_DURATION));
                model.setTimestemp(c.getString(iKEY_STEP_TIMESTMP));
                model.setSumstep(sum);

                list.add(model);
            }
        } finally {
            if (c != null)
                c.close();
        }
        return list;
    }

    public void DeleteCurrentDayStepCountData(int date, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        int b = db.delete(TABLE_STEPCOUNT, KEY_STEP_DATE + " = ?  AND "
                        + KEY_STEP_MONTH + " =? AND "
                        + KEY_STEP_YEAR + " =?",
                new String[]{String.valueOf(date), String.valueOf(month), String.valueOf(year)});
        db.close();
    }


    public void addWeightData(WeightModel weightModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        Log.e("TAG", "insertdata: ");

        initialValues.put(KEY_WEIGHT_DATE, weightModel.getDate());
        initialValues.put(KEY_WEIGHT_MONTH, weightModel.getMonth());
        initialValues.put(KEY_WEIGHT_YEAR, weightModel.getYear());
        initialValues.put(KEY_WEIGHT_KG, weightModel.getKg());
        initialValues.put(KEY_WEIGHT_TIMESTMP, weightModel.getTimestemp());

        try {
            int i = updateWeightData(weightModel);
            if (i == 0) {
                db.insert(TABLE_WEIGHT, null, initialValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
    }

    private int updateWeightData(WeightModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(TABLE_WEIGHT, getContentValuesForWaterCount(model), KEY_WEIGHT_DATE + "=? ",
                new String[]{String.valueOf(model.getDate())});

    }

    private ContentValues getContentValuesForWaterCount(WeightModel model) {
        ContentValues values = new ContentValues();
        values.put(KEY_WEIGHT_DATE, model.getDate());
        values.put(KEY_WEIGHT_MONTH, model.getMonth());
        values.put(KEY_WEIGHT_YEAR, model.getYear());
        values.put(KEY_WEIGHT_KG, model.getKg());
        values.put(KEY_WEIGHT_TIMESTMP, model.getTimestemp());

        return values;
    }

    public ArrayList<WeightModel> getWeightlist() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.query(TABLE_WEIGHT, getWeightlistColumns(), null,
                null,
                null, null, null, null);

        ArrayList<WeightModel> list = getWeightlistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    private String[] getWeightlistColumns() {
        return new String[]{KEY_WEIGHT_DATE, KEY_WEIGHT_MONTH, KEY_WEIGHT_YEAR, KEY_WEIGHT_KG, KEY_WEIGHT_TIMESTMP};
    }

    private ArrayList<WeightModel> getWeightlistFromCursor(Cursor c) {
        ArrayList<WeightModel> list = new ArrayList<WeightModel>();

        try {
            int iKEY_WEIGHT_DATE = c.getColumnIndex(KEY_WEIGHT_DATE);
            int iKEY_WEIGHT_MONTH = c.getColumnIndex(KEY_WEIGHT_MONTH);
            int iKEY_WEIGHT_YEAR = c.getColumnIndex(KEY_WEIGHT_YEAR);
            int iKEY_WEIGHT_KG = c.getColumnIndex(KEY_WEIGHT_KG);
            int iKEY_WEIGHT_TIMESTMP = c.getColumnIndex(KEY_WEIGHT_TIMESTMP);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                WeightModel model = new WeightModel();

                model.setDate(c.getInt(iKEY_WEIGHT_DATE));
                model.setMonth(c.getInt(iKEY_WEIGHT_MONTH));
                model.setYear(c.getInt(iKEY_WEIGHT_YEAR));
                model.setKg(c.getInt(iKEY_WEIGHT_KG));
                model.setTimestemp(c.getString(iKEY_WEIGHT_TIMESTMP));

                list.add(model);
            }
        } finally {
            if (c != null)
                c.close();
        }
        return list;
    }

    public ArrayList<WeightModel> getCurrentDayWeightlist(int date, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "select * from " + TABLE_WEIGHT + " where " + KEY_WEIGHT_DATE + " = " + date + " AND " + KEY_WEIGHT_MONTH +
                " = " + month + " AND  " + KEY_WEIGHT_YEAR + " = " + year;

//        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<WeightModel> list = getWeightlistFromCursor(c);

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    @SuppressLint("Range")
    public ArrayList<WeightModel> getMonthWeightdata(String fristdate, String lastdate) {
        SQLiteDatabase db = this.getWritableDatabase();

        String s = "SELECT * FROM " + TABLE_WEIGHT + " where " + KEY_WEIGHT_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + KEY_WEIGHT_DATE;

        Log.e("TAG", "getMonthWeightdata: " + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<WeightModel> temp = new ArrayList<WeightModel>();
        ArrayList<WeightModel> list = new ArrayList<WeightModel>();

        int iKEY_WEIGHT_DATE = c.getColumnIndex(KEY_WEIGHT_DATE);
        int iKEY_WEIGHT_MONTH = c.getColumnIndex(KEY_WEIGHT_MONTH);
        int iKEY_WEIGHT_YEAR = c.getColumnIndex(KEY_WEIGHT_YEAR);
        int iKEY_WEIGHT_KG = c.getColumnIndex(KEY_WEIGHT_KG);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            WeightModel model = new WeightModel();

            model.setDate(c.getInt(iKEY_WEIGHT_DATE));
            model.setMonth(c.getInt(iKEY_WEIGHT_MONTH));
            model.setYear(c.getInt(iKEY_WEIGHT_YEAR));
            model.setKg(c.getInt(iKEY_WEIGHT_KG));
            temp.add(model);
        }

        int j = 0;
        int kg = 0;
        for (int i = 0; i < 30; i++) {
            if (j < temp.size() && i == temp.get(j).getDate()) {
                list.add(temp.get(j));
                kg = temp.get(j).getKg();
                j++;
            } else {
                WeightModel model = new WeightModel();
                model.setDate(i);
                model.setMonth(0);
                model.setYear(0);
                model.setTimestemp("0");
                model.setKg(kg);
                list.add(model);
            }
        }

        c.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }


}
