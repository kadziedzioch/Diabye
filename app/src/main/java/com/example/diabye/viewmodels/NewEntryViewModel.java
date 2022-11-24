package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.Food;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.repositories.MeasurementRepository;
import com.example.diabye.services.AppExecutors;
import com.example.diabye.services.DosageCallback;
import com.example.diabye.services.InsulinDosageService;
import com.example.diabye.utils.FoodUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class NewEntryViewModel extends ViewModel implements DosageCallback {

    MutableLiveData<ArrayList<Food>> foodList = new MutableLiveData<>();
    MutableLiveData<String> date = new MutableLiveData<>();
    MutableLiveData<String> time = new MutableLiveData<>();
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

    public void addFoodToList(Food food){
        ArrayList<Food> foods = new ArrayList<>();
        if(foodList.getValue()!=null){
            if(foodList.getValue().size()>0){
                foods.addAll(foodList.getValue());
            }
        }
        foods.add(food);
        foodList.postValue(foods);
    }


    public LiveData<ArrayList<Food>> getFoodList(){
        return foodList;
    }
    public LiveData<String> getDate(){
        return date;
    }
    public LiveData<String> getTime(){
        return time;
    }

    public void addDateAndTime(String date, String time){
        this.date.postValue(date);
        this.time.postValue(time);
    }

    public void clearTime(){
        this.time.postValue("");
    }
    public void clearDate(){
        this.date.postValue("");
    }

    public void deleteFood(Food food){
        ArrayList<Food> foods = new ArrayList<>();
        if(foodList.getValue()!=null){
            if(foodList.getValue().size()>0){
                foods.addAll(foodList.getValue());
            }
        }
        foods.remove(food);
        foodList.postValue(foods);
    }

    public double calculateCHO(){
        if(foodList.getValue()!=null){
            return FoodUtils.calculateCho(foodList.getValue());
        }
        else{
            return 0.0;
        }
    }

    public double calculateFPU(){
        if(foodList.getValue()!=null){
            return FoodUtils.calculateFpu(foodList.getValue());
        }
        else{
            return 0.0;
        }
    }

    public double calculateCalories(){

        if(foodList.getValue()!=null){
            return FoodUtils.calculateCalories(foodList.getValue());
        }
        else{
            return 0.0;
        }
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
