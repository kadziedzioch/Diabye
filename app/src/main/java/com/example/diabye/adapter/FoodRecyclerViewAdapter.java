package com.example.diabye.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabye.R;
import com.example.diabye.listeners.RecyclerFoodListener;
import com.example.diabye.models.Food;

import java.util.List;

public class FoodRecyclerViewAdapter extends RecyclerView.Adapter<FoodRecyclerViewAdapter.ViewHolder> {


    private final List<Food> foodArrayList;
    private final RecyclerFoodListener recyclerFoodListener;

    public FoodRecyclerViewAdapter(List<Food> foodArrayList, RecyclerFoodListener recyclerFoodListener) {
        this.foodArrayList = foodArrayList;
        this.recyclerFoodListener = recyclerFoodListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_row_recycle_view_new_entry_fragment,parent,false);
        return new FoodRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String foodName = foodArrayList.get(position).getFoodName();
        String unit = foodArrayList.get(position).getUnit();
        String quantity = String.valueOf(foodArrayList.get(position).getPortion());

        holder.foodUnit.setText(unit);
        holder.foodName.setText(foodName);
        holder.quantity.setText(String.format("%s x", quantity));

    }

    @Override
    public int getItemCount() {
        return foodArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView foodName, foodUnit, quantity;
        ImageButton deleteFoodButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName =  itemView.findViewById(R.id.foodNameNewEntryTV);
            foodUnit = itemView.findViewById(R.id.foodUnitNewEntryTV);
            quantity = itemView.findViewById(R.id.foodQuantityNewEntryTV);
            deleteFoodButton = itemView.findViewById(R.id.deleteFoodButton);

            deleteFoodButton.setOnClickListener(view -> {
                Food food = foodArrayList.get(getAdapterPosition());
                recyclerFoodListener.onDeleteButtonClicked(food);
            });

        }

    }
}
