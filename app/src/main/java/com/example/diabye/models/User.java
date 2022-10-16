package com.example.diabye.models;

public class User {

    private String userId;
    private String firstName;
    private String email;
    private int isCompleted = 0;

    public User(String userId, String firstName, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.email = email;
    }

    public User() {
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
