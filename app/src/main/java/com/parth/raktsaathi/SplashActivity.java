// C:\Users\PARTH\AndroidStudioProjects\RaktSaathi\app\src\main\java\com\parth\raktsaathi\SplashActivity.java

package com.parth.raktsaathi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 🔥 LOAD SAVED THEME MODE BEFORE ONCREATE
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        int mode = sp.getInt("mode", 1); // 1 = Light, 2 = Dark
        if (mode == 2) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, IntroScreenActivity.class));
        }
        finish();
    }
}