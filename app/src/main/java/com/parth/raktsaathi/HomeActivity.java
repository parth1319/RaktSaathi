package com.parth.raktsaathi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parth.raktsaathi.Fragments.*;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    Toolbar toolbar; // 🔥 ADD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 LOGIN CHECK
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);

        if (!sp.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.homeBottomNavigationView);
        toolbar = findViewById(R.id.toolbarHome); // 🔥 ADD
        setSupportActionBar(toolbar);

        // 🔥 DEFAULT = HOME → SHOW HEADER
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            toolbar.setVisibility(View.VISIBLE); // 🔥 SHOW
            bottomNav.setSelectedItemId(R.id.homebottomnavHome);
        }

        // 🔥 NAVIGATION
        bottomNav.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            if (item.getItemId() == R.id.homebottomnavHome) {
                fragment = new HomeFragment();
                toolbar.setVisibility(View.VISIBLE); // 🔥 ONLY HOME SHOW
            }
            else if (item.getItemId() == R.id.homebottomnavDonate) {
                fragment = new DonateFragment();
                toolbar.setVisibility(View.GONE); // 🔥 HIDE
            }
            else if (item.getItemId() == R.id.homebottomnavRequests) {
                fragment = new RequestFragment();
                toolbar.setVisibility(View.GONE); // 🔥 HIDE
            }
            else if (item.getItemId() == R.id.homebottomnavProfile) {
                fragment = new ProfileFragment();
                toolbar.setVisibility(View.GONE); // 🔥 HIDE
            }

            return loadFragment(fragment);
        });

        // 🔥 BACK PRESS
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    // 🔥 COMMON METHOD
    private boolean loadFragment(Fragment fragment){
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