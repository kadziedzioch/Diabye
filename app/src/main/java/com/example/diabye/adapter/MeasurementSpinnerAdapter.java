package com.example.diabye.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.diabye.R;
import com.example.diabye.models.Category;

import java.util.List;

public class MeasurementSpinnerAdapter extends ArrayAdapter<Category> {

    LayoutInflater layoutInflater;
    public MeasurementSpinnerAdapter(@NonNull Context context, int resource,@NonNull List<Category> categories) {
        super(context, resource,categories);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View rowView = layoutInflater.inflate(R.layout.measure_category_row, null,true);
        Category category = getItem(position);
        TextView categoryTv = (TextView)rowView.findViewById(R.id.categoryTV);
        categoryTv.setText(category.getCategoryName());
        categoryTv.setCompoundDrawablesRelativeWithIntrinsicBounds(category.getImageId(),0,0,0);
        return rowView;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.measure_category_row, parent,false);

        Category category = getItem(position);
        TextView categoryTv = (TextView)convertView.findViewById(R.id.categoryTV);
        categoryTv.setText(category.getCategoryName());
        categoryTv.setCompoundDrawablesRelativeWithIntrinsicBounds(category.getImageId(),0,0,0);

        return convertView;
    }

}
