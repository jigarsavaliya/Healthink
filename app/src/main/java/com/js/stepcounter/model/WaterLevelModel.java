package com.js.stepcounter.model;

public class WaterLevelModel {

    int date;
    int month;
    int year;
    int Hour;
    int min;
    String unit;
    String Timestemp;
    int sumwater;

    public int getSumwater() {
        return sumwater;
    }

    public void setSumwater(int sumwater) {
        this.sumwater = sumwater;
    }

    public String getTimestemp() {
        return Timestemp;
    }

    public void setTimestemp(String timestemp) {
        Timestemp = timestemp;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return Hour;
    }

    public void setHour(int hour) {
        Hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}


