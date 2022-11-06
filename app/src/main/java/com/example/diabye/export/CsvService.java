package com.example.diabye.export;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.utils.Constants;
import com.example.diabye.utils.FoodUtils;
import com.itextpdf.layout.element.Table;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class CsvService {

    ExecutorService executorService;
    public CsvService(ExecutorService executorService) {
        this.executorService = executorService;
    }
    private File createFile(String fileName) throws IOException {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(path, fileName);
        if (!file.exists()){
            file.createNewFile();
        }
        return file;
    }

    private CSVWriter setUpWriters(File file) throws IOException{
        return new CSVWriter(new FileWriter(file));
    }

    public void createCsvFile(List<MeasurementWithFoods> measurementWithFoodsList,
                              List<String> categories,
                              ExportCallback exportCallback)  {

        executorService.execute(() -> {
            try{
                String fileName = UUID.randomUUID().toString()+".csv";
                File file = createFile(fileName);
                CSVWriter csvWriter = setUpWriters(file);

                List<String[]> data = new ArrayList<String[]>();

                String[] firstRow;
                if(!categories.contains(Constants.FOOD_INFO)) {
                    firstRow = new String[categories.size()+1];
                }
                else{
                    firstRow = new String[categories.size()+3];
                }

                firstRow[0] = "Date & time";

                if(!categories.contains(Constants.FOOD_INFO)){
                    for (int i = 1; i < firstRow.length; i++) {
                        firstRow[i] = categories.get(i-1);
                    }

                }
                else{
                    for (int i = 1; i < firstRow.length; i++) {
                        if(!Objects.equals(categories.get(i-1), Constants.FOOD_INFO)){
                            firstRow[i] = categories.get(i-1);
                        }
                    }
                    firstRow[firstRow.length-3] = "Calories (kcal)";
                    firstRow[firstRow.length-2] = "Carbs (CHO)";
                    firstRow[firstRow.length-1] = "Fats&Proteins (FPU)";

                }
                data.add(firstRow);

                String pattern = "d-MM-yyyy hh:mm";
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                int index = 1;
                for(MeasurementWithFoods measurementWithFoods: measurementWithFoodsList){
                    Measurement m = measurementWithFoods.getMeasurement();
                    String[] row;
                    if(!categories.contains(Constants.FOOD_INFO)) {
                        row = new String[categories.size()+1];
                    }
                    else{
                        row = new String[categories.size()+3];
                    }
                    row[0] = simpleDateFormat.format(m.getDatetime());
                    if(categories.contains(Constants.BLOOD_SUGAR)){
                        if(m.getSugarLevel()>0){
                            row[index] = String.valueOf(m.getSugarLevel());
                        }
                        else{
                            row[index] = "-";
                        }
                        index++;
                    }
                    if(categories.contains(Constants.MEAL_INSULIN)){
                        if(m.getMealInsulin()>0){
                            row[index] = String.valueOf(m.getMealInsulin());
                        }
                        else{
                            row[index] = "-";
                        }
                        index++;
                    }
                    if(categories.contains(Constants.CORR_INSULIN)){
                        if(m.getCorrInsulin()>0){
                            row[index] = String.valueOf(m.getCorrInsulin());
                        }
                        else{
                            row[index] = "-";
                        }
                        index++;
                    }
                    if(categories.contains(Constants.TEMP_BOLUS)){
                        if(m.getTempBolus()>0){
                            row[index] = m.getTempBolus()+"% "+ m.getTempBolusTime()+"min";
                        }
                        else{
                            row[index] = "-";
                        }
                        index++;
                    }
                    if(categories.contains(Constants.LONG_INSULIN)){
                        if(m.getLongInsulin()>0){
                            row[index] = String.valueOf(m.getLongInsulin());
                        }
                        else{
                            row[index] = "-";
                        }
                        index++;
                    }
                    if(categories.contains(Constants.ACTIVITY)){
                        if(m.getActivity()>0){
                            row[index] = String.valueOf(m.getActivity());
                        }
                        else{
                            row[index] = "-";
                        }
                        index++;
                    }
                    if(categories.contains(Constants.PRESSURE)){
                        if(m.getDiasPressure()>0){
                            row[index] = m.getSysPressure() +"/"+m.getDiasPressure();
                        }
                        else{
                            row[index] = "-";
                        }
                        index++;
                    }

                    if(categories.contains(Constants.FOOD_INFO)){
                        List<Food> foodList = measurementWithFoods.getFoodList();
                        if(foodList.size()>0){
                            double calories = FoodUtils.calculateCalories(foodList);
                            double cho =FoodUtils.calculateCho(foodList);
                            double fpu = FoodUtils.calculateFpu(foodList);
                            row[index] = String.format(Locale.getDefault(),"%.0f", calories);
                            row[index+1] = String.format(Locale.getDefault(),"%.1f", cho);
                            row[index+2] = String.format(Locale.getDefault(),"%.1f", fpu);

                        }
                        else{
                            row[index] = "-";
                            row[index+1] = "-";
                            row[index+2] = "-";
                        }
                    }
                    index = 1;
                    data.add(row);
                }
                csvWriter.writeAll(data);
                csvWriter.close();
                exportCallback.onFinish(file);

            }
            catch(Exception ex){
                exportCallback.onError(ex);
            }
        });


    }


}
