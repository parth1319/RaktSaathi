package com.parth.raktsaathi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parth.raktsaathi.Fragments.*;

import androidx.activity.OnBackPressedCallback;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);

        if (!sp.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, IntroScreenActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.homeBottomNavigationView);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeFrameLayout, new HomeFragment())
                .commit();

        bottomNav.setSelectedItemId(R.id.homebottomnavHome);

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            if (item.getItemId() == R.id.homebottomnavHome) {
                fragment = new HomeFragment();
            }
            else if (item.getItemId() == R.id.homebottomnavDonate) {
                fragment = new DonateFragment();
            }
            else if (item.getItemId() == R.id.homebottomnavRequests) {
                fragment = new RequestFragment();
            }
            else if (item.getItemId() == R.id.homebottomnavProfile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeFrameLayout, fragment)
                        .commit();
            }

            return true;
        });

        // 🔥 BACK HANDLING
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
}