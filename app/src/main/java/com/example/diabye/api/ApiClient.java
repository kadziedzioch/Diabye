package com.example.diabye.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.diabye.utils.Constants;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static Retrofit getClient(){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl httpUrl = original.url();
            HttpUrl newHttpUrl = httpUrl.newBuilder()
                    .addQueryParameter("results",Constants.API_RESULTS)
                    .addQueryParameter("fields",Constants.FIELDS)
                    .addQueryParameter("appKey", Constants.APP_KEY)
                    .addQueryParameter("appId",Constants.APP_ID)
                    .build();
            Request.Builder requestBuilder = original.newBuilder().url(newHttpUrl);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }).addInterceptor(logging).build();

        return new Retrofit.Builder()
                .baseUrl("https://api.nutritionix.com/v1_1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


}
