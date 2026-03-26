package com.parth.raktsaathi.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parth.raktsaathi.NotificationActivity;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.SliderAdapter;

public class HomeFragment extends Fragment {

    private ViewPager2 slider;
    private Handler handler;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔥 Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbarHome);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        if (getActivity() != null) {
            requireActivity().setTitle("Raktsaathi+");
        }

        // 🔥 Slider
        slider = view.findViewById(R.id.imageSlider);

        int[] images = {
                R.drawable.rs_homefragment_slider1,
                R.drawable.rs_homefragment_slider2,
                R.drawable.rs_homefragment_slider3
        };

        SliderAdapter adapter = new SliderAdapter(images);
        slider.setAdapter(adapter);

        handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (slider == null) return;

                int current = slider.getCurrentItem();
                int total = adapter.getItemCount();

                if (current < total - 1) {
                    slider.setCurrentItem(current + 1);
                } else {
                    slider.setCurrentItem(0);
                }

                handler.postDelayed(this, 3000);
            }
        };

        handler.postDelayed(runnable, 3000);

        // 🔥 Buttons
        CardView btnDonate = view.findViewById(R.id.btnDonate);
        CardView btnRequest = view.findViewById(R.id.btnRequest);
        CardView btnHealthTips = view.findViewById(R.id.btnHealthTips);

        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.homeBottomNavigationView);

            if (bottomNav != null) {
                btnDonate.setOnClickListener(v ->
                        bottomNav.setSelectedItemId(R.id.homebottomnavDonate));

                btnRequest.setOnClickListener(v ->
                        bottomNav.setSelectedItemId(R.id.homebottomnavRequests));
            }
        }

        // 🔥 Health Tips Click (optional)
        btnHealthTips.setOnClickListener(v -> {
            // Tu future madhe activity add karu shakto
            // startActivity(new Intent(getActivity(), HealthTipsActivity.class));
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_notification) {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        slider = null;
    }
}