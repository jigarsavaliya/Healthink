package com.android.stepcounter.model;

public class StepCountModel {
    Integer step;
    Integer date;
    Integer month;
    Integer year;
    String calorie;
    String distance;
    String Timestemp;
    Integer duration;
    Integer MaxStep;
    int sumstep;
    int sumcalorie;
    int sumdistance;

    public Integer getMaxStep() {
        return MaxStep;
    }

    public void setMaxStep(Integer maxStep) {
        MaxStep = maxStep;
    }

    public int getSumdistance() {
        return sumdistance;
    }

    public void setSumdistance(int sumdistance) {
        this.sumdistance = sumdistance;
    }

    public int getSumcalorie() {
        return sumcalorie;
    }

    public void setSumcalorie(int sumcalorie) {
        this.sumcalorie = sumcalorie;
    }

    public int getSumstep() {
        return sumstep;
    }

    public void setSumstep(int sumstep) {
        this.sumstep = sumstep;
    }

    public String getTimestemp() {
        return Timestemp;
    }

    public void setTimestemp(String timestemp) {
        Timestemp = timestemp;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
