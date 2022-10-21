package com.example.diabye.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MeasurementRepository {

    private MutableLiveData<Boolean> isSavingSuccessful = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private FirebaseFirestore mFirestore;
    public MeasurementRepository() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Boolean> getIsSavingSuccessful() {
        return isSavingSuccessful;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void saveMeasurement(Measurement measurement, List<Food> foods){
        mFirestore.collection(Constants.MEASUREMENTS)
                .add(measurement)
                .addOnSuccessListener(documentReference -> {
                    if(foods.size()>0){
                        for(Food food: foods){
                            food.setMeasurementId(documentReference.getId());
                            mFirestore.collection(Constants.FOODS)
                                    .add(food)
                                    .addOnSuccessListener(documentReference1 -> {
                                        isSavingSuccessful.postValue(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        errorMessage.postValue(e.getMessage());
                                        isSavingSuccessful.postValue(false);
                                    });
                        }
                    }
                    else{
                        isSavingSuccessful.postValue(true);
                    }

                })
                .addOnFailureListener(e -> {
                    errorMessage.postValue(e.getMessage());
                    isSavingSuccessful.postValue(false);
                });
    }
}
