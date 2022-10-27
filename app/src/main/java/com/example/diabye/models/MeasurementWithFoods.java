package com.example.diabye.models;

import java.util.List;

public class MeasurementWithFoods {

    private Measurement measurement;
    private List<Food> foodList;

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
    }
}
