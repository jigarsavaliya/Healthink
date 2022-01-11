package com.android.stepcounter.model;

import java.io.Serializable;

public class GpsModel implements Serializable {
    int CurrStep;
    int Second;
    String StepTitle;
    String GoalDuration;
    String CurrDuration;
    String DurationTitle;
    String CurrMile;
    String MileTitle;
    String GoalMile;
    String GoalKcal;
    String CurrKcal;
    String KcalTitle;

    public int getSecond() {
        return Second;
    }

    public void setSecond(int second) {
        Second = second;
    }

    public String getGoalDuration() {
        return GoalDuration;
    }

    public void setGoalDuration(String goalDuration) {
        GoalDuration = goalDuration;
    }

    public String getGoalMile() {
        return GoalMile;
    }

    public void setGoalMile(String goalMile) {
        GoalMile = goalMile;
    }

    public String getGoalKcal() {
        return GoalKcal;
    }

    public void setGoalKcal(String goalKcal) {
        GoalKcal = goalKcal;
    }

    public int getCurrStep() {
        return CurrStep;
    }

    public void setCurrStep(int currStep) {
        CurrStep = currStep;
    }

    public String getStepTitle() {
        return StepTitle;
    }

    public void setStepTitle(String stepTitle) {
        StepTitle = stepTitle;
    }

    public String getCurrDuration() {
        return CurrDuration;
    }

    public void setCurrDuration(String currDuration) {
        CurrDuration = currDuration;
    }

    public String getDurationTitle() {
        return DurationTitle;
    }

    public void setDurationTitle(String durationTitle) {
        DurationTitle = durationTitle;
    }

    public String getCurrMile() {
        return CurrMile;
    }

    public void setCurrMile(String currMile) {
        CurrMile = currMile;
    }

    public String getMileTitle() {
        return MileTitle;
    }

    public void setMileTitle(String mileTitle) {
        MileTitle = mileTitle;
    }

    public String getCurrKcal() {
        return CurrKcal;
    }

    public void setCurrKcal(String currKcal) {
        CurrKcal = currKcal;
    }

    public String getKcalTitle() {
        return KcalTitle;
    }

    public void setKcalTitle(String kcalTitle) {
        KcalTitle = kcalTitle;
    }
}
