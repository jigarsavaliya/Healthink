package com.android.stepcounter.model;

public class HeaderModel extends ListEvent {

    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }
}
