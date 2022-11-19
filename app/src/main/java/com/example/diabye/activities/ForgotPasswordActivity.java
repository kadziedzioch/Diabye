package com.example.diabye.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.diabye.R;
import com.example.diabye.databinding.ActivityForgotPasswordBinding;
import com.example.diabye.utils.AppUtils;
import com.example.diabye.viewmodels.ForgotPasswordViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private ForgotPasswordViewModel forgotPasswordViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if(getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }
        forgotPasswordViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);
        binding.sendPasswordButton.setOnClickListener(this::sendMessage);
        forgotPasswordViewModel.getIsOperationSuccessful().observe(this, this::displayMessage);
    }

    public void sendMessage(View view){
        AppUtils.hideKeyboard(view);
        if(validateEntry()){
            forgotPasswordViewModel.sendEmail(binding.forgotEmailEditText.getText().toString().trim());
        }
    }

    public void displayMessage(Boolean isSuccessful){
        if(isSuccessful){
            AppUtils.showMessage(ForgotPasswordActivity.this, binding.forgotEmailEditText,
                    getResources().getString(R.string.email_sent_text),false);
        }
        else{
            AppUtils.showMessage(ForgotPasswordActivity.this, binding.forgotEmailEditText,
                    forgotPasswordViewModel.errorMessage,true);
        }
    }

    public boolean validateEntry(){
        if(TextUtils.isEmpty(binding.forgotEmailEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.forgotEmailEditText,
                    getResources().getString(R.string.enter_email_text),true);
            return false;
        }
        else{
            return true;
        }
    }


}