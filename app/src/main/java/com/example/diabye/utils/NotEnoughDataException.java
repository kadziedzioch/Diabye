package com.example.diabye.utils;

public class NotEnoughDataException extends Exception{
    public NotEnoughDataException(String errorMessage){
        super(errorMessage);
    }
}
