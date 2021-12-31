package com.android.stepcounter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

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
    public static final String PREF_APP_KEY_COMBO_DAY_COUNT = "ComboDayCOunt";
    public static final String PREF_APP_KEY_DAILY_REMINDER_TIME = "DailyReminderTime";
    public static final String PREF_APP_KEY_DAILY_REMINDER = "DailyReminder";
    public static final String PREF_APP_KEY_DAILY_REMINDER_DAY = "DailyReminderDay";
    public static final String PREF_APP_KEY_CURRENTDAY_TIMESTAMP = "CurrentDayTimestamp";
    public static final String PREF_APP_KEY_DASHBOARD_COMPONENT = "DashboardComponent";

    public static final String PREF_APP_KEY_WATER_STARTTIME = "WaterReminderStarttime";
    public static final String PREF_APP_KEY_WATER_ENDTIME = "WaterReminderEndStarttime";
    public static final String PREF_APP_KEY_WATER_INTERVAL = "WaterReminderInterval";

    public static final String PREF_APP_KEY_WATER_INDEX_GOAL = "WaterGoalindex";
    public static final String PREF_APP_KEY_WATER_INDEX_DEFULT_CUP = "WaterDefultcupindex";
    public static final String PREF_APP_KEY_WATER_REMINDER = "WaterReminder";
    public static final String PREF_APP_KEY_WATER_GOAL_TARGET = "WaterGoalTarget";
    public static final String PREF_APP_KEY_FIRST_TIME_APP = "FirstTimeInstall";

    public static final String PREF_APP_KEY_LEVEL_COMPLETE = "LevelComplete";
    public static final String PREF_APP_KEY_STEP_SERVICE = "IsStepService";
    public static final String PREF_APP_KEY_STEP_THRESHOLD = "StepThreshold";


    public static final String PREF_APP_KEY_TTYPE = "TargetType";
    public static final String PREF_APP_KEY_TDISTANCE = "TargetDistance";
    public static final String PREF_APP_KEY_TDURATION = "TargetDuration";
    public static final String PREF_APP_KEY_TCALORIES = "TargetCalories";
    public static final String PREF_APP_KEY_TYPE = "StepType";

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
        return preferences.getInt(StorageManager.PREF_APP_KEY_STEP, 6000);
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

    public void setgoalIndex(Integer message) {
        editor.putInt(StorageManager.PREF_APP_KEY_WATER_INDEX_GOAL, message).apply();
    }

    public Integer getgoalIndex() {
        return preferences.getInt(StorageManager.PREF_APP_KEY_WATER_INDEX_GOAL, 1);
    }

    public void setDefultcupIndex(Integer message) {
        editor.putInt(StorageManager.PREF_APP_KEY_WATER_INDEX_DEFULT_CUP, message).apply();
    }

    public Integer getDefultcupIndex() {
        return preferences.getInt(StorageManager.PREF_APP_KEY_WATER_INDEX_DEFULT_CUP, 1);
    }

    public void setReminder(Boolean message) {
        editor.putBoolean(StorageManager.PREF_APP_KEY_WATER_REMINDER, message).apply();
    }

    public Boolean getReminder() {
        return preferences.getBoolean(StorageManager.PREF_APP_KEY_WATER_REMINDER, false);
    }

    public void setFirstTimeInstall(Boolean message) {
        editor.putBoolean(StorageManager.PREF_APP_KEY_FIRST_TIME_APP, message).apply();
    }

    public Boolean getFirstTimeInstall() {
        return preferences.getBoolean(StorageManager.PREF_APP_KEY_FIRST_TIME_APP, true);
    }

    public void setWatergoalTarget(Integer message) {
        editor.putInt(StorageManager.PREF_APP_KEY_WATER_GOAL_TARGET, message).apply();
    }

    public Integer getWatergoalTarget() {
        return preferences.getInt(StorageManager.PREF_APP_KEY_WATER_GOAL_TARGET, 0);
    }

    public void setComboDayCount(Integer message) {
        editor.putInt(StorageManager.PREF_APP_KEY_COMBO_DAY_COUNT, message).apply();
    }

    public Integer getComboDayCount() {
        return preferences.getInt(StorageManager.PREF_APP_KEY_COMBO_DAY_COUNT, 0);
    }

    public void setDailyReminder(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_DAILY_REMINDER_TIME, message).apply();
    }

    public String getDailyReminder() {
        return preferences.getString(StorageManager.PREF_APP_KEY_DAILY_REMINDER_TIME, "9:00 AM");
    }

    public void setDailyReminderFlag(Boolean message) {
        editor.putBoolean(StorageManager.PREF_APP_KEY_DAILY_REMINDER, message).apply();
    }

    public Boolean getDailyReminderFlag() {
        return preferences.getBoolean(StorageManager.PREF_APP_KEY_DAILY_REMINDER, false);
    }

    public void setDailyReminderDay(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_DAILY_REMINDER_DAY, message).apply();
    }

    public String getDailyReminderDay() {
        return preferences.getString(StorageManager.PREF_APP_KEY_DAILY_REMINDER_DAY, "[1,2,3,4,5,6,7]");
    }

    public void setLevelArchivement(Boolean message) {
        editor.putBoolean(StorageManager.PREF_APP_KEY_LEVEL_COMPLETE, message).apply();
    }

    public Boolean getLevelArchivement() {
        return preferences.getBoolean(StorageManager.PREF_APP_KEY_LEVEL_COMPLETE, true);
    }

    public void setCurrentDay(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_CURRENTDAY_TIMESTAMP, message).apply();
    }

    public String getCurrentDay() {
        return preferences.getString(StorageManager.PREF_APP_KEY_CURRENTDAY_TIMESTAMP, "");
    }

    public void setIsStepService(Boolean message) {
        editor.putBoolean(StorageManager.PREF_APP_KEY_STEP_SERVICE, message).apply();
    }

    public Boolean getIsStepService() {
        return preferences.getBoolean(StorageManager.PREF_APP_KEY_STEP_SERVICE, true);
    }

    public void setDashboardComponent(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_DASHBOARD_COMPONENT, message).apply();
    }

    public String getDashboardComponent() {
        return preferences.getString(StorageManager.PREF_APP_KEY_DASHBOARD_COMPONENT, "");
    }

    public void setSetThreshold(Integer message) {
        editor.putInt(StorageManager.PREF_APP_KEY_STEP_THRESHOLD, message).apply();
    }

    public Integer getSetThreshold() {
        return preferences.getInt(StorageManager.PREF_APP_KEY_STEP_THRESHOLD, 50);
    }

    public void setTargetDistance(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_TDISTANCE, message).apply();
    }

    public String getTargetDistance() {
        return preferences.getString(StorageManager.PREF_APP_KEY_TDISTANCE, "2.0");
    }

    public void setTargetDuration(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_TDURATION, message).apply();
    }

    public String getTargetDuration() {
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.MINUTE,20);
        return preferences.getString(StorageManager.PREF_APP_KEY_TDURATION, String.valueOf(calendar.get(Calendar.MINUTE)));
    }

    public void setTargetCalories(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_TCALORIES, message).apply();
    }

    public String getTargetCalories() {
        return preferences.getString(StorageManager.PREF_APP_KEY_TCALORIES, "50");
    }

    public void setTargetType(Integer message) {
        editor.putInt(StorageManager.PREF_APP_KEY_TTYPE, message).apply();
    }

    public Integer getTargetType() {
        return preferences.getInt(StorageManager.PREF_APP_KEY_TTYPE, 0);
    }

    public void setStepType(String message) {
        editor.putString(StorageManager.PREF_APP_KEY_TYPE, message).apply();
    }

    public String getStepType() {
        return preferences.getString(StorageManager.PREF_APP_KEY_TYPE, "Walk");
    }
}