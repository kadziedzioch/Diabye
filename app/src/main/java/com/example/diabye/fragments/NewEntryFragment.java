package com.example.diabye.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.diabye.R;
import com.example.diabye.adapter.FoodRecyclerViewAdapter;
import com.example.diabye.databinding.FragmentNewEntryBinding;
import com.example.diabye.listeners.RecyclerFoodListener;
import com.example.diabye.models.Food;
import com.example.diabye.viewmodels.NewEntryViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class NewEntryFragment extends Fragment implements RecyclerFoodListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private FragmentNewEntryBinding binding;
    private NewEntryViewModel newEntryViewModel;
    private RecyclerView recyclerView;
    private FoodRecyclerViewAdapter recyclerViewAdapter;
    public NewEntryFragment() {

    }

    public static NewEntryFragment newInstance() {
        NewEntryFragment fragment = new NewEntryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNewEntryBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        recyclerView = binding.recyclerViewNewEntry;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpToolBar();

        binding.addNewFoodButton.setOnClickListener(view12 -> {
            NavHostFragment.findNavController(NewEntryFragment.this)
                    .navigate(R.id.action_newEntryFragment_to_searchFoodFragment);
        });

        binding.addTimeButton.setOnClickListener(this::showTimePickerDialog);
        binding.addDateButton.setOnClickListener(this::showDatePickerDialog);

        newEntryViewModel = new ViewModelProvider(requireActivity()).get(NewEntryViewModel.class);
        newEntryViewModel.getFoodList().observe(getViewLifecycleOwner(), foodRetrofits -> {
            if(foodRetrofits !=null){
                String CHO = String.format(Locale.getDefault(),"%.1f CHO",newEntryViewModel.calculateCHO());
                String FPU = String.format(Locale.getDefault(),"%.1f FPU",newEntryViewModel.calculateFPU());
                String kcal = String.format(Locale.getDefault(),"%.0f kcal",newEntryViewModel.calculateCalories());
                binding.CHOValueTextView.setText(CHO);
                binding.FPUvalueTextView.setText(FPU);
                binding.caloriesValueTextView.setText(kcal);
                recyclerViewAdapter = new FoodRecyclerViewAdapter(foodRetrofits, NewEntryFragment.this);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });
    }

    private void setUpToolBar(){
        Toolbar toolbar = binding.toolbarId;
        toolbar.setNavigationOnClickListener(view1 -> {
            if(newEntryViewModel.getFoodList().getValue() !=null){
                newEntryViewModel.getFoodList().getValue().clear();
            }
            requireActivity().onBackPressed();
        });
    }


    @Override
    public void onDeleteButtonClicked(Food food) {
        newEntryViewModel.deleteFood(food);
    }

    public void showTimePickerDialog(View v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getActivity(),
                R.style.TimePickerTheme,
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getActivity()
        ));
        timePickerDialog.show();
    }

    public void showDatePickerDialog(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                R.style.DatePickerTheme,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String date = LocalDate.of(year,month,day).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));;
        binding.addDateButton.setText(date);
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String time = hourOfDay+":"+minute;
        binding.addTimeButton.setText(time);
    }
}