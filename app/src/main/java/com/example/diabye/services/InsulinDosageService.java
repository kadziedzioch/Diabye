package com.example.diabye.services;

import com.example.diabye.models.Food;
import com.example.diabye.models.Measurement;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.models.normalization.NormObject;
import com.example.diabye.models.normalization.Result;
import com.example.diabye.utils.NotEnoughDataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class InsulinDosageService {

    ExecutorService executorService;
    public InsulinDosageService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    private List<NormObject> normalizeData(List<MeasurementWithFoods> measurementWithFoodsList, double [] query){

        double normalizedHigh = 1;
        double normalizedLow = 0;

        List<Double> sugarLevels = new ArrayList<>();
        List<Double> activities = new ArrayList<>();
        List<Double> sysPressures = new ArrayList<>();
        List<Double> diasPressures = new ArrayList<>();
        List<Double> fats = new ArrayList<>();
        List<Double> proteins = new ArrayList<>();
        List<Double> carbs = new ArrayList<>();
        List<Double> insulinDosages = new ArrayList<>();

        for(MeasurementWithFoods measurementWithFoods: measurementWithFoodsList){
            Measurement m = measurementWithFoods.getMeasurement();
            if(m.getSugarLevel()>0 && m.getDiasPressure()>0 && m.getMealInsulin()>0
                    && measurementWithFoods.getFoodList().size()>0){
                sugarLevels.add(m.getSugarLevel());
                activities.add(m.getActivity());
                sysPressures.add(m.getSysPressure());
                diasPressures.add(m.getDiasPressure());
                insulinDosages.add(m.getMealInsulin());
                double mFats = 0;
                double mProtein = 0;
                double mCarbs = 0;
                for(Food f: measurementWithFoods.getFoodList()){
                    mFats+=f.getFats();
                    mProtein+=f.getProtein();
                    mCarbs+=f.getCarbs();
                }
                fats.add(mFats);
                proteins.add(mProtein);
                carbs.add(mCarbs);
            }
        }

        List<NormObject> normObjectList = new ArrayList<>();


        final double minMaxSugar = Collections.max(sugarLevels) - Collections.min(sugarLevels);
        final double minMaxActivity = Collections.max(activities) - Collections.min(activities);
        final double minMaxSysPressure = Collections.max(sysPressures) - Collections.min(sysPressures);
        final double minMaxDiasPressure = Collections.max(diasPressures) - Collections.min(diasPressures);
        final double minMaxFats = Collections.max(fats) - Collections.min(fats);
        final double minMaxProtein = Collections.max(proteins) - Collections.min(proteins);
        final double minMaxCarb = Collections.max(carbs) - Collections.min(carbs);

        for (int i = 0; i < activities.size(); i++) {
            NormObject normObject = new NormObject();
            normObject.setInsulinDosage(insulinDosages.get(i));

            double normSugar = ((sugarLevels.get(i) - Collections.min(sugarLevels))
                    / minMaxSugar)
                    * (normalizedHigh - normalizedLow) + normalizedLow;
            double normActivity = ((activities.get(i) - Collections.min(activities))
                    / minMaxActivity)
                    * (normalizedHigh - normalizedLow) + normalizedLow;
            double normSysPressure = ((sysPressures.get(i) - Collections.min(sysPressures))
                    / minMaxSysPressure)
                    * (normalizedHigh - normalizedLow) + normalizedLow;
            double normDiasPressure = ((diasPressures.get(i) - Collections.min(diasPressures))
                    / minMaxDiasPressure)
                    * (normalizedHigh - normalizedLow) + normalizedLow;
            double normFats = ((fats.get(i) - Collections.min(fats))
                    / minMaxFats)
                    * (normalizedHigh - normalizedLow) + normalizedLow;
            double normProteins = ((proteins.get(i) - Collections.min(proteins))
                    / minMaxProtein)
                    * (normalizedHigh - normalizedLow) + normalizedLow;
            double normCarbs = ((carbs.get(i) - Collections.min(carbs))
                    / minMaxCarb)
                    * (normalizedHigh - normalizedLow) + normalizedLow;

            double [] attrs = new double[]{normSugar,normActivity,normSysPressure,normDiasPressure,normFats,normProteins,normCarbs};
            normObject.setMeasurementAttributes(attrs);
            normObjectList.add(normObject);
        }

        query[0] = ((query[0]- Collections.min(sugarLevels))
                / minMaxSugar)
                * (normalizedHigh - normalizedLow) + normalizedLow;
        query[1] = ((query[1] - Collections.min(activities))
                / minMaxActivity)
                * (normalizedHigh - normalizedLow) + normalizedLow;
        query[2] = ((query[2] - Collections.min(sysPressures))
                / minMaxSysPressure)
                * (normalizedHigh - normalizedLow) + normalizedLow;
        query[3] = ((query[3] - Collections.min(diasPressures))
                / minMaxDiasPressure)
                * (normalizedHigh - normalizedLow) + normalizedLow;
        query[4] = ((query[4] - Collections.min(fats))
                / minMaxFats)
                * (normalizedHigh - normalizedLow) + normalizedLow;
        query[5] = ((query[5] - Collections.min(proteins))
                / minMaxProtein)
                * (normalizedHigh - normalizedLow) + normalizedLow;
        query[6] = ((query[6] - Collections.min(carbs))
                / minMaxCarb)
                * (normalizedHigh - normalizedLow) + normalizedLow;

        NormObject n = new NormObject();
        n.setMeasurementAttributes(query);
        normObjectList.add(n);

        return normObjectList;
    }

    private List<Double> findNeighbours(List<NormObject> normObjectList) throws NotEnoughDataException {

        List<Result> resultList = new ArrayList<>();
        double [] query = normObjectList.get(normObjectList.size()-1).getMeasurementAttributes();
        normObjectList.remove(normObjectList.size()-1);
        for(NormObject normObject : normObjectList){
            double dist =0;
            for (int i = 0; i < normObject.getMeasurementAttributes().length-1 ; i++) {
                dist+=Math.pow(normObject.getMeasurementAttributes()[i]-query[i],2);

            }
            double distance = Math.sqrt(dist);
            resultList.add(new Result(distance,normObject.getInsulinDosage()));
        }

        resultList.sort(Comparator.comparingDouble(Result::getDistance));

        if(resultList.size()<10){
            throw new NotEnoughDataException("There are not enough measurements to count dosage");
        }

        List<Double> dosages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dosages.add(resultList.get(i).getInsulinDosage());
        }
        return dosages;
    }

    private double countDosage(List<Double> dosages){
        double value = 0;
        for(Double d: dosages){
            value+=d;
        }
        return value/dosages.size();
    }

    public void predictDosage(List<MeasurementWithFoods> measurementWithFoodsList,
                               double [] query,
                               DosageCallback dosageCallback){

        executorService.execute(() -> {
            try{
                List<NormObject> normalizedDataList = normalizeData(measurementWithFoodsList,query);
                List<Double> neighbours = findNeighbours(normalizedDataList);
                double dosage = countDosage(neighbours);
                dosageCallback.onFinish(dosage);
            }
            catch (Exception ex){
                dosageCallback.onError(ex);
            }
        });

    }




}
