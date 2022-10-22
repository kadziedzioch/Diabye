package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.MeasurementRepository;
import com.example.diabye.repositories.UserSettingRepository;

import java.util.Date;
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

    public void clearIsSavingSuccessful(){
        measurementRepository.clearIsSavingSuccessful();
    }

    public LiveData<UserSettings> getUserSettings(String userId){
        return userSettingRepository.getUserSettings(userId);
    }

    public LiveData<List<Measurement>> getMeasurements(Date date){
        return measurementRepository.getMeasurements(date);
    }

    public void saveMeasurements(Measurement measurement, List<Food> foods) {
        measurementRepository.saveMeasurement(measurement,foods);
    }
}
