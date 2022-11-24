package com.example.diabye.models.normalization;

public class Result {
    private double insulinDosage;
    private double distance;

    public Result(double distance, double insulinDosage) {
        this.insulinDosage = insulinDosage;
        this.distance = distance;
    }

    public double getInsulinDosage() {
        return insulinDosage;
    }

    public void setInsulinDosage(double insulinDosage) {
        this.insulinDosage = insulinDosage;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
