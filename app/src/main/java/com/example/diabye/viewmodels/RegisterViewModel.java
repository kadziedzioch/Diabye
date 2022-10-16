package com.example.diabye.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.User;
import com.example.diabye.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterViewModel extends ViewModel {

    private LiveData<Boolean> isRegisterSuccessful;
    private LiveData<String> registerErrorMessage;
    public String errorMessage="";
    private FirebaseAuth firebaseAuth;
    private UserRepository userRepository;

    public RegisterViewModel() {
        userRepository = new UserRepository();
        isRegisterSuccessful = userRepository.getIsRegisteringUserSuccessful();
        registerErrorMessage = userRepository.getRegisterErrorMessage();
    }
    public void init(){
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Boolean> getIsRegisterSuccessful(){
        return isRegisterSuccessful;
    }
    public LiveData<String> getRegisterErrorMessage(){
        return registerErrorMessage;
    }

    public void registerUser(String email, String password, String name){
        userRepository.registerUser(email,password,name);
    }
}
