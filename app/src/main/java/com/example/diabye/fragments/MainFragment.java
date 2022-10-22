package com.example.diabye.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diabye.R;
import com.example.diabye.databinding.FragmentMainBinding;
import com.example.diabye.models.Measurement;
import com.example.diabye.repositories.SharedPrefRepository;
import com.example.diabye.viewmodels.MainActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;

public class MainFragment extends Fragment {

    private FloatingActionButton fab;
    private FragmentMainBinding binding;
    private SharedPrefRepository sharedPrefRepository;
    private MainActivityViewModel mainActivityViewModel;

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
        setUpView();

        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);


        Date today = Timestamp.now().toDate();
        Log.d("TAG", "onCreateView: "+today);

        mainActivityViewModel.getMeasurements(today).observe(getViewLifecycleOwner(), measurements -> {
            if(measurements!=null && measurements.size()>0){
                binding.avgGlucoseMainTv.setText(String.valueOf(calculateMeanSugar(measurements)));
            }

        });

        return binding.getRoot();
    }

    private void setUpView(){
        binding.addNewEntryButton.setOnClickListener(view1 -> NavHostFragment.findNavController(MainFragment.this)
                .navigate(R.id.action_mainFragment_to_newEntryFragment));
        String name = sharedPrefRepository.getUsername();
        binding.helloTv.setText(String.format("Hello, %s!", name));
        LocalDate date = LocalDate.now();
        binding.dateTodayTv.setText(date.format((DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG))));
    }

    private double calculateMeanSugar(List<Measurement> measurements){
        double mean =0;
        double count =0;
        for(Measurement m: measurements){
            if(m.getSugarLevel()!=0){
                mean+=m.getSugarLevel();
                count++;
            }
        }
        if(count==0){
            return mean;
        }
        return mean/count;
    }


}