package com.example.diabye.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.diabye.R;
import com.example.diabye.adapter.DocumentTypeSpinnerAdapter;
import com.example.diabye.databinding.FragmentExportBinding;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.models.TherapyType;
import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.SharedPrefRepository;
import com.example.diabye.utils.AppUtils;
import com.example.diabye.utils.Constants;
import com.example.diabye.utils.CustomSpinner;
import com.example.diabye.utils.PercentValueFormatter;
import com.example.diabye.viewmodels.ExportFragmentViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ExportFragment extends Fragment implements CustomSpinner.OnSpinnerEventsListener, AdapterView.OnItemSelectedListener {

    private FragmentExportBinding binding;
    private String documentType;
    private SharedPrefRepository sharedPrefRepository;
    private ExportFragmentViewModel exportFragmentViewModel;

    public ExportFragment() {

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


    private boolean countHyperAndHypos(List<MeasurementWithFoods> measurementWithFoodsList){
        int hyper = 0;
        int hypo = 0;
        int inTargetRange = 0;
        int others = 0;
        UserSettings userSettings = exportFragmentViewModel.getUserSettings().getValue();
        if(userSettings!=null){
            for(MeasurementWithFoods measurementWithFoods: measurementWithFoodsList){
                Measurement m = measurementWithFoods.getMeasurement();
                if(m.getSugarLevel()>= userSettings.getHyperValue()){
                    hyper++;
                }
                else if(m.getSugarLevel()<= userSettings.getHypoValue()
                        && m.getSugarLevel()>0){
                    hypo++;
                }
                else if(m.getSugarLevel()<= userSettings.getHighSugarRangeLevel()
                        && m.getSugarLevel()>= userSettings.getLowSugarRangeLevel()){
                    inTargetRange++;
                }
                else{
                    if(m.getSugarLevel()>0){
                        others++;
                    }
                }
            }

            if(hypo >0|| hyper>0|| inTargetRange>0){
                initPieChart();
                setUpPieChart(hyper,hypo,inTargetRange,others);
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }

    private void initPieChart(){
        binding.pieChartExport.getLegend().setEnabled(false);
        binding.pieChartExport.setCenterTextColor(ContextCompat.getColor(requireActivity(),R.color.dark_gray));
        binding.pieChartExport.getDescription().setEnabled(false);
        binding.pieChartExport.setUsePercentValues(true);
        binding.pieChartExport.setEntryLabelColor(Color.WHITE);
        binding.pieChartExport.setMinimumHeight(350);
        binding.pieChartExport.setMinimumWidth(350);
        binding.pieChartExport.setDrawHoleEnabled(false);
    }

    private void setUpPieChart(int hyper, int hypo, int inTargetRange, int others) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";
        Map<String, Integer> typeAmountMap = new HashMap<>();
        if(hypo>0){
            typeAmountMap.put("Hypo",hypo);
        }
        if(hyper>0){
            typeAmountMap.put("Hyper",hyper);
        }
        if(inTargetRange>0){
            typeAmountMap.put("Target",inTargetRange);
        }
        if(others>0){
            typeAmountMap.put("Others",others);
        }
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(requireActivity(),R.color.main_blue));
        colors.add(ContextCompat.getColor(requireActivity(),R.color.darker_blue));
        colors.add(ContextCompat.getColor(requireActivity(),R.color.very_dark_blue));
        colors.add(ContextCompat.getColor(requireActivity(),R.color.lighter_blue));

        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        pieDataSet.setValueTextSize(11f);
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueFormatter(new PercentValueFormatter());
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);
        binding.pieChartExport.setData(pieData);
        binding.pieChartExport.invalidate();
    }

    private List<Entry> createLineChart(List<MeasurementWithFoods> measurementWithFoodsList){
        List<Entry> entries = new ArrayList<>();
        float count = 0;
        float values = 0;
        float average = 0;
        LocalDate date = measurementWithFoodsList.get(0).getMeasurement().getDatetime()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        for(MeasurementWithFoods measurementWithFoods: measurementWithFoodsList){
            Measurement m = measurementWithFoods.getMeasurement();
            LocalDate secondDate = m.getDatetime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            if (date.isEqual(secondDate)) {
                count++;
                values += m.getSugarLevel();
                if (m.getSugarLevel() == 0) {
                    count--;
                }
                if (Objects.equals(m.getMeasurementId(), measurementWithFoodsList.get(measurementWithFoodsList.size() - 1).getMeasurement().getMeasurementId())&& count>0) {
                    average = values / count;
                    if(average>0){
                        entries.add(new Entry(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),average));
                    }
                }

            }
            else{
                if(count>0){
                    average = values / count;
                    if(average>0){
                        entries.add(new Entry(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),average));
                    }
                }
                date = m.getDatetime()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                average = 0;
                values = 0;
                count = 0;
                count++;

                values += m.getSugarLevel();
                if(m.getSugarLevel()==0){
                    count--;
                }

                if (Objects.equals(m.getMeasurementId(), measurementWithFoodsList.get(measurementWithFoodsList.size() - 1).getMeasurement().getMeasurementId())&& count>0) {
                    average = values / count;
                    if(average>0){
                        entries.add(new Entry(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),average));
                    }
                }
            }
        }

        return entries;

    }

    private LineChart setUpLineChart(List<Entry> entries){
        LineChart lineChart = binding.lineChartExport;
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setTextSize(12f);
        lineChart.setExtraBottomOffset(20f);
        lineChart.getXAxis().setTextSize(12f);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();

        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("d MMM", Locale.ENGLISH);
            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value;
                return mFormat.format(new Date(millis));
            }
        });

        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1000f);

        LineDataSet lineDataSet = new LineDataSet(entries,"value");
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setCircleColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
        lineDataSet.setCircleHoleColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawVerticalHighlightIndicator(false);
        lineDataSet.setColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
        lineDataSet.setValueTextColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
        LineData lineData = new LineData(lineDataSet);
        lineChart.getLegend().setEnabled(false);
        lineChart.getXAxis().resetAxisMaximum();
        lineChart.getXAxis().resetAxisMinimum();
        lineChart.setMinimumHeight(450);
        lineChart.setMinimumWidth(300);
        lineChart.setData(lineData);
        lineChart.invalidate();
        return lineChart;
    }

    private void exportData(List<MeasurementWithFoods> measurementWithFoods){
        List<String> categories = createCategoryList();
        if(measurementWithFoods!=null&& measurementWithFoods.size()>0
                && categories.size()>0 && Objects.equals(documentType, "PDF")){
            String startDay = binding.addStartDateButton.getText().toString();
            String endDay = binding.addEndDateButton.getText().toString();
            List<Entry> entries= createLineChart(measurementWithFoods);
            LineChart lineChart;
            if(entries.size()>5){
                lineChart = setUpLineChart(entries);
            }
            else{
                lineChart = null;
            }
            PieChart pieChart;
            if(countHyperAndHypos(measurementWithFoods)){
                pieChart = binding.pieChartExport;
            }
            else{
                pieChart = null;
            }
            exportFragmentViewModel.createPdfDocument(measurementWithFoods,categories, startDay,endDay,lineChart,pieChart);
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
        Uri path = FileProvider.getUriForFile(requireActivity(), requireActivity().getApplicationContext().getPackageName() + ".provider", file);
        Intent csvIntent = new Intent(Intent.ACTION_VIEW);
        csvIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        csvIntent.setDataAndType(path, "text/csv");

        try {
            startActivity(csvIntent);
        } catch (ActivityNotFoundException exception) {
            AppUtils.showMessage(requireActivity(),binding.activityCheckBox,
                    getString(R.string.no_csv_reader_info),true);
        }
    }

    private void openPdfFile(File file) {
      Uri path = FileProvider.getUriForFile(requireActivity(), requireActivity().getApplicationContext().getPackageName() + ".provider", file);
      Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
      pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      pdfIntent.setDataAndType(path, "application/pdf");

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
        toolbar.setNavigationOnClickListener(view -> NavHostFragment.findNavController(ExportFragment.this)
                .navigate(R.id.action_exportFragment_to_settingsFragment));
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