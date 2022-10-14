package com.example.diabye.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<Boolean> isRegisterSuccessful = new MutableLiveData<>();
    public String errorMessage="";
    private FirebaseAuth firebaseAuth;

    public RegisterViewModel() {


    }
    public void init(){
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Boolean> getIsRegisterSuccessful(){
        return isRegisterSuccessful;
    }

    public void registerUser(String email, String password){

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult ->{
                    isRegisterSuccessful.postValue(true);
                    FirebaseAuth.getInstance().signOut();
                })
                .addOnFailureListener(e -> {
                    errorMessage = e.getMessage();
                    isRegisterSuccessful.postValue(false);
                });
    }
}
