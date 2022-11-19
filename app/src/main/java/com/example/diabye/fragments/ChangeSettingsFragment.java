package com.example.diabye.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diabye.R;
import com.example.diabye.databinding.FragmentChangeSettingsBinding;
import com.example.diabye.models.TherapyType;
import com.example.diabye.models.UserSettings;
import com.example.diabye.repositories.SharedPrefRepository;
import com.example.diabye.utils.AppUtils;
import com.example.diabye.viewmodels.ChangeSettingsViewModel;

import java.util.Locale;
import java.util.Objects;

public class ChangeSettingsFragment extends Fragment {

    private FragmentChangeSettingsBinding binding;
    private ChangeSettingsViewModel changeSettingsViewModel;
    private SharedPrefRepository sharedPrefRepository;
    public ChangeSettingsFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeSettingsViewModel = new ViewModelProvider(ChangeSettingsFragment.this).get(ChangeSettingsViewModel.class);
        sharedPrefRepository = new SharedPrefRepository(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChangeSettingsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.changeSettingsToolbar.setNavigationOnClickListener(view1 -> {
            NavHostFragment.findNavController(ChangeSettingsFragment.this)
                    .navigate(R.id.action_changeSettingsFragment_to_settingsFragment);
        });
        changeSettingsViewModel.getUserSettingsData(sharedPrefRepository.getUserId());
        changeSettingsViewModel.getUserSettings().observe(getViewLifecycleOwner(), this::setUpData);
        changeSettingsViewModel.getIsUpdatingSuccessful().observe(getViewLifecycleOwner(), this::showMessage);
        binding.changeUserSettingsButton.setOnClickListener(view12 -> {
            saveUserSettings();
        });

    }

    private void showMessage(Boolean isSuccessful){
        if(isSuccessful){
            AppUtils.showMessage(requireActivity(),binding.hypoChangeEditText,
                    "Successfully updated data", false);
        }
        else{
            AppUtils.showMessage(requireActivity(),binding.hypoChangeEditText,
                    changeSettingsViewModel.getErrorMessage().getValue(), true);
        }
    }

    private void saveUserSettings(){
        if(changeSettingsViewModel.getUserSettings().getValue()!=null && validateData()){
            UserSettings userSettings = new UserSettings();
            userSettings.setUserId(sharedPrefRepository.getUserId());
            if(binding.pumpChangeButton.isChecked()){
                userSettings.setTreatmentType(TherapyType.PUMP.name());
            }
            else{
                userSettings.setTreatmentType(TherapyType.PEN.name());
            }
            String highSugarRange = binding.highRangeChangeEditText.getText().toString();
            String lowSugarRange = binding.lowRangeChangeEditText.getText().toString();
            String hyper = binding.hyperChangeEditText.getText().toString();
            String hypo = binding.hypoChangeEditText.getText().toString();

            userSettings.setHighSugarRangeLevel(Double.parseDouble(highSugarRange));
            userSettings.setLowSugarRangeLevel(Double.parseDouble(lowSugarRange));
            userSettings.setHyperValue(Double.parseDouble(hyper));
            userSettings.setHypoValue(Double.parseDouble(hypo));
            userSettings.setUserSettingsId(changeSettingsViewModel.getUserSettings().getValue().getUserSettingsId());

            changeSettingsViewModel.updateUserSettings(userSettings);
        }
        else{
            AppUtils.showMessage(requireActivity(),binding.hypoChangeEditText,
                    "Wait for data to load properly",true);
        }
    }

    private boolean validateData() {
        String highSugarRange = binding.highRangeChangeEditText.getText().toString();
        String lowSugarRange = binding.lowRangeChangeEditText.getText().toString();
        String hyper = binding.hyperChangeEditText.getText().toString();
        String hypo = binding.hypoChangeEditText.getText().toString();

        if(TextUtils.isEmpty(highSugarRange)){
            AppUtils.showMessage(requireActivity(),binding.hypoChangeEditText,
                    "Enter high sugar range!",true);
            return false;
        }
        if(TextUtils.isEmpty(lowSugarRange)){
            AppUtils.showMessage(requireActivity(),binding.hypoChangeEditText,
                    "Enter low sugar range!",true);
            return false;
        }
        if(TextUtils.isEmpty(hyper)){
            AppUtils.showMessage(requireActivity(),binding.hypoChangeEditText,
                    "Enter hyper!",true);
            return false;
        }
        if(TextUtils.isEmpty(hypo)){
            AppUtils.showMessage(requireActivity(),binding.hypoChangeEditText,
                    "Enter hypo!",true);
            return false;
        }

        return true;
    }


    private void setUpData(UserSettings userSettings){
        binding.highRangeChangeEditText.setText(String.format(Locale.getDefault(),"%.0f", userSettings.getHighSugarRangeLevel()));
        binding.lowRangeChangeEditText.setText(String.format(Locale.getDefault(),"%.0f", userSettings.getLowSugarRangeLevel()));
        binding.hyperChangeEditText.setText(String.format(Locale.getDefault(),"%.0f", userSettings.getHyperValue()));
        binding.hypoChangeEditText.setText(String.format(Locale.getDefault(),"%.0f", userSettings.getHypoValue()));

        String therapyType = userSettings.getTreatmentType();
        if(Objects.equals(therapyType, TherapyType.PUMP.name())){
            binding.pumpChangeButton.setChecked(true);
        }
        else{
            binding.pensChangeButton.setChecked(true);
        }
    }
}