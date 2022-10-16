package com.example.diabye.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.diabye.models.UserSettings;
import com.example.diabye.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserSettingRepository {

    private MutableLiveData<Boolean> isAddingSuccessful = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private FirebaseFirestore mFirestore;
    public UserSettingRepository() {
        mFirestore = FirebaseFirestore.getInstance();
    }


    public LiveData<Boolean> getIsAddingSuccessful(){
        return isAddingSuccessful;
    }

    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }

    public void saveUserSettings(UserSettings userSettings){

        mFirestore.collection(Constants.USER_SETTINGS).add(userSettings)
                .addOnSuccessListener(documentReference -> {
                    userSettings.setUserSettingsId(documentReference.getId());
                    isAddingSuccessful.postValue(true);

                })
                .addOnFailureListener(e -> {
                    errorMessage.postValue(e.getMessage());
                    isAddingSuccessful.postValue(false);
                });
    }

    public void updateUserIsCompleted(String userId){
        mFirestore.collection(Constants.USERS).document(userId)
                .update("isCompleted",1);
    }
}
