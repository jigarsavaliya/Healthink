package com.js.stepcounter.model;

public class WeightModel {
    int date;
    int month;
    int year;
    int kg;
    String Timestemp;

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

    public int getKg() {
        return kg;
    }

    public void setKg(int kg) {
        this.kg = kg;
    }
}
