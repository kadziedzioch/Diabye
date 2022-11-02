package com.example.diabye.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.UserSettingRepository;

public class ExportFragmentViewModel extends ViewModel {

    private final UserSettingRepository userSettingRepository;
    private final LiveData<UserSettings> userSettings;
    public ExportFragmentViewModel() {
        userSettingRepository = new UserSettingRepository();
        userSettings = userSettingRepository.getUserSettings();
    }

    public LiveData<UserSettings> getUserSettings() {
        return userSettings;
    }

    public void searchUserSettings(String userId){
        userSettingRepository.getUserSettings(userId);
    }
}
