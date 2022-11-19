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
import com.example.diabye.models.Category;
import java.util.List;

public class DocumentTypeSpinnerAdapter extends ArrayAdapter<Category> {

    LayoutInflater layoutInflater;
    public DocumentTypeSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Category> categories) {
        super(context, resource,categories);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        @SuppressLint({"ViewHolder", "InflateParams"})
        View rowView = layoutInflater.inflate(R.layout.measure_category_row, null,true);
        Category category = getItem(position);
        TextView categoryTv = rowView.findViewById(R.id.categoryTV);
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
        TextView categoryTv =convertView.findViewById(R.id.categoryTV);
        categoryTv.setText(category.getCategoryName());

        return convertView;
    }
}
