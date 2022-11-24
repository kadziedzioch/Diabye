package com.example.diabye.services;

import java.io.File;

public interface ExportCallback {
    void onFinish(File file);
    void onError(Exception exception);
}
