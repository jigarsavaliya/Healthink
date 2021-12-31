package com.android.stepcounter.model;

public class GpsTrackerModel {
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
