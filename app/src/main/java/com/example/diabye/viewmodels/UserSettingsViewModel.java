package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.UserSettingRepository;

public class UserSettingsViewModel extends ViewModel {

    private MutableLiveData<String> selectedTherapyType = new MutableLiveData<>();
    public LiveData<String> errorMessage;
    public LiveData<Boolean> isAddingSuccessful;
    private UserSettingRepository userSettingRepository;
    public UserSettingsViewModel() {
        userSettingRepository = new UserSettingRepository();
        isAddingSuccessful = userSettingRepository.getIsAddingSuccessful();
        errorMessage = userSettingRepository.getErrorMessage();
    }
    public void selectTherapyType(String therapyType){
        selectedTherapyType.postValue(therapyType);
    }

    public void saveUserSettings(UserSettings userSettings){
        if(selectedTherapyType.getValue() != null){
            userSettings.setTreatmentType(selectedTherapyType.getValue());
            userSettingRepository.saveUserSettings(userSettings);
        }
    }

    public void updateUserIsCompletedField(String userId){
        userSettingRepository.updateUserIsCompleted(userId);
    }


}
