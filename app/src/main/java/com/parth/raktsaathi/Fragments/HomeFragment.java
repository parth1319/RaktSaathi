package com.parth.raktsaathi.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parth.raktsaathi.R;

public class HomeFragment extends Fragment {

    private boolean isEligible = false;

    private LinearLayout statusLayout;
    private TextView statusText;
    private ImageView statusIcon;

    public HomeFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔥 Get eligibility data safely
        if (getArguments() != null) {
            isEligible = getArguments().getBoolean("isEligible", false);
        }

        // 🔗 Bind UI
        statusLayout = view.findViewById(R.id.statusLayout);
        statusText = view.findViewById(R.id.tv_statusText);
        statusIcon = view.findViewById(R.id.iv_statusIcon);

        // 🎯 Apply UI
        updateStatusUI();

        return view;
    }

    // 🔥 Separate method (clean code practice)
    private void updateStatusUI() {

        if (isEligible) {
            // ✅ Eligible (Green)
            statusLayout.setBackgroundResource(R.drawable.bg_green_status);
            statusText.setText("You're eligible to Donate");
            statusIcon.setImageResource(R.drawable.rs_right);

        } else {
            // ❌ Not Eligible (Red)
            statusLayout.setBackgroundResource(R.drawable.bg_red_status);
            statusText.setText("You're not eligible to Donate");
            statusIcon.setImageResource(R.drawable.rs_crossss); // ⚠️ better icon name
        }
    }
}
