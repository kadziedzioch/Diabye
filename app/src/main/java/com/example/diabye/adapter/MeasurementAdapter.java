package com.example.diabye.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabye.R;
import com.example.diabye.listeners.RecyclerMeasurementListener;
import com.example.diabye.models.Food;
import com.example.diabye.models.MeasurementWithFoods;
import com.example.diabye.utils.FoodUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.ViewHolder> {

    private final List<MeasurementWithFoods> measurementWithFoodsList;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
    private final RecyclerMeasurementListener recyclerMeasurementListener;

    public MeasurementAdapter(List<MeasurementWithFoods> measurementWithFoodsList,
                              RecyclerMeasurementListener recyclerMeasurementListener) {
        this.measurementWithFoodsList = measurementWithFoodsList;
        this.recyclerMeasurementListener = recyclerMeasurementListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.measurement_row,parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String time = localDateFormat.format(measurementWithFoodsList.get(position).getMeasurement().getDatetime());
        holder.timeTv.setText(time);

        if(measurementWithFoodsList.get(position).getMeasurement().getSugarLevel()>0){
            if(holder.bloodSugarTv.getVisibility()!=View.VISIBLE){
                holder.bloodSugarTv.setVisibility(View.VISIBLE);
            }
            double bloodSugar = measurementWithFoodsList.get(position).getMeasurement().getSugarLevel();
            holder.bloodSugarTv.setText(String.format(Locale.getDefault(),"%.0f mg/dL",bloodSugar));
        }
        else{
            holder.bloodSugarTv.setVisibility(View.GONE);
        }

        if(measurementWithFoodsList.get(position).getMeasurement().getMealInsulin()>0){
            if(holder.mealInsulinTv.getVisibility()!=View.VISIBLE){
                holder.mealInsulinTv.setVisibility(View.VISIBLE);
            }
            double mealInsulin = measurementWithFoodsList.get(position).getMeasurement().getMealInsulin();
            holder.mealInsulinTv.setText(String.format(Locale.getDefault(),"%.0f I.U. (meal insulin)",mealInsulin));

        }
        else{
            holder.mealInsulinTv.setVisibility(View.GONE);
        }

        if(measurementWithFoodsList.get(position).getMeasurement().getCorrInsulin()>0){
            if(holder.corrInsulinTv.getVisibility()!=View.VISIBLE){
                holder.corrInsulinTv.setVisibility(View.VISIBLE);
            }

            double corrInsulin = measurementWithFoodsList.get(position).getMeasurement().getCorrInsulin();
            holder.corrInsulinTv.setText(String.format(Locale.getDefault(),"%.0f I.U. (corr. insulin)",corrInsulin));
        }
        else{
            holder.corrInsulinTv.setVisibility(View.GONE);
        }

        if(measurementWithFoodsList.get(position).getMeasurement().getLongInsulin()>0){
            if(holder.longInsulinTv.getVisibility()!=View.VISIBLE){
                holder.longInsulinTv.setVisibility(View.VISIBLE);
            }

            double longInsulin = measurementWithFoodsList.get(position).getMeasurement().getLongInsulin();
            holder.longInsulinTv.setText(String.format(Locale.getDefault(),"%.0f I.U. (long insulin)",longInsulin));
        }
        else{
            holder.longInsulinTv.setVisibility(View.GONE);
        }

        if(measurementWithFoodsList.get(position).getMeasurement().getActivity()>0){
            if(holder.activityTv.getVisibility()!=View.VISIBLE){
                holder.activityTv.setVisibility(View.VISIBLE);
            }
            double activity = measurementWithFoodsList.get(position).getMeasurement().getActivity();
            holder.activityTv.setText(String.format(Locale.getDefault(),"%.0f min",activity));
        }
        else{
            holder.activityTv.setVisibility(View.GONE);
        }

        if(measurementWithFoodsList.get(position).getMeasurement().getSysPressure()>0){
            if(holder.pressureTv.getVisibility()!=View.VISIBLE){
                holder.pressureTv.setVisibility(View.VISIBLE);
            }
            double sysPressure = measurementWithFoodsList.get(position).getMeasurement().getSysPressure();
            double diasPressure = measurementWithFoodsList.get(position).getMeasurement().getDiasPressure();
            holder.pressureTv.setText(String.format(Locale.getDefault(),"%1$.0f/%2$.0f mmHg",sysPressure,diasPressure));
        }
        else{
            holder.pressureTv.setVisibility(View.GONE);
        }


        if(measurementWithFoodsList.get(position).getMeasurement().getTempBolus()>0){
            if(holder.temBolusTv.getVisibility()!=View.VISIBLE){
                holder.temBolusTv.setVisibility(View.VISIBLE);
            }
            double bolus = measurementWithFoodsList.get(position).getMeasurement().getTempBolus();
            double bolusTime = measurementWithFoodsList.get(position).getMeasurement().getTempBolusTime();
            holder.temBolusTv.setText(String.format(Locale.getDefault(),"%1$.0f min %2$.0f %%",bolusTime,bolus));
        }
        else{
            holder.temBolusTv.setVisibility(View.GONE);
        }



        if(measurementWithFoodsList.get(position).getFoodList().size()>0){
            if(holder.choTv.getVisibility()!=View.VISIBLE){
                holder.choTv.setVisibility(View.VISIBLE);
            }
            if(holder.fpuTv.getVisibility()!=View.VISIBLE){
                holder.fpuTv.setVisibility(View.VISIBLE);
            }
            if(holder.caloriesTv.getVisibility()!=View.VISIBLE){
                holder.caloriesTv.setVisibility(View.VISIBLE);
            }
            if(holder.foodtv.getVisibility()!=View.VISIBLE){
                holder.foodtv.setVisibility(View.VISIBLE);
            }
            double kcal = FoodUtils.calculateCalories(measurementWithFoodsList.get(position).getFoodList());
            double fpu = FoodUtils.calculateFpu(measurementWithFoodsList.get(position).getFoodList());
            double cho = FoodUtils.calculateCho(measurementWithFoodsList.get(position).getFoodList());

            holder.caloriesTv.setText(String.format(Locale.getDefault(),"%.0f kcal",kcal));
            holder.fpuTv.setText(String.format(Locale.getDefault(),"%.1f FPU",fpu));
            holder.choTv.setText(String.format(Locale.getDefault(),"%.1f CHO",cho));

            StringBuilder foodText= new StringBuilder();
            for(Food f : measurementWithFoodsList.get(position).getFoodList()){
                foodText.append(f.getPortion()).append(" x ").append(f.getUnit()).append(" ").append(f.getFoodName()).append("\n");
            }
            int last = foodText.lastIndexOf("\n");
            if (last >= 0) { foodText.delete(last, foodText.length()); }
            holder.foodtv.setText(foodText);

        }
        else{
            holder.choTv.setVisibility(View.GONE);
            holder.caloriesTv.setVisibility(View.GONE);
            holder.fpuTv.setVisibility(View.GONE);
            holder.foodtv.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return measurementWithFoodsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bloodSugarTv, timeTv, mealInsulinTv, corrInsulinTv, longInsulinTv;
        TextView temBolusTv, activityTv,pressureTv, choTv, caloriesTv, fpuTv, foodtv;
        ImageButton deleteMeasurementButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bloodSugarTv = itemView.findViewById(R.id.blood_sugar_tv);
            timeTv = itemView.findViewById(R.id.timeTv);
            mealInsulinTv = itemView.findViewById(R.id.mealInsulinTV);
            corrInsulinTv = itemView.findViewById(R.id.corrInsulinTV);
            longInsulinTv = itemView.findViewById(R.id.longInsulinTV);
            temBolusTv = itemView.findViewById(R.id.tempBasalTV);
            activityTv = itemView.findViewById(R.id.activityTV);
            pressureTv = itemView.findViewById(R.id.pressureTV);
            fpuTv = itemView.findViewById(R.id.fpuTv);
            choTv = itemView.findViewById(R.id.choTv);
            caloriesTv = itemView.findViewById(R.id.caloriesTv);
            foodtv = itemView.findViewById(R.id.foodListTV);
            deleteMeasurementButton = itemView.findViewById(R.id.deleteMeasurementButton);
            deleteMeasurementButton.setOnClickListener(view -> {
                MeasurementWithFoods measurementWithFoods = measurementWithFoodsList.get(getAdapterPosition());
                recyclerMeasurementListener.OnMeasurementDeleteClicked(measurementWithFoods);
            });
        }
    }
}
