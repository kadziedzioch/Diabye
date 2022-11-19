package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isOperationSuccessful = new MutableLiveData<>();
    public String errorMessage="";
    public ForgotPasswordViewModel() {
    }

    public LiveData<Boolean> getIsOperationSuccessful(){
        return isOperationSuccessful;
    }

    public void sendEmail(String email){

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    isOperationSuccessful.postValue(true);
                })
                .addOnFailureListener(e -> {
                    errorMessage = e.getMessage();
                    isOperationSuccessful.postValue(false);
                });
    }

}
