package com.example.diabye.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabye.R;
import com.example.diabye.listeners.RecyclerFoodRetrofitListener;
import com.example.diabye.models.retrofitModels.FoodRetrofit;

import java.util.List;
import java.util.Locale;

public class FoodRetrofitRecyclerViewAdapter extends RecyclerView.Adapter<FoodRetrofitRecyclerViewAdapter.ViewHolder>{

    private final List<FoodRetrofit> foodArrayList;
    private final RecyclerFoodRetrofitListener foodRetrofitListener;

    public FoodRetrofitRecyclerViewAdapter(List<FoodRetrofit> foodArrayList, RecyclerFoodRetrofitListener foodRetrofitListener) {
        this.foodArrayList = foodArrayList;
        this.foodRetrofitListener = foodRetrofitListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_row_recycler_view_search_fragment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String foodName = foodArrayList.get(position).getItem_name() +
                " (" + foodArrayList.get(position).getBrand_name()+")";

        String quantity = String.format(Locale.getDefault(),"%.1f",foodArrayList.get(position).getNf_serving_size_qty())+
                " x "+foodArrayList.get(position).getNf_serving_size_unit();
        String fats = String.format(Locale.getDefault(),"%.0f",foodArrayList.get(position).getNf_total_fat()) +"g";
        String protein = String.format(Locale.getDefault(),"%.0f",foodArrayList.get(position).getNf_protein()) +"g";
        String carbs = String.format(Locale.getDefault(),"%.0f",foodArrayList.get(position).getNf_total_carbohydrate()) +"g";

        if(foodArrayList.get(position).getNf_serving_weight_grams()>0){
            quantity = foodArrayList.get(position).getNf_serving_size_qty()+
                    " x "+foodArrayList.get(position).getNf_serving_size_unit()+
                    " ("+ foodArrayList.get(position).getNf_serving_weight_grams()+"g)";
        }
        String kcal = String.format(Locale.getDefault(),"%.0f",foodArrayList.get(position).getNf_calories()) +" kcal";

        holder.foodName.setText(foodName);
        holder.quantity.setText(quantity);
        holder.calories.setText(kcal);
        holder.carbs.setText(carbs);
        holder.protein.setText(protein);
        holder.fats.setText(fats);
    }

    @Override
    public int getItemCount() {
        return foodArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView foodName,calories,quantity,fats,protein, carbs;
        public ImageButton addFoodButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodNameTVRetrofit);
            calories = itemView.findViewById(R.id.foodKcalRetrofitTV);
            quantity = itemView.findViewById(R.id.foodPortionTVRetrofit);
            fats = itemView.findViewById(R.id.foodFatsRetrofitTV);
            protein = itemView.findViewById(R.id.foodProteinRetrofitTV);
            carbs = itemView.findViewById(R.id.foodCarbsRetrofitTV);
            addFoodButton = itemView.findViewById(R.id.addRetrofitFoodButton);

            addFoodButton.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            FoodRetrofit food = foodArrayList.get(getAdapterPosition());
            foodRetrofitListener.OnAddRetrofitFoodClicked(food);
        }
    }
}
