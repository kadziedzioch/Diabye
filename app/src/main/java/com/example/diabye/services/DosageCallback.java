package com.example.diabye.services;

import java.io.File;

public interface DosageCallback {
    void onFinish(double dosage);
    void onError(Exception exception);
}
