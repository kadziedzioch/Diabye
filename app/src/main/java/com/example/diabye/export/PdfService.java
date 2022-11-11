package com.example.diabye.export;


import android.annotation.SuppressLint;
import android.os.Environment;

import com.example.diabye.models.Category;
import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.utils.Constants;
import com.example.diabye.utils.FoodUtils;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


public class PdfService {

    ExecutorService executorService;
    public PdfService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    private File createFile(String fileName) throws IOException{
      String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
      File file = new File(path, fileName);
      if (!file.exists()){
          file.createNewFile();
      }
      return file;
  }

  private PdfWriter setPdfWriter(File file) throws FileNotFoundException {
      PdfWriter writer = new PdfWriter(String.valueOf(file));
      writer.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
      return writer;
  }

  private Document createDocument(PdfWriter writer){
      PdfDocument pdfDocument = new PdfDocument(writer);
      pdfDocument.setDefaultPageSize(PageSize.A4);
      return new Document(pdfDocument);
  }


  public void createPdfDocument(List<MeasurementWithFoods> measurementWithFoodsList,
                                List<String> categories,
                                String startDate,
                                String endDate,
                                ExportCallback exportCallback)  {

        executorService.execute(() -> {
            try{
                String fileName = UUID.randomUUID().toString()+".pdf";
                File file = createFile(fileName);
                PdfWriter writer = setPdfWriter(file);
                Document document = createDocument(writer);

                document.add(new Paragraph("Diabye").setHorizontalAlignment(HorizontalAlignment.CENTER));
                document.add(new Paragraph("Time interval: "+startDate+" - "+endDate));
                document.add(new Paragraph("Date created: "+LocalDate.now().toString()).setHorizontalAlignment(HorizontalAlignment.CENTER));
                document.add(new Paragraph(""));

                float [] columnWidth;
                if(!categories.contains(Constants.FOOD_INFO)){
                    columnWidth = new float[categories.size()+1];
                }
                else{
                    columnWidth = new float[categories.size()+3];
                }
                Arrays.fill(columnWidth, 100f);
                Table table = new Table(columnWidth);
                table.addCell("Date and Time");

                if(!categories.contains(Constants.FOOD_INFO)){
                    for(String category: categories){
                        table.addCell(category);
                    }
                }
                else{
                    for(String category: categories){
                        if(!Objects.equals(category, Constants.FOOD_INFO)){
                            table.addCell(category);
                        }
                    }
                    table.addCell("Calories (kcal)");
                    table.addCell("Carbs (CHO)");
                    table.addCell("Fats&Proteins (FPU)");

                }

                List<String> checkList = new ArrayList<>();
                String pattern = "d-MM-yyyy HH:mm";
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                for(MeasurementWithFoods measurementWithFoods :measurementWithFoodsList){
                    Measurement m = measurementWithFoods.getMeasurement();
                    if(categories.contains(Constants.BLOOD_SUGAR)){
                        if(m.getSugarLevel()>0){
                            checkList.add(String.valueOf(m.getSugarLevel()));
                        }
                        else{
                            checkList.add("-");
                        }

                    }
                    if(categories.contains(Constants.MEAL_INSULIN)){
                        if(m.getMealInsulin()>0){
                            checkList.add(String.valueOf(m.getMealInsulin()));
                        }
                        else{
                            checkList.add("-");
                        }
                    }
                    if(categories.contains(Constants.CORR_INSULIN)){
                        if(m.getCorrInsulin()>0){
                            checkList.add(String.valueOf(m.getCorrInsulin()));
                        }
                        else{
                            checkList.add("-");
                        }
                    }
                    if(categories.contains(Constants.TEMP_BOLUS)){
                        if(m.getTempBolus()>0){
                            checkList.add(m.getTempBolus()+"% "+ m.getTempBolusTime()+"min");
                        }
                        else{
                            checkList.add("-");
                        }
                    }
                    if(categories.contains(Constants.LONG_INSULIN)){
                        if(m.getLongInsulin()>0){
                            checkList.add(String.valueOf(m.getLongInsulin()));
                        }
                        else{
                            checkList.add("-");
                        }
                    }
                    if(categories.contains(Constants.ACTIVITY)){
                        if(m.getActivity()>0){
                            checkList.add(String.valueOf(m.getActivity()));
                        }
                        else{
                            checkList.add("-");
                        }
                    }
                    if(categories.contains(Constants.PRESSURE)){
                        if(m.getDiasPressure()>0){
                            String sysPressure= String.format(Locale.getDefault(),"%.0f", m.getSysPressure());
                            String diasPressure = String.format(Locale.getDefault(),"%.0f", m.getDiasPressure());
                            checkList.add(sysPressure +"/"+diasPressure);
                        }
                        else{
                            checkList.add("-");
                        }

                    }
                    if(categories.contains(Constants.FOOD_INFO)){
                        List<Food> foodList = measurementWithFoods.getFoodList();
                        if(foodList.size()>0){
                            double calories = FoodUtils.calculateCalories(foodList);
                            double cho =FoodUtils.calculateCho(foodList);
                            double fpu = FoodUtils.calculateFpu(foodList);
                            checkList.add(String.format(Locale.getDefault(),"%.0f", calories));
                            checkList.add(String.format(Locale.getDefault(),"%.1f", cho));
                            checkList.add(String.format(Locale.getDefault(),"%.1f", fpu));
                        }
                        else{
                            checkList.add("-");
                            checkList.add("-");
                            checkList.add("-");
                        }
                    }
                    boolean found = false;
                    for(String check: checkList){
                        if (!Objects.equals(check, "-")) {
                            found = true;
                            break;
                        }
                    }

                    if(found){
                        table.addCell(simpleDateFormat.format(m.getDatetime()));
                        for(String check: checkList){
                            table.addCell(check);
                        }
                    }
                    checkList.clear();

                }

                document.add(table);
                document.close();
                writer.close();
                exportCallback.onFinish(file);
            }
            catch(Exception exception){
                exportCallback.onError(exception);
            }
        });

  }



}
