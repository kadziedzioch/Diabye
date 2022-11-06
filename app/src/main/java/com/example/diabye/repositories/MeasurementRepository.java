package com.example.diabye.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
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
    private MutableLiveData<Boolean> isDeletingSuccessful = new MutableLiveData<>();

    private MutableLiveData<List<MeasurementWithFoods>> measurementsWithFoods = new MutableLiveData<>();

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Measurement>> measurements = new MutableLiveData<>();

    private MutableLiveData<List<Measurement>> measurementsWithTimeInterval = new MutableLiveData<>();
    private MutableLiveData<List<Food>> foods = new MutableLiveData<>();
    private FirebaseFirestore mFirestore;
    public MeasurementRepository() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<List<MeasurementWithFoods>> getMeasurementsWithFoods() {
        return measurementsWithFoods;
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

    public LiveData<Boolean> getIsDeletingSuccessful() {
        return isDeletingSuccessful;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Measurement>> getMeasurements(Date startDate,Date endDate,String userId){
        List<Measurement> list = new ArrayList<>();
        mFirestore.collection(Constants.MEASUREMENTS)
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("datetime",startDate)
                .whereLessThan("datetime",endDate)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        Measurement m = queryDocumentSnapshot.toObject(Measurement.class);
                        m.setMeasurementId(queryDocumentSnapshot.getId());
                        list.add(m);
                    }
                    measurementsWithTimeInterval.postValue(list);
                })
                .addOnFailureListener(e -> {
                    measurementsWithTimeInterval.postValue(null);
                });

        return measurementsWithTimeInterval;
    }


    public void getMeasurementsWithTimeInterval(Date startDate,Date endDate,String userId){
        List<Measurement> measurementList = new ArrayList<>();
        mFirestore.collection(Constants.MEASUREMENTS)
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("datetime",startDate)
                .whereLessThan("datetime",endDate)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        Measurement m = queryDocumentSnapshot.toObject(Measurement.class);
                        m.setMeasurementId(queryDocumentSnapshot.getId());
                        measurementList.add(m);
                    }
                    getMeasurementsWithFoodsWithTimeInterval(measurementList);
                });

    }

    private void getMeasurementsWithFoodsWithTimeInterval(List<Measurement> measurementList) {

        List<MeasurementWithFoods> measurementWithFoodsList = new ArrayList<>();
        List<Task<QuerySnapshot>> taskList = new ArrayList<>();
        for(Measurement m:measurementList){
            Task<QuerySnapshot> t = mFirestore.collection(Constants.FOODS)
                    .whereEqualTo("measurementId",m.getMeasurementId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Food> foodList = new ArrayList<>();
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                                Food f = queryDocumentSnapshot.toObject(Food.class);
                                f.setFoodId(queryDocumentSnapshot.getId());
                                foodList.add(f);
                            }
                        }
                        MeasurementWithFoods measurementWithFoods = new MeasurementWithFoods();
                        measurementWithFoods.setMeasurement(m);
                        measurementWithFoods.setFoodList(foodList);
                        measurementWithFoodsList.add(measurementWithFoods);
                    });
            taskList.add(t);
        }

        Task<Void> allTask = Tasks.whenAll(taskList);
        allTask.addOnSuccessListener(unused -> {
            measurementsWithFoods.postValue(measurementWithFoodsList);
        });


    }

    public LiveData<List<Measurement>> getMeasurements(Date date, String userId){
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
                .whereEqualTo("userId", userId)
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

    public void clearIsDeletingSuccessful() {
        isDeletingSuccessful.postValue(null);
    }


    public void deleteMeasurementWithFoods(MeasurementWithFoods measurementWithFoods){
        List<Task<Void>> taskList = new ArrayList<>();
        Task<Void> task = mFirestore.collection(Constants.MEASUREMENTS)
                .document(measurementWithFoods.getMeasurement().getMeasurementId())
                .delete()
                .addOnSuccessListener(unused -> {
                    if(measurementWithFoods.getFoodList().size()>0){
                        for(Food f: measurementWithFoods.getFoodList()){
                            Task<Void> t = mFirestore.collection(Constants.FOODS)
                                    .document(f.getFoodId())
                                    .delete();
                            taskList.add(t);
                        }
                    }
                });

        taskList.add(task);

        Task<Void> allTask = Tasks.whenAll(taskList);
        allTask.addOnSuccessListener(unused -> {
            isDeletingSuccessful.postValue(true);
        });



    }
}
