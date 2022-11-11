package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diabye.export.AppExecutors;
import com.example.diabye.export.CsvService;
import com.example.diabye.export.ExportCallback;
import com.example.diabye.export.PdfService;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.MeasurementRepository;
import com.example.diabye.repositories.UserSettingRepository;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ExportFragmentViewModel extends ViewModel implements ExportCallback {

    private final UserSettingRepository userSettingRepository;
    private final MeasurementRepository measurementRepository;
    private final LiveData<UserSettings> userSettings;
    private final MutableLiveData<File> file = new MutableLiveData<>();
    private final MutableLiveData<Exception> exception = new MutableLiveData<>();
    private final PdfService pdfService;
    private final CsvService csvService;
    private final LiveData<List<MeasurementWithFoods>> measurementsWithFoodsList;
    public ExportFragmentViewModel() {
        userSettingRepository = new UserSettingRepository();
        measurementRepository = new MeasurementRepository();
        ExecutorService executorService = AppExecutors.getInstance().getExecutorService();
        pdfService = new PdfService(executorService);
        csvService = new CsvService(executorService);
        measurementsWithFoodsList = measurementRepository.getMeasurementsWithFoods();
        userSettings = userSettingRepository.getUserSettings();
    }

    public LiveData<List<MeasurementWithFoods>> getMeasurementsWithFoodsList() {
        return measurementsWithFoodsList;
    }

    public LiveData<File> getFile() {
        return file;
    }

    public LiveData<Exception> getException() {
        return exception;
    }

    public LiveData<UserSettings> getUserSettings() {
        return userSettings;
    }

    public void searchUserSettings(String userId){
        userSettingRepository.getUserSettings(userId);
    }

    public void searchMeasurementsWithFoods(Date startDate, Date endDate, String userId){
        measurementRepository.getMeasurementsWithTimeInterval(startDate,endDate,userId);
    }


    public void createCsvFile(List<MeasurementWithFoods> measurementWithFoodsList,
                               List<String> categories){
        csvService.createCsvFile( measurementWithFoodsList, categories, this);
    }

    public void createPdfDocument(List<MeasurementWithFoods> measurementWithFoodsList,
                                  List<String> categories, String startDay, String endDay){
        pdfService.createPdfDocument(measurementWithFoodsList, categories, startDay,endDay, this);
    }

    @Override
    public void onFinish(File mFile) {
        file.postValue(mFile);
    }

    @Override
    public void onError(Exception mException) {
        exception.postValue(mException);
    }
}
