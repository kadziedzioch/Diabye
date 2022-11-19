package com.example.diabye.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.diabye.api.ApiClient;
import com.example.diabye.api.ApiInterface;
import com.example.diabye.models.retrofitModels.ApiResponse;
import com.example.diabye.models.retrofitModels.FoodRetrofit;
import com.example.diabye.models.retrofitModels.Hit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitRepository {

    private final MutableLiveData<List<FoodRetrofit>> data = new MutableLiveData<>();
    private static RetrofitRepository instance;
    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

    public static RetrofitRepository getInstance(){
        if(instance==null){
            instance = new RetrofitRepository();
        }
        return instance;
    }

    public LiveData<List<FoodRetrofit>> searchFood(String foodName){
        apiInterface.getData(foodName).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if(response.body() !=null && response.isSuccessful()){
                    ApiResponse apiResponse = response.body();
                    ArrayList<FoodRetrofit> foodArrayList = new ArrayList<>();
                    for(Hit hit : apiResponse.getHits()){
                        foodArrayList.add(hit.getFields());
                    }
                    data.postValue(foodArrayList);

                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                call.cancel();
            }
        });

        return data;
    }
}
