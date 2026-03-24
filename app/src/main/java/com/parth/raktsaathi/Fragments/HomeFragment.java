package com.parth.raktsaathi.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parth.raktsaathi.NotificationActivity;
import com.parth.raktsaathi.R;

public class HomeFragment extends Fragment {

    private boolean isEligible = false;

    private LinearLayout statusLayout;
    private TextView statusText;
    private ImageView statusIcon;

    private TextView badgeText;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);

        if (getActivity() != null) {
            requireActivity().setTitle("Raktsaathi+");
        }

        if (getArguments() != null) {
            isEligible = getArguments().getBoolean("isEligible", false);
        }

        statusLayout = view.findViewById(R.id.statusLayout);
        statusText = view.findViewById(R.id.tv_statusText);
        statusIcon = view.findViewById(R.id.iv_statusIcon);

        updateStatusUI();

        return view;
    }

    // 🔔 MENU CREATE
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem item = menu.findItem(R.id.action_notification);
        View actionView = item.getActionView();

        if (actionView != null) {

            badgeText = actionView.findViewById(R.id.tv_badge);

            actionView.setOnClickListener(v -> {
                onOptionsItemSelected(item); // ✅ FIXED
            });

            updateBadge();
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    // 🔔 CLICK HANDLE (ONLY ONE METHOD)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_notification) {

            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // 🔴 Badge Logic
    private void updateBadge() {

        int notificationCount = 1;

        if (badgeText == null) return;

        if (notificationCount > 0) {
            badgeText.setVisibility(View.VISIBLE);
        } else {
            badgeText.setVisibility(View.GONE);
        }
    }

    // 🎯 Status UI
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