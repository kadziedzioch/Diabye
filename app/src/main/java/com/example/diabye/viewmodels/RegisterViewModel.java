package com.example.diabye.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.diabye.repositories.UserRepository;

public class RegisterViewModel extends ViewModel {

    private final LiveData<Boolean> isRegisterSuccessful;
    private final LiveData<String> registerErrorMessage;
    private final UserRepository userRepository;

    public RegisterViewModel() {
        userRepository = new UserRepository();
        isRegisterSuccessful = userRepository.getIsRegisteringUserSuccessful();
        registerErrorMessage = userRepository.getRegisterErrorMessage();
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
