package com.example.diabye.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.diabye.R;
import com.example.diabye.activities.LoginActivity;
import com.example.diabye.activities.MainActivity;
import com.example.diabye.databinding.FragmentSettingsBinding;
import com.example.diabye.repositories.SharedPrefRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SettingsFragment extends Fragment implements View.OnTouchListener {

    private FragmentSettingsBinding binding;
    private SharedPrefRepository sharedPrefRepository;
    public SettingsFragment() {

    }


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefRepository = new SharedPrefRepository(requireActivity());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater,container,false);

        binding.exportCardView.setOnTouchListener(this);
        binding.changeSettingsCardView.setOnTouchListener(this);
        binding.logoutCardView.setOnTouchListener(this);

        binding.logoutCardView.setOnClickListener(view -> {
           logOut();
        });

        binding.exportCardView.setOnClickListener(view -> NavHostFragment.findNavController(SettingsFragment.this)
                .navigate(R.id.action_settingsFragment_to_exportFragment));




        return binding.getRoot();
    }


    private void logOut(){
        FirebaseAuth.getInstance().signOut();
        sharedPrefRepository.cleanUserInfo();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        CardView cardView = view.findViewById(view.getId());
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.very_light_blue));

        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.white));
            view.performClick();
        }
        return true;
    }
}