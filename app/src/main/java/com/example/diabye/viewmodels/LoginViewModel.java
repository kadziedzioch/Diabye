package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel  extends ViewModel {
    private MutableLiveData<Boolean> isLoginSuccessful = new MutableLiveData<>();
    public String errorMessage="";
    public LoginViewModel() {
    }

    public LiveData<Boolean> getIsLoginSuccessful(){
        return isLoginSuccessful;
    }

    public void loginUser(String email, String password){

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult ->{
                    isLoginSuccessful.postValue(true);
                })
                .addOnFailureListener(e -> {
                    errorMessage = e.getMessage();
                    isLoginSuccessful.postValue(false);
                });
    }
}
