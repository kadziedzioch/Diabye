package com.example.diabye.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.diabye.R;
import com.example.diabye.adapter.DocumentTypeSpinnerAdapter;
import com.example.diabye.databinding.FragmentExportBinding;
import com.example.diabye.models.TherapyType;
import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.SharedPrefRepository;
import com.example.diabye.utils.Constants;
import com.example.diabye.utils.CustomSpinner;
import com.example.diabye.viewmodels.ExportFragmentViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class ExportFragment extends Fragment implements CustomSpinner.OnSpinnerEventsListener, AdapterView.OnItemSelectedListener {

    private FragmentExportBinding binding;
    private String documentType;
    private SharedPrefRepository sharedPrefRepository;
    private ExportFragmentViewModel exportFragmentViewModel;
    public ExportFragment() {

    }


    public static ExportFragment newInstance() {
        return new ExportFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefRepository = new SharedPrefRepository(requireActivity());
        exportFragmentViewModel = new ExportFragmentViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExportBinding.inflate(inflater,container,false);
        setUpToolBar();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DocumentTypeSpinnerAdapter documentTypeSpinnerAdapter = new DocumentTypeSpinnerAdapter(requireActivity(), binding.spinnerDocumentType.getId(),
                Constants.getDocumentTypesList());
        binding.spinnerDocumentType.setAdapter(documentTypeSpinnerAdapter);
        binding.spinnerDocumentType.setSpinnerEventsListener(this);
        binding.spinnerDocumentType.setOnItemSelectedListener(this);

        binding.exportButton.setOnClickListener(view1 -> {
            requestData();
        });
        setDateButtons();
        setUserSettingRelatedData();

    }

    public void setUserSettingRelatedData(){
        String userId = sharedPrefRepository.getUserId();
        exportFragmentViewModel.searchUserSettings(userId);

        exportFragmentViewModel.getUserSettings().observe(getViewLifecycleOwner(), userSettings -> {
            if(Objects.equals(userSettings.getTreatmentType(), TherapyType.PUMP.name())){
                binding.longInsulinCheckBox.setVisibility(View.GONE);
            }
            else{
                binding.tempBolCheckBox.setVisibility(View.GONE);
            }
        });
    }

    private void setDateButtons(){
        LocalDate dateNow = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM. yyyy");
        String dateNowString = dateNow.format(formatter);
        binding.addStartDateButton.setText(dateNowString);
        binding.addEndDateButton.setText(dateNowString);
        binding.addEndDateButton.setOnClickListener(this::showDatePickerDialog);
        binding.addStartDateButton.setOnClickListener(this::showDatePickerDialog);
    }

    private void requestData() {
        String startDateString = binding.addStartDateButton.getText().toString();
        String endDateString = binding.addEndDateButton.getText().toString();
    }

    private void setUpToolBar(){
        Toolbar toolbar = binding.exportToolbar;
        toolbar.setNavigationOnClickListener(view -> {
            requireActivity().onBackPressed();
        });
    }

    private List<String> createCategoryList(){
        List<String> categoryList = new ArrayList<>();
        if(binding.bloodSugarCheckBox.isChecked()){
            categoryList.add(Constants.BLOOD_SUGAR);
        }
        if(binding.mealInsulinCheckBox.isChecked()){
            categoryList.add(Constants.MEAL_INSULIN);
        }
        if(binding.longInsulinCheckBox.isChecked()){
            categoryList.add(Constants.LONG_INSULIN);
        }
        if(binding.corrInsulinCheckBox.isChecked()){
            categoryList.add(Constants.CORR_INSULIN);
        }
        if(binding.tempBolCheckBox.isChecked()){
            categoryList.add(Constants.TEMP_BOLUS);
        }
        if(binding.activityCheckBox.isChecked()){
            categoryList.add(Constants.ACTIVITY);
        }
        if(binding.pressureCheckBox.isChecked()){
            categoryList.add(Constants.PRESSURE);
        }
        if(binding.foodCheckBox.isChecked()){
            categoryList.add(Constants.FOOD_INFO);
        }
        return categoryList;
    }

    @Override
    public void onPopupWindowOpened(Spinner spinner) {
        binding.spinnerDocumentType.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.spinner_background));
    }

    @Override
    public void onPopupWindowClosed(Spinner spinner) {
        binding.spinnerDocumentType.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.spinner_down_background));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        documentType = Constants.getDocumentTypesList().get(i).getCategoryName();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //TO DO
    //porownaj daty zeby nie mozna bylo dac daty pozniejszej w start date
    public void showDatePickerDialog(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                R.style.DatePickerTheme,
                (datePicker, year, month, day) -> {
                    String date = LocalDate.of(year,month+1,day).format(DateTimeFormatter.ofPattern("d MMM. yyyy"));
                    if(v.getId()== binding.addEndDateButton.getId()){
                        binding.addEndDateButton.setText(date);
                    }
                    else{
                        binding.addStartDateButton.setText(date);
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }




}