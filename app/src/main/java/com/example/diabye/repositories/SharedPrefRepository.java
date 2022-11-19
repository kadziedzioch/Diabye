package com.example.diabye.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.diabye.models.User;
import com.example.diabye.utils.Constants;

public class SharedPrefRepository {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPrefRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.DIABYE_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserInfo(User user){
        editor.putString(Constants.USER_NAME,user.getFirstName());
        editor.putString(Constants.USER_ID,user.getUserId());
        editor.apply();
    }

    public void cleanUserInfo(){
        editor.putString(Constants.USER_NAME,"");
        editor.putString(Constants.USER_ID,"");
        editor.apply();
    }




    public String getUsername(){
        return sharedPreferences.getString(Constants.USER_NAME,"");
    }
    public String getUserId(){
        return sharedPreferences.getString(Constants.USER_ID,"");
    }
}
