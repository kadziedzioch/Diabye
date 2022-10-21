package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.MeasurementRepository;
import com.example.diabye.repositories.UserSettingRepository;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private UserSettingRepository userSettingRepository;
    private MeasurementRepository measurementRepository;
    private LiveData<Boolean> isSavingSuccessful;
    private LiveData<String> errorMessage;
    public MainActivityViewModel() {
        userSettingRepository = new UserSettingRepository();
        measurementRepository = new MeasurementRepository();
        isSavingSuccessful = measurementRepository.getIsSavingSuccessful();
        errorMessage = measurementRepository.getErrorMessage();
    }

    public LiveData<Boolean> getIsSavingSuccessful() {
        return isSavingSuccessful;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<UserSettings> getUserSettings(String userId){
        return userSettingRepository.getUserSettings(userId);
    }

    public void saveMeasurements(Measurement measurement, List<Food> foods) {
        measurementRepository.saveMeasurement(measurement,foods);
    }
}
