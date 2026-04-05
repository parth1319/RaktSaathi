package com.parth.raktsaathi.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.loopj.android.http.*;
import com.parth.raktsaathi.*;
import com.parth.raktsaathi.R;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileFragment extends Fragment {

    TextView tvName, tvMobile, tvEmail, tvBlood, tvCity;
    TextView btnEditProfile, btnChangePass, btnLogout;
    LinearLayout btnSettingsToggle, btnAboutToggle;
    TextView arrowSettings, arrowAbout , aboutContent;
    LinearLayout settingsContent;
    Switch switchDark;

    boolean isSettingsOpen = false;
    boolean isAboutOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Context context = requireActivity();

        // 🔥 SharedPreferences
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

        if(!isLoggedIn){
            startActivity(new Intent(context, LoginActivity.class));
            requireActivity().finish();
            return view;
        }

        String email = sp.getString("email", "");

        // 🔥 IDs
        tvName = view.findViewById(R.id.tvName);
        tvMobile = view.findViewById(R.id.tvMobile);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvBlood = view.findViewById(R.id.tvBlood);
        tvCity = view.findViewById(R.id.tvCity);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePass = view.findViewById(R.id.btnChangePass);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnSettingsToggle = view.findViewById(R.id.btnSettingsToggle);
        btnAboutToggle = view.findViewById(R.id.btnAboutToggle);

        settingsContent = view.findViewById(R.id.settingsContent);
        aboutContent = view.findViewById(R.id.aboutContent);

        arrowSettings = view.findViewById(R.id.arrowSettings);
        arrowAbout = view.findViewById(R.id.arrowAbout);

        switchDark = view.findViewById(R.id.switchDark);

        tvEmail.setText("📧 " + email);

        // 🔥 FETCH PROFILE FROM DATABASE
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);

        client.post(Urls.GET_PROFILE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String res = new String(responseBody);
                    JSONObject obj = new JSONObject(res);

                    if(obj.getString("status").equals("success")){

                        tvName.setText(obj.getString("name"));
                        tvMobile.setText("📞 " + obj.getString("phone"));
                        tvBlood.setText("🩸 " + obj.getString("blood"));
                        tvCity.setText("📍 " + obj.getString("location"));

                    } else {
                        Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){
                    Toast.makeText(context, "JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔽 SETTINGS TOGGLE
        btnSettingsToggle.setOnClickListener(v -> {
            if (isSettingsOpen) {
                settingsContent.setVisibility(View.GONE);
                arrowSettings.setText("▼");
            } else {
                settingsContent.setVisibility(View.VISIBLE);
                arrowSettings.setText("▲");
            }
            isSettingsOpen = !isSettingsOpen;
        });

        // 🔽 ABOUT TOGGLE
        btnAboutToggle.setOnClickListener(v -> {
            if (isAboutOpen) {
                aboutContent.setVisibility(View.GONE);
                arrowAbout.setText("▼");
            } else {
                aboutContent.setVisibility(View.VISIBLE);
                arrowAbout.setText("▲");
            }
            isAboutOpen = !isAboutOpen;
        });

        // 🌙 DARK MODE
        switchDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // ✏️ EDIT PROFILE
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(context, EditProfileActivity.class))
        );

        // 🔒 CHANGE PASSWORD
        btnChangePass.setOnClickListener(v ->
                startActivity(new Intent(context, ChangePasswordActivity.class))
        );

        // 🚪 LOGOUT
        btnLogout.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (d, w) -> {

                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();

                        startActivity(new Intent(context, LoginActivity.class));
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return view;
    }
}