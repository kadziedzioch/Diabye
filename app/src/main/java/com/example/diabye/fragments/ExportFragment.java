package com.example.diabye.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.diabye.R;
import com.example.diabye.adapter.DocumentTypeSpinnerAdapter;
import com.example.diabye.databinding.FragmentExportBinding;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.models.TherapyType;
import com.example.diabye.repositories.SharedPrefRepository;
import com.example.diabye.utils.AppUtils;
import com.example.diabye.utils.Constants;
import com.example.diabye.utils.CustomSpinner;
import com.example.diabye.viewmodels.ExportFragmentViewModel;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        setExportButton();
        setSpinner();
        setDateButtons();
        setUserSettingRelatedData();

        exportFragmentViewModel.getMeasurementsWithFoodsList().observe(getViewLifecycleOwner(), this::exportData);

        exportFragmentViewModel.getFile().observe(getViewLifecycleOwner(), file -> {
            binding.exportProgressBar.setVisibility(View.GONE);
            if(Objects.equals(documentType, "PDF")){
                openPdfFile(file);
            }
            if(Objects.equals(documentType, "CSV")){
                openCsvFile(file);
            }

        });
        exportFragmentViewModel.getException().observe(getViewLifecycleOwner(), exception -> {
            binding.exportProgressBar.setVisibility(View.GONE);
            AppUtils.showMessage(requireActivity(),binding.activityCheckBox,
                    exception.getMessage(),true);
        });

    }

    private void exportData(List<MeasurementWithFoods> measurementWithFoods){
        List<String> categories = createCategoryList();
        if(measurementWithFoods!=null&& measurementWithFoods.size()>0
                && categories.size()>0 && Objects.equals(documentType, "PDF")){
            String startDay = binding.addStartDateButton.getText().toString();
            String endDay = binding.addEndDateButton.getText().toString();
            exportFragmentViewModel.createPdfDocument(measurementWithFoods,categories, startDay,endDay);
        }
        else if(measurementWithFoods!=null&& measurementWithFoods.size()>0
                && categories.size()>0 && Objects.equals(documentType, "CSV")){
            exportFragmentViewModel.createCsvFile(measurementWithFoods,categories);
        }
        else if(measurementWithFoods!=null&& measurementWithFoods.size()==0){
            binding.exportProgressBar.setVisibility(View.GONE);
            AppUtils.showMessage(requireActivity(),binding.activityCheckBox,
                    getString(R.string.no_measurements_in_this_time_interval), true);
        }
        else if(categories.size()==0){
            binding.exportProgressBar.setVisibility(View.GONE);
            AppUtils.showMessage(requireActivity(),binding.activityCheckBox,
                    getString(R.string.no_category_selected), true);
        }
    }

    private void openCsvFile(File file) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        Intent csvIntent = new Intent(Intent.ACTION_VIEW);
        csvIntent.setDataAndType(Uri.fromFile(file), "text/csv");
        csvIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(csvIntent);
        } catch (ActivityNotFoundException exception) {
            AppUtils.showMessage(requireActivity(),binding.activityCheckBox,
                    getString(R.string.no_csv_reader_info),true);
        }
    }

    private void openPdfFile(File file) {
      StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
      StrictMode.setVmPolicy(builder.build());
      builder.detectFileUriExposure();
      Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
      pdfIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
      pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException exception) {
            AppUtils.showMessage(requireActivity(),binding.activityCheckBox,
                    getString(R.string.no_pdf_reader_message),true);
        }
    }


    private void setSpinner(){
        DocumentTypeSpinnerAdapter documentTypeSpinnerAdapter = new DocumentTypeSpinnerAdapter(requireActivity(), binding.spinnerDocumentType.getId(),
                Constants.getDocumentTypesList());
        binding.spinnerDocumentType.setAdapter(documentTypeSpinnerAdapter);
        binding.spinnerDocumentType.setSpinnerEventsListener(this);
        binding.spinnerDocumentType.setOnItemSelectedListener(this);
    }

    private void setExportButton() {
        binding.exportButton.setOnClickListener(view -> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE));

    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                   searchMeasurementsWithFoods();
                } else {
                    AppUtils.showMessage(requireActivity(),binding.activityCheckBox,
                            getString(R.string.permission_denied_info),true);
                }
            }
    );

    private void searchMeasurementsWithFoods() {
            binding.exportProgressBar.setVisibility(View.VISIBLE);
            String firstDateString = binding.addStartDateButton.getText().toString().trim();
            String secondDateString = binding.addEndDateButton.getText().toString().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime firstLocalDate = LocalDate.parse(firstDateString, formatter).atStartOfDay();
            LocalDateTime secondLocalDate = LocalDate.parse(secondDateString, formatter).atStartOfDay().plusDays(1);
            Date firstDate = Date.from(firstLocalDate.atZone(ZoneId.systemDefault()).toInstant());
            Date secondDate = Date.from(secondLocalDate.atZone(ZoneId.systemDefault()).toInstant());
            exportFragmentViewModel.searchMeasurementsWithFoods(firstDate,secondDate,sharedPrefRepository.getUserId());
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateNowString = dateNow.format(formatter);
        binding.addStartDateButton.setText(dateNowString);
        binding.addEndDateButton.setText(dateNowString);
        binding.addEndDateButton.setOnClickListener(this::showDatePickerDialog);
        binding.addStartDateButton.setOnClickListener(this::showDatePickerDialog);
    }



    private void setUpToolBar(){
        Toolbar toolbar = binding.exportToolbar;
        toolbar.setNavigationOnClickListener(view -> requireActivity().onBackPressed());
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


    public void showDatePickerDialog(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                R.style.DatePickerTheme,
                (datePicker, year, month, day) -> {
                    String date = LocalDate.of(year,month+1,day).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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