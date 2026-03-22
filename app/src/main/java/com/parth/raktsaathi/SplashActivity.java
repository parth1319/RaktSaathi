package com.parth.raktsaathi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.parth.raktsaathi.IntroScreenActivity;
import com.parth.raktsaathi.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, IntroScreenActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME);
    }
}