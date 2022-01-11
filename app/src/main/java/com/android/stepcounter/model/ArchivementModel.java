package com.android.stepcounter.model;

import com.google.gson.annotations.SerializedName;

public class ArchivementModel {
    @SerializedName("Type")
    private String type;
    @SerializedName("Label")
    private String label;
    @SerializedName("Value")
    private long value;
    @SerializedName("Description")
    private String description;
    @SerializedName("CompeleteStatus")
    private boolean compeleteStatus;

    @SerializedName("ReminderStatus")
    private boolean ReminderStatus;

    @SerializedName("Count")
    private long count;

    public boolean isReminderStatus() {
        return ReminderStatus;
    }

    public void setReminderStatus(boolean reminderStatus) {
        ReminderStatus = reminderStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompeleteStatus() {
        return compeleteStatus;
    }

    public void setCompeleteStatus(boolean compeleteStatus) {
        this.compeleteStatus = compeleteStatus;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}