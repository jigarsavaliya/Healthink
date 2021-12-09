package com.android.stepcounter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StorageManager {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    static StorageManager mInstance;

    public static final String PREF_APP_KEY_GENDER = "Gender";
    public static final String PREF_APP_KEY_WEIGHT = "Weight";
    public static final String PREF_APP_KEY_HEIGHT = "Height";

    public static final String PREF_APP_KEY_WATER_GOAL = "WaterGoal";
    public static final String PREF_APP_KEY_WATER_CUP = "DefultCup";
    public static final String PREF_APP_KEY_UNIT = "Unit";

    public static final String PREF_APP_KEY_STEP = "StepGoalcount";

    public static final String PREF_APP_KEY_WATER_STARTTIME = "WaterReminderStarttime";
    public static final String PREF_APP_KEY_WATER_ENDTIME = "WaterReminderEndStarttime";
    public static final String PREF_APP_KEY_WATER_INTERVAL = "WaterReminderInterval";

    private float userWeight = constant.DEFAULT_WEIGHT;
    private float userHeight = constant.DEFAULT_HEIGHT;

    public static final String PREF_APP_ISPROFILE = "IsProfile";

    public StorageManager(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        editor.apply();
    }

    public static void init(Context context) {
        if (mInstance == null) mInstance = new StorageManager(context);
    }

    public static StorageManager getInstance() {
        return mInstance;
    }

    public boolean setIsProfile(boolean series) {
        editor.putBoolean(StorageManager.PREF_APP_ISPROFILE, series).apply();
        return series;
    }

    public boolean getIsProfile() {
        return preferences.getBoolean(StorageManager.PREF_APP_ISPROFILE, true);
    }

    public void setGender(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_GENDER, message).apply();
    }

    public String getGender() {
        return preferences.getString(StorageManager.PREF_APP_KEY_GENDER, "male");
    }

    public void setHeight(Float time) {
        editor.putFloat(PREF_APP_KEY_HEIGHT, time).apply();
    }

    public Float getHeight() {
        return preferences.getFloat(PREF_APP_KEY_HEIGHT, userHeight);
    }

    public void setWeight(Float time) {
        editor.putFloat(PREF_APP_KEY_WEIGHT, time).apply();
    }

    public Float getWeight() {
        return preferences.getFloat(PREF_APP_KEY_WEIGHT, userWeight);
    }

    public void setWaterGoal(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_WATER_GOAL, message).apply();
    }

    public String getWaterGoal() {
        return preferences.getString(StorageManager.PREF_APP_KEY_WATER_GOAL, "250 ml");
    }

    public void setWaterCup(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_WATER_CUP, message).apply();
    }

    public String getWaterCup() {
        return preferences.getString(StorageManager.PREF_APP_KEY_WATER_CUP, "100 ml");
    }

    public void setWaterUnit(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_UNIT, message).apply();
    }

    public String getWaterUnit() {
        return preferences.getString(StorageManager.PREF_APP_KEY_UNIT, "ml");
    }

    public void setStepCountGoalUnit(Integer message) {
        editor.putInt(StorageManager.PREF_APP_KEY_STEP, message).apply();
    }

    public Integer getStepCountGoalUnit() {
        return preferences.getInt(StorageManager.PREF_APP_KEY_STEP, 1000);
    }

    public void setWaterReminderStart(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_WATER_STARTTIME, message).apply();
    }

    public String getWaterReminderStart() {
        return preferences.getString(StorageManager.PREF_APP_KEY_WATER_STARTTIME, "9:00 AM");
    }


    public void setWaterReminderEnd(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_WATER_ENDTIME, message).apply();
    }

    public String getWaterReminderEnd() {
        return preferences.getString(StorageManager.PREF_APP_KEY_WATER_ENDTIME, "9:00 PM");
    }

    public void setWaterinterval(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_WATER_INTERVAL, message).apply();
    }

    public String getWaterinterval() {
        return preferences.getString(StorageManager.PREF_APP_KEY_WATER_INTERVAL, "1");
    }
}