package com.example.diabye.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.diabye.R;
import com.example.diabye.databinding.ActivityLoginBinding;
import com.example.diabye.databinding.ActivityRegisterBinding;
import com.example.diabye.models.User;
import com.example.diabye.repositories.SharedPrefRepository;
import com.example.diabye.utils.AppUtils;
import com.example.diabye.viewmodels.LoginViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if(getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }

        SharedPrefRepository sharedPrefRepository = new SharedPrefRepository(this);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getIsLoginSuccessful().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccessful) {
                binding.loginProgressBar.setVisibility(View.INVISIBLE);
                if(isSuccessful){
                    User user = loginViewModel.getCurrentUser().getValue();
                    Intent intent;
                    if(user!=null){
                        sharedPrefRepository.saveUserInfo(user);
                        if (user.getIsCompleted() == 0) {
                            intent = new Intent(LoginActivity.this,UserSettingsActivity.class);
                        }else{
                            intent = new Intent(LoginActivity.this,MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    AppUtils.showMessage(LoginActivity.this,binding.titleLoginTextView,
                            loginViewModel.getErrorMessage().getValue(),true);
                }
            }
        });

        binding.loginButton.setOnClickListener(this);
        binding.registerTextView.setOnClickListener(this);
        binding.forgotPassTextView.setOnClickListener(this);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean validateEntries(){
        if(TextUtils.isEmpty(binding.emailEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.emailEditText,getResources().getString(R.string.enter_email_text),true);
            return false;
        }
        else if(TextUtils.isEmpty(binding.passwordEditText.getText().toString().trim())){
            AppUtils.showMessage(this,binding.emailEditText,getResources().getString(R.string.enter_pass_text),true);
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        if(view!=null){
            switch(view.getId()){
                case R.id.registerTextView:
                    Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    break;
                case R.id.loginButton:
                    AppUtils.hideKeyboard(view);
                    loginUser();
                    break;
                case R.id.forgotPassTextView:
                    startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
                    break;
            }
        }
    }

    private void loginUser() {
        if(validateEntries()){
            binding.loginProgressBar.setVisibility(View.VISIBLE);
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();
            loginViewModel.loginUser(email,password);
        }
    }
}