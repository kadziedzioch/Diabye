package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.User;
import com.example.diabye.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel  extends ViewModel {
    private LiveData<Boolean> isLoginSuccessful;
    private LiveData<String> errorMessage;
    private LiveData<User> currentUser;
    private UserRepository userRepository;
    public LoginViewModel() {
        userRepository = new UserRepository();
        isLoginSuccessful = userRepository.getIsLoginUserSuccessful();
        errorMessage = userRepository.getLoginErrorMessage();
        currentUser = userRepository.getUser();

    }

    public LiveData<Boolean> getIsLoginSuccessful(){
        return isLoginSuccessful;
    }
    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }
    public LiveData<User> getCurrentUser(){
        return currentUser;
    }

    public void loginUser(String email, String password){
        userRepository.loginUser(email,password);
//        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
//                .addOnSuccessListener(authResult ->{
//                    isLoginSuccessful.postValue(true);
//                })
//                .addOnFailureListener(e -> {
//                    errorMessage = e.getMessage();
//                    isLoginSuccessful.postValue(false);
//                });
    }
}
