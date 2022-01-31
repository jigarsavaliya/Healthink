package com.android.stepcounter.model;

public class GpsTrackerModel {
    int id;
    String type;
    String Action;
    String goal;
    String distance;
    String duration;
    Integer calories;
    Integer step;
    String slatitude;
    String slogtitude;
    String elatitude;
    String elongtitude;

    int date;
    int month;
    int year;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getSlatitude() {
        return slatitude;
    }

    public void setSlatitude(String slatitude) {
        this.slatitude = slatitude;
    }

    public String getSlogtitude() {
        return slogtitude;
    }

    public void setSlogtitude(String slogtitude) {
        this.slogtitude = slogtitude;
    }

    public String getElatitude() {
        return elatitude;
    }

    public void setElatitude(String elatitude) {
        this.elatitude = elatitude;
    }

    public String getElongtitude() {
        return elongtitude;
    }

    public void setElongtitude(String elongtitude) {
        this.elongtitude = elongtitude;
    }
}
