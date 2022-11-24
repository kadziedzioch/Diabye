package com.example.diabye.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
import com.example.diabye.models.Measurement;
import com.example.diabye.repositories.SharedPrefRepository;
import com.example.diabye.utils.AppUtils;
import com.example.diabye.viewmodels.MainActivityViewModel;
import com.example.diabye.viewmodels.NewEntryViewModel;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class NewEntryFragment extends Fragment implements RecyclerFoodListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private FragmentNewEntryBinding binding;
    private NewEntryViewModel newEntryViewModel;
    private RecyclerView recyclerView;
    private FoodRecyclerViewAdapter recyclerViewAdapter;
    private MainActivityViewModel mainActivityViewModel;
    private SharedPrefRepository sharedPrefRepository;
    private List<Food> foodList;

    public NewEntryFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNewEntryBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        recyclerView = binding.recyclerViewNewEntry;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        newEntryViewModel = new ViewModelProvider(requireActivity()).get(NewEntryViewModel.class);
        binding.predictDosageButton.setOnClickListener(view13 -> {
            if(validateDosageInput() && newEntryViewModel.getFoodList().getValue()!=null) {
                binding.progressBar.setVisibility(View.VISIBLE);
                SharedPrefRepository sharedPrefRepository = new SharedPrefRepository(requireActivity());
                newEntryViewModel.searchAllMeasurementsWithFoods(sharedPrefRepository.getUserId());
            }
        });

        newEntryViewModel.getAllMeasurementsWithFoods().observe(getViewLifecycleOwner(), measurementWithFoodsList -> {
            if(!TextUtils.isEmpty(binding.sugarLevelET.getText().toString().trim())){
                double sugarLevel = Double.parseDouble(binding.sugarLevelET.getText().toString().trim());
                double sysPressure = Double.parseDouble(binding.sysPressureET.getText().toString().trim());
                double diasPressure = Double.parseDouble(binding.diasPressureET.getText().toString().trim());
                double activity = Double.parseDouble(binding.activityET.getText().toString().trim());
                double carbs = 0;
                double proteins = 0;
                double fats = 0;
                if(newEntryViewModel.getFoodList().getValue()!=null){
                    for(Food f: newEntryViewModel.getFoodList().getValue()){
                        carbs+=f.getCarbs();
                        proteins+=f.getProtein();
                        fats+=f.getFats();
                    }
                    double [] query = new double[]{sugarLevel,activity,sysPressure,diasPressure,fats,proteins,carbs};
                    newEntryViewModel.predictDosage(measurementWithFoodsList,query);
                }
            }
        });
        newEntryViewModel.getPredictedDosage().observe(getViewLifecycleOwner(), dosage -> {
            if(dosage!=null){
                binding.progressBar.setVisibility(View.GONE);
                binding.predictedDosageTv.setText(String.format(Locale.getDefault(),"Predicted meal insulin dosage: %.1f I.U.", dosage));
            }
        });
        newEntryViewModel.getException().observe(getViewLifecycleOwner(), exception -> {
            if(exception!=null && !TextUtils.isEmpty(exception.getMessage())){
                binding.progressBar.setVisibility(View.GONE);
                AppUtils.showMessage(requireActivity(),binding.saveMeasurmentsButton,exception.getMessage(),true);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpToolBar();
        foodList = new ArrayList<>();

        binding.addTimeButton.setOnClickListener(this::showTimePickerDialog);
        binding.addDateButton.setOnClickListener(this::showDatePickerDialog);

        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        sharedPrefRepository = new SharedPrefRepository(requireActivity());

        newEntryViewModel.getFoodList().observe(getViewLifecycleOwner(), foodRetrofits -> {
            if(foodRetrofits !=null){
                if(foodList!=null){
                    foodList.clear();
                }
                foodList.addAll(foodRetrofits);
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
        setUpEditTexts();
        binding.addNewFoodButton.setOnClickListener(view12 -> {
            newEntryViewModel.addDateAndTime(binding.addDateButton.getText().toString(),
                    binding.addTimeButton.getText().toString());
            if(newEntryViewModel.getAllMeasurementsWithFoods().getValue() !=null){
                newEntryViewModel.getAllMeasurementsWithFoods().getValue().clear();
            }
            if(newEntryViewModel.getPredictedDosage().getValue()!=null){
                newEntryViewModel.clearPredictedDosage();
            }
            if(newEntryViewModel.getException().getValue()!=null){
                newEntryViewModel.clearException();
            }
            NavHostFragment.findNavController(NewEntryFragment.this)
                    .navigate(R.id.action_newEntryFragment_to_searchFoodFragment);
        });


        mainActivityViewModel.getUserSettings(sharedPrefRepository.getUserId()).observe(getViewLifecycleOwner(), userSettings -> {
            if(userSettings!=null){
               switch (userSettings.getTreatmentType()){
                   case "PEN":
                       if (binding.tempBasalLayout.getVisibility() == View.VISIBLE &&
                       binding.tempBasalTimeLayout.getVisibility()==View.VISIBLE)
                       {
                           binding.tempBasalLayout.setVisibility(View.GONE);
                           binding.tempBasalTimeLayout.setVisibility(View.GONE);
                       }
                       binding.longInsulinLayout.setVisibility(View.VISIBLE);
                       break;
                   case "PUMP":
                       if (binding.longInsulinLayout.getVisibility() == View.VISIBLE) {
                           binding.longInsulinLayout.setVisibility(View.GONE);
                       }
                       binding.tempBasalLayout.setVisibility(View.VISIBLE);
                       binding.tempBasalTimeLayout.setVisibility(View.VISIBLE);
                       break;
               }
            }
        });

        binding.saveMeasurmentsButton.setOnClickListener(view1 -> {
            if(validateInput()){
                saveMeasurements();
            }
        });



        mainActivityViewModel.getIsSavingSuccessful().observe(getViewLifecycleOwner(), isSuccessful -> {
            if(isSuccessful!=null&& isSuccessful){
                if(newEntryViewModel.getFoodList().getValue() !=null){
                    newEntryViewModel.getFoodList().getValue().clear();
                }
                if(newEntryViewModel.getTime().getValue()!=null){
                    newEntryViewModel.clearTime();
                }
                if(newEntryViewModel.getDate().getValue()!=null){
                    newEntryViewModel.clearDate();
                }
                mainActivityViewModel.clearIsSavingSuccessful();
                if(newEntryViewModel.getAllMeasurementsWithFoods().getValue() !=null){
                    newEntryViewModel.getAllMeasurementsWithFoods().getValue().clear();
                }
                if(newEntryViewModel.getPredictedDosage().getValue()!=null){
                    newEntryViewModel.clearPredictedDosage();
                }
                if(newEntryViewModel.getException().getValue()!=null){
                    newEntryViewModel.clearException();
                }
                requireActivity().onBackPressed();
            }
            else if(isSuccessful!=null&&!isSuccessful){
                AppUtils.showMessage(requireActivity(), binding.saveMeasurmentsButton,
                        mainActivityViewModel.getErrorMessage().getValue(),true);
            }
        });


    }

    private boolean validateDosageInput(){
        if(binding.sugarLevelET.getText().toString().equals("") || binding.sugarLevelET.getText()==null){
            AppUtils.showMessage(requireActivity(),binding.addTimeButton,"Enter sugar level!",true);
            return false;
        }
        if(binding.sysPressureET.getText().toString().equals("") || binding.sysPressureET.getText()==null){
            AppUtils.showMessage(requireActivity(),binding.addTimeButton,"Enter systolic pressure!",true);
            return false;
        }
        if(TextUtils.isEmpty(binding.diasPressureET.getText().toString().trim()) || binding.diasPressureET.getText()==null){
            AppUtils.showMessage(requireActivity(),binding.addTimeButton,"Enter diastolic pressure!",true);
            return false;

        }
        if(binding.activityET.getText().toString().equals("") || binding.activityET.getText()==null){
            AppUtils.showMessage(requireActivity(),binding.addTimeButton,"Enter activity!",true);
            return false;
        }
        if(newEntryViewModel.getFoodList().getValue()!=null && newEntryViewModel.getFoodList().getValue().size()==0){
            AppUtils.showMessage(requireActivity(),binding.addTimeButton,"You need to fill what you have eaten",true);
            return false;
        }

        if(newEntryViewModel.getFoodList().getValue()==null){
            AppUtils.showMessage(requireActivity(),binding.addTimeButton,"You need to fill what you have eaten",true);
            return false;
        }
        return true;
    }



    private boolean validateInput() {
        if(binding.tempBasalLayout.getVisibility() == View.VISIBLE){
            if(TextUtils.isEmpty(binding.tempBasalET.getText().toString().trim()) || binding.tempBasalET.getText()==null){
                if(!binding.tempBasalTimeET.getText().toString().equals("") && binding.tempBasalTimeET.getText()!=null){
                    AppUtils.showMessage(requireActivity(),binding.addTimeButton,"Enter temporary basal rate!",true);
                    return false;
                }
            }
            if(TextUtils.isEmpty(binding.tempBasalTimeET.getText().toString().trim()) || binding.tempBasalTimeET.getText()==null){
                if(!binding.tempBasalET.getText().toString().equals("") && binding.tempBasalET.getText()!=null){
                    AppUtils.showMessage(requireActivity(),binding.addTimeButton,"Enter temporary basal time!",true);
                    return false;
                }
            }
        }
        if(TextUtils.isEmpty(binding.diasPressureET.getText().toString().trim()) || binding.diasPressureET.getText()==null){
            if(!TextUtils.isEmpty(binding.sysPressureET.getText().toString()) && binding.sysPressureET.getText()!=null){
                AppUtils.showMessage(requireActivity(),binding.addTimeButton,"Enter diastolic pressure!",true);
                return false;
            }
        }
        if(binding.sysPressureET.getText().toString().equals("") || binding.sysPressureET.getText()==null){
            if(!binding.diasPressureET.getText().toString().equals("") && binding.diasPressureET.getText()!=null){
                AppUtils.showMessage(requireActivity(),binding.addTimeButton,"Enter systolic pressure!",true);
                return false;
            }
        }

        if(newEntryViewModel.getFoodList().getValue()!=null){
            if(TextUtils.isEmpty(binding.sugarLevelET.getText().toString().trim()) && TextUtils.isEmpty(binding.mealinsulinET.getText().toString().trim())
                    && TextUtils.isEmpty(binding.corrInsulinET.getText().toString().trim()) && TextUtils.isEmpty(binding.sysPressureET.getText().toString().trim())
                    && TextUtils.isEmpty(binding.activityET.getText().toString().trim()) && TextUtils.isEmpty(binding.tempBasalET.getText().toString().trim())
                    && TextUtils.isEmpty(binding.longInsulinET.getText().toString().trim()) && newEntryViewModel.getFoodList().getValue().size()==0){
                AppUtils.showMessage(requireActivity(),binding.addTimeButton,"You need to fill at least one field",true);
                return false;
            }
        }
        else{
            if(TextUtils.isEmpty(binding.sugarLevelET.getText().toString().trim()) && TextUtils.isEmpty(binding.mealinsulinET.getText().toString().trim())
                    && TextUtils.isEmpty(binding.corrInsulinET.getText().toString().trim()) && TextUtils.isEmpty(binding.sysPressureET.getText().toString().trim())
                    && TextUtils.isEmpty(binding.activityET.getText().toString().trim()) && TextUtils.isEmpty(binding.CHOValueTextView.getText().toString().trim())
                    && TextUtils.isEmpty(binding.tempBasalET.getText().toString().trim()) && TextUtils.isEmpty(binding.longInsulinET.getText().toString().trim())){
                AppUtils.showMessage(requireActivity(),binding.addTimeButton,"You need to fill at least one field",true);
                return false;
            }
        }
        return true;
    }

    private void setUpEditTexts(){

        if(newEntryViewModel.getDate().getValue()==null || Objects.equals(newEntryViewModel.getDate().getValue(), "")){
            binding.addDateButton.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        else{
            binding.addDateButton.setText(newEntryViewModel.getDate().getValue());
        }
        if(newEntryViewModel.getTime().getValue()==null|| Objects.equals(newEntryViewModel.getTime().getValue(), "")){
            binding.addTimeButton.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        else{
            binding.addTimeButton.setText(newEntryViewModel.getTime().getValue());
        }

    }

    private void setUpToolBar(){
        Toolbar toolbar = binding.toolbarId;
        toolbar.setNavigationOnClickListener(view1 -> {
            if(newEntryViewModel.getFoodList().getValue() !=null){
                newEntryViewModel.getFoodList().getValue().clear();
            }
            if(newEntryViewModel.getTime().getValue()!=null){
                newEntryViewModel.clearTime();
            }
            if(newEntryViewModel.getDate().getValue()!=null){
                newEntryViewModel.clearDate();
            }
            if(newEntryViewModel.getAllMeasurementsWithFoods().getValue() !=null){
                newEntryViewModel.getAllMeasurementsWithFoods().getValue().clear();
            }
            if(newEntryViewModel.getPredictedDosage().getValue()!=null){
                newEntryViewModel.clearPredictedDosage();
            }
            if(newEntryViewModel.getException().getValue()!=null){
                newEntryViewModel.clearException();
            }
            NavHostFragment.findNavController(NewEntryFragment.this)
                    .navigate(R.id.action_newEntryFragment_to_mainFragment);

        });
    }

    private void saveMeasurements(){
       Measurement measurement = prepareData();
       mainActivityViewModel.saveMeasurements(measurement,foodList);
    }

    public Measurement prepareData(){
        Measurement measurement = new Measurement();
        String time = binding.addTimeButton.getText().toString().trim();
        String date = binding.addDateButton.getText().toString().trim();
        String datetime = date +" "+ time+":00";
        Timestamp timestamp = Timestamp.valueOf(datetime);
        measurement.setDatetime(new Date(timestamp.getTime()));

        if(!TextUtils.isEmpty(binding.sugarLevelET.getText().toString().trim()))
        {
            measurement.setSugarLevel(Double.parseDouble(binding.sugarLevelET.getText().toString().trim()));
        }
        if(!TextUtils.isEmpty(binding.mealinsulinET.getText().toString().trim())){
            measurement.setMealInsulin(Double.parseDouble(binding.mealinsulinET.getText().toString().trim()));
        }
        if(!TextUtils.isEmpty(binding.corrInsulinET.getText().toString().trim())){
            measurement.setCorrInsulin(Double.parseDouble(binding.corrInsulinET.getText().toString().trim()));
        }
        if(!TextUtils.isEmpty(binding.longInsulinET.getText().toString().trim())){
            measurement.setLongInsulin(Double.parseDouble(binding.longInsulinET.getText().toString().trim()));
        }
        if(!TextUtils.isEmpty(binding.tempBasalET.getText().toString().trim())){
            measurement.setTempBolus(Double.parseDouble(binding.tempBasalET.getText().toString().trim()));
        }
        if(!TextUtils.isEmpty(binding.tempBasalTimeET.getText().toString().trim())){
            measurement.setTempBolusTime(Double.parseDouble(binding.tempBasalTimeET.getText().toString().trim()));
        }
        if(!TextUtils.isEmpty(binding.activityET.getText().toString().trim())){
            measurement.setActivity(Double.parseDouble(binding.activityET.getText().toString().trim()));
        }
        if(!TextUtils.isEmpty(binding.sysPressureET.getText().toString().trim())){
            measurement.setSysPressure(Double.parseDouble(binding.sysPressureET.getText().toString().trim()));
        }
        if(!TextUtils.isEmpty(binding.diasPressureET.getText().toString().trim())){
            measurement.setDiasPressure(Double.parseDouble(binding.diasPressureET.getText().toString().trim()));
        }
        measurement.setUserId(sharedPrefRepository.getUserId());
        return measurement;
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
        String date = LocalDate.of(year,month+1,day).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        binding.addDateButton.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String time = LocalTime.of(hourOfDay,minute).format(DateTimeFormatter.ofPattern("HH:mm"));
        binding.addTimeButton.setText(time);
    }


}