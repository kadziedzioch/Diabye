package com.example.diabye.viewmodels;

import android.text.TextUtils;
import android.util.Pair;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.Measurement;
import com.example.diabye.models.MeasurementWithUserSettings;
import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.MeasurementRepository;
import com.example.diabye.repositories.UserSettingRepository;
import com.example.diabye.utils.Constants;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class StatisticsFragmentViewModel extends ViewModel {

    private final MutableLiveData<String> timeInterval = new MutableLiveData<>();
    private final MutableLiveData<String> measurementCategory = new MutableLiveData<>();
    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final LiveData<UserSettings> userSettings;
    private final MediatorLiveData<Pair<Date, Date>> categoryAndInterval = new MediatorLiveData<>();
    private final MediatorLiveData<MeasurementWithUserSettings> measurementsWithUserSettings = new MediatorLiveData<>();
    private final MeasurementRepository measurementRepository;
    private final UserSettingRepository userSettingRepository;
    private final LiveData<List<Measurement>> measurements;

    public StatisticsFragmentViewModel() {
        measurementRepository = new MeasurementRepository();
        userSettingRepository = new UserSettingRepository();
        userSettings = userSettingRepository.getUserSettings();

        measurements = Transformations.switchMap(categoryAndInterval,
                input -> measurementRepository.getMeasurements(input.first,input.second,userId.getValue()));
        categoryAndInterval.addSource(timeInterval,
                s -> setCategoryAndIntervalData(measurementCategory.getValue(),timeInterval.getValue()));
        categoryAndInterval.addSource(measurementCategory,
                s -> setCategoryAndIntervalData(measurementCategory.getValue(),timeInterval.getValue()));

        measurementsWithUserSettings.addSource(userSettings, mUserSettings -> setMeasurementsWithUserSettings(userSettings.getValue(), measurements.getValue()));
        measurementsWithUserSettings.addSource(measurements, mMeasurements -> setMeasurementsWithUserSettings(userSettings.getValue(), measurements.getValue()));
    }

    private void setMeasurementsWithUserSettings(UserSettings mUserSettings, List<Measurement> mMeasurements) {
        if(mUserSettings==null || mMeasurements==null){
            return;
        }
        MeasurementWithUserSettings value = new MeasurementWithUserSettings(mMeasurements,mUserSettings);
        measurementsWithUserSettings.setValue(value);
    }

    private void setCategoryAndIntervalData(String category, String timeInterval) {
        if(TextUtils.isEmpty(category)||TextUtils.isEmpty(timeInterval)){
            return;
        }
        if(Objects.equals(timeInterval, Constants.MONTH)){
            LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate lastDayOfMonth =  LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).plusDays(1);
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Date firstDate = Date.from(firstDayOfMonth.atStartOfDay(defaultZoneId).toInstant());
            Date secondDate = Date.from(lastDayOfMonth.atStartOfDay(defaultZoneId).toInstant());
            Pair<Date,Date> values = new Pair<>(firstDate,secondDate);
            categoryAndInterval.setValue(values);
        }
        else if(Objects.equals(timeInterval, Constants.THREE_MONTHS)){
            LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1).minusMonths(2);
            LocalDate lastDayOfMonth =  LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).plusDays(1);
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Date firstDate = Date.from(firstDayOfMonth.atStartOfDay(defaultZoneId).toInstant());
            Date secondDate = Date.from(lastDayOfMonth.atStartOfDay(defaultZoneId).toInstant());
            Pair<Date,Date> values = new Pair<>(firstDate,secondDate);
            categoryAndInterval.setValue(values);
        }

        else if(Objects.equals(timeInterval, Constants.YEAR)){
            LocalDate firstdayOfYear = LocalDate.now().withDayOfYear(1);
            LocalDate lastDayOfYear =  LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear()).plusDays(1);
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Date firstDate = Date.from(firstdayOfYear.atStartOfDay(defaultZoneId).toInstant());
            Date secondDate = Date.from(lastDayOfYear.atStartOfDay(defaultZoneId).toInstant());
            Pair<Date,Date> values = new Pair<>(firstDate,secondDate);
            categoryAndInterval.setValue(values);
        }
        else if(Objects.equals(timeInterval, Constants.WEEK)){
            LocalDate firstdayOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY).minusDays(7);
            LocalDate lastDayOfWeek =  LocalDate.now().with(DayOfWeek.SUNDAY).plusDays(1);
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Date firstDate = Date.from(firstdayOfWeek.atStartOfDay(defaultZoneId).toInstant());
            Date secondDate = Date.from(lastDayOfWeek.atStartOfDay(defaultZoneId).toInstant());
            Pair<Date,Date> values = new Pair<>(firstDate,secondDate);
            categoryAndInterval.setValue(values);
        }

    }

    public void setUserId(String userId) {
        this.userId.setValue(userId);
        userSettingRepository.getUserSettings(userId);
    }


    public void setTimeInterval(String timeInterval) {
        this.timeInterval.setValue(timeInterval);
    }

    public LiveData<String> getTimeInterval() {
        return timeInterval;
    }

    public LiveData<String> getMeasurementCategory() {
        return measurementCategory;
    }

    public void setMeasurementCategory(String measurementCategory) {
        this.measurementCategory.setValue(measurementCategory);
    }

    public LiveData<MeasurementWithUserSettings> getMeasurementsWithUserSettings() {
        return measurementsWithUserSettings;
    }
}
