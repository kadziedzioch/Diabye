package com.example.diabye.models.retrofitModels;

import java.util.ArrayList;

public class ApiResponse {
    private int total_hits;
    private float max_score;
    private ArrayList<Hit> hits;

    public int getTotal_hits() {
        return total_hits;
    }

    public void setTotal_hits(int total_hits) {
        this.total_hits = total_hits;
    }

    public float getMax_score() {
        return max_score;
    }

    public void setMax_score(float max_score) {
        this.max_score = max_score;
    }

    public ArrayList<Hit> getHits() {
        return hits;
    }

    public void setHits(ArrayList<Hit> hits) {
        this.hits = hits;
    }
}
