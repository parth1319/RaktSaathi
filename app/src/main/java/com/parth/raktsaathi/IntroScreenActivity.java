package com.parth.raktsaathi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class IntroScreenActivity extends AppCompatActivity {

    Button btnLogin, btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 CHECK USER ALREADY LOGGED IN
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);

        if(sp.getBoolean("isLoggedIn", false)){
            // 👉 Already login → direct Home
            startActivity(new Intent(IntroScreenActivity.this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_intro_screen);

        btnLogin = findViewById(R.id.btnLogin);
        btnCreate = findViewById(R.id.btncreteaccIntroScrn);

        // 🔘 LOGIN BUTTON
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(IntroScreenActivity.this, LoginActivity.class));
        });

        // 🔘 CREATE ACCOUNT BUTTON
        btnCreate.setOnClickListener(v -> {

            startActivity(new Intent(IntroScreenActivity.this, RegistrationActivity.class));
        });
    }
}