package com.example.diabye.fragments;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.diabye.R;
import com.example.diabye.adapter.MeasurementSpinnerAdapter;
import com.example.diabye.adapter.TimeIntervalSpinnerAdapter;
import com.example.diabye.databinding.FragmentStatisticsBinding;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.MeasurementWithUserSettings;
import com.example.diabye.repositories.SharedPrefRepository;
import com.example.diabye.utils.Constants;
import com.example.diabye.utils.CustomSpinner;
import com.example.diabye.viewmodels.StatisticsFragmentViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class StatisticsFragment extends Fragment implements CustomSpinner.OnSpinnerEventsListener, AdapterView.OnItemSelectedListener {

    private FragmentStatisticsBinding binding;
    private StatisticsFragmentViewModel statisticsFragmentViewModel;
    private SharedPrefRepository sharedPrefRepository;

    public StatisticsFragment() {

    }


    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater,container,false);
        statisticsFragmentViewModel = new ViewModelProvider(this).get(StatisticsFragmentViewModel.class);
        sharedPrefRepository = new SharedPrefRepository(requireActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MeasurementSpinnerAdapter measurementSpinnerAdapter = new MeasurementSpinnerAdapter(requireActivity(), binding.spinnerMeasurement.getId(),
                Constants.getMeasurementCategoryList());
        binding.spinnerMeasurement.setAdapter(measurementSpinnerAdapter);
        binding.spinnerMeasurement.setSpinnerEventsListener(this);
        binding.spinnerMeasurement.setOnItemSelectedListener(this);

        TimeIntervalSpinnerAdapter timeIntervalSpinnerAdapter = new TimeIntervalSpinnerAdapter(requireActivity(), binding.spinnerTime.getId(),
                Constants.getTimeList());
        binding.spinnerTime.setAdapter(timeIntervalSpinnerAdapter);
        binding.spinnerTime.setSpinnerEventsListener(this);
        binding.spinnerTime.setOnItemSelectedListener(this);

        statisticsFragmentViewModel.setUserId(sharedPrefRepository.getUserId());
        statisticsFragmentViewModel.getMeasurementsWithUserSettings().observe(getViewLifecycleOwner(),
                measurementWithUserSettings -> {
            if(measurementWithUserSettings.getMeasurementList().size()>0){
                if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.BLOOD_SUGAR)){
                    countHyperAndHypos(measurementWithUserSettings);
                }
               prepareDataAndDrawLineChart(measurementWithUserSettings);
                countAverages(measurementWithUserSettings);
            }
            else{
                if(binding.lineChartCardView.getVisibility() == View.VISIBLE){
                    binding.lineChartCardView.setVisibility(View.GONE);
                }
                if(binding.pieChartCardView.getVisibility() == View.VISIBLE){
                    binding.pieChartCardView.setVisibility(View.GONE);
                }
                if(binding.statisticsCardView.getVisibility() == View.VISIBLE){
                    binding.statisticsCardView.setVisibility(View.GONE);
                }
            }
        });


   }


    private void countAverages(MeasurementWithUserSettings measurementWithUserSettings) {
        float value = 0;
        float hyperValue = 0;
        int hyperCount = 0;
        int hypoCount=0;
        float hypoValue = 0;
        float sysvalue=0;
        int count=0;
        String category = statisticsFragmentViewModel.getMeasurementCategory().getValue();

        for(Measurement m: measurementWithUserSettings.getMeasurementList()){
            if(Objects.equals(category, Constants.BLOOD_SUGAR)){
                if(m.getSugarLevel()>0){
                    value+=m.getSugarLevel();
                    count++;
                    if(m.getSugarLevel()>= measurementWithUserSettings.getUserSettings().getHyperValue()){
                        hyperValue++;
                        hyperCount++;
                    }
                    if(m.getSugarLevel()<= measurementWithUserSettings.getUserSettings().getHypoValue()){
                        hypoCount++;
                        hypoCount++;
                    }
                }
            }
            else if(Objects.equals(category, Constants.ACTIVITY)){
                if(m.getActivity()>0){
                    value+=m.getActivity();
                    count++;
                }
            }
            else if(Objects.equals(category, Constants.PRESSURE)){
                if(m.getSysPressure()>0){
                    value+=m.getDiasPressure();
                    sysvalue+=m.getSysPressure();
                    count++;
                }
            }
        }

        if(Objects.equals(category, Constants.BLOOD_SUGAR)){
            if(count>0){
                binding.avgValuePerDayInfo.setText(R.string.avg_blood_sugar_per_day);
                binding.avgSecondValuePerDay.setText(R.string.avg_hyper_per_day);
                binding.avgThirdValuePerDay.setText(R.string.avg_hypos_per_day);
                binding.avgValuePerDayInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_baseline_opacity_24),null,null,null);
                binding.avgSecondValuePerDay.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_baseline_error_outline_24),null,null,null);
                binding.avgThirdValuePerDay.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_baseline_error_outline_24),null,null,null);
                binding.avgFirstValueTv.setText(String.format(Locale.getDefault(),"%.0f mg/dL",value/count));

                if(hyperCount>0){
                    binding.AvgSecondValueTv.setText(String.format(Locale.getDefault(),"%.1f",hyperValue/hyperCount));
                }
                else{
                    binding.AvgSecondValueTv.setText("0");
                }
                if(hypoCount>0){
                    binding.avgThirdValueTv.setText(String.format(Locale.getDefault(),"%.1f",hypoValue/hypoCount));
                }
                else{
                    binding.avgThirdValueTv.setText("0");
                }
                if(binding.secondValueLinearLayout.getVisibility()== View.GONE){
                    binding.secondValueLinearLayout.setVisibility(View.VISIBLE);
                    binding.thirdValueLinearLayout.setVisibility(View.VISIBLE);
                }
            }
            else{
                binding.avgFirstValueTv.setText(R.string.no_avg_sugar_text);
                binding.AvgSecondValueTv.setText("0");
                binding.avgThirdValueTv.setText("0");
            }

        }
        else if(Objects.equals(category, Constants.ACTIVITY)){
            binding.avgValuePerDayInfo.setText(R.string.avg_activity_per_day);
            binding.avgValuePerDayInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_baseline_directions_run_24),null,null,null);
            if(count>0){
                binding.avgFirstValueTv.setText(String.format(Locale.getDefault(),"%.0f min",value/count));
            }
            else{
                binding.avgFirstValueTv.setText(R.string.no_avg_activity_text);
            }
            if(binding.secondValueLinearLayout.getVisibility() !=View.GONE){
                binding.secondValueLinearLayout.setVisibility(View.GONE);
                binding.thirdValueLinearLayout.setVisibility(View.GONE);
            }

        }
        else if(Objects.equals(category, Constants.PRESSURE)){
            binding.avgValuePerDayInfo.setText(R.string.avg_pressure_text);
            binding.avgValuePerDayInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_baseline_biotech_24),null,null,null);
            if(count>0){
                binding.avgFirstValueTv.setText(String.format(Locale.getDefault(),"%1$.0f/%2$.0f mmHg", sysvalue/count, value/count));
            }
            else{
                binding.avgFirstValueTv.setText(R.string.no_avg_pressure_text);
            }
            if(binding.secondValueLinearLayout.getVisibility() !=View.GONE){
                binding.secondValueLinearLayout.setVisibility(View.GONE);
                binding.thirdValueLinearLayout.setVisibility(View.GONE);
            }
        }

        String timeInterval = statisticsFragmentViewModel.getTimeInterval().getValue();

        if(Objects.equals(timeInterval, Constants.WEEK)){
            LocalDate startWeek = LocalDate.now().with(DayOfWeek.SUNDAY).minusDays(7);
            LocalDate endWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
            String text = startWeek.getDayOfMonth()+ "."+ startWeek.getMonth().getValue()+" - "+ endWeek.getDayOfMonth() + "."+ endWeek.getMonth().getValue();
            binding.periodNameTv.setText(text);
        }
        else if(Objects.equals(timeInterval, Constants.MONTH)){
            String text = LocalDate.now().getMonth().name();
            binding.periodNameTv.setText(text);

        }
        else if(Objects.equals(timeInterval, Constants.THREE_MONTHS)){
            String firstMonth = LocalDate.now().minusMonths(2).getMonth().name();
            String secondMonth = LocalDate.now().getMonth().name();
            String text = firstMonth.substring(0,3) + ". -" + secondMonth.substring(0,3)+".";
            binding.periodNameTv.setText(text);
        }
        else{
            String text =  String.valueOf(LocalDate.now().getYear());
            binding.periodNameTv.setText(text);
        }


    }

    private void prepareDataAndDrawLineChart(MeasurementWithUserSettings measurementWithUserSettings) {
        List<Entry> entries = new ArrayList<>();
        List<Entry> sysEntries = new ArrayList<>();
        float count = 0;
        float values = 0;
        float sysValues =0;
        float average = 0;
        float sysAverage = 0;
        LocalDate date = measurementWithUserSettings.getMeasurementList().get(0).getDatetime()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        for(Measurement m : measurementWithUserSettings.getMeasurementList()) {
            LocalDate secondDate = m.getDatetime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (date.isEqual(secondDate)) {
                count++;
                if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.BLOOD_SUGAR)){
                    values += m.getSugarLevel();
                    if(m.getSugarLevel()==0){
                        count--;
                    }
                }
                else if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.ACTIVITY)){
                    values+=m.getActivity();
                    if(m.getActivity()==0){
                        count--;
                    }
                }
                else if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.PRESSURE)){
                    values+=m.getDiasPressure();
                    sysValues +=m.getSysPressure();
                    if(m.getSysPressure()==0){
                        count--;
                    }
                }
                if (Objects.equals(m.getMeasurementId(), measurementWithUserSettings.getMeasurementList().get(measurementWithUserSettings.getMeasurementList().size() - 1).getMeasurementId())&& count>0) {
                    average = values / count;
                    if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.PRESSURE)){
                        sysAverage = sysValues/count;
                        if(sysAverage>0){
                            sysEntries.add(new Entry(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),sysAverage));
                        }
                    }
                    if(average>0){
                        entries.add(new Entry(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),average));
                    }
                }
            } else {
                if(count>0){
                    average = values / count;
                    if(average>0){
                        entries.add(new Entry(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),average));
                    }
                    if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.PRESSURE)){
                        sysAverage = sysValues/count;
                        if(sysAverage>0){
                            sysEntries.add(new Entry(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),sysAverage));
                        }
                    }
                }

                date = m.getDatetime()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                sysAverage = 0;
                sysValues = 0;
                average = 0;
                values = 0;
                count = 0;
                count++;
                if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.BLOOD_SUGAR)){
                    values += m.getSugarLevel();
                    if(m.getSugarLevel()==0){
                        count--;
                    }
                }
                else if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.ACTIVITY)){
                    values+=m.getActivity();
                    if(m.getActivity()==0){
                        count--;
                    }
                }
                else if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.PRESSURE)){
                    values+=m.getDiasPressure();
                    sysValues +=m.getSysPressure();
                    if(m.getSysPressure()==0){
                        count--;
                    }
                }
                if (Objects.equals(m.getMeasurementId(), measurementWithUserSettings.getMeasurementList().get(measurementWithUserSettings.getMeasurementList().size() - 1).getMeasurementId())&& count>0) {
                    average = values / count;
                    if(average>0){
                        entries.add(new Entry(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),average));
                    }
                    if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.PRESSURE)){
                        sysAverage = sysValues/count;
                        if(sysAverage>0){
                            sysEntries.add(new Entry(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),sysAverage));
                        }
                    }

                }
            }
        }
        if(entries.size()>0){
            if(binding.lineChartCardView.getVisibility() == View.GONE){
                binding.lineChartCardView.setVisibility(View.VISIBLE);
            }
            if(binding.statisticsCardView.getVisibility()==View.GONE){
                binding.statisticsCardView.setVisibility(View.VISIBLE);
            }
            initLineChart(measurementWithUserSettings);
            setUpLineChart(entries,sysEntries);

        }
        else{
            if(binding.lineChartCardView.getVisibility() == View.VISIBLE) {
                binding.lineChartCardView.setVisibility(View.GONE);
            }
            if(binding.statisticsCardView.getVisibility()==View.VISIBLE){
                binding.statisticsCardView.setVisibility(View.GONE);
            }
        }

    }

    private void setUpLineChart(List<Entry> entries, List<Entry> sysEntries) {
        if(!Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.PRESSURE)){
            LineDataSet lineDataSet = new LineDataSet(entries,"value");
            lineDataSet.setLineWidth(2f);
            lineDataSet.setDrawValues(false);
            lineDataSet.setDrawFilled(true);
            lineDataSet.setCircleColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
            lineDataSet.setCircleHoleColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
            lineDataSet.setFillDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.chart_gradient));
            lineDataSet.setDrawHorizontalHighlightIndicator(false);
            lineDataSet.setDrawVerticalHighlightIndicator(false);
            lineDataSet.setColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
            lineDataSet.setValueTextColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));

            LineData lineData = new LineData(lineDataSet);
            binding.lineChartView.getLegend().setEnabled(false);
            binding.lineChartView.getXAxis().resetAxisMaximum();
            binding.lineChartView.getXAxis().resetAxisMinimum();
            binding.lineChartView.setData(lineData);


        }
        else{
            binding.lineChartView.getLegend().setEnabled(true);
            binding.lineChartView.getLegend().setTextSize(11f);
            binding.lineChartView.getLegend().setTextColor(ContextCompat.getColor(requireActivity(),R.color.dark_gray));


            LineDataSet firstDataSet = new LineDataSet(entries,"Diastolic");
            firstDataSet.setLineWidth(2f);
            firstDataSet.setColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
            firstDataSet.setDrawValues(false);
            firstDataSet.setCircleColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
            firstDataSet.setCircleHoleColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
            firstDataSet.setValueTextColor(ContextCompat.getColor(requireActivity(),R.color.main_blue));
            firstDataSet.setDrawHorizontalHighlightIndicator(false);
            firstDataSet.setDrawVerticalHighlightIndicator(false);
            LineDataSet secondDatSet = new LineDataSet(sysEntries,"Systolic");
            secondDatSet.setLineWidth(2f);
            secondDatSet.setDrawValues(false);
            secondDatSet.setCircleColor(ContextCompat.getColor(requireActivity(),R.color.very_dark_blue));
            secondDatSet.setCircleHoleColor(ContextCompat.getColor(requireActivity(),R.color.very_dark_blue));
            secondDatSet.setColor(ContextCompat.getColor(requireActivity(),R.color.very_dark_blue));
            secondDatSet.setDrawHorizontalHighlightIndicator(false);
            secondDatSet.setDrawVerticalHighlightIndicator(false);
            secondDatSet.setValueTextColor(ContextCompat.getColor(requireActivity(),R.color.very_dark_blue));
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(firstDataSet);
            dataSets.add(secondDatSet);
            LineData lineData = new LineData(dataSets);
            binding.lineChartView.setData(lineData);


        }

        if(entries.size()<5){
            if(Objects.equals(statisticsFragmentViewModel.getTimeInterval().getValue(), Constants.WEEK)){
                LocalDate startWeek = LocalDate.now().with(DayOfWeek.SUNDAY).minusDays(7);
                LocalDate endWeek = LocalDate.now().with(DayOfWeek.SUNDAY).plusDays(1);
                binding.lineChartView.getXAxis().setAxisMinimum(startWeek.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
                binding.lineChartView.getXAxis().setAxisMaximum(endWeek.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            else if(Objects.equals(statisticsFragmentViewModel.getTimeInterval().getValue(), Constants.MONTH)){
                LocalDate startMonth =  LocalDate.now().withDayOfMonth(1);
                LocalDate endMonth =  LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
                binding.lineChartView.getXAxis().setAxisMinimum(startMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
                binding.lineChartView.getXAxis().setAxisMaximum(endMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            else if(Objects.equals(statisticsFragmentViewModel.getTimeInterval().getValue(), Constants.THREE_MONTHS)){
                LocalDate startMonth =  LocalDate.now().withDayOfMonth(1).minusMonths(2);
                LocalDate endMonth =   LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
                binding.lineChartView.getXAxis().setAxisMinimum(startMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
                binding.lineChartView.getXAxis().setAxisMaximum(endMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            else if(Objects.equals(statisticsFragmentViewModel.getTimeInterval().getValue(), Constants.YEAR)){
                LocalDate startYear=  LocalDate.now().withDayOfYear(1);
                LocalDate endYear =   LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
                binding.lineChartView.getXAxis().setAxisMinimum(startYear.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
                binding.lineChartView.getXAxis().setAxisMaximum(endYear.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }

        }
        binding.lineChartView.invalidate();
    }

    private void initLineChart(MeasurementWithUserSettings measurementWithUserSettings) {
        binding.lineChartView.getDescription().setEnabled(false);
        binding.lineChartView.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.lineChartView.getXAxis().setDrawGridLines(false);
        binding.lineChartView.getXAxis().setTextColor(ContextCompat.getColor(requireActivity(),R.color.dark_gray));
        binding.lineChartView.getAxisRight().setEnabled(false);
        binding.lineChartView.getAxisLeft().setGridColor(ContextCompat.getColor(requireActivity(),R.color.light_gray));
        binding.lineChartView.getAxisLeft().setTextSize(12f);
        binding.lineChartView.getAxisLeft().setTextColor(ContextCompat.getColor(requireActivity(),R.color.dark_gray));
        binding.lineChartView.setExtraBottomOffset(20f);
        binding.lineChartView.getXAxis().setTextSize(12f);

        YAxis leftAxis = binding.lineChartView.getAxisLeft();
        leftAxis.removeAllLimitLines();
        binding.lineChartView.getXAxis().setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("d MMM", Locale.ENGLISH);
            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value;
                return mFormat.format(new Date(millis));
            }
        });
        binding.lineChartView.getXAxis().setGranularityEnabled(true);
        binding.lineChartView.getXAxis().setGranularity(1000f);


        if(Objects.equals(statisticsFragmentViewModel.getMeasurementCategory().getValue(), Constants.BLOOD_SUGAR)){
            float hyper = (float) measurementWithUserSettings.getUserSettings().getHyperValue();
            float hypo = (float) measurementWithUserSettings.getUserSettings().getHypoValue();
            float targetHigh = (float) measurementWithUserSettings.getUserSettings().getHighSugarRangeLevel();
            float targetLow = (float) measurementWithUserSettings.getUserSettings().getLowSugarRangeLevel();
            LimitLine ll1 = new LimitLine(hyper, "Hyper");
            ll1.setLineWidth(1f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
            ll1.setTextColor(ContextCompat.getColor(requireActivity(),R.color.error_background_color));
            ll1.setLineColor(ContextCompat.getColor(requireActivity(),R.color.error_background_color));

            LimitLine ll2 = new LimitLine(hypo, "Hypo");
            ll2.setLineWidth(1f);
            ll2.enableDashedLine(10f, 10f, 0f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll2.setTextSize(10f);
            ll2.setTextColor(ContextCompat.getColor(requireActivity(),R.color.success_background_color));
            ll2.setLineColor(ContextCompat.getColor(requireActivity(),R.color.success_background_color));

            LimitLine ll3 = new LimitLine(targetHigh, "Target");
            ll3.setLineWidth(1f);
            ll3.enableDashedLine(10f, 10f, 0f);
            ll3.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll3.setTextSize(10f);
            ll3.setTextColor(ContextCompat.getColor(requireActivity(),R.color.dark_gray));
            ll3.setLineColor(ContextCompat.getColor(requireActivity(),R.color.dark_gray));

            LimitLine ll4 = new LimitLine(targetLow, "Target");
            ll4.setLineWidth(1f);
            ll4.enableDashedLine(10f, 10f, 0f);
            ll4.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll4.setTextSize(10f);
            ll4.setTextColor(ContextCompat.getColor(requireActivity(),R.color.dark_gray));
            ll4.setLineColor(ContextCompat.getColor(requireActivity(),R.color.dark_gray));

            leftAxis.removeAllLimitLines();
            leftAxis.addLimitLine(ll1);
            leftAxis.addLimitLine(ll2);
            leftAxis.addLimitLine(ll3);
            leftAxis.addLimitLine(ll4);
        }
    }

    private void countHyperAndHypos(MeasurementWithUserSettings measurementWithUserSettings) {
        int hyper = 0;
        int hypo = 0;
        int inTargetRange = 0;
        int others = 0;
        for(Measurement m: measurementWithUserSettings.getMeasurementList()){
            if(m.getSugarLevel()>= measurementWithUserSettings.getUserSettings().getHyperValue()){
                hyper++;
            }
            else if(m.getSugarLevel()<= measurementWithUserSettings.getUserSettings().getHypoValue()
            && m.getSugarLevel()>0){
                hypo++;
            }
            else if(m.getSugarLevel()<= measurementWithUserSettings.getUserSettings().getHighSugarRangeLevel()
            && m.getSugarLevel()>= measurementWithUserSettings.getUserSettings().getLowSugarRangeLevel()){
                inTargetRange++;
            }
            else{
                if(m.getSugarLevel()>0){
                    others++;
                }
            }

        }

        if(hypo >0|| hyper>0|| inTargetRange>0){
            if(binding.pieChartCardView.getVisibility()!=View.VISIBLE){
                binding.pieChartCardView.setVisibility(View.VISIBLE);
            }
            initPieChart();
            setUpPieChart(hyper,hypo,inTargetRange,others);
        }
        else{
            if(binding.pieChartCardView.getVisibility()!=View.GONE){
                binding.pieChartCardView.setVisibility(View.GONE);
            }
        }
    }

    private void initPieChart(){
        binding.pieChartView.getLegend().setEnabled(false);
        binding.pieChartView.setCenterText("Distribution");
        binding.pieChartView.setCenterTextSize(13f);
        binding.pieChartView.setCenterTextColor(ContextCompat.getColor(requireActivity(),R.color.dark_gray));
        binding.pieChartView.getDescription().setEnabled(false);
        binding.pieChartView.setRotationEnabled(true);
        binding.pieChartView.setDragDecelerationFrictionCoef(0.9f);
        binding.pieChartView.setRotationAngle(0);
        binding.pieChartView.setEntryLabelColor(Color.WHITE);
        binding.pieChartView.setHighlightPerTapEnabled(true);
        binding.pieChartView.setUsePercentValues(true);
        binding.pieChartView.animateY(1400, Easing.EaseInOutQuad);
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
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueFormatter(new PercentValueFormatter());
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);

        binding.pieChartView.setData(pieData);
        binding.pieChartView.invalidate();
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


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == binding.spinnerMeasurement.getId()){
            String categoryName = Constants.getMeasurementCategoryList().get(i).getCategoryName();
            if(!Objects.equals(categoryName, Constants.BLOOD_SUGAR)){
                if(binding.pieChartCardView.getVisibility()!=View.GONE){
                    binding.pieChartCardView.setVisibility(View.GONE);
                }
            }
            statisticsFragmentViewModel.setMeasurementCategory(categoryName);

        }
        else{
            statisticsFragmentViewModel.setTimeInterval(Constants.getTimeList().get(i).getIntervalName());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

class PercentValueFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return String.format("%d%%", (int) value);
    }
}