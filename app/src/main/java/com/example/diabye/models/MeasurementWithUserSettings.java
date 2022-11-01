package com.example.diabye.models;

import java.util.List;

public class MeasurementWithUserSettings {
    private List<Measurement> measurementList;
    private UserSettings userSettings;

    public MeasurementWithUserSettings(List<Measurement> measurementList, UserSettings userSettings) {
        this.measurementList = measurementList;
        this.userSettings = userSettings;
    }

    public List<Measurement> getMeasurementList() {
        return measurementList;
    }

    public void setMeasurementList(List<Measurement> measurementList) {
        this.measurementList = measurementList;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }
}
