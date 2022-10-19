package com.example.diabye.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diabye.R;
import com.example.diabye.databinding.FragmentMainBinding;
import com.example.diabye.repositories.SharedPrefRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class MainFragment extends Fragment {

    private FloatingActionButton fab;
    private FragmentMainBinding binding;
    private SharedPrefRepository sharedPrefRepository;
    public MainFragment() {
    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefRepository = new SharedPrefRepository(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        binding.addNewEntryButton.setOnClickListener(view1 -> NavHostFragment.findNavController(MainFragment.this)
                .navigate(R.id.action_mainFragment_to_newEntryFragment));
        String name = sharedPrefRepository.getUsername();
        binding.helloTv.setText(String.format("Hello, %s!", name));
        LocalDate date = LocalDate.now();
        binding.dateTodayTv.setText(date.format((DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG))));

        return view;
    }
}