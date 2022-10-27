package com.example.diabye.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.diabye.R;
import com.example.diabye.adapter.MeasurementSpinnerAdapter;
import com.example.diabye.adapter.TimeIntervalSpinnerAdapter;
import com.example.diabye.databinding.FragmentStatisticsBinding;
import com.example.diabye.utils.Constants;
import com.example.diabye.utils.CustomSpinner;


public class StatisticsFragment extends Fragment implements CustomSpinner.OnSpinnerEventsListener {
    private FragmentStatisticsBinding binding;

    public StatisticsFragment() {

    }


    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MeasurementSpinnerAdapter measurementSpinnerAdapter = new MeasurementSpinnerAdapter(requireActivity(), binding.spinnerMeasurement.getId(),
                Constants.getMeasurementCategoryList());
        binding.spinnerMeasurement.setAdapter(measurementSpinnerAdapter);
        binding.spinnerMeasurement.setSpinnerEventsListener(this);

        TimeIntervalSpinnerAdapter timeIntervalSpinnerAdapter = new TimeIntervalSpinnerAdapter(requireActivity(), binding.spinnerTime.getId(),
                Constants.getTimeList());
        binding.spinnerTime.setAdapter(timeIntervalSpinnerAdapter);
        binding.spinnerTime.setSpinnerEventsListener(this);

    }

    @Override
    public void onPopupWindowOpened(Spinner spinner) {
        if(spinner.getId() == binding.spinnerMeasurement.getId()){
            binding.spinnerMeasurement.setBackground(ContextCompat.getDrawable(requireActivity(),R.drawable.spinner_background));
        }
        else{
            binding.spinnerTime.setBackground(ContextCompat.getDrawable(requireActivity(),R.drawable.spinner_background));
        }

    }

    @Override
    public void onPopupWindowClosed(Spinner spinner) {
        if(spinner.getId() == binding.spinnerMeasurement.getId()){
            binding.spinnerMeasurement.setBackground(ContextCompat.getDrawable(requireActivity(),R.drawable.spinner_down_background));
        }
        else{
            binding.spinnerTime.setBackground(ContextCompat.getDrawable(requireActivity(),R.drawable.spinner_down_background));
        }

    }
}