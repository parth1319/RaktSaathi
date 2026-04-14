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

    public BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences themeSp = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDark = themeSp.getBoolean("isDark", false);
        if (isDark) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);


        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        if (!sp.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.homeBottomNavigationView);


        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNav.setSelectedItemId(R.id.homebottomnavHome);
        }


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