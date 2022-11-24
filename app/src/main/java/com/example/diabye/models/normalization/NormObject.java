package com.example.diabye.models.normalization;

public class NormObject {

//    private double bloodSugar;
//    private double sysPressure;
//    private double diasPressure;
//    private double activity;
//    private double carbs;
//    private double proteins;
//    private double fats;

    private double [] measurementAttributes;
    private double insulinDosage;

    public double[] getMeasurementAttributes() {
        return measurementAttributes;
    }

    public void setMeasurementAttributes(double[] measurementAttributes) {
        this.measurementAttributes = measurementAttributes;
    }

    public double getInsulinDosage() {
        return insulinDosage;
    }

    public void setInsulinDosage(double insulinDosage) {
        this.insulinDosage = insulinDosage;
    }
}
