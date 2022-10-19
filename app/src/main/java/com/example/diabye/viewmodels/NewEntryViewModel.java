package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.Food;
import com.example.diabye.models.retrofitModels.FoodRetrofit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewEntryViewModel extends ViewModel {

    MutableLiveData<ArrayList<Food>> foodList = new MutableLiveData<>();

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
            if(foodList.getValue().size() !=0){
                double CHO = 0;
                for(Food f: foodList.getValue()){
                    CHO+=(f.getCarbs()/10);
                }
                return CHO;
            }
            else{
                return 0.0;
            }
        }
        else{
            return 0.0;
        }
    }

    public double calculateFPU(){
        if(foodList.getValue()!=null){
            if(foodList.getValue().size() !=0){
                double FPU = 0.0;
                for(Food f: foodList.getValue()){
                    FPU+= (f.getFats()*9);
                    FPU +=(f.getProtein()*4);}
                return FPU/100;
            }
            else{
                return 0.0;
            }
        }
        else{
            return 0.0;
        }
    }

    public double calculateCalories(){

        if(foodList.getValue()!=null){
            if(foodList.getValue().size() !=0){
                double calories = 0.0;
                for(Food f: foodList.getValue()){
                    calories+=f.getCalories();
                }
                return calories;
            }
            else{
                return 0.0;
            }
        }
        else{
            return 0.0;
        }
    }




}
