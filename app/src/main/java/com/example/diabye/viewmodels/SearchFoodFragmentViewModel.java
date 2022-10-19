package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.retrofitModels.FoodRetrofit;
import com.example.diabye.repositories.RetrofitRepository;

import java.util.List;

public class SearchFoodFragmentViewModel extends ViewModel {

    private RetrofitRepository retrofitRepository;
    private MutableLiveData<String> foodName = new MutableLiveData<>();
    private LiveData<List<FoodRetrofit>> foodRetrofitList;

    public SearchFoodFragmentViewModel() {
        retrofitRepository = RetrofitRepository.getInstance();
        foodName.postValue("");
        foodRetrofitList = Transformations.switchMap(foodName, name ->
                retrofitRepository.searchFood(name)
        );
    }

    public void setFoodName(String name){
        foodName.postValue(name);
    }

    public LiveData<List<FoodRetrofit>> getFoodRetrofitList(){
        return foodRetrofitList;
    }
}
