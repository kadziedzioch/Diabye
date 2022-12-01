package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.repositories.MeasurementRepository;
import com.example.diabye.services.AppExecutors;
import com.example.diabye.services.DosageCallback;
import com.example.diabye.services.InsulinDosageService;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class NewEntryViewModel extends ViewModel implements DosageCallback {

    private final MeasurementRepository measurementRepository;
    private final InsulinDosageService insulinDosageService;
    LiveData<List<MeasurementWithFoods>> allMeasurementsWithFoods;
    MutableLiveData<Double> predictedDosage = new MutableLiveData<>();
    MutableLiveData<Exception> exception = new MutableLiveData<>();

    public NewEntryViewModel() {
        ExecutorService executorService = AppExecutors.getInstance().getExecutorService();
        measurementRepository = new MeasurementRepository();
        allMeasurementsWithFoods = measurementRepository.getAllMeasurementsWithFoods();
        insulinDosageService = new InsulinDosageService(executorService);
    }

    public LiveData<Double> getPredictedDosage() {
        return predictedDosage;
    }

    public LiveData<Exception> getException() {
        return exception;
    }

    public void clearPredictedDosage(){
        predictedDosage.postValue(null);
    }

    public void clearException(){
        exception.postValue(null);
    }

    public LiveData<List<MeasurementWithFoods>> getAllMeasurementsWithFoods() {
        return allMeasurementsWithFoods;
    }

    public void searchAllMeasurementsWithFoods(String userId){
        measurementRepository.findAllMeasurements(userId);
    }

    public void predictDosage(List<MeasurementWithFoods> measurementWithFoodsList, double [] query){
        insulinDosageService.predictDosage(measurementWithFoodsList,query,this);
    }


    @Override
    public void onFinish(double dosage) {
        predictedDosage.postValue(dosage);
    }

    @Override
    public void onError(Exception ex) {
        exception.postValue(ex);
    }

}
