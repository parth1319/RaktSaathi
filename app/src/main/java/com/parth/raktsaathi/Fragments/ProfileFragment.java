package com.parth.raktsaathi.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.parth.raktsaathi.LoginActivity;
import com.parth.raktsaathi.R;

public class ProfileFragment extends Fragment {

    TextView name, email, phone;
    Button logoutBtn, editBtn;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 🔗 Bind Views
        name = view.findViewById(R.id.profileName);
        email = view.findViewById(R.id.profileEmail);
        phone = view.findViewById(R.id.profilePhone);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        editBtn = view.findViewById(R.id.editProfileBtn);

        // 🔥 Get session data
        SharedPreferences sp = getActivity().getSharedPreferences("user_session", getActivity().MODE_PRIVATE);

        String userEmail = sp.getString("email", "User");
        String userPhone = sp.getString("phone", "Not Available");

        name.setText(userEmail); // later name add karu
        email.setText(userEmail);
        phone.setText(userPhone);

        logoutBtn.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();

            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        editBtn.setOnClickListener(v -> {
        });

        return view;
    }
}