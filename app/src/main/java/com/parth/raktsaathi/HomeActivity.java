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

        // 🔥 LOGIN CHECK
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);

        // 🔥 APPLY THEME
        int mode = sp.getInt("mode", 1); // 1 = Light, 2 = Dark
        if (mode == 2) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (!sp.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.homeBottomNavigationView);

        // 🔥 DEFAULT FRAGMENT
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNav.setSelectedItemId(R.id.homebottomnavHome);
        }

        // 🔥 NAVIGATION
        bottomNav.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            if (item.getItemId() == R.id.homebottomnavHome) {
                fragment = new HomeFragment();
            }
            else if (item.getItemId() == R.id.homebottomnavRequests) {
                fragment = new RequestFragment();
            }
            else if (item.getItemId() == R.id.homebottomnavDonate) {
                fragment = new DonateFragment();
            }
            else if (item.getItemId() == R.id.homebottomnavProfile) {
                fragment = new ProfileFragment();
            }

            return loadFragment(fragment);
        });

        // 🔥 BACK PRESS
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    if (bottomNav.getSelectedItemId() != R.id.homebottomnavHome) {
                        bottomNav.setSelectedItemId(R.id.homebottomnavHome);
                    } else {
                        finish();
                    }
                }
            }
        });
    }

    // 🔥 COMMON METHOD
    public boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeFrameLayout, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}