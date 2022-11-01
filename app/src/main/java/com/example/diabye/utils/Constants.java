package com.example.diabye.utils;

import com.example.diabye.R;
import com.example.diabye.models.Category;
import com.example.diabye.models.TimeInterval;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static String FOODS = "foods";
    public static String USERS ="users";
    public static String USER_SETTINGS ="user_settings";
    public static String DIABYE_PREFS = "diabye_prefs";
    public static String USER_NAME ="user_name";
    public static String MEASUREMENTS ="measurements";
    public static String USER_ID ="user_id";
    public static String APP_KEY = "97e01e69604321bc7a1e0c575b4a0ba7";
    public static String APP_ID = "a3c43da6";
    public static String API_RESULTS ="0:20";
    public static String BLOOD_SUGAR ="Blood sugar";
    public static String ACTIVITY ="Activity";
    public static String PRESSURE ="Pressure";
    public static String WEEK ="Week";
    public static String MONTH ="Month";
    public static String THREE_MONTHS ="Three Months";
    public static String YEAR ="Year";
    public static String FIELDS ="item_name,brand_name,item_id,nf_calories,nf_calories,nf_serving_size_qty,nf_serving_size_unit,nf_serving_weight_grams,metric_qty,metric_uom,nf_total_fat,nf_protein,nf_total_carbohydrate";

    public static List<Category> getMeasurementCategoryList(){
        List<Category> categories = new ArrayList<>();
        Category c1 = new Category(BLOOD_SUGAR, R.drawable.ic_baseline_opacity_24);
        Category c2 = new Category(ACTIVITY, R.drawable.ic_baseline_directions_run_24);
        Category c3 = new Category(PRESSURE, R.drawable.ic_baseline_biotech_24);
        categories.add(c1);
        categories.add(c2);
        categories.add(c3);
        return categories;
    }

    public static List<TimeInterval> getTimeList(){
        List<TimeInterval> times = new ArrayList<>();
        TimeInterval t1 = new TimeInterval(WEEK,R.drawable.ic_baseline_access_time_24);
        TimeInterval t2 = new TimeInterval(MONTH,R.drawable.ic_baseline_filter_1_24);
        TimeInterval t3 = new TimeInterval(THREE_MONTHS,R.drawable.ic_baseline_filter_3_24);
        TimeInterval t4 = new TimeInterval(YEAR,R.drawable.ic_baseline_calendar_month_24);
        times.add(t1);
        times.add(t2);
        times.add(t3);
        times.add(t4);
        return times;
    }
}
