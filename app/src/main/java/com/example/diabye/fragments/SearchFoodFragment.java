package com.example.diabye.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.diabye.R;
import com.example.diabye.adapter.FoodRetrofitRecyclerViewAdapter;
import com.example.diabye.databinding.DialogAddFoodBinding;
import com.example.diabye.databinding.FragmentSearchFoodBinding;
import com.example.diabye.listeners.RecyclerFoodRetrofitListener;
import com.example.diabye.models.Food;
import com.example.diabye.models.retrofitModels.FoodRetrofit;
import com.example.diabye.viewmodels.NewEntryViewModel;
import com.example.diabye.viewmodels.SearchFoodFragmentViewModel;

import java.util.List;
import java.util.Objects;


public class SearchFoodFragment extends Fragment implements RecyclerFoodRetrofitListener {

    private RecyclerView recyclerView;
    private FragmentSearchFoodBinding binding;
    private FoodRetrofitRecyclerViewAdapter recyclerViewAdapter;
    private NewEntryViewModel newEntryViewModel;
    public SearchFoodFragment() {

    }

    public static SearchFoodFragment newInstance() {
        SearchFoodFragment fragment = new SearchFoodFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchFoodBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        recyclerView = binding.recyclerViewSearchFood;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchFoodFragmentViewModel searchFoodFragmentViewModel= new ViewModelProvider(this)
                .get(SearchFoodFragmentViewModel.class);
        newEntryViewModel = new ViewModelProvider(requireActivity())
                .get(NewEntryViewModel.class);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFoodFragmentViewModel.setFoodName(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFoodFragmentViewModel.setFoodName(newText);
                return false;
            }
        });

        binding.toolbarSearchFoodFragment.setNavigationOnClickListener(view1 -> {
            requireActivity().onBackPressed();
        });

        searchFoodFragmentViewModel.getFoodRetrofitList().observe(getViewLifecycleOwner(), foodRetrofits -> {
            if(binding.progressBarSearchFood.getVisibility() == View.VISIBLE){
                binding.progressBarSearchFood.setVisibility(View.INVISIBLE);
            }
            if(foodRetrofits !=null){
                recyclerViewAdapter = new FoodRetrofitRecyclerViewAdapter(foodRetrofits, SearchFoodFragment.this);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });

    }

    @Override
    public void OnAddRetrofitFoodClicked(FoodRetrofit food) {
        showDialog(food);
    }


    private void showDialog(FoodRetrofit food){
        final Dialog dialog = new Dialog(requireActivity());
        DialogAddFoodBinding dialogAddFoodBinding= DialogAddFoodBinding.inflate(LayoutInflater.from(requireActivity()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogAddFoodBinding.getRoot());

        dialogAddFoodBinding.foodNameTVDialog.setText(food.getItem_name());
        dialogAddFoodBinding.cancelActionButton.setOnClickListener(view -> {
            dialog.dismiss();
        });
        String unit = "g";
        if(food.getNf_serving_weight_grams()==0){
            unit = food.getNf_serving_size_unit();
        }
        dialogAddFoodBinding.addFoodButtonDialog.setOnClickListener(view -> {
            if(!TextUtils.isEmpty(dialogAddFoodBinding.quantityETDialog.getText().toString().trim())){
                Food newFood = new Food();
                newFood.setFoodName(food.getItem_name());
                newFood.setBrandName(food.getBrand_name());
                double portion = Double.parseDouble(dialogAddFoodBinding.quantityETDialog.getText().toString().trim());

                if(food.getNf_serving_weight_grams()!=0){
                    newFood.setCarbs(food.getNf_total_carbohydrate()*portion/food.getNf_serving_weight_grams());
                    newFood.setFats(food.getNf_total_fat()*portion/food.getNf_serving_weight_grams());
                    newFood.setProtein(food.getNf_protein()*portion/food.getNf_serving_weight_grams());
                    newFood.setCalories(food.getNf_calories()*portion/food.getNf_serving_weight_grams());
                }
                else{
                    newFood.setCarbs(food.getNf_total_carbohydrate()*portion/food.getNf_serving_size_qty());
                    newFood.setFats(food.getNf_total_fat()*portion/food.getNf_serving_size_qty());
                    newFood.setProtein(food.getNf_protein()*portion/food.getNf_serving_size_qty());
                    newFood.setCalories(food.getNf_calories()*portion/food.getNf_serving_size_qty());
                }

                newFood.setPortion(portion);
                String myunit = "g";
                if(food.getNf_serving_weight_grams()==0){
                    myunit = food.getNf_serving_size_unit();
                }
                newFood.setUnit(myunit);
                newEntryViewModel.addFoodToList(newFood);
                dialog.dismiss();
                requireActivity().onBackPressed();
            }
        });


        dialogAddFoodBinding.unitTVDialog.setText(unit);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogAddFoodBinding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }


}