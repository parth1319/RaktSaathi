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