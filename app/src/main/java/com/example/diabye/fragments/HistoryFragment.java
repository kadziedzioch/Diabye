package com.example.diabye.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diabye.R;
import com.example.diabye.adapter.CalendarAdapter;
import com.example.diabye.adapter.MeasurementAdapter;
import com.example.diabye.databinding.FragmentHistoryBinding;
import com.example.diabye.listeners.CalendarItemListener;
import com.example.diabye.listeners.RecyclerMeasurementListener;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.utils.CalendarUtils;
import com.example.diabye.viewmodels.MainActivityViewModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HistoryFragment extends Fragment implements CalendarItemListener, View.OnClickListener, RecyclerMeasurementListener {

    private FragmentHistoryBinding binding;
    private RecyclerView recyclerView;
    private MainActivityViewModel mainActivityViewModel;

    public HistoryFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        CalendarUtils.selectedDate = LocalDate.now();
        setWeekView();
        changeDate(CalendarUtils.selectedDate);
        setMeasurementRecyclerView();
        binding.previousWeekButton.setOnClickListener(this);
        binding.nextWeekButton.setOnClickListener(this);

        mainActivityViewModel.getMeasurementsWithFoods()
                .observe(getViewLifecycleOwner(), measurementWithFoods -> {
                    setVisibility(measurementWithFoods);
                    setAdapter(measurementWithFoods);
                });

        mainActivityViewModel.getIsDeletingSuccessful()
                .observe(getViewLifecycleOwner(), isSuccessful -> {
                    if(isSuccessful!=null&& isSuccessful){
                        changeDate(CalendarUtils.selectedDate);
                        mainActivityViewModel.clearIsDeletingSuccessful();
                    }
                });
    }

    private void setAdapter(List<MeasurementWithFoods> measurementWithFoods){
        MeasurementAdapter measurementAdapter = new MeasurementAdapter(measurementWithFoods, HistoryFragment.this);
        recyclerView.setAdapter(measurementAdapter);
    }

    private void setVisibility(List<MeasurementWithFoods> measurementWithFoods){
        if(measurementWithFoods.size()==0){
            binding.noDataTV.setVisibility(View.VISIBLE);
        }
        else{
            binding.noDataTV.setVisibility(View.GONE);
        }
    }

    public void setMeasurementRecyclerView(){
        recyclerView = binding.measurementsRecyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void setWeekView(){
        binding.monthYearTV.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, HistoryFragment.this, requireContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireActivity(), 7);
        binding.calendarRecyclerView.setLayoutManager(layoutManager);
        binding.calendarRecyclerView.setAdapter(calendarAdapter);
    }


    @Override
    public void onCalendarCellClicked(int position, LocalDate localDate) {
        CalendarUtils.selectedDate = localDate;
        setWeekView();
        changeDate(localDate);
    }

    public void changeDate(LocalDate localDate){
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
        mainActivityViewModel.setDateOfMeasurement(date);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.previousWeekButton){
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
            setWeekView();
            changeDate(CalendarUtils.selectedDate);
        }
        if(view.getId()==R.id.nextWeekButton){
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
            setWeekView();
            changeDate(CalendarUtils.selectedDate);
        }
    }

    private void showDialog(MeasurementWithFoods measurementWithFoods){
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    mainActivityViewModel.deleteMeasurementWithFoods(measurementWithFoods);
                    dialog.dismiss();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to delete this measurement?")
                .setPositiveButton("Delete", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }

    @Override
    public void OnMeasurementDeleteClicked(MeasurementWithFoods measurementWithFoods) {
       showDialog(measurementWithFoods);
    }



}