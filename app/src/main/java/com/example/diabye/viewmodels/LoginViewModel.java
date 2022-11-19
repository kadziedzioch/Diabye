package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.User;
import com.example.diabye.repositories.UserRepository;

public class LoginViewModel  extends ViewModel {
    private final LiveData<Boolean> isLoginSuccessful;
    private final LiveData<String> errorMessage;
    private final LiveData<User> currentUser;
    private final UserRepository userRepository;
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

    }
}
