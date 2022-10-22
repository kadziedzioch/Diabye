package com.example.diabye.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.sql.Timestamp;
import java.util.Date;

public class Measurement {
    private String measurementId;
    @ServerTimestamp
    private Date datetime;
    private double sugarLevel;
    private double mealInsulin;
    private double corrInsulin;
    private double tempBolus;
    private double tempBolusTime;
    private double longInsulin;
    private double activity;
    private String userId;
    private double sysPressure;
    private double diasPressure;

    public Measurement() {
    }

    @Exclude
    public String getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(String measurementId) {
        this.measurementId = measurementId;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public double getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(double sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public double getMealInsulin() {
        return mealInsulin;
    }

    public void setMealInsulin(double mealInsulin) {
        this.mealInsulin = mealInsulin;
    }

    public double getCorrInsulin() {
        return corrInsulin;
    }

    public void setCorrInsulin(double corrInsulin) {
        this.corrInsulin = corrInsulin;
    }

    public double getTempBolus() {
        return tempBolus;
    }

    public void setTempBolus(double tempBolus) {
        this.tempBolus = tempBolus;
    }

    public double getTempBolusTime() {
        return tempBolusTime;
    }

    public void setTempBolusTime(double tempBolusTime) {
        this.tempBolusTime = tempBolusTime;
    }

    public double getLongInsulin() {
        return longInsulin;
    }

    public void setLongInsulin(double longInsulin) {
        this.longInsulin = longInsulin;
    }

    public double getActivity() {
        return activity;
    }

    public void setActivity(double activity) {
        this.activity = activity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getSysPressure() {
        return sysPressure;
    }

    public void setSysPressure(double sysPressure) {
        this.sysPressure = sysPressure;
    }

    public double getDiasPressure() {
        return diasPressure;
    }

    public void setDiasPressure(double diasPressure) {
        this.diasPressure = diasPressure;
    }
}
