package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.Food;
import com.example.diabye.utils.FoodUtils;

import java.util.ArrayList;

public class NewEntryViewModel extends ViewModel {

    MutableLiveData<ArrayList<Food>> foodList = new MutableLiveData<>();
    MutableLiveData<String> date = new MutableLiveData<>();
    MutableLiveData<String> time = new MutableLiveData<>();


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




}
