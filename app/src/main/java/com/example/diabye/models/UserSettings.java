package com.example.diabye.models;

import com.google.firebase.firestore.Exclude;

public class UserSettings {

    private String userSettingsId;
    private String treatmentType;
    private double lowSugarRangeLevel;
    private double highSugarRangeLevel;
    private double hypoValue;
    private double hyperValue;
    private String userId;


    public UserSettings(double lowSugarRangeLevel, double highSugarRangeLevel, double hypoValue, double hyperValue, String userId) {
        this.lowSugarRangeLevel = lowSugarRangeLevel;
        this.highSugarRangeLevel = highSugarRangeLevel;
        this.hypoValue = hypoValue;
        this.hyperValue = hyperValue;
        this.userId = userId;
    }

    @Exclude
    public String getUserSettingsId() {
        return userSettingsId;
    }

    public void setUserSettingsId(String userSettingsId) {
        this.userSettingsId = userSettingsId;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public double getLowSugarRangeLevel() {
        return lowSugarRangeLevel;
    }

    public void setLowSugarRangeLevel(double lowSugarRangeLevel) {
        this.lowSugarRangeLevel = lowSugarRangeLevel;
    }

    public double getHighSugarRangeLevel() {
        return highSugarRangeLevel;
    }

    public void setHighSugarRangeLevel(double highSugarRangeLevel) {
        this.highSugarRangeLevel = highSugarRangeLevel;
    }

    public double getHypoValue() {
        return hypoValue;
    }

    public void setHypoValue(double hypoValue) {
        this.hypoValue = hypoValue;
    }

    public double getHyperValue() {
        return hyperValue;
    }

    public void setHyperValue(double hyperValue) {
        this.hyperValue = hyperValue;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
