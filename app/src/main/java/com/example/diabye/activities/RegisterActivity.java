package com.example.diabye.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.diabye.R;
import com.example.diabye.databinding.ActivityRegisterBinding;
import com.example.diabye.utils.AppUtils;
import com.example.diabye.viewmodels.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {


    private ActivityRegisterBinding binding;
    private RegisterViewModel registerViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if(getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        registerViewModel.init();

        binding.loginTextView.setOnClickListener(myView -> onBackPressed());
        binding.registerButton.setOnClickListener(view1 -> {
            AppUtils.hideKeyboard(binding.emailRegisterEditText);
            boolean isValid = validateInput();
            if(isValid){
                binding.registerProgressBar.setVisibility(View.VISIBLE);
                String email = binding.emailRegisterEditText.getText().toString().trim();
                String password = binding.passRegisterEditText.getText().toString().trim();
                String name = binding.personNameEditText.getText().toString().trim();
                registerViewModel.registerUser(email,password,name);
            }
        });

        registerViewModel.getIsRegisterSuccessful().observe(this, isSuccessful -> {
            binding.registerProgressBar.setVisibility(View.INVISIBLE);
            if(isSuccessful){
                AppUtils.showMessage(RegisterActivity.this,binding.confirmPassRegisterEditText,
                        getResources().getString(R.string.register_successful),false);
                finish();
            }
            else{
                AppUtils.showMessage(RegisterActivity.this,binding.confirmPassRegisterEditText,
                        registerViewModel.getRegisterErrorMessage().getValue(),true);
            }
        });
    }



    public boolean validateInput(){
        if(TextUtils.isEmpty(binding.personNameEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.personNameEditText,getResources().getString(R.string.enter_name_text),true);
            return false;
        }
        else if(TextUtils.isEmpty(binding.emailRegisterEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.personNameEditText,getResources().getString(R.string.enter_email_text),true);
            return false;
        }
        else if(TextUtils.isEmpty(binding.passRegisterEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.personNameEditText,getResources().getString(R.string.enter_pass_text),true);
            return false;
        }
        else if(TextUtils.isEmpty(binding.confirmPassRegisterEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.personNameEditText,getResources().getString(R.string.enter_confirm_pass_text),true);
            return false;
        }
        else if(!binding.confirmPassRegisterEditText.getText().toString().trim().equals(binding.passRegisterEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.personNameEditText,getResources().getString(R.string.pass_cofirmpass_dont_match),true);
            return false;
        }
        else{
            return true;
        }
    }
}