package com.parth.raktsaathi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parth.raktsaathi.Fragments.DonateFragment;
import com.parth.raktsaathi.Fragments.HomeFragment;
import com.parth.raktsaathi.Fragments.ProfileFragment;
import com.parth.raktsaathi.Fragments.RequestFragment;
import androidx.activity.OnBackPressedCallback;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.homeBottomNavigationView);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeFrameLayout, new HomeFragment())
                .commit();

        // 🔥 Bottom nav highlight (Home selected)
        bottomNav.setSelectedItemId(R.id.homebottomnavHome);

        // 🔥 Bottom Navigation Click
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
                        .addToBackStack(null) // 🔥 BACK SUPPORT
                        .commit();
            }

            return true;
        });

        // 🔥 BACK BUTTON HANDLE
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish(); // app close
                }
            }
        });
    }
}
