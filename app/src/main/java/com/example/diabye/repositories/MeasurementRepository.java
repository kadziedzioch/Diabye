package com.example.diabye.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.models.UserSettings;
import com.example.diabye.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MeasurementRepository {

    private final MutableLiveData<Boolean> isSavingSuccessful = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDeletingSuccessful = new MutableLiveData<>();
    private final MutableLiveData<List<MeasurementWithFoods>> measurementsWithFoods = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Measurement>> measurements = new MutableLiveData<>();
    private final MutableLiveData<List<Measurement>> measurementsWithTimeInterval = new MutableLiveData<>();
    private final MutableLiveData<List<Food>> foods = new MutableLiveData<>();
    private final MutableLiveData<List<Measurement>> measurementsFromThreeMonths = new MutableLiveData<>();
    private final MutableLiveData<List<MeasurementWithFoods>> allMeasurementsWithFoods = new MutableLiveData<>();
    private final FirebaseFirestore mFirestore;

    public MeasurementRepository() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<List<MeasurementWithFoods>> getMeasurementsWithFoods() {
        return measurementsWithFoods;
    }

    public LiveData<List<MeasurementWithFoods>> getAllMeasurementsWithFoods() {
        return allMeasurementsWithFoods;
    }

    public MutableLiveData<List<Food>> getFoods() {
        return foods;
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
            measurementWithFoodsList.sort(Comparator.comparing(measurementWithFoods -> measurementWithFoods.getMeasurement().getDatetime()));
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

    public LiveData<List<Measurement>> getMeasurementsFromLastThreeMonths(UserSettings userSettings){

        Date today = Calendar.getInstance().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date secondDay = cal.getTime();

        cal.setTime(today);
        cal.add(Calendar.MONTH,-3);
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, -1);
        Date firstDay = cal.getTime();

        List<Measurement> measurementList = new ArrayList<>();
        mFirestore.collection(Constants.MEASUREMENTS)
                .whereEqualTo("userId", userSettings.getUserId())
                .whereGreaterThan("datetime", firstDay)
                .whereLessThan("datetime",secondDay)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        Measurement m = queryDocumentSnapshot.toObject(Measurement.class);
                        m.setMeasurementId(queryDocumentSnapshot.getId());
                        measurementList.add(m);
                    }
                    measurementsFromThreeMonths.postValue(measurementList);
                })
                .addOnFailureListener(e -> {
                    measurementsFromThreeMonths.postValue(null);
                });

        return measurementsFromThreeMonths;
    }

    public void findAllMeasurements(String userId){
        List<Measurement> measurementList = new ArrayList<>();
        mFirestore.collection(Constants.MEASUREMENTS)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        Measurement m = queryDocumentSnapshot.toObject(Measurement.class);
                        m.setMeasurementId(queryDocumentSnapshot.getId());
                        measurementList.add(m);
                    }
                    findAllMeasurementsWithFood(measurementList);
                });
    }

    public void findAllMeasurementsWithFood(List<Measurement> measurementList){
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
            allMeasurementsWithFoods.postValue(measurementWithFoodsList);
        });

    }
}
