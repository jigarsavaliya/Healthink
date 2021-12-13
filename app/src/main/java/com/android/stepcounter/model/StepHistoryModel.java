package com.android.stepcounter.model;

public class StepHistoryModel {
    Long firstdate;
    Long lastdate;
    int sumstep=0;

    public Long getFirstdate() {
        return firstdate;
    }

    public void setFirstdate(Long firstdate) {
        this.firstdate = firstdate;
    }

    public Long getLastdate() {
        return lastdate;
    }

    public void setLastdate(Long lastdate) {
        this.lastdate = lastdate;
    }

    public int getSumstep() {
        return sumstep;
    }

    public void setSumstep(int sumstep) {
        this.sumstep = sumstep;
    }
}
