package com.example.diabye.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Locale;

public class PercentValueFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return String.format(Locale.getDefault(), "%d%%", (int) value);
    }
}
