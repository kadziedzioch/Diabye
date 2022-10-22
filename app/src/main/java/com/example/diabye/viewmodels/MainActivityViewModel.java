package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.MeasurementRepository;
import com.example.diabye.repositories.UserSettingRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final UserSettingRepository userSettingRepository;
    private final MeasurementRepository measurementRepository;
    private final LiveData<Boolean> isSavingSuccessful;
    private final LiveData<String> errorMessage;
    private final LiveData<List<Measurement>> measurements;
    private final LiveData<UserSettings> userSettings;
    private final LiveData<List<Food>> foods;
    private final MediatorLiveData<HashMap<String,Double>> hyperAndHypo = new MediatorLiveData<>();


    public MainActivityViewModel() {
        userSettingRepository = new UserSettingRepository();
        measurementRepository = new MeasurementRepository();
        isSavingSuccessful = measurementRepository.getIsSavingSuccessful();
        errorMessage = measurementRepository.getErrorMessage();
        userSettings = userSettingRepository.getUserSettings();
        measurements = measurementRepository.getMeasurements();
        foods = measurementRepository.getFoods();

        hyperAndHypo.addSource(measurements, mMeasurements -> {
            calculateHyperAndHypo(measurements.getValue(),userSettings.getValue());
        });
        hyperAndHypo.addSource(userSettings, mUserSettings -> calculateHyperAndHypo(measurements.getValue(),userSettings.getValue()));

    }

    public LiveData<HashMap<String, Double>> getHyperAndHypo() {
        return hyperAndHypo;
    }

    private void calculateHyperAndHypo(List<Measurement> measurementList, UserSettings mUserSettings) {
        if(mUserSettings ==null|| measurementList==null){
            return;
        }
        double hyper = 0;
        double hypo = 0;
        if(measurementList.size()>0){
            for(Measurement m : measurementList){
                if(m.getSugarLevel()>=mUserSettings.getHyperValue()){
                    hyper++;
                }
                if(m.getSugarLevel()<=mUserSettings.getHypoValue()&& m.getSugarLevel()>0){
                    hypo++;
                }
            }
        }
        HashMap<String,Double> values = new HashMap<>();
        values.put("hyper",hyper);
        values.put("hypo",hypo);
        hyperAndHypo.setValue(values);

    }

    public LiveData<List<Food>> getFoods() {
        return foods;
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
