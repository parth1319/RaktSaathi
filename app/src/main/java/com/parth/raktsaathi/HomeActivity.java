package com.parth.raktsaathi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parth.raktsaathi.Fragments.Find_DonorFragment;
import com.parth.raktsaathi.Fragments.HomeFragment;
import com.parth.raktsaathi.Fragments.ProfileFragment;
import com.parth.raktsaathi.Fragments.RequestsFragment;

import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    public boolean doubletap = false;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    BottomNavigationView bottomNavigationView;

    // Fragments
    HomeFragment homeFragment = new HomeFragment();
    RequestsFragment requestsFragment = new RequestsFragment();
    Find_DonorFragment findDonorFragment = new Find_DonorFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        editor = preferences.edit();

        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            welcome();
        }

        // ✅ 🔥 NEW CODE (ADD THIS HERE)
        boolean isEligible = getIntent().getBooleanExtra("isEligible", false);

        Bundle bundle = new Bundle();
        bundle.putBoolean("isEligible", isEligible);

        homeFragment.setArguments(bundle);
        // ✅ END

        bottomNavigationView = findViewById(R.id.homeBottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Check if we should show a specific fragment
        String target = getIntent().getStringExtra("targetFragment");
        if (target != null && target.equals("Home")) {
            loadHomeFragment();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeFrameLayout, homeFragment) // ✅ use same instance
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.homebottomnavHome);
        }
    }

    private void loadHomeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeFrameLayout, homeFragment)
                .commit();
        bottomNavigationView.setSelectedItemId(R.id.homebottomnavHome);
    }

    private void welcome() {
        AlertDialog.Builder ad = new AlertDialog.Builder(HomeActivity.this);
        ad.setTitle("RaktSaathi App");
        ad.setMessage("Welcome to RaktSaathi App");
        ad.setPositiveButton("Thank You", (dialog, which) -> dialog.dismiss());
        ad.show();

        editor.putBoolean("isFirstTime", false);
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.homebottomnavHome) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeFrameLayout, homeFragment) // ✅ keep same instance
                    .commit();

        } else if (menuItem.getItemId() == R.id.homebottomnavRequests) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeFrameLayout, requestsFragment)
                    .commit();

        } else if (menuItem.getItemId() == R.id.homebottomnavDonate) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeFrameLayout, findDonorFragment)
                    .commit();

        } else if (menuItem.getItemId() == R.id.homebottomnavProfile) {
            Intent intent = new Intent(HomeActivity.this, ProfileFragment.class);
            startActivity(intent);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == R.id.menuMyLocation)
        {
            Intent intent = new Intent(HomeActivity.this, MyLocationActivity.class);
            startActivity(intent);
        }
        return true;
    }
}