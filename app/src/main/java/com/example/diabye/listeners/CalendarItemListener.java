package com.example.diabye.listeners;

import java.time.LocalDate;

public interface CalendarItemListener {
    void onCalendarCellClicked(int position, LocalDate date);
}
