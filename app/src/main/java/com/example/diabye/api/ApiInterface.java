package com.example.diabye.api;

import com.example.diabye.models.retrofitModels.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("search/{foodName}")
    Call<ApiResponse> getData(@Path("foodName") String foodName);

}
