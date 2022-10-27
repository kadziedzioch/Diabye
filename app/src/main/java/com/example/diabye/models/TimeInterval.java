package com.example.diabye.models;

public class TimeInterval {
    private String intervalName;
    private int intervalImageId;

    public TimeInterval(String intervalName, int intervalImageId) {
        this.intervalName = intervalName;
        this.intervalImageId = intervalImageId;
    }

    public String getIntervalName() {
        return intervalName;
    }

    public void setIntervalName(String intervalName) {
        this.intervalName = intervalName;
    }

    public int getIntervalImageId() {
        return intervalImageId;
    }

    public void setIntervalImageId(int intervalImageId) {
        this.intervalImageId = intervalImageId;
    }
}
