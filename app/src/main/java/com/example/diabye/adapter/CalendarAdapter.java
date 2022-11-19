package com.example.diabye.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabye.R;
import com.example.diabye.listeners.CalendarItemListener;
import com.example.diabye.utils.CalendarUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder>{

    private final ArrayList<LocalDate> days;
    private final CalendarItemListener calendarItemListener;
    private final Context context;

    public CalendarAdapter(ArrayList<LocalDate> days, CalendarItemListener calendarItemListener, Context context)
    {
        this.days = days;
        this.calendarItemListener = calendarItemListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        return new ViewHolder(view, calendarItemListener, days);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final LocalDate date = days.get(position);
        if(date == null)
            holder.dayOfMonth.setText("");
        else
        {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if(date.equals(CalendarUtils.selectedDate)){
                holder.parentView.setBackgroundColor(ContextCompat.getColor(context,R.color.background_color));
                holder.dayOfMonth.setTextColor(ContextCompat.getColor(context,R.color.main_blue));
            }
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ArrayList<LocalDate> days;
        public final View parentView;
        public final TextView dayOfMonth;
        private final CalendarItemListener myCalendarItemListener;
        public ViewHolder(@NonNull View itemView, CalendarItemListener myCalendarItemListener, ArrayList<LocalDate> days) {
            super(itemView);

            parentView = itemView.findViewById(R.id.parentView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            this.myCalendarItemListener = myCalendarItemListener;
            itemView.setOnClickListener(this);
            this.days = days;
        }

        @Override
        public void onClick(View view) {
            myCalendarItemListener.onCalendarCellClicked(getAdapterPosition(), days.get(getAdapterPosition()));
        }
    }


}
