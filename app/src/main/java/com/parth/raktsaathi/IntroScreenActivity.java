package com.parth.raktsaathi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parth.raktsaathi.Fragments.HomeFragment;
import com.parth.raktsaathi.LoginActivity;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.RegistrationActivity;

public class IntroScreenActivity extends AppCompatActivity {

    TextView tvSkip;
    Button btnLogin, btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen); // your XML file name

        // Initialize views
        tvSkip = findViewById(R.id.tvSkip);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreate = findViewById(R.id.btncreteaccIntroScrn);

        // 🔹 Login Button
        // Login
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(IntroScreenActivity.this, LoginActivity.class));
        });

// Create Account
        btnCreate.setOnClickListener(v -> {
            startActivity(new Intent(IntroScreenActivity.this, RegistrationActivity.class));
        });

        // 🔹 Skip Now → Go to Home (MainActivity)
        tvSkip.setOnClickListener(v -> {
            Intent intent = new Intent(IntroScreenActivity.this, HomeActivity.class);
            intent.putExtra("isEligible", false);
            startActivity(intent);
            finish();
        });
    }
}