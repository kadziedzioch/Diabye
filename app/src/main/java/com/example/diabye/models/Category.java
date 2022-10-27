package com.example.diabye.models;

public class Category {

    private String categoryName;
    private int imageId;

    public Category(String categoryName, int imageId) {
        this.categoryName = categoryName;
        this.imageId = imageId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
