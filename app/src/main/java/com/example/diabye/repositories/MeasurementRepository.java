package com.example.diabye.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MeasurementRepository {

    private MutableLiveData<Boolean> isSavingSuccessful = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Measurement>> measurements = new MutableLiveData<>();
    private MutableLiveData<List<Food>> foods = new MutableLiveData<>();
    private FirebaseFirestore mFirestore;
    public MeasurementRepository() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Food>> getFoods() {
        return foods;
    }

    public LiveData<List<Measurement>> getMeasurements() {
        return measurements;
    }

    public LiveData<Boolean> getIsSavingSuccessful() {
        return isSavingSuccessful;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }


    public LiveData<List<Measurement>> getMeasurements(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date nextDay = cal.getTime();

        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, -1);
        Date previousDay = cal.getTime();

        List<Measurement> list = new ArrayList<>();
        mFirestore.collection(Constants.MEASUREMENTS)
                .whereGreaterThan("datetime",previousDay)
                .whereLessThan("datetime",nextDay)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        Measurement m = queryDocumentSnapshot.toObject(Measurement.class);
                        m.setMeasurementId(queryDocumentSnapshot.getId());
                        list.add(m);
                    }
                    getFood(list);
                    measurements.postValue(list);
                })
                .addOnFailureListener(e -> {
                    measurements.postValue(null);
                });

        return measurements;
    }

    private void getFood(List<Measurement> measurements){
        List<Food> list = new ArrayList<>();
        List<Task<QuerySnapshot>> taskList = new ArrayList<>();
        for(Measurement m:measurements){
            Task<QuerySnapshot> t = mFirestore.collection(Constants.FOODS)
                    .whereEqualTo("measurementId",m.getMeasurementId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                                Food f = queryDocumentSnapshot.toObject(Food.class);
                                f.setFoodId(queryDocumentSnapshot.getId());
                                list.add(f);

                            }
                        }
                    });
            taskList.add(t);
        }

        Task<Void> allTask = Tasks.whenAll(taskList);
        allTask.addOnSuccessListener(unused -> {
            foods.postValue(list);
        });


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

    public void clearIsSavingSuccessful() {
        isSavingSuccessful.postValue(null);
    }
}
