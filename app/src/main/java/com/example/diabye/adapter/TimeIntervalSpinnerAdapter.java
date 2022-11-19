package com.example.diabye.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.diabye.R;
import com.example.diabye.models.TimeInterval;

import java.util.List;

public class TimeIntervalSpinnerAdapter extends ArrayAdapter<TimeInterval> {

    LayoutInflater layoutInflater;
    public TimeIntervalSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<TimeInterval> times) {
        super(context, resource,times);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = layoutInflater.inflate(R.layout.measure_category_row, null,true);
        TimeInterval timeInterval = getItem(position);
        TextView categoryTv = rowView.findViewById(R.id.categoryTV);
        categoryTv.setText(timeInterval.getIntervalName());
        categoryTv.setCompoundDrawablesRelativeWithIntrinsicBounds(timeInterval.getIntervalImageId(),0,0,0);
        return rowView;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.measure_category_row, parent,false);

        TimeInterval timeInterval = getItem(position);
        TextView categoryTv = convertView.findViewById(R.id.categoryTV);
        categoryTv.setText(timeInterval.getIntervalName());
        categoryTv.setCompoundDrawablesRelativeWithIntrinsicBounds(timeInterval.getIntervalImageId(),0,0,0);

        return convertView;
    }
}
