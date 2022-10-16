package com.example.diabye.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.example.diabye.R;
import com.example.diabye.databinding.ActivityUserSettingsBinding;
import com.example.diabye.models.TherapyType;
import com.example.diabye.models.User;
import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.SharedPrefRepository;
import com.example.diabye.utils.AppUtils;
import com.example.diabye.viewmodels.UserSettingsViewModel;

import java.util.Objects;

public class UserSettingsActivity extends AppCompatActivity {

    private ActivityUserSettingsBinding binding;
    private UserSettingsViewModel userSettingsViewModel;
    private SharedPrefRepository sharedPrefRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        binding = ActivityUserSettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if(getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }

        sharedPrefRepository = new SharedPrefRepository(this);
        userSettingsViewModel = new ViewModelProvider(this).get(UserSettingsViewModel.class);

        userSettingsViewModel.isAddingSuccessful.observe(this, isAddingSuccessful -> {
            if(isAddingSuccessful){
                userSettingsViewModel.updateUserIsCompletedField(sharedPrefRepository.getUserId());
                Intent intent = new Intent(UserSettingsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                AppUtils.showMessage(UserSettingsActivity.this,binding.highRangeEditText,
                        userSettingsViewModel.errorMessage.getValue(),true);
            }
        });

        binding.submitUserSettingsButton.setOnClickListener(view1 -> {
            AppUtils.hideKeyboard(view1);
            if(validateInput()){
                saveUserInfo();
            }
        });
        userSettingsViewModel.selectTherapyType(TherapyType.PEN.name());
        binding.therapyTypeGroup.setOnCheckedChangeListener((radioGroup, selectedButtonId) -> {
            if(selectedButtonId == R.id.pens_button){
                userSettingsViewModel.selectTherapyType(TherapyType.PEN.name());
            }
            else{
                userSettingsViewModel.selectTherapyType(TherapyType.PUMP.name());
            }

        });

    }

    private void saveUserInfo(){
        double hypoValue =Double.parseDouble(binding.hypoEditText.getText().toString().trim());
        double hyperValue =Double.parseDouble(binding.hyperEditText.getText().toString().trim());
        double minRange =Double.parseDouble(binding.lowRangeEditText.getText().toString().trim());
        double maxRange =Double.parseDouble(binding.highRangeEditText.getText().toString().trim());
        String userId = sharedPrefRepository.getUserId();
        if(!Objects.equals(userId, "")){
            UserSettings userSettings = new UserSettings(minRange,maxRange,hypoValue,hyperValue,userId);
            userSettingsViewModel.saveUserSettings(userSettings);
        }
    }

    private boolean validateInput(){
        if(TextUtils.isEmpty(binding.highRangeEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.highRangeEditText,
                    getResources().getString(R.string.enter_range_max_text),true);
            return false;
        }
        else if(TextUtils.isEmpty(binding.lowRangeEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.highRangeEditText,
                    getResources().getString(R.string.enter_range_low_text),true);
            return false;
        }
        else if(TextUtils.isEmpty(binding.hyperEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.highRangeEditText,
                    getResources().getString(R.string.enter_hiper_text),true);
            return false;
        }
        else if(TextUtils.isEmpty(binding.hypoEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.highRangeEditText,
                    getResources().getString(R.string.enter_hypo_text),true);
            return false;
        }
        else{
            return true;
        }
    }
}