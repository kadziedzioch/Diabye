package com.example.diabye.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.example.diabye.R;
import com.example.diabye.fragments.NewEntryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        if(getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }

        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if(navDestination.getId()== R.id.newEntryFragment || navDestination.getId()== R.id.searchFoodFragment){
                if(bottomNavigationView.getVisibility()!=View.GONE){
                    bottomNavigationView.setVisibility(View.GONE);
                }
            }
            else{
                if(bottomNavigationView.getVisibility()!=View.VISIBLE){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}