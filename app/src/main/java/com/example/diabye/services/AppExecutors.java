package com.example.diabye.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static AppExecutors instance;

    public static AppExecutors getInstance(){
        if(instance==null){
            instance = new AppExecutors();
        }
        return instance;
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public ExecutorService getExecutorService(){
        return  executorService;
    }
}
