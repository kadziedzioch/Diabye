package com.example.diabye.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diabye.R;
import com.example.diabye.databinding.FragmentMainBinding;
import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.repositories.SharedPrefRepository;
import com.example.diabye.viewmodels.MainActivityViewModel;
import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private SharedPrefRepository sharedPrefRepository;
    private MainActivityViewModel mainActivityViewModel;

    public MainFragment() {
    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefRepository = new SharedPrefRepository(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater,container,false);
        setUpView();
        SharedPrefRepository sharedPrefRepository = new SharedPrefRepository(requireActivity());
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        Date today = Calendar.getInstance().getTime();
        mainActivityViewModel.setDateOfMeasurement(today);

        mainActivityViewModel.getMeasurements().observe(getViewLifecycleOwner(), measurements -> {
            if(measurements!=null && measurements.size()>0){
                double glucoseValue = calculateMeanSugar(measurements);
                if(glucoseValue!=0){
                    String glucose = String.format(Locale.getDefault(),"%.0f",calculateMeanSugar(measurements));
                    binding.avgGlucoseMainTv.setText(glucose);
                }
                calculateAndSetOtherFields(measurements);
            }
            else{
                binding.bolusMainTv.setText("0");
                binding.activityMainTv.setText("0");
                binding.avgGlucoseMainTv.setText("-");
                binding.pressureMainTv.setText("-");
            }
        });

        mainActivityViewModel.getFoods().observe(getViewLifecycleOwner(), foods -> {
            if(foods!=null&& foods.size()>0){
                calculateAndSetFoodFields(foods);
            }
            else{
                binding.caloriesMainTv.setText("0");
                binding.choMainTv.setText("0");
                binding.fpuMainTv.setText("0");
            }

        });
        mainActivityViewModel.getUserSettings(sharedPrefRepository.getUserId());
        mainActivityViewModel.getHyperAndHypo().observe(getViewLifecycleOwner(), stringDoubleHashMap -> {
            binding.hyperMainTv.setText(String.format(Locale.getDefault(),"%.0f",stringDoubleHashMap.get("hyper")));
            binding.hypoMainTv.setText(String.format(Locale.getDefault(),"%.0f",stringDoubleHashMap.get("hypo")));
        });

        return binding.getRoot();
    }

    private void calculateAndSetOtherFields(List<Measurement> measurements) {
        double sysPressure = 0;
        double pressureCount =0;
        double diasPressure = 0;
        double activity = 0;
        double bolus = 0;
        for(Measurement m: measurements){
            bolus+=m.getMealInsulin();
            bolus+=m.getCorrInsulin();
            activity+=m.getActivity();
            if(m.getSysPressure()!=0){
                sysPressure+=m.getSysPressure();
                diasPressure+=m.getDiasPressure();
                pressureCount++;
            }
        }
        if(pressureCount!=0){
            String pressure = String.format(Locale.getDefault(),"%.0f/",sysPressure/pressureCount)+
                    String.format(Locale.getDefault(),"%.0f",diasPressure/pressureCount);
            binding.pressureMainTv.setText(pressure);
        }
        binding.activityMainTv.setText(String.format(Locale.getDefault(),"%.0f",activity));
        if(bolus!=0){
            binding.bolusMainTv.setText(String.format(Locale.getDefault(),"%.1f",bolus));
        }
        else{
            binding.bolusMainTv.setText("0");
        }


    }


    private void setUpView(){
        binding.addNewEntryButton.setOnClickListener(view1 -> NavHostFragment.findNavController(MainFragment.this)
                .navigate(R.id.action_mainFragment_to_newEntryFragment));
        String name = sharedPrefRepository.getUsername();
        binding.helloTv.setText(String.format("Hello, %s!", name));
        LocalDate date = LocalDate.now();
        binding.dateTodayTv.setText(date.format((DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG))));
    }


    private void calculateAndSetFoodFields(List<Food> foods){
        double calories = 0;
        double cho = 0;
        double fpu = 0;
        for(Food f: foods){
            double mFpu = 0;
            calories+=f.getCalories();
            cho+=(f.getCarbs()/10);
            mFpu+= (f.getFats()*9);
            mFpu +=(f.getProtein()*4);
            fpu+=mFpu/100;
        }
        binding.caloriesMainTv.setText(String.format(Locale.getDefault(),"%.0f",calories));
        binding.choMainTv.setText(String.format(Locale.getDefault(),"%.1f",cho));
        binding.fpuMainTv.setText(String.format(Locale.getDefault(),"%.1f",fpu));
    }

    private double calculateMeanSugar(List<Measurement> measurements){
        double mean =0;
        double count =0;
        for(Measurement m: measurements){
            if(m.getSugarLevel()!=0){
                mean+=m.getSugarLevel();
                count++;
            }
        }
        if(count==0){
            return mean;
        }
        return mean/count;
    }



}