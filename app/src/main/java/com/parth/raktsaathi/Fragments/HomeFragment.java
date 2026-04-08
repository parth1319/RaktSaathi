package com.parth.raktsaathi.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parth.raktsaathi.HealthTipsActivity;
import com.parth.raktsaathi.NotificationActivity;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.SliderAdapter;

public class HomeFragment extends Fragment {

    private ViewPager2 slider;
    private Handler handler;
    private Runnable runnable;


    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔥 TOOLBAR SETUP
        Toolbar toolbar = view.findViewById(R.id.toolbarHome);

        setHasOptionsMenu(true); // 🔥 VERY IMPORTANT

        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar()
                    .setDisplayShowTitleEnabled(true);
        }

        // 🔥 SLIDER SETUP
        slider = view.findViewById(R.id.imageSlider);
        slider.setClipToPadding(false);
        slider.setClipChildren(false);
        slider.setOffscreenPageLimit(3);
        slider.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        int[] images = {
                R.drawable.rs_homefragment_slider1,
                R.drawable.rs_homefragment_slider2,
                R.drawable.rs_homefragment_slider3
        };

        SliderAdapter adapter = new SliderAdapter(images);
        slider.setAdapter(adapter);

        handler = new Handler(Looper.getMainLooper());

        startSlider(adapter);

        // 🔥 BUTTONS
        CardView btnDonate = view.findViewById(R.id.btnDonate);
        CardView btnRequest = view.findViewById(R.id.btnRequest);
        CardView btnHealthTips = view.findViewById(R.id.btnHealthTips);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.homeBottomNavigationView);

        btnDonate.setOnClickListener(v ->
                bottomNav.setSelectedItemId(R.id.homebottomnavDonate));

        btnRequest.setOnClickListener(v ->
                bottomNav.setSelectedItemId(R.id.homebottomnavRequests));

        btnHealthTips.setOnClickListener(v ->
            startActivity(new Intent(getActivity(), HealthTipsActivity.class))
            );

        return view;
    }

    // 🔥 AUTO SLIDER
    private void startSlider(SliderAdapter adapter) {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (slider == null) return;

                int current = slider.getCurrentItem();
                int total = adapter.getItemCount();

                slider.setCurrentItem((current + 1) % total);

                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    // 🔥 MENU ICON LOAD
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // 🔥 ICON CLICK
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_notification) {

            startActivity(new Intent(getActivity(), NotificationActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // 🔥 MEMORY FIX
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        slider = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        androidx.appcompat.app.ActionBar actionBar = ((androidx.appcompat.app.AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }


}