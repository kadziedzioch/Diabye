package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.UserSettingRepository;

public class ChangeSettingsViewModel extends ViewModel {

    private final UserSettingRepository userSettingRepository;
    private final LiveData<UserSettings> userSettings;
    private final LiveData<Boolean> isUpdatingSuccessful;
    private final LiveData<String> errorMessage;
    public ChangeSettingsViewModel() {
        userSettingRepository = new UserSettingRepository();
        userSettings = userSettingRepository.getUserSettings();
        isUpdatingSuccessful = userSettingRepository.getIsUpdatingSuccessful();
        errorMessage = userSettingRepository.getErrorMessage();
    }

    public LiveData<Boolean> getIsUpdatingSuccessful() {
        return isUpdatingSuccessful;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<UserSettings> getUserSettings() {
        return userSettings;
    }

    public void getUserSettingsData(String userId){
        userSettingRepository.getUserSettings(userId);
    }

    public void updateUserSettings(UserSettings userSettings){
        userSettingRepository.updateUserSettings(userSettings);
    }
}
