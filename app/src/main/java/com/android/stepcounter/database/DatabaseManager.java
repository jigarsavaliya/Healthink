package com.android.stepcounter.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.stepcounter.model.ArchivementModel;
import com.android.stepcounter.model.StepCountModel;
import com.android.stepcounter.model.WaterLevelModel;
import com.android.stepcounter.model.WeightModel;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseManager {
    private static DatabaseManager adapter;
    private DbHelper helper;
    private SQLiteDatabase db;

    // Database Information
    static final String DB_NAME = "StepCounter.db";
    // database version
    static final int DB_VERSION = 2;

    public DatabaseManager(Context context) {
        helper = new DbHelper(context);
    }

    public static void init(Context context) {
        if (adapter == null) {
            adapter = new DatabaseManager(context);
        }
    }

    public static DatabaseManager getInstance() {
        return adapter;
    }

    private class DbHelper extends SQLiteOpenHelper {
        Context mContext;
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


        // Table Name
        public static final String TABLE_ARCHIVEMENT = "Table_Arhivement";
        // Table columns
        public static final String ARCHIVEMENT_ID = "Archivement_id";
        public static final String KEY_ARCHIVEMENT_TYPE = "Type";
        public static final String KEY_ARCHIVEMENT_LABEL = "Label";
        public static final String KEY_ARCHIVEMENT_VALUE = "Value";
        public static final String KEY_ARCHIVEMENT_DESCRIPTION = "Description";
        public static final String KEY_ARCHIVEMENT_COMPLETED_STATUS = "CompleteStatus";
        public static final String KEY_ARCHIVEMENT_COUNT = "Count";

        public static final String WaterTable = "CREATE TABLE " + TABLE_WATER + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_WATER_DATE + " INTEGER,"
                + KEY_WATER_MONTH + " INTEGER,"
                + KEY_WATER_YEAR + " INTEGER,"
                + KEY_WATER_HOURS + " INTEGER,"
                + KEY_WATER_MINITS + " INTEGER,"
                + KEY_WATER_TIMESTMP + " TEXT,"
                + KEY_WATER_ML + " TEXT)";

        public static final String StepTable = "CREATE TABLE " + TABLE_STEPCOUNT + " ("
                + STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_STEP_COUNT + " INTEGER,"
                + KEY_STEP_DATE + " INTEGER,"
                + KEY_STEP_MONTH + " INTEGER,"
                + KEY_STEP_YEAR + " INTEGER,"
                + KEY_STEP_CALORIES + " TEXT,"
                + KEY_STEP_DISTANCE + " TEXT,"
                + KEY_STEP_TIMESTMP + " TEXT,"
                + KEY_STEP_DURATION + " INTEGER)";

        public static final String WeightTable = "CREATE TABLE " + TABLE_WEIGHT + " ("
                + WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_WEIGHT_DATE + " INTEGER,"
                + KEY_WEIGHT_MONTH + " INTEGER,"
                + KEY_WEIGHT_YEAR + " INTEGER,"
                + KEY_WEIGHT_TIMESTMP + " TEXT,"
                + KEY_WEIGHT_KG + " INTEGER)";

        public static final String ArchivementTable = "CREATE TABLE " + TABLE_ARCHIVEMENT + " ("
                + ARCHIVEMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ARCHIVEMENT_TYPE + " TEXT,"
                + KEY_ARCHIVEMENT_LABEL + " TEXT,"
                + KEY_ARCHIVEMENT_VALUE + " INTEGER,"
                + KEY_ARCHIVEMENT_DESCRIPTION + " TEXT,"
                + KEY_ARCHIVEMENT_COMPLETED_STATUS + " INTEGER,"
                + KEY_ARCHIVEMENT_COUNT + " INTEGER)";


        DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(WaterTable);
            sqLiteDatabase.execSQL(StepTable);
            sqLiteDatabase.execSQL(WeightTable);
            sqLiteDatabase.execSQL(ArchivementTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WATER); // drop table ni query ave
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPCOUNT);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ARCHIVEMENT);
            onCreate(sqLiteDatabase);
        }

    }

    public void addWaterData(WaterLevelModel waterlevel) {
        db = helper.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        Log.e("TAG", "insertdata: ");

        initialValues.put(DbHelper.KEY_WATER_DATE, waterlevel.getDate());
        initialValues.put(DbHelper.KEY_WATER_MONTH, waterlevel.getMonth());
        initialValues.put(DbHelper.KEY_WATER_YEAR, waterlevel.getYear());
        initialValues.put(DbHelper.KEY_WATER_HOURS, waterlevel.getHour());
        initialValues.put(DbHelper.KEY_WATER_MINITS, waterlevel.getMin());
        initialValues.put(DbHelper.KEY_WATER_ML, waterlevel.getUnit());
        initialValues.put(DbHelper.KEY_WATER_TIMESTMP, waterlevel.getTimestemp());

        try {
//            int i = updateWaterData(waterlevel);
//            if (i == 0) {
            db.insert(DbHelper.TABLE_WATER, null, initialValues);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
    }

    private int updateWaterData(WaterLevelModel model) {
        return db.update(DbHelper.TABLE_WATER, getContentValuesForWaterCount(model), DbHelper.KEY_WATER_HOURS + "=? ",
                new String[]{String.valueOf(model.getHour())});
    }

    private ContentValues getContentValuesForWaterCount(WaterLevelModel model) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.KEY_WATER_DATE, model.getDate());
        values.put(DbHelper.KEY_WATER_MONTH, model.getMonth());
        values.put(DbHelper.KEY_WATER_YEAR, model.getYear());
        values.put(DbHelper.KEY_WATER_HOURS, model.getHour());
        values.put(DbHelper.KEY_WATER_MINITS, model.getMin());
        values.put(DbHelper.KEY_WATER_ML, model.getUnit());
        values.put(DbHelper.KEY_WATER_TIMESTMP, model.getTimestemp());

        return values;
    }

    public ArrayList<WaterLevelModel> getWatercountlist() {
        db = helper.getReadableDatabase();
        Cursor c = db.query(DbHelper.TABLE_WATER, getWaterlistColumns(), null,
                null,
                null, null, null, null);

        ArrayList<WaterLevelModel> list = getWaterlistFromCursor(c);

        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    private String[] getWaterlistColumns() {
        return new String[]{DbHelper.KEY_WATER_DATE, DbHelper.KEY_WATER_MONTH, DbHelper.KEY_WATER_YEAR, DbHelper.KEY_WATER_HOURS, DbHelper.KEY_WATER_MINITS, DbHelper.KEY_WATER_ML, DbHelper.KEY_WATER_TIMESTMP};
    }

    private ArrayList<WaterLevelModel> getWaterlistFromCursor(Cursor c) {
        ArrayList<WaterLevelModel> list = new ArrayList<WaterLevelModel>();

        try {
            int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_WATER_DATE);
            int iKEY_STEP_MONTH = c.getColumnIndex(DbHelper.KEY_WATER_MONTH);
            int iKEY_STEP_YEAR = c.getColumnIndex(DbHelper.KEY_WATER_YEAR);
            int iKEY_STEP_HOUR = c.getColumnIndex(DbHelper.KEY_WATER_HOURS);
            int iKEY_STEP_MINITS = c.getColumnIndex(DbHelper.KEY_WATER_MINITS);
            int iKEY_STEP_UNIT = c.getColumnIndex(DbHelper.KEY_WATER_ML);
            int iKEY_STEP_TIMESTMP = c.getColumnIndex(DbHelper.KEY_WATER_TIMESTMP);

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
        db = helper.getReadableDatabase();

        String s = "select * from " + DbHelper.TABLE_WATER + " where " + DbHelper.KEY_WATER_DATE + " = " + date + " AND " + DbHelper.KEY_WATER_MONTH +
                " = " + month + " AND  " + DbHelper.KEY_WATER_YEAR + " = " + year;

        Cursor c = db.rawQuery(s, null);

        ArrayList<WaterLevelModel> list = getWaterlistFromCursor(c);

        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    @SuppressLint("Range")
    public ArrayList<WaterLevelModel> getDayWaterdata(int date, int month, int year) {
        db = helper.getReadableDatabase();

        String s = "select " + DbHelper.KEY_WATER_DATE + " ,sum(" + DbHelper.KEY_WATER_ML + ")  as total from " + DbHelper.TABLE_WATER + " where " + DbHelper.KEY_WATER_DATE + " = " + date + " AND " + DbHelper.KEY_WATER_MONTH +
                " = " + month + " AND  " + DbHelper.KEY_WATER_YEAR + " = " + year + " GROUP BY " + DbHelper.KEY_WATER_DATE;

        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<WaterLevelModel> list = new ArrayList<WaterLevelModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_WATER_DATE);
        int sum;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            WaterLevelModel model = new WaterLevelModel();
            sum = c.getInt(c.getColumnIndex("total"));

            model.setDate(c.getInt(iKEY_STEP_DATE));
            model.setSumwater(sum);
            list.add(model);
        }

        c.close();
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<WaterLevelModel> getweekWaterdata(String fristdate, String lastdate) {
        db = helper.getReadableDatabase();

        /*String s = "SELECT " +DbHelper.KEY_WATER_DATE + " , sum(" +DbHelper.KEY_WATER_ML + ") as total FROM " +DbHelper.TABLE_WATER + " where " +DbHelper.KEY_WATER_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " +DbHelper.KEY_WATER_DATE;*/

        String s = "SELECT * , sum(" + DbHelper.KEY_WATER_ML + ") as total FROM " + DbHelper.TABLE_WATER + " where " + DbHelper.KEY_WATER_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + DbHelper.KEY_WATER_DATE;

//        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<WaterLevelModel> temp = new ArrayList<WaterLevelModel>();
        ArrayList<WaterLevelModel> list = new ArrayList<WaterLevelModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_WATER_DATE);
        int iKEY_WATER_MONTH = c.getColumnIndex(DbHelper.KEY_WATER_MONTH);
        int iKEY_WATER_YEAR = c.getColumnIndex(DbHelper.KEY_WATER_YEAR);
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
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<WaterLevelModel> getMonthWaterdata(String fristdate, String lastdate, int a) {
        db = helper.getReadableDatabase();

        String s = "SELECT " + DbHelper.KEY_WATER_DATE + " , sum(" + DbHelper.KEY_WATER_ML + ") as total FROM " + DbHelper.TABLE_WATER + " where " + DbHelper.KEY_WATER_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + DbHelper.KEY_WATER_DATE;

        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<WaterLevelModel> temp = new ArrayList<WaterLevelModel>();
        ArrayList<WaterLevelModel> list = new ArrayList<WaterLevelModel>();

        int iKEY_WATER_DATE = c.getColumnIndex(DbHelper.KEY_WATER_DATE);
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
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    public void DeleteCurrentDayWaterData(int date, int month, int year) {
        db = helper.getWritableDatabase();
        int b = db.delete(DbHelper.TABLE_WATER, DbHelper.KEY_WATER_DATE + " = ?  AND "
                        + DbHelper.KEY_WATER_MONTH + " =? AND "
                        + DbHelper.KEY_WATER_YEAR + " =?",
                new String[]{String.valueOf(date), String.valueOf(month), String.valueOf(year)});
        db.close();
    }

    public void DeletelastWaterData(String time) {
        db = helper.getWritableDatabase();
        int b = db.delete(DbHelper.TABLE_WATER,
                DbHelper.KEY_WATER_TIMESTMP + " =?",
                new String[]{time});
        db.close();
    }
    ///step count

    public void addStepcountData(StepCountModel stepcountModel) {

        db = helper.getWritableDatabase();
        ContentValues initialValues = new ContentValues();

//        Log.e("TAG", "insertdata: ");

        initialValues.put(DbHelper.KEY_STEP_COUNT, stepcountModel.getStep());
        initialValues.put(DbHelper.KEY_STEP_DATE, stepcountModel.getDate());
        initialValues.put(DbHelper.KEY_STEP_MONTH, stepcountModel.getMonth());
        initialValues.put(DbHelper.KEY_STEP_YEAR, stepcountModel.getYear());
        initialValues.put(DbHelper.KEY_STEP_CALORIES, stepcountModel.getCalorie());
        initialValues.put(DbHelper.KEY_STEP_DISTANCE, stepcountModel.getDistance());
        initialValues.put(DbHelper.KEY_STEP_DURATION, stepcountModel.getDuration());
        initialValues.put(DbHelper.KEY_STEP_TIMESTMP, stepcountModel.getTimestemp());

        try {
            int i = updateStepData(stepcountModel);
            if (i == 0) {
                db.insert(DbHelper.TABLE_STEPCOUNT, null, initialValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    private int updateStepData(StepCountModel model) {
        return db.update(DbHelper.TABLE_STEPCOUNT, getContentValuesForStepCount(model), DbHelper.KEY_STEP_DURATION + "=? AND "
                        + DbHelper.KEY_STEP_DATE + " =? AND "
                        + DbHelper.KEY_STEP_MONTH + " =? AND "
                        + DbHelper.KEY_STEP_YEAR + " =?",
                new String[]{String.valueOf(model.getDuration()), String.valueOf(model.getDate()), String.valueOf(model.getMonth()), String.valueOf(model.getYear())});

    }

    private ContentValues getContentValuesForStepCount(StepCountModel model) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.KEY_STEP_COUNT, model.getStep());
        values.put(DbHelper.KEY_STEP_DATE, model.getDate());
        values.put(DbHelper.KEY_STEP_MONTH, model.getMonth());
        values.put(DbHelper.KEY_STEP_YEAR, model.getYear());
        values.put(DbHelper.KEY_STEP_CALORIES, model.getCalorie());
        values.put(DbHelper.KEY_STEP_DISTANCE, model.getDistance());
        values.put(DbHelper.KEY_STEP_DURATION, model.getDuration());
        values.put(DbHelper.KEY_STEP_TIMESTMP, model.getTimestemp());

        return values;
    }

    public ArrayList<StepCountModel> getStepcountlist() {
        db = helper.getReadableDatabase();

        Cursor c = db.query(DbHelper.TABLE_STEPCOUNT, getCategorylistColumns(), null,
                null,
                null, null, null, null);

        ArrayList<StepCountModel> list = getCategorylistFromCursor(c);

        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    private String[] getCategorylistColumns() {
        return new String[]{DbHelper.KEY_STEP_COUNT, DbHelper.KEY_STEP_DATE, DbHelper.KEY_STEP_MONTH, DbHelper.KEY_STEP_YEAR, DbHelper.KEY_STEP_CALORIES, DbHelper.KEY_STEP_DISTANCE, DbHelper.KEY_STEP_DURATION, DbHelper.KEY_STEP_TIMESTMP};
    }

    @SuppressLint("Range")
    private ArrayList<StepCountModel> getCategorylistFromCursor(Cursor c) {
        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();
        try {
            int iKEY_STEP_COUNT = c.getColumnIndex(DbHelper.KEY_STEP_COUNT);
            int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_STEP_DATE);
            int iKEY_STEP_MONTH = c.getColumnIndex(DbHelper.KEY_STEP_MONTH);
            int iKEY_STEP_YEAR = c.getColumnIndex(DbHelper.KEY_STEP_YEAR);
            int iKEY_STEP_CALORIES = c.getColumnIndex(DbHelper.KEY_STEP_CALORIES);
            int iKEY_STEP_DISTANCE = c.getColumnIndex(DbHelper.KEY_STEP_DISTANCE);
            int iKEY_STEP_DURATION = c.getColumnIndex(DbHelper.KEY_STEP_DURATION);
            int iKEY_STEP_TIMESTMP = c.getColumnIndex(DbHelper.KEY_STEP_TIMESTMP);

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

        db = helper.getReadableDatabase();

        String s = "SELECT * , sum(" + DbHelper.KEY_STEP_COUNT + ") as total FROM " + DbHelper.TABLE_STEPCOUNT + " GROUP BY " + DbHelper.KEY_STEP_DATE + "," + DbHelper.KEY_STEP_MONTH + "," + DbHelper.KEY_STEP_YEAR
                + " ORDER BY " + DbHelper.KEY_STEP_TIMESTMP + " ASC";

        Log.e("TAG", "getDaywiseStepdata: " + s);
        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getSteplistFromCursor(c);

        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    public ArrayList<StepCountModel> getCurrentDayStepcountlist(int date, int month, int year) {

        db = helper.getReadableDatabase();

        String s = "select * from " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_DATE + " = " + date + " AND " + DbHelper.KEY_STEP_MONTH +
                " = " + month + " AND  " + DbHelper.KEY_STEP_YEAR + " = " + year + " ORDER BY " + DbHelper.KEY_STEP_DURATION + " ASC ";

        Log.e("TAG", "getCurrentDayStepcountlist: " + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getCategorylistFromCursor(c);

        c.close();
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    public ArrayList<StepCountModel> getCurrentDaySumofStepcountlist(int date, int month, int year) {

        db = helper.getReadableDatabase();

        String s = "select * , sum(" + DbHelper.KEY_STEP_COUNT + ") as total FROM " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_DATE + " = " + date + " AND " + DbHelper.KEY_STEP_MONTH +
                " = " + month + " AND  " + DbHelper.KEY_STEP_YEAR + " = " + year + " ORDER BY " + DbHelper.KEY_STEP_DURATION + " ASC ";

        Log.e("TAG", "getCurrentDayStepcountlist: " + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getSteplistFromCursor(c);

        c.close();
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    public ArrayList<StepCountModel> getYesterDayStepcountlist(int date, int month, int year) {

        db = helper.getReadableDatabase();

        String s = "SELECT * , sum(" + DbHelper.KEY_STEP_COUNT + ") as total FROM " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_DATE + " = " + date + " AND " + DbHelper.KEY_STEP_MONTH +
                " = " + month + " AND  " + DbHelper.KEY_STEP_YEAR + " = " + year + " GROUP BY " + DbHelper.KEY_STEP_DATE;

        Log.e("TAG", "getDaywiseStepdata: " + s);
        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getSteplistFromCursor(c);

        c.close();
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    public ArrayList<StepCountModel> getCurrentDayHoursStepcountlist(int date, int month, int year, int hour) {

        db = helper.getReadableDatabase();

        String s = "select * from " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_DATE + " = " + date + " AND " + DbHelper.KEY_STEP_MONTH +
                " = " + month + " AND  " + DbHelper.KEY_STEP_YEAR + " = " + year + " AND  " + DbHelper.KEY_STEP_DURATION + " = " + hour;

//        Logger.e(s);
        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getHoursSteplistFromCursor(c);
        c.close();
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    private ArrayList<StepCountModel> getHoursSteplistFromCursor(Cursor c) {
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();
        try {
            int iKEY_STEP_COUNT = c.getColumnIndex(DbHelper.KEY_STEP_COUNT);
            int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_STEP_DATE);
            int iKEY_STEP_MONTH = c.getColumnIndex(DbHelper.KEY_STEP_MONTH);
            int iKEY_STEP_YEAR = c.getColumnIndex(DbHelper.KEY_STEP_YEAR);
            int iKEY_STEP_CALORIES = c.getColumnIndex(DbHelper.KEY_STEP_CALORIES);
            int iKEY_STEP_DISTANCE = c.getColumnIndex(DbHelper.KEY_STEP_DISTANCE);
            int iKEY_STEP_DURATION = c.getColumnIndex(DbHelper.KEY_STEP_DURATION);
            int iKEY_STEP_TIMESTMP = c.getColumnIndex(DbHelper.KEY_STEP_TIMESTMP);

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
        db = helper.getReadableDatabase();
        String s = "SELECT * , sum(" + DbHelper.KEY_STEP_COUNT + ") as total FROM " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + DbHelper.KEY_STEP_DATE;

//        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_STEP_DATE);
        int iKEY_STEP_MONTH = c.getColumnIndex(DbHelper.KEY_STEP_MONTH);
        int iKEY_STEP_YEAR = c.getColumnIndex(DbHelper.KEY_STEP_YEAR);
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
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getweekCaloriesdata(String fristdate, String lastdate) {

        db = helper.getReadableDatabase();

        String s = "SELECT *, sum(" + DbHelper.KEY_STEP_CALORIES + ") as total FROM " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_TIMESTMP + " BETWEEN " + fristdate + " AND " +
                lastdate + " GROUP BY " + DbHelper.KEY_STEP_DATE;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_STEP_DATE);
        int iKEY_STEP_MONTH = c.getColumnIndex(DbHelper.KEY_STEP_MONTH);
        int iKEY_STEP_YEAR = c.getColumnIndex(DbHelper.KEY_STEP_YEAR);
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
        db.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getweekDistancedata(String fristdate, String lastdate) {

        db = helper.getReadableDatabase();

        String s = "SELECT *, sum (" + DbHelper.KEY_STEP_DISTANCE + ") as total FROM " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_TIMESTMP + " BETWEEN " + fristdate + " AND " +
                lastdate + " GROUP BY " + DbHelper.KEY_STEP_DATE;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_STEP_DATE);
        int iKEY_STEP_MONTH = c.getColumnIndex(DbHelper.KEY_STEP_MONTH);
        int iKEY_STEP_YEAR = c.getColumnIndex(DbHelper.KEY_STEP_YEAR);

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
        db.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getMonthstepdata(String fristdate, String lastdate, int a) {
        db = helper.getReadableDatabase();

        String s = "SELECT " + DbHelper.KEY_STEP_DATE + " , sum(" + DbHelper.KEY_STEP_COUNT + ") as total FROM " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + DbHelper.KEY_STEP_DATE;

//        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_STEP_DATE);
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
        db.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getMonthCaloriesdata(String fristdate, String lastdate, int a) {

        db = helper.getReadableDatabase();

        String s = "SELECT " + DbHelper.KEY_STEP_DATE + ", sum(" + DbHelper.KEY_STEP_CALORIES + ") as total FROM " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_TIMESTMP + " BETWEEN " + fristdate + " AND " +
                lastdate + " GROUP BY " + DbHelper.KEY_STEP_DATE;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_STEP_DATE);
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
        db.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<StepCountModel> getMonthDistancedata(String fristdate, String lastdate, int a) {

        db = helper.getReadableDatabase();

        String s = "SELECT " + DbHelper.KEY_STEP_DATE + ", sum (" + DbHelper.KEY_STEP_DISTANCE + ") as total FROM " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_TIMESTMP + " BETWEEN " + fristdate + " AND " +
                lastdate + " GROUP BY " + DbHelper.KEY_STEP_DATE;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> temp = new ArrayList<StepCountModel>();
        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();

        int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_STEP_DATE);

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
        db.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }


    @SuppressLint("Range")
    public int getSumOfStepList(int date, int month, int year) {
        db = helper.getReadableDatabase();

        int sum;
        String s = "select sum(" + DbHelper.KEY_STEP_COUNT + ")  as total from " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_DATE + " = " + date + " AND " + DbHelper.KEY_STEP_MONTH +
                " = " + month + " AND  " + DbHelper.KEY_STEP_YEAR + " = " + year;

        Cursor c = db.rawQuery(s, null);

        c.moveToFirst();
        do {
            sum = c.getInt(c.getColumnIndex("total"));
        } while (c.moveToNext());


        c.close();
        db.close();
        return sum;
    }


    @SuppressLint("Range")
    public ArrayList<StepCountModel> getSumOfStepList(String fristdate, String lastdate) {
        db = helper.getReadableDatabase();


        String s = "select *, sum(" + DbHelper.KEY_STEP_COUNT + ")  as total from " + DbHelper.TABLE_STEPCOUNT + " where " + DbHelper.KEY_STEP_TIMESTMP + " BETWEEN " + fristdate + " AND " +
                lastdate + " GROUP BY " + DbHelper.KEY_STEP_DATE;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getSteplistFromCursor(c);
        c.close();
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }


    @SuppressLint("Range")
    public long getTotalDaysCount() {
        db = helper.getReadableDatabase();

        String s = "select count(*)  as total from " + DbHelper.TABLE_STEPCOUNT + " GROUP BY " + DbHelper.KEY_STEP_DATE + " , " + DbHelper.KEY_STEP_MONTH +
                " ,  " + DbHelper.KEY_STEP_YEAR;

//        Logger.e(s);
        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = new ArrayList<StepCountModel>();
        int sum;
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                StepCountModel model = new StepCountModel();

                sum = c.getInt(c.getColumnIndex("total"));

                model.setSumstep(sum);

                list.add(model);
            }
        } finally {
            if (c != null)
                c.close();
        }

        c.close();
        db.close();

        if (list != null && list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }

    @SuppressLint("Range")
    public long getTotalStepCount() {
        db = helper.getReadableDatabase();

        int sum;
        String s = "select sum(" + DbHelper.KEY_STEP_COUNT + ")  as total from " + DbHelper.TABLE_STEPCOUNT;

        Cursor c = db.rawQuery(s, null);

        c.moveToFirst();
        do {
            sum = c.getInt(c.getColumnIndex("total"));
        } while (c.moveToNext());


        c.close();
        db.close();
        return sum;
    }

    @SuppressLint("Range")
    public long getTotalDistanceCount() {
        db = helper.getReadableDatabase();

        int sum;
        String s = "select sum(" + DbHelper.KEY_STEP_DISTANCE + ")  as total from " + DbHelper.TABLE_STEPCOUNT;

        Cursor c = db.rawQuery(s, null);

        c.moveToFirst();
        do {
            sum = c.getInt(c.getColumnIndex("total"));
        } while (c.moveToNext());


        c.close();
        db.close();
        return sum;
    }


    @SuppressLint("Range")
    public ArrayList<StepCountModel> getMaxStepCount() {
        db = helper.getReadableDatabase();

        String s = "select *,sum(" + DbHelper.KEY_STEP_COUNT + ")  as total from " + DbHelper.TABLE_STEPCOUNT + " GROUP BY " + DbHelper.KEY_STEP_DATE + "," +
                DbHelper.KEY_STEP_MONTH + "," + DbHelper.KEY_STEP_YEAR;

        Cursor c = db.rawQuery(s, null);

        ArrayList<StepCountModel> list = getSteplistFromCursor(c);

        db.close();
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
            int iKEY_STEP_COUNT = c.getColumnIndex(DbHelper.KEY_STEP_COUNT);
            int iKEY_STEP_DATE = c.getColumnIndex(DbHelper.KEY_STEP_DATE);
            int iKEY_STEP_MONTH = c.getColumnIndex(DbHelper.KEY_STEP_MONTH);
            int iKEY_STEP_YEAR = c.getColumnIndex(DbHelper.KEY_STEP_YEAR);
            int iKEY_STEP_CALORIES = c.getColumnIndex(DbHelper.KEY_STEP_CALORIES);
            int iKEY_STEP_DISTANCE = c.getColumnIndex(DbHelper.KEY_STEP_DISTANCE);
            int iKEY_STEP_DURATION = c.getColumnIndex(DbHelper.KEY_STEP_DURATION);
            int iKEY_STEP_TIMESTMP = c.getColumnIndex(DbHelper.KEY_STEP_TIMESTMP);

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
        db = helper.getWritableDatabase();

        int b = db.delete(DbHelper.TABLE_STEPCOUNT, DbHelper.KEY_STEP_DATE + " = ?  AND "
                        + DbHelper.KEY_STEP_MONTH + " =? AND "
                        + DbHelper.KEY_STEP_YEAR + " =?",
                new String[]{String.valueOf(date), String.valueOf(month), String.valueOf(year)});
        db.close();
    }

    ///////////////////////////////weight table
    public void addWeightData(WeightModel weightModel) {

        db = helper.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        Log.e("TAG", "insertdata: ");

        initialValues.put(DbHelper.KEY_WEIGHT_DATE, weightModel.getDate());
        initialValues.put(DbHelper.KEY_WEIGHT_MONTH, weightModel.getMonth());
        initialValues.put(DbHelper.KEY_WEIGHT_YEAR, weightModel.getYear());
        initialValues.put(DbHelper.KEY_WEIGHT_KG, weightModel.getKg());
        initialValues.put(DbHelper.KEY_WEIGHT_TIMESTMP, weightModel.getTimestemp());

        try {
            // get data nai thata only insert j thy 6
            int i = updateWeightData(weightModel);
            if (i == 0) {
                db.insert(DbHelper.TABLE_WEIGHT, null, initialValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
    }

    private int updateWeightData(WeightModel model) {

        return db.update(DbHelper.TABLE_WEIGHT, getContentValuesForWaterCount(model), DbHelper.KEY_WEIGHT_DATE + "=? ",
                new String[]{String.valueOf(model.getDate())});

    }

    private ContentValues getContentValuesForWaterCount(WeightModel model) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.KEY_WEIGHT_DATE, model.getDate());
        values.put(DbHelper.KEY_WEIGHT_MONTH, model.getMonth());
        values.put(DbHelper.KEY_WEIGHT_YEAR, model.getYear());
        values.put(DbHelper.KEY_WEIGHT_KG, model.getKg());
        values.put(DbHelper.KEY_WEIGHT_TIMESTMP, model.getTimestemp());

        return values;
    }

    public ArrayList<WeightModel> getWeightlist() {

        db = helper.getReadableDatabase();

        Cursor c = db.query(DbHelper.TABLE_WEIGHT, getWeightlistColumns(), null,
                null,
                null, null, null, null);

        ArrayList<WeightModel> list = getWeightlistFromCursor(c);

        c.close();
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    private String[] getWeightlistColumns() {
        return new String[]{DbHelper.KEY_WEIGHT_DATE, DbHelper.KEY_WEIGHT_MONTH, DbHelper.KEY_WEIGHT_YEAR, DbHelper.KEY_WEIGHT_KG, DbHelper.KEY_WEIGHT_TIMESTMP};
    }

    private ArrayList<WeightModel> getWeightlistFromCursor(Cursor c) {
        ArrayList<WeightModel> list = new ArrayList<WeightModel>();

        try {
            int iKEY_WEIGHT_DATE = c.getColumnIndex(DbHelper.KEY_WEIGHT_DATE);
            int iKEY_WEIGHT_MONTH = c.getColumnIndex(DbHelper.KEY_WEIGHT_MONTH);
            int iKEY_WEIGHT_YEAR = c.getColumnIndex(DbHelper.KEY_WEIGHT_YEAR);
            int iKEY_WEIGHT_KG = c.getColumnIndex(DbHelper.KEY_WEIGHT_KG);
            int iKEY_WEIGHT_TIMESTMP = c.getColumnIndex(DbHelper.KEY_WEIGHT_TIMESTMP);

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

        db = helper.getReadableDatabase();

        String s = "select * from " + DbHelper.TABLE_WEIGHT + " where " + DbHelper.KEY_WEIGHT_DATE + " = " + date + " AND " + DbHelper.KEY_WEIGHT_MONTH +
                " = " + month + " AND  " + DbHelper.KEY_WEIGHT_YEAR + " = " + year;

//        Log.e("list", "" + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<WeightModel> list = getWeightlistFromCursor(c);

        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    @SuppressLint("Range")
    public ArrayList<WeightModel> getMonthWeightdata(String fristdate, String lastdate) {
        db = helper.getReadableDatabase();

        String s = "SELECT * FROM " + DbHelper.TABLE_WEIGHT + " where " + DbHelper.KEY_WEIGHT_TIMESTMP
                + " BETWEEN " + fristdate + " AND " + lastdate + " GROUP BY " + DbHelper.KEY_WEIGHT_DATE;

        Log.e("TAG", "getMonthWeightdata: " + s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<WeightModel> temp = new ArrayList<WeightModel>();
        ArrayList<WeightModel> list = new ArrayList<WeightModel>();

        int iKEY_WEIGHT_DATE = c.getColumnIndex(DbHelper.KEY_WEIGHT_DATE);
        int iKEY_WEIGHT_MONTH = c.getColumnIndex(DbHelper.KEY_WEIGHT_MONTH);
        int iKEY_WEIGHT_YEAR = c.getColumnIndex(DbHelper.KEY_WEIGHT_YEAR);
        int iKEY_WEIGHT_KG = c.getColumnIndex(DbHelper.KEY_WEIGHT_KG);

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
        db.close();
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    public void addArchivementData(ArrayList<ArchivementModel> archivementModels) {
        db = helper.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        Log.e("TAG", "insertdata: ");

        for (int i = 0; i < archivementModels.size(); i++) {
            ArchivementModel archivementModel = archivementModels.get(i);

            initialValues.put(DbHelper.KEY_ARCHIVEMENT_TYPE, archivementModel.getType());
            initialValues.put(DbHelper.KEY_ARCHIVEMENT_LABEL, archivementModel.getLabel());
            initialValues.put(DbHelper.KEY_ARCHIVEMENT_VALUE, archivementModel.getValue());
            initialValues.put(DbHelper.KEY_ARCHIVEMENT_DESCRIPTION, archivementModel.getDescription());
            initialValues.put(DbHelper.KEY_ARCHIVEMENT_COMPLETED_STATUS, archivementModel.isCompeleteStatus());
            initialValues.put(DbHelper.KEY_ARCHIVEMENT_COUNT, archivementModel.getCount());

            try {
                db.insert(DbHelper.TABLE_ARCHIVEMENT, null, initialValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        db.close();
    }

    @SuppressLint("Range")
    public long getValueofTypeWiseData(String label) {
        db = helper.getReadableDatabase();

        int sum;
        String s = "select min(" + DbHelper.KEY_ARCHIVEMENT_VALUE + ")  as total from " + DbHelper.TABLE_ARCHIVEMENT
                + " where " + DbHelper.KEY_ARCHIVEMENT_TYPE + " = \"" + label + "\" AND " + DbHelper.KEY_ARCHIVEMENT_COUNT + " = 0";
//        Logger.e(s);
        Cursor c = db.rawQuery(s, null);

        c.moveToFirst();
        do {
            sum = c.getInt(c.getColumnIndex("total"));
        } while (c.moveToNext());


        c.close();
        db.close();
        return sum;
    }

    @SuppressLint("Range")
    public long getValueTypeWiseData(String label) {
        db = helper.getReadableDatabase();

        int sum;
        String s = "select min(" + DbHelper.KEY_ARCHIVEMENT_VALUE + ")  as total from " + DbHelper.TABLE_ARCHIVEMENT
                + " where " + DbHelper.KEY_ARCHIVEMENT_TYPE + " = \"" + label + "\" AND " + DbHelper.KEY_ARCHIVEMENT_COMPLETED_STATUS + " = 0";
//        Logger.e(s);
        Cursor c = db.rawQuery(s, null);

        c.moveToFirst();
        do {
            sum = c.getInt(c.getColumnIndex("total"));
        } while (c.moveToNext());


        c.close();
        db.close();
        return sum;
    }


    public ArrayList<ArchivementModel> getArchivementlist(String label) {

        db = helper.getReadableDatabase();

        String s = "select * from " + DbHelper.TABLE_ARCHIVEMENT + " where " + DbHelper.KEY_ARCHIVEMENT_TYPE + " = \"" + label + "\" ";

//        Logger.e(s);

        Cursor c = db.rawQuery(s, null);

        ArrayList<ArchivementModel> list = getArchivementFromCursor(c);
        db.close();

        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    private ArrayList<ArchivementModel> getArchivementFromCursor(Cursor c) {
        ArrayList<ArchivementModel> list = new ArrayList<ArchivementModel>();

        try {
            int iKEY_ARCHIVEMENT_TYPE = c.getColumnIndex(DbHelper.KEY_ARCHIVEMENT_TYPE);
            int iKEY_ARCHIVEMENT_LABEL = c.getColumnIndex(DbHelper.KEY_ARCHIVEMENT_LABEL);
            int iKEY_ARCHIVEMENT_VALUE = c.getColumnIndex(DbHelper.KEY_ARCHIVEMENT_VALUE);
            int iKEY_ARCHIVEMENT_DESCRIPTION = c.getColumnIndex(DbHelper.KEY_ARCHIVEMENT_DESCRIPTION);
            int iKEY_ARCHIVEMENT_COMPLETED_STATUS = c.getColumnIndex(DbHelper.KEY_ARCHIVEMENT_COMPLETED_STATUS);
            int iKEY_ARCHIVEMENT_COUNT = c.getColumnIndex(DbHelper.KEY_ARCHIVEMENT_COUNT);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ArchivementModel model = new ArchivementModel();

                model.setType(c.getString(iKEY_ARCHIVEMENT_TYPE));
                model.setLabel(c.getString(iKEY_ARCHIVEMENT_LABEL));
                model.setValue(c.getInt(iKEY_ARCHIVEMENT_VALUE));
                model.setDescription(c.getString(iKEY_ARCHIVEMENT_DESCRIPTION));
                if (c.getInt(iKEY_ARCHIVEMENT_COMPLETED_STATUS) == 1) {
                    model.setCompeleteStatus(true);
                } else {
                    model.setCompeleteStatus(false);
                }
                model.setCount(c.getInt(iKEY_ARCHIVEMENT_COUNT));

                list.add(model);
            }
        } finally {
            if (c != null)
                c.close();
        }
        return list;
    }

    public int updateArchivementDailyStep(ArchivementModel model) {
        db = helper.getWritableDatabase();
        int a;
        a = db.update(DbHelper.TABLE_ARCHIVEMENT, getContentValuesArchivementDailyStep(model), DbHelper.KEY_ARCHIVEMENT_VALUE + "=? ",
                new String[]{String.valueOf(model.getValue())});
        db.close();
        return a;
    }

    public ContentValues getContentValuesArchivementDailyStep(ArchivementModel model) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.KEY_ARCHIVEMENT_COUNT, model.getCount() + 1);
        values.put(DbHelper.KEY_ARCHIVEMENT_COMPLETED_STATUS, model.isCompeleteStatus());

        return values;
    }

    public int updateArchivementTotalDistance(ArchivementModel model) {
        db = helper.getWritableDatabase();
        int a;
        a = db.update(DbHelper.TABLE_ARCHIVEMENT, getContentValuesArchivementTotalDistance(model), DbHelper.KEY_ARCHIVEMENT_VALUE + "=? AND "
                        + DbHelper.KEY_ARCHIVEMENT_TYPE + " =? ",
                new String[]{String.valueOf(model.getValue()), model.getType()});
        db.close();
        return a;
    }

    public ContentValues getContentValuesArchivementTotalDistance(ArchivementModel model) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.KEY_ARCHIVEMENT_COMPLETED_STATUS, 1);

        return values;
    }

}
