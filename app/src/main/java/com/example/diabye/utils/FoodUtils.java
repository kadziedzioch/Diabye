package com.example.diabye.utils;

import com.example.diabye.models.Food;

import java.util.List;

public class FoodUtils {

    public static double calculateCalories(List<Food> foodList){
        if(foodList.size()>0){
            double kcal=0;
            for(Food f: foodList){
              kcal+=f.getCalories();
            }
            return kcal;
        }
        else{
            return 0;
        }
    }

    public static double calculateFpu(List<Food> foodList){
        if(foodList!=null){
            if(foodList.size() !=0){
                double FPU = 0.0;
                for(Food f: foodList){
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

    public static double calculateCho(List<Food> foodList){
        if(foodList !=null){
            if(foodList.size() !=0){
                double CHO = 0;
                for(Food f: foodList){
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



}
