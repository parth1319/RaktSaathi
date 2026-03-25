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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parth.raktsaathi.NotificationActivity;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.SliderAdapter;

public class HomeFragment extends Fragment {

    private boolean isEligible = false;

    private LinearLayout statusLayout;
    private TextView statusText;
    private ImageView statusIcon;

    private TextView badgeText;

    private ViewPager2 slider;
    private Handler handler;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {   // ✅ FIXED

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);

        if (getActivity() != null) {
            requireActivity().setTitle("Raktsaathi+");
        }

        // STATUS UI
        statusLayout = view.findViewById(R.id.statusLayout);
        statusText = view.findViewById(R.id.tv_statusText);
        statusIcon = view.findViewById(R.id.iv_statusIcon);

        updateStatusUI();

        // 🔥 SLIDER
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

        // 🔥 BUTTON NAVIGATION (CORRECT WAY)
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.homebottomnavHome);

        CardView btnDonate = view.findViewById(R.id.btnDonate);
        CardView btnRequest = view.findViewById(R.id.btnRequest);

        btnDonate.setOnClickListener(v ->
                bottomNav.setSelectedItemId(R.id.homebottomnavDonate));

        btnRequest.setOnClickListener(v ->
                bottomNav.setSelectedItemId(R.id.homebottomnavRequests));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem item = menu.findItem(R.id.action_notification);
        View actionView = item.getActionView();

        if (actionView != null) {
            badgeText = actionView.findViewById(R.id.tv_badge);
            actionView.setOnClickListener(v -> onOptionsItemSelected(item));
            updateBadge();
        }

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

    private void updateBadge() {
        int notificationCount = 1;

        if (badgeText == null) return;

        badgeText.setVisibility(notificationCount > 0 ? View.VISIBLE : View.GONE);
    }

    private void updateStatusUI() {
        if (isEligible) {
            statusLayout.setBackgroundResource(R.drawable.bg_green_status);
            statusText.setText("You're eligible to Donate");
            statusIcon.setImageResource(R.drawable.rs_right);
        } else {
            statusLayout.setBackgroundResource(R.drawable.bg_red_status);
            statusText.setText("You're not eligible to Donate");
            statusIcon.setImageResource(R.drawable.rs_crossss);
        }
    }
}