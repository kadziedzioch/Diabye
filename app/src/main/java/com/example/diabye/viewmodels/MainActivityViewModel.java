package com.example.diabye.viewmodels;

import android.util.Pair;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.MeasurementRepository;
import com.example.diabye.repositories.UserSettingRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivityViewModel extends ViewModel {

    private final UserSettingRepository userSettingRepository;
    private final MeasurementRepository measurementRepository;
    private final LiveData<Boolean> isSavingSuccessful;
    private final LiveData<Boolean> isDeletingSuccessful;
    private final LiveData<String> errorMessage;
    private final LiveData<List<Measurement>> measurements;
    private final LiveData<List<Measurement>> measurementsFromLastThreeMonths;
    private final LiveData<UserSettings> userSettings;
    private final LiveData<List<Food>> foods;
    private final MediatorLiveData<HashMap<String,Double>> hyperAndHypo = new MediatorLiveData<>();
    private final MediatorLiveData<Pair<String,Date>> userIdAndDate = new MediatorLiveData<>();
    private final MediatorLiveData<List<MeasurementWithFoods>> measurementsWithFoods = new MediatorLiveData<>();
    private final MutableLiveData<Date> dateOfMeasurement = new MutableLiveData<>();

    public MainActivityViewModel() {
        userSettingRepository = new UserSettingRepository();
        measurementRepository = new MeasurementRepository();
        isSavingSuccessful = measurementRepository.getIsSavingSuccessful();
        isDeletingSuccessful = measurementRepository.getIsDeletingSuccessful();
        errorMessage = measurementRepository.getErrorMessage();
        userSettings = userSettingRepository.getUserSettings();
        foods = measurementRepository.getFoods();
        dateOfMeasurement.setValue(Calendar.getInstance().getTime());

        measurements = Transformations.switchMap(userIdAndDate, input -> measurementRepository.getMeasurements(input.second,input.first));

        hyperAndHypo.addSource(measurements, mMeasurements -> {
            calculateHyperAndHypo(measurements.getValue(),userSettings.getValue());
        });
        hyperAndHypo.addSource(userSettings, mUserSettings -> calculateHyperAndHypo(measurements.getValue(),userSettings.getValue()));

        userIdAndDate.addSource(userSettings, mUserSettings -> setUserIdAndDate(userSettings.getValue(), dateOfMeasurement.getValue()));
        userIdAndDate.addSource(dateOfMeasurement, mDate -> setUserIdAndDate(userSettings.getValue(), dateOfMeasurement.getValue()));

        measurementsWithFoods.addSource(measurements, mMeasurements -> {
            setMeasurementWithFoodsList(measurements.getValue(),foods.getValue());
        });
        measurementsWithFoods.addSource(foods, mFoods -> {
            setMeasurementWithFoodsList(measurements.getValue(),foods.getValue());
        });

        measurementsFromLastThreeMonths = Transformations.switchMap(userSettings, measurementRepository::getMeasurementsFromLastThreeMonths);

    }

    public LiveData<List<Measurement>> getMeasurementsFromLastThreeMonths() {
        return measurementsFromLastThreeMonths;
    }

    private void setMeasurementWithFoodsList(List<Measurement> mMeasurements, List<Food> mFoods) {
        if(mMeasurements ==null||mFoods==null){
            return;
        }
        List<MeasurementWithFoods> measurementWithFoodsList = new ArrayList<>();
        for(Measurement m: mMeasurements){
            MeasurementWithFoods measurementWithFoods = new MeasurementWithFoods();
            measurementWithFoods.setMeasurement(m);
            List<Food> foodList = new ArrayList<>();
            if(mFoods.size()>0){
                for(Food f: mFoods){
                    if(Objects.equals(f.getMeasurementId(), m.getMeasurementId())){
                        foodList.add(f);
                    }
                }
            }
            measurementWithFoods.setFoodList(foodList);
            measurementWithFoodsList.add(measurementWithFoods);
        }
        measurementsWithFoods.setValue(measurementWithFoodsList);
    }

    public LiveData<List<MeasurementWithFoods>> getMeasurementsWithFoods() {
        return measurementsWithFoods;
    }


    public void setUserIdAndDate(UserSettings mUserSettings, Date mDate){
        if(mUserSettings ==null|| mDate==null){
            return;
        }
        Pair<String,Date> values = new Pair<>(mUserSettings.getUserId(),mDate);
        userIdAndDate.setValue(values);
    }

    public void setDateOfMeasurement(Date date) {
        dateOfMeasurement.setValue(date);
    }

    public LiveData<HashMap<String, Double>> getHyperAndHypo() {
        return hyperAndHypo;
    }


    public LiveData<List<Measurement>> getMeasurements() {
        return measurements;
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

    public LiveData<Boolean> getIsDeletingSuccessful() {
        return isDeletingSuccessful;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void clearIsSavingSuccessful(){
        measurementRepository.clearIsSavingSuccessful();
    }

    public void clearIsDeletingSuccessful(){
        measurementRepository.clearIsDeletingSuccessful();
    }

    public LiveData<UserSettings> getUserSettings(String userId){
        return userSettingRepository.getUserSettings(userId);
    }

    public void saveMeasurements(Measurement measurement, List<Food> foods) {
        measurementRepository.saveMeasurement(measurement,foods);
    }

    public void deleteMeasurementWithFoods(MeasurementWithFoods measurementWithFoods){
        measurementRepository.deleteMeasurementWithFoods(measurementWithFoods);
    }


}
