package com.example.diabye.models.retrofitModels;

public class FoodRetrofit {
    private String item_id;
    private String item_name;
    private String brand_name;
    private double nf_calories;
    private double nf_total_fat;
    private double nf_total_carbohydrate;
    private double nf_protein;
    private double nf_serving_size_qty;
    private String nf_serving_size_unit;
    private double nf_serving_weight_grams;

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public double getNf_calories() {
        return nf_calories;
    }

    public void setNf_calories(double nf_calories) {
        this.nf_calories = nf_calories;
    }

    public double getNf_total_fat() {
        return nf_total_fat;
    }

    public void setNf_total_fat(double nf_total_fat) {
        this.nf_total_fat = nf_total_fat;
    }

    public double getNf_total_carbohydrate() {
        return nf_total_carbohydrate;
    }

    public void setNf_total_carbohydrate(double nf_total_carbohydrate) {
        this.nf_total_carbohydrate = nf_total_carbohydrate;
    }

    public double getNf_protein() {
        return nf_protein;
    }

    public void setNf_protein(double nf_protein) {
        this.nf_protein = nf_protein;
    }

    public double getNf_serving_size_qty() {
        return nf_serving_size_qty;
    }

    public void setNf_serving_size_qty(int nf_serving_size_qty) {
        this.nf_serving_size_qty = nf_serving_size_qty;
    }

    public String getNf_serving_size_unit() {
        return nf_serving_size_unit;
    }

    public void setNf_serving_size_unit(String nf_serving_size_unit) {
        this.nf_serving_size_unit = nf_serving_size_unit;
    }

    public double getNf_serving_weight_grams() {
        return nf_serving_weight_grams;
    }

    public void setNf_serving_weight_grams(int nf_serving_weight_grams) {
        this.nf_serving_weight_grams = nf_serving_weight_grams;
    }
}
