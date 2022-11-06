package com.example.diabye.export;

import java.io.File;

public interface ExportCallback {
    void onFinish(File file);
    void onError(Exception exception);
}
