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

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);

        if(sp.getBoolean("isLoggedIn", false)){
            startActivity(new Intent(IntroScreenActivity.this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_intro_screen);

        btnLogin = findViewById(R.id.btnLogin);
        btnCreate = findViewById(R.id.btncreteaccIntroScrn);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(IntroScreenActivity.this, LoginActivity.class));
        });

        btnCreate.setOnClickListener(v -> {

            startActivity(new Intent(IntroScreenActivity.this, RegistrationActivity.class));
        });
    }
}