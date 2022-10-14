package com.example.diabye.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.example.diabye.R;
import com.google.android.material.snackbar.Snackbar;

public class AppUtils {

    public static void showMessage(Context context, View view, String message, boolean isError){
        Snackbar snackbar = Snackbar.make(view,message,Snackbar.LENGTH_LONG);
        if(isError){
            snackbar.setBackgroundTint(ContextCompat.getColor(context,R.color.error_background_color));
        }
        else{
            snackbar.setBackgroundTint(ContextCompat.getColor(context,R.color.success_background_color));
        }
        snackbar.show();
    }

    public static void hideKeyboard(View view){
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
