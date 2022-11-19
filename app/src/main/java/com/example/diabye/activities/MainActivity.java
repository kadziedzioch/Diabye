package com.example.diabye.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;
import android.view.View;
import com.example.diabye.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpNavigation();
        if(getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }



    }

    private void setUpNavigation(){
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        navController = navHostFragment != null ? navHostFragment.getNavController() : null;
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if(navDestination.getId()== R.id.newEntryFragment || navDestination.getId()== R.id.searchFoodFragment
                    || navDestination.getId()==R.id.exportFragment || navDestination.getId()==R.id.changeSettingsFragment){
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